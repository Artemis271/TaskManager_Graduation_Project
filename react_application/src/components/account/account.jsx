import './account.css'
import {useAuth} from "../../hooks/auth";
import {Navigate, useLocation, useNavigate, useParams} from "react-router-dom";
import {useEffect, useRef, useState} from "react";
import axios from "axios";
import LoadingData from "../info/loading-data/loading-data";
import Button from "../button/button";
import {CiCalendarDate} from "react-icons/ci";
import {MdAlternateEmail, MdOutlineAdminPanelSettings} from "react-icons/md";
import {TbActivity} from "react-icons/tb";
import {TiClipboard} from "react-icons/ti";
import {FaRegCircleUser} from "react-icons/fa6";
import ModalWindow from "../modal-window/modal-window";
import AvatarSlider from "../avatar-slider/avatar-slider";
import RegisterForm from "../login-form/register-form/register-form";

export default function Account() {
    const {userId} = useParams();
    const {isAuthenticated, logout, user} = useAuth();
    const location = useLocation();
    const [userData, setUserData] = useState(null);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();
    const didFetch = useRef(false);
    const [tasks, setTasks] = useState([]);
    const [statuses, setStatuses] = useState([]);
    const [errorFetchData, setErrorFetchData] = useState(false);

    const backHost = process.env.REACT_APP_BACKEND_PROJECT_SERVICE_HOST;
    const backPort = process.env.REACT_APP_BACKEND_PORT;

    if (!isAuthenticated)
        return <Navigate to="/login" state={{from: location}} replace/>;

    const fetchData = async () => {
        let mounted = true;
        try {
            const userRes = await axios.get(
                `http://${backHost}:${backPort}/api/auth/user/${userId}`,
                {headers: {Authorization: `Bearer ${localStorage.getItem('jwtToken')}`}}
            );
            if (!mounted) return;
            setUserData(userRes.data);

            const projectsRes = await axios.get(
                `http://${backHost}:${backPort}/api/projects/allProjects`,
                {params: {size: 100, userId}, headers: {Authorization: `Bearer ${localStorage.getItem('jwtToken')}`}}
            );
            if (!mounted) return;
            const uuids = projectsRes.data.map(p => p.id).join(',');
            const tasksRes = await axios.get(
                `http://${backHost}:${backPort}/api/tasks/allTasks-ProjectIds`,
                {params: {uuids}}
            );
            if (!mounted) return;
            setTasks(tasksRes.data);

            const statusesRes = await axios.get(
                `http://${backHost}:${backPort}/api/tasks/allTaskStatus`
            );
            if (!mounted) return;
            setStatuses(statusesRes.data);
        } catch (err) {
            if (axios.isAxiosError(err) && err.response) {
                const status = err.response.status;
                if (status === 404) { navigate('/404'); return; }
                if (status === 401) { setErrorFetchData(true); return; }
                navigate('/500'); return;
            }
            setErrorFetchData(true);
        } finally {
            if (mounted) setLoading(false);
        }
        return () => { mounted = false; };
    };

    const barStatusData = statuses.map(status =>
        tasks.filter(task => task.taskStatus === status).length);

    // eslint-disable-next-line react-hooks/rules-of-hooks
    useEffect(() => {
        if (!didFetch.current && user) {
            didFetch.current = true;
            fetchData();
        }
    }, [logout, user]);

    if (loading) return <LoadingData/>;
    if (!userData) return <Navigate to="/404"/>;

    const deleteAccount = async () => {
        await axios.delete(`http://${backHost}:${backPort}/api/auth/user/delete/${userId}`);
        logout();
    };

    const isOwn = userId == user.id;

    const taskLabels = ['Планирование', 'В процессе', 'Завершено', 'Отменено', 'Просрочено'];
    const taskClasses = [
        'account-task-planing',
        'account-task-in-progress',
        'account-task-finished',
        'account-task-canceled',
        'account-task-expired',
    ];

    return (
        <div className="main">
            <div className="account-square"/>
            <div className="account-ellipsis"/>
            <div className="account-circle"/>

            <div className="account-data">

                {/* ── Profile header ── */}
                <div className="account-profile-header">
                    <div className="account-avatar-block">
                        <AvatarSlider
                            data={userData.avatars}
                            baseUrl={`http://${backHost}:${backPort}/api/auth/user/${userId}/update-avatar`}
                        />
                    </div>
                    <div className="account-name-block">
                        <h2 className="account-display-name">
                            {userData.name ? `${userData.name} ${userData.lastName ?? ''}`.trim() : userData.username}
                        </h2>
                        <p className="account-username-line">
                            <FaRegCircleUser/> @{userData.username}
                        </p>
                        <div className="account-badges">
                            {userData.roles.map(role => (
                                <span key={role.id} className="account-role-badge">
                                    {role.role.split('_')[1]}
                                </span>
                            ))}
                            <span className={`account-activity-badge ${userData.activity ? 'active' : 'inactive'}`}>
                                <TbActivity/>
                                {userData.activity ? 'Активен' : 'Неактивен'}
                            </span>
                        </div>
                    </div>
                </div>

                {/* ── Body: info + stats ── */}
                <div className="account-body">

                    {/* Info card */}
                    <div className="account-info-card">
                        <p className="account-info-card-title">Данные профиля</p>

                        <div className="account-data-row">
                            <FaRegCircleUser/>
                            <strong>Юзернейм</strong>
                            {userData.username}
                        </div>
                        {userData.name && (
                            <div className="account-data-row">
                                <TiClipboard/>
                                <strong>Имя</strong>
                                {userData.name}
                            </div>
                        )}
                        {isOwn && userData.lastName && (
                            <div className="account-data-row">
                                <TiClipboard/>
                                <strong>Фамилия</strong>
                                {userData.lastName}
                            </div>
                        )}
                        {isOwn && (
                            <div className="account-data-row">
                                <MdAlternateEmail/>
                                <strong>Почта</strong>
                                {userData.email}
                            </div>
                        )}
                        <div className="account-data-row">
                            <CiCalendarDate/>
                            <strong>Регистрация</strong>
                            {userData.dateRegistration.split('T')[0]}
                        </div>
                        <div className="account-data-row">
                            <MdOutlineAdminPanelSettings/>
                            <strong>Роли</strong>
                            {userData.roles.map(r => r.role.split('_')[1]).join(', ')}
                        </div>
                    </div>

                    {/* Stats card */}
                    <div className="account-stats-card">
                        <p className="account-info-card-title">Статистика задач</p>
                        {!errorFetchData ? (
                            <div className="account-task-info">
                                {taskLabels.map((label, i) => (
                                    <div key={label} className={`account-task-info-item ${taskClasses[i]}`}>
                                        <span>{label}</span>
                                        <span className="account-task-count">{barStatusData[i] ?? 0}</span>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <div className="account-error-fetch-tasks">Ошибка загрузки данных</div>
                        )}
                    </div>

                    {/* Actions card */}
                    {isOwn && (
                        <div className="account-actions-card">
                            <Button onClickFunction={logout}>Выйти</Button>

                            <ModalWindow trigger={<Button>Редактировать профиль</Button>}>
                                {({close}) => (
                                    <RegisterForm
                                        isEdit={true}
                                        style={{width: '100%'}}
                                        initUserData={userData}
                                        onSubmit={async formData => {
                                            await axios.patch(
                                                `http://${backHost}:${backPort}/api/auth/user/update/${userId}`,
                                                formData,
                                                {headers: {'Content-Type': 'multipart/form-data'}}
                                            );
                                            close();
                                            window.location.reload();
                                        }}
                                    />
                                )}
                            </ModalWindow>

                            <Button onClickFunction={() => navigate('/projects')}>К проектам</Button>

                            <ModalWindow trigger={
                                <Button className="btn-danger" style={{backgroundColor: '#ef4444', marginLeft: 'auto'}}>
                                    Удалить аккаунт
                                </Button>
                            }>
                                {({close}) => (
                                    <>
                                        <h3>Действительно удалить аккаунт?</h3>
                                        <div className="modal-actions">
                                            <Button onClickFunction={close}>Нет</Button>
                                            <Button onClickFunction={() => { deleteAccount(); close(); }}>Да</Button>
                                        </div>
                                    </>
                                )}
                            </ModalWindow>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}
