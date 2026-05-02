import Button from "../button/button";
import './project-card.css';
import {useNavigate} from "react-router-dom";
import axios from "axios";
import ModalWindow from "../modal-window/modal-window";
import NewProjectForm from "../project-form/new-project-form";
import {FaCalendarAlt} from "react-icons/fa";

export default function ProjectCard({project, avatars}) {
    const navigate = useNavigate();

    const backHost = process.env.REACT_APP_BACKEND_PROJECT_SERVICE_HOST;
    const backPort = process.env.REACT_APP_BACKEND_PORT;

    const openProject = (e) => {
        if (e.target.closest(".project-card-buttons"))
            return;
        navigate(`/projects/${project.id}`);
    }

    const deleteProject = () => {
        const backHost = process.env.REACT_APP_BACKEND_PROJECT_SERVICE_HOST;
        const backPort = process.env.REACT_APP_BACKEND_PORT;
        axios.delete(`http://${backHost}:${backPort}/api/projects/delete/${project.id}`)
            .then(res => {
                if (res.status === 204)
                    navigate('/projects')
            })
    }

    return (
        <div className="project-card">
            <div className="project-card-main">
                <div className="project-card-container"
                     onClick={e => {
                         if (e.target.closest('.project-card-buttons') || e.target.closest('.image-project-card')) return;
                         navigate(`/projects/${project.id}`);
                     }}
                     role="button"
                     tabIndex={0}
                     aria-label={`Перейти к проекту ${project.name}`}
                     onKeyDown={e => {
                         if (e.key === 'Escape')
                             return;
                         if (e.key === 'Enter')
                            openProject(e);
                     }}>
                    <ModalWindow
                        style={{padding: "0", width: "230%", right: "65%", display: "flex"}}
                        trigger={<img className="image-project-card"
                            src={`data:${avatars[0].contentType};base64,${avatars[0].binaryData}`}
                            alt={`Аватар проекта: ${avatars[0].name}`}/>}>
                        {({close}) => (
                            <img
                                className="image-task-card-open"
                                src={`data:${avatars[0].contentType};base64,${avatars[0].binaryData}`}
                                alt={`Аватар: ${avatars[0].name}`}/>
                        )}
                    </ModalWindow>
                    <div className="text-info">
                        <h2>{project.name}</h2>
                        <div className="project-description">{project.description}</div>
                        <div className="date-added"><FaCalendarAlt /> {`Дата добавления: ${project.dateAdded}`}</div>
                    </div>

                    <div className="project-card-buttons">
                        <div className="inner-buttons">
                            <ModalWindow style={{padding: "0"}}
                                trigger={<Button>Изменить</Button>}>
                                {({ close }) => (
                                    <NewProjectForm
                                        isEdit={true}
                                        style={{width: "100%"}}
                                        initProjectData={project}
                                        onSubmit={async formData => {
                                            await axios.patch(
                                                `http://${backHost}:${backPort}/api/projects/update/${project.id}`, formData,
                                                { headers: {'Content-Type':'multipart/form-data'} });
                                            close();
                                            window.location.reload();
                                        }}
                                    />
                                )}
                            </ModalWindow>
                            <ModalWindow trigger={<Button>Удалить</Button>}>
                                {({close}) => (
                                    <>
                                        <h3>Действительно удалить?</h3>
                                        <p>{`Название проекта: ${project.name}`}</p>
                                        <div className="modal-actions">
                                            <Button onClickFunction={close}>Нет</Button>
                                            <Button onClickFunction={() => {
                                                deleteProject();
                                                close();
                                            }}>Да
                                            </Button>
                                        </div>
                                    </>
                                )}
                            </ModalWindow>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}