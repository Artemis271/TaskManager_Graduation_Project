import './register-form.css'
import '../login-form.css'
import {Link, useNavigate} from "react-router-dom";
import Button from "../../button/button";
import {useRef, useState} from "react";
import {FaTrashCan} from "react-icons/fa6";
import {FaEyeSlash, FaRegEye} from "react-icons/fa";
import axios, {HttpStatusCode} from "axios";
import {useAuth} from "../../../hooks/auth";
import Oauth2Icon from "../oauth2-icon";

export default function RegisterForm({isEdit = false, initUserData = {}, onSubmit, ...props})
{
    const {login} = useAuth();
    const navigate = useNavigate();
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [formData, setFormData] = useState({
        username: initUserData.username ?? '',
        name: initUserData.name ?? '',
        lastName: initUserData.lastName ?? '',
        password: '',
        confirmPassword: '',
        email: '',
    });
    const [errors, setErrors] = useState({});
    const [loading, setLoading] = useState(false);
    const fileRef = useRef();

    const handleChange = (e) => {
        const {name, value, files} = e.target;
        if (files)
            setFormData((prev) => ({...prev, avatars: files[0]}));
        else setFormData((prev) => ({...prev, [name]: value}));
    };

    const validate = () => {
        const errs = {};
        if (!formData.username.trim()) {
            errs.username = 'Нужно заполнить';
        }

        if (!isEdit) {
            ['password', 'confirmPassword', 'email'].forEach(field => {
                if (!formData[field]?.trim())
                    errs[field] = 'Нужно заполнить';
            })
        }

        if (formData.password && formData.password.length < 6)
            errs.password = 'Минимум 6 символов';

        if (formData.password && formData.confirmPassword
            && formData.password !== formData.confirmPassword)
            errs.confirmPassword = 'Пароли не совпадают';

        if (formData.email) {
            const emailRe = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRe.test(formData.email))
                errs.email = 'Некорректный e‑mail';
        }

        if (formData.avatars && formData.avatars.size > 5 * 1024 * 1024)
            errs.avatars = 'Максимальный размер 5MB';
        return errs;
    };

    const handleSubmit = async (e) => {

        e.preventDefault();
        const errs = validate();
        setErrors(errs);
        if (Object.keys(errs).length > 0) return;

        const backHost = process.env.REACT_APP_BACKEND_PROJECT_SERVICE_HOST;
        const backPort = process.env.REACT_APP_BACKEND_PORT;

        setLoading(true);
        try {
            const payload = new FormData();
            payload.append('username', formData.username);
            payload.append('name', formData.name === null ? '' : formData.name);
            payload.append('lastName', formData.lastName === null ? '' : formData.lastName);
            if (formData.avatars)
                payload.append('avatars', formData.avatars);

            if (!isEdit) {
                payload.append('password', formData.password);
                payload.append('email', formData.email);
                const response =
                    await axios.post(`http://${backHost}:${backPort}/api/auth/registration`, payload,
                        {headers: {'Content-Type': 'multipart/form-data'}});

                if (!isEdit && response.status === HttpStatusCode.Created) {
                    await login({
                        username: formData.email,
                        password: formData.password
                    });
                    navigate('/')
                }
            }
            else await onSubmit(payload);
        } catch (err) {
            setErrors({submit: 'Ошибка при регистрации'});
        } finally {
            setLoading(false);
        }
    };

    const dropFile = () => {
        if (fileRef.current) fileRef.current.value = ""
    };

    return (
        <div className="register-form-wrapper">
            <form onSubmit={handleSubmit} className="login-form" style={props.style}>
                <h2>{isEdit ? 'Редактирование' : 'Регистрация'}</h2>

                <div className="form-group">
                    <label htmlFor="username">Юзернейм:</label>
                    <input
                        type="text"
                        id="username"
                        name="username"
                        placeholder="Ваш юзернейм"
                        value={formData.username}
                        onChange={handleChange}
                        required
                    />
                    {errors.username && <div className="field-error">{errors.username}</div>}
                </div>

                <div className="form-group">
                    <label htmlFor="name">Имя:</label>
                    <input
                        type="text"
                        id="name"
                        name="name"
                        placeholder="Ваше имя (опционально)"
                        value={formData.name}
                        onChange={handleChange}
                    />
                    {errors.name && <div className="field-error">{errors.name}</div>}
                </div>

                <div className="form-group">
                    <label htmlFor="lastName">Фамилия:</label>
                    <input
                        type="text"
                        id="lastName"
                        name="lastName"
                        placeholder="Ваша фамилия (опционально)"
                        value={formData.lastName}
                        onChange={handleChange}
                    />
                    {errors.lastName && <div className="field-error">{errors.lastName}</div>}
                </div>

                {!isEdit && <div className="password-inputs">
                    <div className="password-inputs-1 password-wrapper">
                        <label htmlFor="password">Пароль:</label>
                        <div className="password-inner-wrapper">
                            <input
                                type={showPassword ? "text" : "password"}
                                id="password"
                                name="password"
                                placeholder="Ваш пароль"
                                value={formData.password}
                                onChange={handleChange}
                                required
                            />
                            <span
                                className="registration-password-toggle-icon"
                                onClick={() => setShowPassword(s => !s)}>
                                {showPassword ? <FaEyeSlash/> : <FaRegEye/>}
                            </span>
                        </div>
                        {errors.password && <div className="field-error">{errors.password}</div>}
                    </div>
                    <div className="password-inputs-1 password-confirm-wrapper">
                        <label htmlFor="confirm-password">Повтор пароля:</label>
                        <div className="password-inner-wrapper">
                            <input
                                type={showConfirmPassword ? "text" : "password"}
                                id="confirm-password"
                                name="confirmPassword"
                                placeholder="Повторный пароль"
                                value={formData.confirmPassword}
                                onChange={handleChange}
                                required
                            />
                            <span
                                className="registration-password-toggle-icon"
                                onClick={() => setShowConfirmPassword(s => !s)}>
                                {showConfirmPassword ? <FaEyeSlash/> : <FaRegEye/>}
                            </span>
                        </div>
                        {errors.confirmPassword && <div className="field-error">{errors.confirmPassword}</div>}
                    </div>
                </div>}

                {!isEdit && <div className="form-group">
                    <label htmlFor="email">Электронная почта:</label>
                    <input
                        type="text"
                        id="email"
                        name="email"
                        placeholder="Ваша почта"
                        value={formData.email}
                        onChange={handleChange}
                        required
                    />
                    {errors.email && <div className="field-error">{errors.email}</div>}
                </div>}

                <div className="avatar-form-group">
                    <div className="avatar-wrapper">
                        <label htmlFor="avatars">Аватар:</label>
                        <input
                            type="file"
                            id="avatars"
                            name="avatars"
                            onChange={handleChange}
                            ref={fileRef}
                        />
                        {errors.avatars && <div className="field-error">{errors.avatars}</div>}
                    </div>
                    <div className="avatar-drop-button">
                        <Button onClickFunction={dropFile}>
                            <FaTrashCan/> Сбросить
                        </Button>
                    </div>
                </div>

                {!isEdit &&
                    <>
                        <Oauth2Icon/>
                        <div className="register-container">
                            Есть аккаунт?
                            <Link to="/login"> Войти</Link>
                        </div>
                    </>}

                {errors.submit && <div className="form-error">{errors.submit}</div>}

                <Button type="submit" disabled={loading}>
                    {loading ? 'Загрузка...' : isEdit ? 'Изменить данные' : 'Зарегистрироваться'}
                </Button>
            </form>
        </div>
    )
}