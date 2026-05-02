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
            setImportance(importanceResponse.data)
        } catch (err) {
            if (axios.isAxiosError(err))
                navigate('/500')
            else navigate('/404');
        }
        finally {
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

    const pieData = projects.map(project =>
        tasks.filter(task => task.projectId === project.id).length);

    const pieLabels = projects.map(project => project.name);

    const barStatusData = statuses.map(status =>
        tasks.filter(task => task.taskStatus === status).length);

    const barImportanceData = importance.map(importance =>
        tasks.filter(task => task.taskImportance === importance).length);

    const sortedDates = tasks
        .map(task => task.dateAdded)
        .sort((a, b) => new Date(a) - new Date(b));
    const dates = {}
    sortedDates.forEach(date => dates[date] = 0)

    sortedDates.forEach(date => dates[date] = (dates[date] || 0) + 1);


    return (
        <div className="infograf-main">
            <Background/>
            <div className="charts">
                <div className="chart pie">
                    <PieChart label="Количество задач в проекте"
                              data={pieData}
                              labels={pieLabels}/>
                    <p className="chart-description">
                        Инфографика отображения задач в каждом проекте
                    </p>
                </div>
                <div className="chart bar">
                    <BarChart label="Количество задач"
                              data={barStatusData}
                              labels={statuses}
                    />
                    <p className="chart-description">
                        Инфографика отображения задач с конкретным статусом выполнения
                    </p>
                </div>
                <div className="chart bar importance">
                    <BarChart label="Количество задач"
                              data={barImportanceData}
                              labels={importance}
                    />
                    <p className="chart-description">
                        Инфографика отображения задач с конкретной важностью
                    </p>
                </div>
                <div className="chart line">
                    <LineChart label="Количество созданных задач"
                              data={Object.values(dates)}
                              labels={Object.keys(dates).map(date => date.split('T')[0])}
                    />
                    <p className="chart-description">
                        Инфографика отображения даты создания задач
                    </p>
                </div>
            </div>
        </div>
    )
}