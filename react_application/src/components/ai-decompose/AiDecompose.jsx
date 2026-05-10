import {useState} from "react";
import axios from "axios";
import toast from "react-hot-toast";
import Button from "../button/button";
import './ai-decompose.css';

const backHost = process.env.REACT_APP_BACKEND_HOST;
const backPort = process.env.REACT_APP_BACKEND_PORT;

const IMPORTANCE_LABEL = {LOW: 'Низкая', INTERMEDIATE: 'Средняя', HIGH: 'Высокая'};

export default function AiDecompose({projectId, onTasksCreated}) {
    const [goal, setGoal] = useState('');
    const [suggestions, setSuggestions] = useState([]);
    const [loading, setLoading] = useState(false);
    const [creating, setCreating] = useState(false);

    const handleDecompose = async () => {
        if (!goal.trim()) return;
        setLoading(true);
        setSuggestions([]);
        try {
            const res = await axios.post(`http://${backHost}:${backPort}/api/ai/decompose`, {
                goal,
                projectId,
            });
            setSuggestions(res.data);
        } finally {
            setLoading(false);
        }
    };

    const handleCreateAll = async () => {
        setCreating(true);
        try {
            for (const task of suggestions) {
                const formData = new FormData();
                formData.append('name', task.name);
                formData.append('description', task.description);
                formData.append('taskImportance', task.importance);
                formData.append('taskStatus', task.taskStatus);
                formData.append('projectId', projectId);
                await axios.post(
                    `http://${backHost}:${backPort}/api/tasks/createTask`,
                    formData,
                    {headers: {'Content-Type': 'multipart/form-data'}}
                );
            }
            toast.success(`Создано ${suggestions.length} задач`);
            setSuggestions([]);
            setGoal('');
            onTasksCreated?.();
        } finally {
            setCreating(false);
        }
    };

    return (
        <div className="ai-decompose">
            <h3 className="ai-decompose__title">AI-декомпозиция задач</h3>
            <p className="ai-decompose__hint">Опишите цель — AI разобьёт её на конкретные задачи</p>

            <textarea
                className="ai-decompose__input"
                placeholder="Например: реализовать систему авторизации с JWT и OAuth2"
                value={goal}
                onChange={e => setGoal(e.target.value)}
                rows={3}
            />

            <Button onClickFunction={handleDecompose} disabled={loading || !goal.trim()}>
                {loading ? 'Генерирую...' : 'Разбить на задачи'}
            </Button>

            {suggestions.length > 0 && (
                <div className="ai-decompose__results">
                    <div className="ai-decompose__results-list">
                        {suggestions.map((task, i) => (
                            <div key={i} className="ai-decompose__task-item">
                                <div className="ai-decompose__task-header">
                                    <span className="ai-decompose__task-name">{task.name}</span>
                                    <span className={`ai-decompose__badge ai-decompose__badge--${task.importance.toLowerCase()}`}>
                                        {IMPORTANCE_LABEL[task.importance]}
                                    </span>
                                </div>
                                <p className="ai-decompose__task-desc">{task.description}</p>
                            </div>
                        ))}
                    </div>
                    <Button onClickFunction={handleCreateAll} disabled={creating}>
                        {creating ? 'Создаю...' : `Создать все ${suggestions.length} задачи`}
                    </Button>
                </div>
            )}
        </div>
    );
}
