'use client';
import React, {useEffect, useRef, useState} from "react";
import axios from "axios";
import './task-form.css'
import Button from "../button/button";

export default function TaskForm({isEdit = false, initTaskData = {}, onSubmit, projectId, ...props})
{
    const [data, setData] = useState({
        allImportance: [],
        allStatus: [],
        loading: true,
        error: null
    });

    const [messageError, setMessageError] = useState({
        taskName: '',
        taskPhoto: {}
    })

    const fileInputRef = useRef(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const backHost = process.env.REACT_APP_BACKEND_PROJECT_SERVICE_HOST;
                const backPort = process.env.REACT_APP_BACKEND_PORT;

                const [importanceRes, statusRes] = await Promise.all([
                    axios.get(`http://${backHost}:${backPort}/api/tasks/allTaskImportance`),
                    axios.get(`http://${backHost}:${backPort}/api/tasks/allTaskStatus`)
                ]);

                setData({
                    allImportance: importanceRes.data,
                    allStatus: statusRes.data,
                    loading: false,
                    error: null
                });
            } catch (error) {
                setData(prev => ({
                    ...prev,
                    loading: false,
                    error: error.message
                }));
            }
        };

        fetchData();
    }, []);


    const handleNewTask = async (e) => {
        e.preventDefault();

        const errors = {
            taskName: "",
            taskPhoto: {},
        };

        const formElements = e.target.elements;
        const taskNameInput = formElements.taskName;
        const taskPhotoInput = formElements.taskPhoto;

        if (taskNameInput.value.length <= 2)
            errors.taskName = "Имя слишком короткое";

        if (!taskPhotoInput.files || taskPhotoInput.files.length === 0)
            errors.taskPhoto.photoAbsent = "Изображение обязательно для загрузки";
        else if (taskPhotoInput.files[0].size > 5 * 1024 * 1024)
            errors.taskPhoto.photoOverSize = "Изображение слишком большое для загрузки. Max: 5MB";

        setMessageError(errors);

        if (!errors.taskName && Object.keys(errors.taskPhoto).length === 0) {
            const backHost = process.env.REACT_APP_BACKEND_PROJECT_SERVICE_HOST;
            const backPort = process.env.REACT_APP_BACKEND_PORT;

            const newTask = new FormData();
            newTask.append("name", taskNameInput.value);
            newTask.append("description", formElements.taskDescription.value);
            newTask.append("taskImportance", formElements.taskImportance.value);
            newTask.append("taskStatus", formElements.taskStatus.value);
            newTask.append("dateFinished", formElements.dateFinish.value);
            newTask.append("images", taskPhotoInput.files[0]);
            newTask.append("projectId", projectId)

            /*if (!isEdit)
                axios.post(
                    `http://${backHost}:${backPort}/api/tasks/createTask`,
                    newTask,
                    {headers: {'Content-Type': 'multipart/form-data'}}
                )
            else*/ await onSubmit(newTask);
        }
    };

    const acceptedTime = new Date().toISOString().split("T")[0];

    return (
        <div className="task-form parent">
            <form method="POST" onSubmit={handleNewTask} encType="multipart/form-data" className="newTaskForm">
                <h3>Создание новой задачи</h3>
                <div className="task-form">
                    <label htmlFor="taskName">Название задачи:</label>
                    <input
                        placeholder="Введите название задачи"
                        id="taskName"
                        type="text"
                        name="taskName"
                        required
                    />
                    {messageError.taskName && <span style={{color: "red"}}>{messageError.taskName}</span>}
                </div>
                <div className="task-form">
                    <fieldset>
                        <legend>Важность задачи</legend>
                        {data.loading && <div>Загрузка...</div>}
                        {data.allImportance.map((item) => (
                            <label key={item}>
                                <input
                                    type="radio"
                                    name="taskImportance"
                                    value={item}
                                />
                                {item}
                            </label>
                        ))}
                    </fieldset>
                </div>
                <div className="task-form">
                    <fieldset>
                        <legend>Статус задачи</legend>
                        {data.loading && <div>Загрузка...</div>}
                        {data.allStatus.map((item) => (
                            <label key={item}>
                                <input
                                    type="radio"
                                    name="taskStatus"
                                    value={item}
                                />
                                {item}
                            </label>
                        ))}
                    </fieldset>
                </div>
                <div className="task-form">
                    <label htmlFor="dateFinish">Дедлайн:</label>
                    <input type="date" id="dateFinish" name="dateFinish" min={acceptedTime} required/>
                </div>
                <div className="task-form">
                    <label htmlFor="taskDescription" className="taskDescriptionLabel">
                        Описание задачи:
                    </label>
                    <textarea
                        placeholder="Описание (опционально)"
                        id="taskDescription"
                        name="taskDescription"
                        rows="12"
                    ></textarea>
                </div>
                <div className="task-form">
                    <label htmlFor="taskPhoto">Выберите фото для задачи</label>
                    <input
                        id="taskPhoto"
                        type="file"
                        name="taskPhoto"
                        accept="image/jpeg, image/png, image/gif"
                        ref={fileInputRef}
                    />
                    <Button
                        onClickFunction={() => {
                            if (fileInputRef.current) {
                                fileInputRef.current.value = "";
                            }
                        }}>
                        Сбросить файл</Button>
                    {Object.keys(messageError.taskPhoto).length > 0 &&
                        Object.keys(messageError.taskPhoto).map((key) => (
                            <div style={{color: "red"}} key={key}>{messageError.taskPhoto[key]}</div>
                        ))}
                    <p className="fileFormat">Формат файлов: PNG, JPEG, GIF</p>
                </div>
                <Button type="submit">Создать</Button>
            </form>
        </div>
    )
}