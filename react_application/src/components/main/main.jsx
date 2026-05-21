import './main.css'
import Button from "../button/button";
import {useNavigate} from "react-router-dom";
import {LuKanban, LuBrain, LuMessageSquare, LuUsers, LuListTodo, LuChartBar} from "react-icons/lu";

const features = [
    {
        icon: <LuListTodo />,
        title: "Проекты и задачи",
        desc: "Создавайте проекты, ставьте задачи с дедлайнами и приоритетами"
    },
    {
        icon: <LuKanban />,
        title: "Канбан-доска",
        desc: "Визуально управляйте задачами по статусам: планирование, в работе, завершено"
    },
    {
        icon: <LuBrain />,
        title: "AI декомпозиция",
        desc: "Gemini автоматически разбивает сложную задачу на подзадачи"
    },
    {
        icon: <LuMessageSquare />,
        title: "Командный чат",
        desc: "Real-time общение через WebSocket прямо внутри платформы"
    },
    {
        icon: <LuUsers />,
        title: "Управление командой",
        desc: "Добавляйте участников, назначайте роли и разграничивайте доступ"
    },
    {
        icon: <LuChartBar />,
        title: "Аналитика",
        desc: "Статистика задач и активности команды в личном кабинете"
    },
];

export default function Main() {
    const navigate = useNavigate();

    return (
        <div className="hero-page">
            <section className="hero-section">
                <div className="hero-glow hero-glow--left" />
                <div className="hero-glow hero-glow--right" />
                <div className="hero-badge">Микросервисная платформа</div>
                <h1 className="hero-title">
                    <span className="hero-title-accent">Task</span>Manager
                </h1>
                <p className="hero-subtitle">
                    Инструмент для управления проектами, задачами и командой с AI-поддержкой
                </p>
                <div className="hero-buttons">
                    <Button onClickFunction={() => navigate('/login')}>Войти</Button>
                    <Button onClickFunction={() => navigate('/register')} className="button--outline">
                        Зарегистрироваться
                    </Button>
                </div>
            </section>

            <section className="features-section">
                <h2 className="features-title">Возможности</h2>
                <div className="features-grid">
                    {features.map(f => (
                        <div key={f.title} className="feature-card">
                            <div className="feature-card__icon">{f.icon}</div>
                            <h3 className="feature-card__title">{f.title}</h3>
                            <p className="feature-card__desc">{f.desc}</p>
                        </div>
                    ))}
                </div>
            </section>
        </div>
    );
}
