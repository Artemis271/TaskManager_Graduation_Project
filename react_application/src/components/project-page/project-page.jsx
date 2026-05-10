import {useCallback, useEffect, useRef, useState} from "react";
import {CiCirclePlus} from "react-icons/ci";
import {RiRobot2Line} from "react-icons/ri";
import axios, {HttpStatusCode} from "axios";
import './project-page.css';
import TaskCard from "../task-card/task-card";
import Http404 from "../info/http-error/404";
import {Link, useNavigate, useParams} from "react-router-dom";
import Autosuggest from "react-autosuggest";
import {useAuth} from "../../hooks/auth";
import Button from "../button/button";
import ModalWindow from "../modal-window/modal-window";
import TaskForm from "../task-form/task-form";
import AiDecompose from "../ai-decompose/AiDecompose";

const COLUMNS = [
    {key: 'PLANING',     label: 'Планирование'},
    {key: 'IN_PROGRESS', label: 'В работе'},
    {key: 'COMPLETED',   label: 'Завершено'},
    {key: 'CANCELED',    label: 'Отменено'},
    {key: 'EXPIRED',     label: 'Просрочено'},
];

const backHost = process.env.REACT_APP_BACKEND_PROJECT_SERVICE_HOST;
const backPort = process.env.REACT_APP_BACKEND_PORT;

