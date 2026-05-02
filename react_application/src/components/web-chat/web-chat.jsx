import './web-chat.css';
import Button from "../button/button";
import {useEffect, useLayoutEffect, useRef, useState} from "react";
import {useNavigate} from "react-router-dom";
import {useAuth} from "../../hooks/auth";
import Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import axios from "axios";
import CreateChat from "./create-chat/create-chat";
import LoadingData from "../info/loading-data/loading-data";
import SearchUser from "./search-user/search-user";

export default function WebChat() {
    const {user} = useAuth();
    const navigate = useNavigate();
    const stompRef = useRef(null);
    const listRef = useRef(null);

    const [chats, setChats] = useState([]);
    const [currentChatId, setCurrentChatId] = useState(null);
    const [messages, setMessages] = useState([]);
    const [draft, setDraft] = useState("");

    const [loadingChats, setLoadingChats] = useState(true);

    const webSocketUri = process.env.REACT_APP_WEBSOCKET_URL;
    const backHost = process.env.REACT_APP_BACKEND_HOST;
    const backPort = process.env.REACT_APP_BACKEND_PORT;

    async function loadChats() {
        try {
            const res = await axios.get(
                `http://${backHost}:${backPort}/api/chat/all`,
                {
                    headers: {Authorization: `Bearer ${localStorage.getItem('jwtToken')}`},
                    params: {userId: user.id}
                }
            );
            setChats(res.data);
            console.log(res.data)
            if (res.data.length > 0) {
                setCurrentChatId(res.data[0].id);
            }
        } catch (err) {
            console.error("Не удалось загрузить чаты:", err);
            // Возможно, стоит показать ошибку
        } finally {
            setLoadingChats(false);
        }
    }

    useEffect(() => {
        loadChats();
    }, []);
    useEffect(() => {
        if (!currentChatId) return;

        (async () => {
            try {
                const res = await axios.get(
                    `http://${backHost}:${backPort}/api/chat/messages/all`,
                    {
                        headers: {Authorization: `Bearer ${localStorage.getItem('jwtToken')}`},
                        params: {chatId: currentChatId}
                    }
                );
                setMessages(res.data);
            } catch (_) { /* ... */
            }
        })();

        const socket = new SockJS(webSocketUri);
        const client = Stomp.over(socket);
        client.connect({}, () => {
            stompRef.current = client;
            client.subscribe(
                `/topic/chats/${currentChatId}/new-message`,
                ({body}) => {
                    const msg = JSON.parse(body);
                    setMessages(prev =>
                        prev.some(m => m.id === msg.id) ? prev : [...prev, msg]
                    );
                }
            );
        });
        return () => {
            if (stompRef.current?.connected) {
                stompRef.current.disconnect();
            }
        };
    }, [currentChatId]);

    useLayoutEffect(() => {
        if (listRef.current) {
            listRef.current.scrollTop = listRef.current.scrollHeight;
        }
    }, [messages]);

    function send(messageType) {
        if (!draft.trim() || !stompRef.current?.connected) return;
        const payload = {
            chatId: currentChatId,
            action: messageType.action,
            type: messageType.type,
            ownerId: user.id,
            content: draft.trim()
        };
        stompRef.current.send("/app/chat/send-message", {}, JSON.stringify(payload));
        setDraft("");
    }

    function handleChatCreated(newChat) {
        setChats(prev => [newChat, ...prev]);
        setCurrentChatId(newChat.id);
    }

    if (loadingChats)
        return <LoadingData/>;

    const groupMessagesByDate = (messages) => {
        return messages.reduce((groups, message) => {
            const date = new Date(message.dateCreated);
            const dateKey = date.toLocaleDateString('ru-RU', {
                day: 'numeric',
                month: 'long',
                year: 'numeric'
            });

            if (!groups[dateKey]) {
                groups[dateKey] = [];
            }
            groups[dateKey].push(message);
            return groups;
        }, {});
    };

    if (loadingChats)
        return <LoadingData/>

    return (
        <div className="web-chat-container-open">
            {chats.length === 0 ? <CreateChat onCreate={handleChatCreated}/>
                : <>
                    <h3 className="web-chat-header">Task-Manager</h3>
                    <div className="web-chat-options">
                        <select>
                            {chats.map(chat => (
                                <option
                                    key={chat.id}
                                    className={chat.id === currentChatId ? "active" : ""}
                                    onClick={() => setCurrentChatId(chat.id)}>
                                    {chat.title}
                                </option>
                            ))}
                        </select>
                        <div className="add-user-to-chat-icon">
                            <SearchUser onAddUser={userId => {
                                stompRef.current?.send(
                                    "/app/chat/send-message",
                                    {},
                                    JSON.stringify({
                                        chatId: currentChatId,
                                        action: "ADD_USER",
                                        type:   "JOIN",
                                        ownerId: user.id,
                                        userId: userId
                                    })
                                );
                            }}/>
                        </div>
                    </div>
                    <div className="messages-wrapper" ref={listRef}>
                        <ul className="chat-messages">
                            {Object.entries(groupMessagesByDate(messages)).map(([date, dayMessages]) => (
                                <>
                                    <div className="message-date-header">
                                        {date}
                                    </div>
                                    {dayMessages.map(m => (
                                        m.type === 'JOIN' ? <h4 className="new-user-message">{m.content}</h4>
                                            : <li key={m.id}
                                            className={m.ownerId === user.id ? 'my-chat-message' : 'chat-message'}>
                                            {m.ownerId !== user.id && (
                                                <>
                                                    <div className="web-chat-username">{m.username}</div>
                                                    <img src={m.avatarData ? `data:image/jpeg;base64,${m.avatarData}`
                                                        : '/default-avatar.png'}
                                                         className="chat-user-avatar"
                                                         role="button"
                                                         onClick={() => navigate(`/account/${m.ownerId}`)}/>
                                                </>
                                            )}
                                            <div className={m.ownerId === user.id
                                                ? 'my-chat-message-content'
                                                : 'chat-message-content'}>
                                                <span>{m.content}</span>
                                                <span className="timestamp-sended">
                                          {new Date(m.dateCreated).toLocaleTimeString([], {
                                              hour: '2-digit', minute: '2-digit',
                                          })}
                                        </span>
                                            </div>
                                            {m.ownerId === user.id && (
                                                <img
                                                    src={m.avatarData ? `data:image/jpeg;base64,${m.avatarData}`
                                                        : '/default-avatar.png'}
                                                    className="chat-user-avatar"
                                                    role="button"
                                                    onClick={() => navigate(`/account/${m.ownerId}`)}
                                                />
                                            )}
                                        </li>
                                    ))}
                                </>
                            ))}
                        </ul>
                    </div>
                    <div className="input-group">
                        <input
                            type="text"
                            placeholder="Сообщение..."
                            value={draft}
                            onChange={e => setDraft(e.target.value)}
                            onKeyDown={e =>
                                e.key === 'Enter' && send({action: 'NEW_MESSAGE', type: 'TEXT_MESSAGE'})}
                        />
                        <Button onClickFunction={
                            () => send(
                                {
                                    action: 'NEW_MESSAGE',
                                    type: 'TEXT_MESSAGE'
                                })
                        }>Отправить
                        </Button>
                    </div>
                </>}
        </div>
    );
}