import React from 'react';
import './navbar.css'
import {Link, Route, Routes, useNavigate} from "react-router-dom";
import Main from "../main/main";
import Account from "../account/account";
import NewProjectForm from "../project-form/new-project-form";
import Http404 from "../info/http-error/404";
import ProjectPage from "../project-page/project-page";
import ProjectShow from "../project-show/project-show";
import TaskCard from "../task-card/task-card";
import Infograf from "../charts/infograf";
import LoginForm from "../login-form/login-form";
import ProtectedRoute from "../protected-route";
import Http500 from "../info/http-error/500";
import {useAuth} from "../../hooks/auth";
import RegisterForm from "../login-form/register-form/register-form";
import OAuth2RedirectHandler from "../login-form/oauth2-redirect-handler";
import Button from "../button/button";
import ModalWindow from "../modal-window/modal-window";
import WebChat from "../web-chat/web-chat";
import MyTeam from "../account/my-team/my-team";

export default function Navbar() {
    const {isAuthenticated, logout, user} = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    return (
        <>
            <nav className="navigation-menu">
                <div className="svg-main-avatar">
                    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M4 13H10V19H4V13Z" fill="currentColor"/>
                        <path d="M4 5H10V11H4V5Z" fill="currentColor"/>
                        <path d="M12 5H18V11H12V5Z" fill="currentColor"/>
                        <path d="M14 13H20V19H14V13Z" fill="currentColor"/>
                    </svg>
                    <h2 className="h2-main-page">Task Manager</h2>
                </div>
                <ul className="navbar">
                    <li>
                        <span className="link-wrap">
                          <Link to="/">Главная</Link>
                        </span>
                    </li>

                    {isAuthenticated ? (
                        <li className="dropdown">
                            <span className="link-wrap">
                                <Link to={`/account/${user.id}`}>Аккаунт</Link>
                            </span>
                            <ul className="dropdown-menu">
                                <li>
                                    <div className="inner-list-item">
                                        <ModalWindow trigger={<span role="button">Выйти</span>}>
                                            {({close}) => (
                                                <>
                                                    <h3>Действительно выйти из аккаунта?</h3>
                                                    <div className="modal-actions">
                                                        <Button onClickFunction={close}>Нет</Button>
                                                        <Button onClickFunction={() => {
                                                            handleLogout();
                                                            close();
                                                        }}>Да
                                                        </Button>
                                                    </div>
                                                </>
                                            )}
                                        </ModalWindow>
                                    </div>
                                </li>
                                <li>
                                    <div className="inner-list-item my-team">
                                        <Link to='/account/my-team'>Моя команда</Link>
                                    </div>
                                </li>
                                <li>
                                    <div className="inner-list-item">
                                        <Link to='/account/web-chat'>Групповой чат</Link>
                                    </div>
                                </li>
                            </ul>
                        </li>
                    ) : (
                        <>
                            <li>
                                <span className="link-wrap">
                                    <Link to="/login">Войти</Link>
                                </span>
                            </li>
                            <li>
                                <span className="link-wrap">
                                    <Link to="/register">Зарегистрироваться</Link>
                                </span>
                            </li>
                        </>
                    )}

                    {isAuthenticated && (
                        <>
                            <li>
                                <span className="link-wrap">
                                  <Link to="/create-project">Создать проект</Link>
                                </span>
                            </li>
                            <li>
                                <span className="link-wrap">
                                  <Link to={`/projects`}>Просмотр проектов</Link>
                                </span>
                            </li>
                            <li>
                                <span className="link-wrap">
                                  <Link to="/info">Инфографика</Link>
                                </span>
                            </li>
                        </>
                    )}
                </ul>

            </nav>

            <Routes>
                <Route path="/" element={<Main/>}/>
                <Route path="/login" element={<LoginForm/>}/>
                <Route path="/register" element={<RegisterForm/>}/>
                <Route path="/account/:userId" element={<ProtectedRoute><Account/></ProtectedRoute>}/>
                <Route path="/account/my-team" element={<ProtectedRoute><MyTeam/></ProtectedRoute>}/>
                <Route path="/create-project" element={<NewProjectForm/>}/>
                <Route path="/update-project/:projectId" element={<NewProjectForm/>}/>
                <Route path="/projects" element={<ProtectedRoute><ProjectShow/></ProtectedRoute>}/>
                <Route path="/info" element={<Infograf/>}/>
                <Route path="/projects/:projectId" element={<ProjectPage/>}/>
                <Route path="/projects/:projectId/tasks/:taskId" element={<TaskCard/>}/>
                <Route path="/login/oauth2/code/:provider" element={<OAuth2RedirectHandler/>}/>
                <Route path="/account/web-chat" element={<ProtectedRoute><WebChat/></ProtectedRoute>}/>

                <Route path="/500" element={<Http500/>}/>
                <Route path="/404" element={<Http404/>}/>
                <Route path="*" element={<Http404/>}/>
            </Routes>
        </>
    )
}