import { useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";

export default function OAuth2RedirectHandler() {
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        const params = new URLSearchParams(location.search);
        const token = params.get("token");
        const error = params.get("error");
        console.log(token)

        if (token) {
            localStorage.setItem("jwtToken", token);
            window.location.href = "/";
        } else if (error) {
            navigate("/login");
        }
    }, [location, navigate]);

    return <p>Обработка авторизации...</p>;
}