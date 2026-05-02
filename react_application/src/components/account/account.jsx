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
    const navigate = useNavigate()
    const didFetch = useRef(false);
    const [tasks, setTasks] = useState([]);
    const [statuses, setStatuses] = useState([]);
    const [errorFetchData, setErrorFetchData] = useState(false);

    const backHost = process.env.REACT_APP_BACKEND_PROJECT_SERVICE_HOST;
    const backPort = process.env.REACT_APP_BACKEND_PORT;

    if (!isAuthenticated)
        return (
            <Navigate
                to="/login"
                state={{from: location}}
                replace
            />
        );

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
                {
                    params: {size: 100, userId},
                    headers: {Authorization: `Bearer ${localStorage.getItem('jwtToken')}`}
                }
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
                if (status === 404) {
                    navigate('/404');
                    return;
                }
                if (status === 401) {
                    setErrorFetchData(true);
                    return;
                }
                navigate('/500');
                return;
            }
            setErrorFetchData(true);

        } finally {
            if (mounted) setLoading(false);
        }

        return () => {
            mounted = false;
        };
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

    if (loading)
        return <LoadingData/>;

    if (!userData)
        return <Navigate to="/404"/>;

    const deleteAccount = async () => {
        await axios.delete(`http://${backHost}:${backPort}/api/auth/user/delete/${userId}`);
        logout();
    }

    return (
        <div className="main">
            <div className="account-square"></div>
            <div className="account-ellipsis"></div>
            <div className="account-circle"></div>
            <div className="account-data">
                {userData && (
                    <AvatarSlider
                        data={userData.avatars}
                        baseUrl={`http://${backHost}:${backPort}/api/auth/user/${userId}/update-avatar`}
                    />
                )}
                <div className="account-main-info">
                    <div className="account-data-group-1">
                        <div className="account-data-group-inner">
                            <span><FaRegCircleUser/> <strong>Юзернейм:</strong> {userData.username}</span></div>
                        <div className="account-data-group-inner">
                            <span><TiClipboard/> <strong>Имя:</strong> {userData.name}</span>
                        </div>
                        {userId == user.id && <div className="account-data-group-inner">
                            <span><TiClipboard/> <strong>Фамилия:</strong> {userData.lastName}</span></div>}
                    </div>
                    <div className="account-data-group-2">
                        {userId == user.id && <div className="account-data-group-inner">
                            <span><MdAlternateEmail/> <strong>Почта:</strong> {userData.email}</span></div>}
                        <div className="account-data-group-inner">
                            <span><CiCalendarDate/> <strong>Дата регистрации:</strong> {userData.dateRegistration.split('T')?.[0]}</span>
                        </div>
                    </div>
                    <div className="account-activity"
                         style={{backgroundColor: userData.activity ? "#acffc7" : "#ffbebe"}}>
                        <span><TbActivity/> <strong>Активность:</strong> {userData.activity.toString()}</span></div>
                    <div
                        className="account-roles"><span><MdOutlineAdminPanelSettings/> <strong>Роли:</strong> {userData.roles.map(role =>
                        <span key={role.id}>{role.role.split('_')?.[1]}</span>
                    )}</span></div>
                </div>
                <div className="account-buttons-container">
                    {!errorFetchData && <div className="account-task-info">
                        <div className="account-task-info-item account-task-planing">Задач на этапе
                            планирования: {barStatusData[0]}</div>
                        <div className="account-task-info-item account-task-in-progress">Задач в
                            прогрессе: {barStatusData[1]}</div>
                        <div className="account-task-info-item account-task-finished">Завершённых
                            задач: {barStatusData[2]}</div>
                        <div className="account-task-info-item account-task-canceled">Отменённых
                            задач: {barStatusData[3]}</div>
                        <div className="account-task-info-item account-task-expired">Просроченных
                            задач: {barStatusData[4]}</div>
                    </div>}
                    {errorFetchData &&
                        <div className="account-error-fetch-tasks">Произошла ошибка загрузки данных</div>}
                    {userId == user.id && <div className="group-buttons">
                        <div className="group-buttons-main-func">
                            <Button onClickFunction={logout}>Выйти</Button>
                            <ModalWindow style={{padding: "0"}}
                                trigger={<Button>Редактировать профиль</Button>}>
                                {({ close }) => (
                                    <RegisterForm
                                        isEdit={true}
                                        style={{width: "100%"}}
                                        initUserData={userData}
                                        onSubmit={async formData => {
                                            await axios.patch(
                                                `http://${backHost}:${backPort}/api/auth/user/update/${userId}`, formData,
                                                { headers: {'Content-Type':'multipart/form-data'} });
                                            close();
                                            window.location.reload();
                                        }}
                                    />
                                )}
                            </ModalWindow>
                            <Button onClickFunction={() => navigate('/projects')}>К проектам</Button>
                        </div>
                        <ModalWindow trigger={<Button style={{backgroundColor: "#f47c7c"}}>Удалить аккаунт</Button>}>
                            {({close}) => (
                                <>
                                    <h3>Действительно удалить аккаунт?</h3>
                                    <div className="modal-actions">
                                        <Button onClickFunction={close}>Нет</Button>
                                        <Button onClickFunction={() => {
                                            deleteAccount();
                                            close();
                                        }}>Да
                                        </Button>
                                    </div>
                                </>
                            )}
                        </ModalWindow>
                    </div>}
                </div>

                <div className="svg-image">
                    <svg width="560" height="350" xmlns="http://www.w3.org/2000/svg">
                        <path d="M20 300 Q140 150, 260 200 T400 120 T540 240"
                              stroke="limegreen" strokeWidth="6" fill="none"
                              strokeLinecap="round" strokeLinejoin="round"/>
                        <circle cx="20" cy="300" r="5" fill="limegreen"/>
                        <circle cx="260" cy="200" r="5" fill="limegreen"/>
                        <circle cx="400" cy="120" r="5" fill="limegreen"/>
                        <circle cx="540" cy="240" r="5" fill="limegreen"/>
                    </svg>
                </div>
            </div>
        </div>
    )
}