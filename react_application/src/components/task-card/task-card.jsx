import './task-card.css';
import Button from "../button/button";
import axios from "axios";
import ModalWindow from "../modal-window/modal-window";
import TaskForm from "../task-form/task-form";
import {GiCheckMark} from "react-icons/gi";
import {ImCross} from "react-icons/im";

const IMPORTANCE_LABEL = {LOW: 'Низкая', INTERMEDIATE: 'Средняя', HIGH: 'Высокая'};

export default function TaskCard({task, avatars, onRefresh}) {
    const backHost = process.env.REACT_APP_BACKEND_PROJECT_SERVICE_HOST;
    const backPort = process.env.REACT_APP_BACKEND_PORT;

    const deleteTask = async (close) => {
        await axios.delete(`http://${backHost}:${backPort}/api/tasks/delete/${task.id}`);
        close();
        onRefresh?.();
    };

    const hasImage = avatars && avatars.length > 0;
    const importance = (task.taskImportance || '').toLowerCase();

    return (
        <div className="task-card">
            {hasImage && (
                <ModalWindow
                    style={{padding: 0, width: '320px'}}
                    trigger={
                        <img
                            className="task-card__image"
                            src={`data:${avatars[0].contentType};base64,${avatars[0].binaryData}`}
                            alt={avatars[0].name}
                        />
                    }>
                    {() => (
                        <img
                            style={{width: '100%', borderRadius: 'var(--radius)'}}
                            src={`data:${avatars[0].contentType};base64,${avatars[0].binaryData}`}
                            alt={avatars[0].name}
                        />
                    )}
                </ModalWindow>
            )}

            <h3 className="task-card__name">{task.name}</h3>

            {task.description && (
                <p className="task-card__desc">{task.description}</p>
            )}

            <div className="task-card__meta">
                <span className={`badge badge--${importance}`}>
                    {IMPORTANCE_LABEL[task.taskImportance] || task.taskImportance}
                </span>
                <span className={`badge ${task.isFinished ? 'badge--finished' : 'badge--unfinished'}`}>
                    {task.isFinished ? <GiCheckMark/> : <ImCross/>}
                </span>
                {task.dateFinished && (
                    <span className="task-card__date">{task.dateFinished}</span>
                )}
            </div>

            <div className="task-card__actions">
                <ModalWindow
                    style={{padding: 0, width: '420px'}}
                    trigger={<Button>Изменить</Button>}>
                    {({close}) => (
                        <TaskForm
                            style={{width: '100%'}}
                            initTaskData={task}
                            avatars={avatars}
                            onSubmit={async formData => {
                                await axios.patch(
                                    `http://${backHost}:${backPort}/api/tasks/update/${task.id}`,
                                    formData,
                                    {headers: {'Content-Type': 'multipart/form-data'}}
                                );
                                close();
                                onRefresh?.();
                            }}
                        />
                    )}
                </ModalWindow>

                <ModalWindow trigger={<Button>Удалить</Button>}>
                    {({close}) => (
                        <>
                            <h3>Удалить задачу?</h3>
                            <p style={{color: 'var(--text-muted)'}}>{task.name}</p>
                            <div className="modal-actions">
                                <Button onClickFunction={close}>Отмена</Button>
                                <Button onClickFunction={() => deleteTask(close)}>Удалить</Button>
                            </div>
                        </>
                    )}
                </ModalWindow>
            </div>
        </div>
    );
}
