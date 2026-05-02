import './http-error.css'
import {Link} from "react-router-dom";

export default function Http404()
{
    return (
    <div className="http-error-container">
        <div className="error-container">
            <div className="error-code">404</div>
            <div className="error-message">Страница не найдена</div>
            <p>К сожалению, страница, которую вы ищете, не найдена.</p>
            <p>Пожалуйста, проверьте URL-адрес или вернитесь на <Link to="/">домашнюю страницу</Link>.</p>
        </div>
    </div>
    )
}