import './task-card.css'
import Button from "../button/button";
import {useNavigate} from "react-router-dom";
import axios from "axios";
import ModalWindow from "../modal-window/modal-window";
import {ImCross} from "react-icons/im";
import {GiCheckMark} from "react-icons/gi";
import {FcCancel} from "react-icons/fc";
import TaskForm from "../task-form/task-form";

export default function TaskCard({task, avatars})
{
    const navigate = useNavigate()

    const backHost = process.env.REACT_APP_BACKEND_PROJECT_SERVICE_HOST;
    const backPort = process.env.REACT_APP_BACKEND_PORT;

    const deleteTask = () => {
        axios.delete(`http://${backHost}:${backPort}/api/tasks/delete/${task.id}`)
            .then(res => {
                if (res.status === 204)
                    navigate('/projects')
            })
    }

    return (
        <div
            className="anchor-task-card"
            role="button">
            <div className="task-card-container-item">
                <ModalWindow
                    style={{padding: "0", width: "230%", right: "65%", display: "flex"}}
                    trigger={<img
                        className="image-task-card"
                        src={`data:${avatars[0].contentType};base64,${avatars[0].binaryData}`}
                        alt={`Аватар: ${avatars[0].name}`}/>}>
                    {({close}) => (
                        <img
                            className="image-task-card-open"
                            src={`data:${avatars[0].contentType};base64,${avatars[0].binaryData}`}
                            alt={`Аватар задачи: ${avatars[0].name}`}/>
                    )}
                </ModalWindow>
            </div>
            <div className="task-card-container-item">
                <h3 className="task-name">{task.name}</h3>
            </div>
            <div className="task-card-container-item">
                {<div className="task-description">{task.description || <FcCancel/>}</div>}
            </div>
            <div className="task-card-container-item">
                <div className="task-date-added">{task.dateAdded}</div>
            </div>
            <div className="task-card-container-item">
                <div className="task-date-finished">{task.dateFinished}</div>
            </div>
            <div className="task-card-container-item">
                <div className={`task-importance ti-${task.taskImportance.toLowerCase()}`}>
                    {task.taskImportance}
                </div>
            </div>
            <div className="task-card-container-item">
                <div className={`task-status ts-${task.taskStatus.toLowerCase()}`}>
                    {task.taskStatus}
                </div>
            </div>
            <div className="task-card-container-item">
                <div className={`task-finish ${task.isFinished ? 'task-finished' : 'task-not-finished'}`}>
                    {task.isFinished ? <GiCheckMark/> : <ImCross/>}
                </div>
            </div>
            <div className="task-card-container-item task-card-buttons">
                <ModalWindow style={{padding: "0", width: "150%", right: "20%"}}
                             trigger={<Button style={{padding: "15px 10px", width: "100px"}}>Изменить</Button>}>
                    {({close}) => (
                        <TaskForm
                            style={{width: "100%"}}
                            initTaskData={task}
                            avatars={avatars}
                            onSubmit={async formData => {
                                await axios.patch(
                                    `http://${backHost}:${backPort}/api/tasks/update/${task.id}`, formData,
                                    {headers: {'Content-Type': 'multipart/form-data'}});
                                close();
                                window.location.reload();
                            }}
                        />
                    )}
                </ModalWindow>
                <ModalWindow trigger={<Button style={{padding: "15px 10px", width: "100px"}}>Удалить</Button>}>
                    {({close}) => (
                        <>
                            <h3>Действительно удалить задачу?</h3>
                            <p>{`Название задачи: ${task.name}`}</p>
                            <div className="modal-actions">
                                <Button onClickFunction={close}>Нет</Button>
                                <Button onClickFunction={() => {
                                    deleteTask();
                                    close();
                                }}>Да
                                </Button>
                            </div>
                        </>
                    )}
                </ModalWindow>
            </div>
        </div>
    )
}