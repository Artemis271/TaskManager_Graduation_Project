import {Link, useLocation, useNavigate} from "react-router-dom";
import {useState} from "react";
import {useAuth} from "../../hooks/auth";
import './login-form.css'
import Button from "../button/button";
import Oauth2Icon from "./oauth2-icon";
import {FaEyeSlash, FaRegEye} from "react-icons/fa";

export default function LoginForm()
{
    const { login } = useAuth();
    const navigate = useNavigate();
    const [formData, setFormData] = useState({ username: '', password: '' });
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);
    const location = useLocation();
    const [showPassword, setShowPassword] = useState(false);

    const from = location.state?.from?.pathname || '/404';

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        try {
            await login(formData);
            navigate(from, { replace: true });
        } catch (err) {
            console.log(err)
            setError('Неверное имя пользователя или пароль');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-form-wrapper">
            <form onSubmit={handleSubmit} className="login-form">
                <h2>Вход</h2>
                <div className="form-group">
                    <label htmlFor="username">Логин:</label>
                    <input
                        type="text"
                        id="username"
                        name="username"
                        placeholder="Ваша почта"
                        value={formData.username}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div className="form-group password-wrapper">
                    <label htmlFor="password">Пароль:</label>
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
                        className="password-toggle-icon"
                        onClick={() => setShowPassword(s => !s)}>
                        {showPassword ? <FaEyeSlash/> : <FaRegEye/>}
                    </span>
                </div>
                {error && <div className="form-error">{error}</div>}

                <Oauth2Icon/>
                <div className="register-container">
                    Нет аккаунта?
                    <Link to="/register"> Зарегистрируйся!</Link>
                </div>
                <Button type="submit" disabled={loading}>
                    {loading ? 'Вход...' : 'Войти'}
                </Button>
            </form>
        </div>
    )
}