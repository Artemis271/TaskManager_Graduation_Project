import {useEffect, useState} from 'react';
import axios from 'axios';
import toast from 'react-hot-toast';
import Button from '../button/button';
import './admin-panel.css';

const backHost = process.env.REACT_APP_BACKEND_HOST;
const backPort = process.env.REACT_APP_BACKEND_PORT;
const base = `http://${backHost}:${backPort}/api`;

export default function AdminPanel() {
    const [users, setUsers] = useState([]);
    const [projects, setProjects] = useState([]);
    const [tab, setTab] = useState('users');

    const loadUsers = async () => {
        const res = await axios.get(`${base}/auth/admin/users`);
        setUsers(res.data);
    };

    const loadProjects = async () => {
        const res = await axios.get(`${base}/projects/admin/all`);
        setProjects(res.data);
    };

    useEffect(() => {
        loadUsers();
        loadProjects();
    }, []);

    const assignAdmin = async (id) => {
        await axios.patch(`${base}/auth/admin/assign/${id}`);
        toast.success('Роль ADMIN выдана');
        loadUsers();
    };

    const revokeAdmin = async (id) => {
        await axios.patch(`${base}/auth/admin/revoke/${id}`);
        toast.success('Роль ADMIN снята');
        loadUsers();
    };

    const deleteUser = async (id) => {
        await axios.delete(`${base}/auth/user/delete/${id}`);
        toast.success('Пользователь удалён');
        loadUsers();
    };

    const restoreUser = async (id) => {
        await axios.patch(`${base}/auth/admin/restore/${id}`);
        toast.success('Пользователь восстановлен');
        loadUsers();
    };

    const isAdmin = (user) => user.roles?.some(r => r.role === 'ROLE_ADMIN');

    return (
        <div className="admin-panel">
            <h2 className="admin-panel__title">Панель администратора</h2>

            <div className="admin-panel__tabs">
                <button
                    className={`admin-tab ${tab === 'users' ? 'admin-tab--active' : ''}`}
                    onClick={() => setTab('users')}>
                    Пользователи ({users.length})
                </button>
                <button
                    className={`admin-tab ${tab === 'projects' ? 'admin-tab--active' : ''}`}
                    onClick={() => setTab('projects')}>
                    Все проекты ({projects.length})
                </button>
            </div>

            {tab === 'users' && (
                <div className="admin-panel__section">
                    <table className="admin-table">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Имя пользователя</th>
                            <th>Email</th>
                            <th>Роли</th>
                            <th>Действия</th>
                        </tr>
                        </thead>
                        <tbody>
                        {users.map(u => (
                            <tr key={u.id}>
                                <td>{u.id}</td>
                                <td>{u.username}</td>
                                <td>{u.email}</td>
                                <td>
                                    {u.roles?.map(r => (
                                        <span key={r.role} className={`role-badge role-badge--${r.role.toLowerCase().replace('role_', '')}`}>
                                            {r.role.replace('ROLE_', '')}
                                        </span>
                                    ))}
                                </td>
                                <td className="admin-table__actions">
                                    {isAdmin(u)
                                        ? <Button onClickFunction={() => revokeAdmin(u.id)}>Снять ADMIN</Button>
                                        : <Button onClickFunction={() => assignAdmin(u.id)}>Дать ADMIN</Button>
                                    }
                                    <Button onClickFunction={() => deleteUser(u.id)}>Удалить</Button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}

            {tab === 'projects' && (
                <div className="admin-panel__section">
                    <table className="admin-table">
                        <thead>
                        <tr>
                            <th>Название</th>
                            <th>Описание</th>
                            <th>Дата создания</th>
                        </tr>
                        </thead>
                        <tbody>
                        {projects.map(p => (
                            <tr key={p.id}>
                                <td>{p.name}</td>
                                <td>{p.description}</td>
                                <td>{p.dateAdded}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}
