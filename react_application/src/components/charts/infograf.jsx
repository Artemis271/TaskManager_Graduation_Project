import './infograf.css'
import PieChart from "./pie-chart/pie-chart";
import Background from "../info/background";
import {useEffect, useRef, useState} from "react";
import axios from "axios";
import LoadingData from "../info/loading-data/loading-data";
import BarChart from "./bar-chart/bar-chart";
import LineChart from "./line-chart/line-chart";
import {useNavigate} from "react-router-dom";
import {useAuth} from "../../hooks/auth";

export default function Infograf()
{
    const {user} = useAuth();
    const [projects, setProjects] = useState([]);
    const [tasks, setTasks] = useState([]);
    const [statuses, setStatuses] = useState([]);
    const [importance, setImportance] = useState([]);
    const [loading, setLoading] = useState(true);
    const didFetch = useRef(false);
    const navigate = useNavigate();

    const backHost = process.env.REACT_APP_BACKEND_PROJECT_SERVICE_HOST;
    const backPort = process.env.REACT_APP_BACKEND_PORT;

    const fetchData = async () => {
        try {
            const projectResponse = await axios.get(
                `http://${backHost}:${backPort}/api/projects/allProjects`,
                { params: { size: 100, userId: user.id } }
            );

            const taskResponse = await axios.get(
                `http://${backHost}:${backPort}/api/tasks/allTasks-ProjectIds`,
                {
                    params: {
                        uuids: projectResponse.data.map(project => project.id).join(',')
                    },
                }
            );

            const statusResponse = await axios.get(
                `http://${backHost}:${backPort}/api/tasks/allTaskStatus`);

            const importanceResponse = await axios.get(
                `http://${backHost}:${backPort}/api/tasks/allTaskImportance`);

            setProjects(projectResponse.data);
            setTasks(taskResponse.data);
            setStatuses(statusResponse.data);
            setImportance(importanceResponse.data);
        } catch (err) {
            if (axios.isAxiosError(err))
                navigate('/500')
            else navigate('/404');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (!didFetch.current) {
            didFetch.current = true;
            fetchData();
        }
    }, []);

    if (loading) return <LoadingData />;

    const completedTasks = tasks.filter(t => t.taskStatus === 'COMPLETED').length;
    const inProgressTasks = tasks.filter(t => t.taskStatus === 'IN_PROGRESS').length;
    const highPriorityTasks = tasks.filter(t => t.taskImportance === 'HIGH').length;

    const pieData = projects.map(project =>
        tasks.filter(task => task.projectId === project.id).length);
    const pieLabels = projects.map(project => project.name);

    const barStatusData = statuses.map(status =>
        tasks.filter(task => task.taskStatus === status).length);

    const barImportanceData = importance.map(imp =>
        tasks.filter(task => task.taskImportance === imp).length);

    const sortedDates = tasks
        .map(task => task.dateAdded)
        .sort((a, b) => new Date(a) - new Date(b));
    const dates = {};
    sortedDates.forEach(date => {
        const day = date.split('T')[0];
        dates[day] = (dates[day] || 0) + 1;
    });

    return (
        <div className="infograf-main">
            <Background/>

            <div className="infograf-header">
                <h1>Аналитика задач</h1>
                <p>Статистика по проектам и задачам вашего рабочего пространства</p>
            </div>

            <div className="stats-row">
                <div className="stat-card">
                    <div className="stat-value">{projects.length}</div>
                    <div className="stat-label">Проектов</div>
                </div>
                <div className="stat-card">
                    <div className="stat-value">{tasks.length}</div>
                    <div className="stat-label">Задач всего</div>
                </div>
                <div className="stat-card">
                    <div className="stat-value">{completedTasks}</div>
                    <div className="stat-label">Завершено</div>
                </div>
                <div className="stat-card">
                    <div className="stat-value">{inProgressTasks}</div>
                    <div className="stat-label">В процессе</div>
                </div>
                <div className="stat-card">
                    <div className="stat-value">{highPriorityTasks}</div>
                    <div className="stat-label">Высокий приоритет</div>
                </div>
            </div>

            <div className="charts">
                <div className="chart pie">
                    <p className="chart-title">Задачи по проектам</p>
                    <PieChart label="Количество задач"
                              data={pieData}
                              labels={pieLabels}/>
                    <p className="chart-description">
                        Распределение задач между проектами
                    </p>
                </div>
                <div className="chart bar">
                    <p className="chart-title">Статусы выполнения</p>
                    <BarChart label="Задачи"
                              data={barStatusData}
                              labels={statuses}/>
                    <p className="chart-description">
                        Количество задач в каждом статусе
                    </p>
                </div>
                <div className="chart bar importance">
                    <p className="chart-title">Уровни важности</p>
                    <BarChart label="Задачи"
                              data={barImportanceData}
                              labels={importance}/>
                    <p className="chart-description">
                        Распределение задач по уровню важности
                    </p>
                </div>
                <div className="chart line">
                    <p className="chart-title">Динамика создания задач</p>
                    <LineChart label="Создано задач"
                               data={Object.values(dates)}
                               labels={Object.keys(dates)}/>
                    <p className="chart-description">
                        Количество задач, созданных по датам
                    </p>
                </div>
            </div>
        </div>
    )
}
