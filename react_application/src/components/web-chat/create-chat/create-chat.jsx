import {useState} from "react";
import Button from "../../button/button";
import axios from "axios";
import {useAuth} from "../../../hooks/auth";
import './create-chat.css'

export default function CreateChat({onCreate}) {
    const {user} = useAuth();
    const [formData, setFormData] = useState({
        chatTitle: '',
        maxChatTitleLength: ''
    });

    const [errors, setErrors] = useState({
        chatTitle: '',
        maxChatTitleLength: ''
    });

    const handleChange = (e) => {
        const {name, value} = e.target;

        setFormData({
            ...formData,
            [name]: value,
        });
    };

    const validate = () => {
        const errs = {};
        if (!formData.chatTitle.trim())
            errs.chatTitle = 'Нужно заполнить';

        if (formData.chatTitle.length >= 50)
            errs.maxChatTitleLength = 'Название больше, чем 50 символов'

        return errs;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const errs = validate();
        setErrors(errs);
        if (Object.keys(errs).length > 0) return;

        const backHost = process.env.REACT_APP_BACKEND_HOST;
        const backPort = process.env.REACT_APP_BACKEND_PORT;

        try {
            const response = await axios.post(
                `http://${backHost}:${backPort}/api/chat/create-chat`, null,
                {params: {chatTitle: formData.chatTitle, ownerId: user.id}});
            onCreate(response.data)
        } catch (err) {
            setErrors({submit: 'Ошибка при регистрации'});
        }
    };

    return (
        <div className="create-chat-container">
            <form onSubmit={handleSubmit} className="create-chat-form">
                <h3>Создать новый чат</h3>
                <label htmlFor="chat-title"/>
                <input type="text"
                       id="chat-title"
                       name="chatTitle"
                       placeholder="Название чата"
                       onChange={handleChange}
                       value={formData.chatTitle}/>

                {errors.chatTitle && <div className="field-error">{errors.chatTitle}</div>}
                {errors.maxChatTitleLength && <div className="field-error">{errors.maxChatTitleLength}</div>}
                {errors.submit && <div className="form-error">{errors.submit}</div>}

                <Button type="submit">Создать чат</Button>
            </form>
        </div>
    )
}