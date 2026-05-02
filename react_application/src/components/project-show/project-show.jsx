import {useNavigate} from "react-router-dom";
import {useEffect, useRef, useState} from "react";
import axios from "axios";
import ProjectCard from "../project-card/project-card";
import Button from "../button/button";
import './project-show.css';
import LoadingData from "../info/loading-data/loading-data";
import {useAuth} from "../../hooks/auth";
import Empty from "./empty/empty";

const PAGE_SIZE = 15;

export default function ProjectShow() {
    const {user} = useAuth();
    const navigate = useNavigate();
    const [projects, setProjects] = useState([]);
    const [loading, setLoading] = useState(true);
    const [page, setPageNumber] = useState(0);
    const [hasMore, setHasMore] = useState(true);
    const didFetch = useRef(false);

    const fetchProject = async (pageToFetch) => {
        try {
            const backHost = process.env.REACT_APP_BACKEND_PROJECT_SERVICE_HOST;
            const backPort = process.env.REACT_APP_BACKEND_PORT;
            const response = await axios.get(
                `http://${backHost}:${backPort}/api/projects/allProjects`, {
                    params: {
                        pageNumber: pageToFetch,
                        size: PAGE_SIZE,
                        userId: user.id
                    },
                    headers: {Authorization: `Bearer ${localStorage.getItem('jwtToken')}`}
                }
            );

            if (pageToFetch === 0)
                setProjects(response.data);
            else setProjects(prev => [...prev, ...response.data]);

            if (response.data.length < PAGE_SIZE - 1)
                setHasMore(false);
        } catch (err) {
            if (axios.isAxiosError(err))
                navigate('/500')
            else navigate('/404');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (!didFetch.current) {
            didFetch.current = true;
            fetchProject();
        }
    }, [page, navigate]);

    const handleLoadMore = () => {
        console.log(page)
        if (hasMore) {
            setPageNumber(prevPage => prevPage + 1);
        }
    };

    if (loading && projects.length === 0) return <LoadingData loadingName="проекта"/>;
    if (!projects || projects.length === 0) return (
        <div>
            <Empty title={"Проекты не найдены"}>
            <Button
                onClickFunction={() => navigate('/create-project')}>
                Создать проект</Button>
            </Empty>
        </div>
    );

    return (
        <>
            {projects.map(element =>
                <ProjectCard
                    key={element.id}
                    project={element}
                    avatars={element.avatars}
                />
            )}
            {hasMore && (
                <div className="project-show-button">
                    <Button onClickFunction={handleLoadMore}>
                        Загрузить ещё
                    </Button>
                </div>
            )}
        </>
    );
}