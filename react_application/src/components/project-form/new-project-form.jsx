import React, {useState} from 'react';
import {Navigate, useLocation, useNavigate} from 'react-router-dom';
import './new-project-form.css'
import '../button/button.css'
import axios from "axios";
import Button from "../button/button";
import {useAuth} from "../../hooks/auth";
import {LuImagePlus} from "react-icons/lu";

export default function NewProjectForm({isEdit = false, initProjectData = {}, onSubmit, ...props}) {
    const {user, isAuthenticated} = useAuth();
    const location = useLocation();
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        projectName: initProjectData.name ?? '',
        projectDescription: initProjectData.description ?? '',
    });
    const [errors, setErrors] = useState({});

    if (!isAuthenticated)
        return <Navigate to="/login" state={{from: location}} replace/>;

    const handleChange = (e) => {
        const {name, value, files} = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: files ? files[0] : value,
        }));
    };

    const validateForm = () => {
        const newErrors = {};

        if (!isEdit) {
            if (!formData.uploadProjectImage) {
                newErrors.uploadProjectImage = 'Изображение обязательно';
            } else {
                const allowed = ['image/jpeg', 'image/png', 'image/gif'];
                if (!allowed.includes(formData.uploadProjectImage.type))
                    newErrors.uploadProjectImage = 'Разрешены только JPEG, PNG и GIF';
                else if (formData.uploadProjectImage.size > 5 * 1024 * 1024)
                    newErrors.uploadProjectImage = 'Максимальный размер — 5 МБ';
            }
        }

        if (!formData.projectName.trim())
            newErrors.projectName = 'Название обязательно';
        else if (formData.projectName.length > 50)
            newErrors.projectName = 'Не более 50 символов';

        if (formData.projectDescription.length > 500)
            newErrors.projectDescription = 'Не более 500 символов';

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!validateForm()) return;

        const fd = new FormData();
        fd.append('name', formData.projectName ?? '');
        fd.append('description', formData.projectDescription ?? '');
        fd.append('userId', user.id);
        if (formData.uploadProjectImage)
            fd.append('avatars', formData.uploadProjectImage);

        try {
            const backHost = process.env.REACT_APP_BACKEND_PROJECT_SERVICE_HOST;
            const backPort = process.env.REACT_APP_BACKEND_PORT;

            if (!isEdit) {
                const res = await axios.post(
                    `http://${backHost}:${backPort}/api/projects/createProject`,
                    fd, {headers: {'Content-Type': 'multipart/form-data'}}
                );
                navigate(res.data?.id ? `/projects/${res.data.id}` : '/');
            } else {
                await onSubmit(fd);
            }
        } catch {}
    };

    return (
        <div className="project-form-main">
            <form method="POST" onSubmit={handleSubmit} encType="multipart/form-data" style={props.style}>
                <h3>{isEdit ? 'Редактировать проект' : 'Новый проект'}</h3>

                {/* File upload */}
                <div className="input-wrap">
                    <label>Обложка проекта</label>
                    <div className="file-upload-area">
                        <input
                            type="file"
                            name="uploadProjectImage"
                            accept="image/jpeg,image/png,image/gif"
                            onChange={handleChange}
                        />
                        <div className="file-upload-icon"><LuImagePlus/></div>
                        {formData.uploadProjectImage
                            ? <div className="file-upload-name">{formData.uploadProjectImage.name}</div>
                            : <div className="file-upload-text">Нажмите или перетащите файл<br/>JPEG, PNG, GIF · до 5 МБ</div>
                        }
                    </div>
                    {errors.uploadProjectImage && <span className="field-error">{errors.uploadProjectImage}</span>}
                </div>

                {/* Name */}
                <div className="input-wrap">
                    <label htmlFor="projectName">Название</label>
                    <input
                        id="projectName"
                        type="text"
                        name="projectName"
                        placeholder="Введите название проекта"
                        value={formData.projectName}
                        onChange={handleChange}
                    />
                    {errors.projectName && <span className="field-error">{errors.projectName}</span>}
                </div>

                {/* Description */}
                <div className="input-wrap">
                    <label htmlFor="projectDescription">Описание</label>
                    <textarea
                        className="new-project-textarea"
                        id="projectDescription"
                        name="projectDescription"
                        placeholder="Краткое описание проекта (необязательно)"
                        rows="5"
                        value={formData.projectDescription}
                        onChange={handleChange}
                    />
                    {errors.projectDescription && <span className="field-error">{errors.projectDescription}</span>}
                </div>

                <Button type="submit">{isEdit ? 'Сохранить' : 'Создать проект'}</Button>
            </form>
        </div>
    );
}
