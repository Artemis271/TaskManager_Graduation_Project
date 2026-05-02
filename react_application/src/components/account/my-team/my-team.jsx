import './my-team.css';
import {useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import axios from "axios";
import {useAuth} from "../../../hooks/auth";

export default function MyTeam() {
    const {user} = useAuth();
    const navigate = useNavigate();
    const [users, setUsers] = useState([]);

    const backHost = process.env.REACT_APP_BACKEND_HOST;
    const backPort = process.env.REACT_APP_BACKEND_PORT;

    useEffect(() => {
        axios.get(
            `http://${backHost}:${backPort}/api/chat/user/my-team`,
            {
                params: {userId: user.id},
                headers: {Authorization: `Bearer ${localStorage.getItem('jwtToken')}`}
            }
        ).then(res => setUsers(res.data));
    }, []);

    return (
        <div className="my-team-container">
            <h1 className="my-team-header">Моя команда:</h1>
            {
                users.map(member => (
                    <div className="current-member"
                         key={member.id}>
                        <img className="member-avatar"
                             src={member.binaryData ? `data:image/jpeg;base64,${member.binaryData}`
                                 : '/default-avatar.png'}
                             alt="member-avatar"
                             role="button"
                             onClick={() => navigate(`/account/${member.id}`)}
                             title="Профиль пользователя"/>
                        <div className="member-username">{member.username}</div>
                        {member.id === user.id && <span>Я</span>}
                    </div>
                ))
            }
        </div>
    );
}