export default function ProjectPage() {
    const {user} = useAuth();
    const {projectId} = useParams();
    const navigate = useNavigate();

    const [tasks, setTasks]               = useState([]);
    const [loading, setLoading]           = useState(true);
    const [projectExists, setProjectExists] = useState(true);
    const [projectList, setProjects]      = useState([]);
    const [projSearch, setProjSearch]     = useState('');
    const [projSuggestions, setProjSuggestions] = useState([]);
    const [statusFilter, setStatusFilter]       = useState('');
    const [importanceFilter, setImportanceFilter] = useState('');
    const [isFinished, setFinished]       = useState(null);
    const [taskStatusImportance, setTaskStatusImportance] = useState({importance: [], status: []});
    const didFetch = useRef(false);

    const fetchTasks = useCallback(async () => {
        setLoading(true);
        try {
            const [tasksRes, projectRes, importanceRes, statusRes] = await Promise.all([
                axios.get(`http://${backHost}:${backPort}/api/tasks/allTasks`, {params: {projectId}}),
                axios.get(`http://${backHost}:${backPort}/api/projects/${projectId}`),
                axios.get(`http://${backHost}:${backPort}/api/tasks/allTaskImportance`),
                axios.get(`http://${backHost}:${backPort}/api/tasks/allTaskStatus`),
            ]);

            if (projectRes.status === HttpStatusCode.Ok) setProjectExists(true);
            if (importanceRes.status === HttpStatusCode.Ok && statusRes.status === HttpStatusCode.Ok)
                setTaskStatusImportance({importance: importanceRes.data, status: statusRes.data});

            setTasks(Array.isArray(tasksRes.data) ? tasksRes.data : []);

            const resp = await axios.get(`http://${backHost}:${backPort}/api/projects/allProjects`, {
                params: {size: 50}
            });
            if (resp.status === HttpStatusCode.Ok) setProjects(resp.data);
        } catch {
            setProjectExists(false);
        } finally {
            setLoading(false);
        }
    }, [projectId]);

    useEffect(() => {
        if (didFetch.current) return;
        didFetch.current = true;
        fetchTasks();
    }, [projectId, fetchTasks]);

    const filteredTasks = tasks.filter(t => {
        const statusMatch = statusFilter ? t.taskStatus === statusFilter : true;
        const importanceMatch = importanceFilter ? t.taskImportance === importanceFilter : true;
        const finishedMatch = isFinished === null ? true : t.isFinished === isFinished;
        return statusMatch && importanceMatch && finishedMatch;
    });

    const getProjSuggestions = value => {
        const v = value.trim().toLowerCase();
        return v.length === 0 ? [] : projectList.filter(p =>
            p.name.toLowerCase().includes(v) || p.description?.toLowerCase().includes(v)
        );
    };

    if (loading) return <div style={{padding: '2rem', color: 'var(--text-muted)'}}>Загрузка...</div>;
    if (!projectExists) return <Http404/>;

    return (
        <div className="project-layout">
            <aside className="project-sidebar">
                <h3>Проекты</h3>
                <Autosuggest
                    suggestions={projSuggestions}
                    onSuggestionsFetchRequested={({value}) => setProjSuggestions(getProjSuggestions(value))}
                    onSuggestionsClearRequested={() => setProjSuggestions([])}
                    getSuggestionValue={s => s.name}
                    renderSuggestion={s => <div>{s.name}</div>}
                    inputProps={{
                        placeholder: 'Найти проект...',
                        value: projSearch,
                        onChange: (_, {newValue}) => setProjSearch(newValue),
                        id: 'project-search',
                    }}
                    onSuggestionSelected={(_, {suggestion}) => navigate(`/projects/${suggestion.id}`)}
                />
                <ul>
                    {projectList.filter(p => p.id !== projectId).map(p => (
                        <li key={p.id}>
                            <Link to={`/projects/${p.id}`}>{p.name}</Link>
                        </li>
                    ))}
                </ul>
            </aside>

            <div className="project-main">
                <div className="task-toolbar">
                    <ModalWindow
                        style={{padding: 0, width: '420px'}}
                        trigger={<Button title="Новая задача"><CiCirclePlus/></Button>}>
                        {({close}) => (
                            <TaskForm
                                style={{width: '100%'}}
                                projectId={projectId}
                                onSubmit={async formData => {
                                    await axios.post(
                                        `http://${backHost}:${backPort}/api/tasks/createTask`, formData,
                                        {headers: {'Content-Type': 'multipart/form-data'}}
                                    );
                                    close();
                                    fetchTasks();
                                }}
                            />
                        )}
                    </ModalWindow>

                    <ModalWindow
                        trigger={<Button title="AI декомпозиция"><RiRobot2Line/></Button>}>
                        {({close}) => (
                            <AiDecompose
                                projectId={projectId}
                                onTasksCreated={() => { close(); fetchTasks(); }}
                            />
                        )}
                    </ModalWindow>

                    <select value={statusFilter} onChange={e => setStatusFilter(e.target.value)}>
                        <option value="">Все статусы</option>
                        {taskStatusImportance.status.map(s => <option key={s}>{s}</option>)}
                    </select>

                    <select value={importanceFilter} onChange={e => setImportanceFilter(e.target.value)}>
                        <option value="">Все важности</option>
                        {taskStatusImportance.importance.map(i => <option key={i}>{i}</option>)}
                    </select>

                    <div className="toggle-wrapper">
                        <label htmlFor="finish-toggle">Завершённые</label>
                        <input
                            id="finish-toggle"
                            className="toggle-checkbox"
                            type="checkbox"
                            onChange={e => setFinished(e.target.checked ? true : null)}
                        />
                        <div className="toggle-container" onClick={() => {
                            document.getElementById('finish-toggle').click();
                        }}>
                            <div className="toggle-button"/>
                        </div>
                    </div>
                </div>

                {tasks.length === 0 ? (
                    <div className="project-empty">
                        <p>Проект пустой — создайте первую задачу</p>
                        <ModalWindow
                            style={{padding: 0, width: '420px'}}
                            trigger={<Button>Создать задачу</Button>}>
                            {({close}) => (
                                <TaskForm
                                    style={{width: '100%'}}
                                    projectId={projectId}
                                    onSubmit={async formData => {
                                        await axios.post(
                                            `http://${backHost}:${backPort}/api/tasks/createTask`, formData,
                                            {headers: {'Content-Type': 'multipart/form-data'}}
                                        );
                                        close();
                                        fetchTasks();
                                    }}
                                />
                            )}
                        </ModalWindow>
                    </div>
                ) : (
                    <div className="kanban-board">
                        {COLUMNS.map(col => {
                            const colTasks = filteredTasks.filter(t => t.taskStatus === col.key);
                            return (
                                <div key={col.key} className={`kanban-column kanban-column--${col.key.toLowerCase()}`}>
                                    <div className="kanban-column__header">
                                        <span className="kanban-column__title">{col.label}</span>
                                        <span className="kanban-column__count">{colTasks.length}</span>
                                    </div>
                                    <div className="kanban-column__cards">
                                        {colTasks.map(task => (
                                            <TaskCard
                                                key={task.id}
                                                task={task}
                                                avatars={task.images || []}
                                                onRefresh={fetchTasks}
                                            />
                                        ))}
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                )}
            </div>
        </div>
    );
}
