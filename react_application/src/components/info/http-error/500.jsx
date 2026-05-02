import './http-error.css'

export default function Http500()
{
    return (
        <div className="http-error-container">
            <div className="error-container">
                <div className="error-code">500</div>
                <div className="error-message">Ошибка сервера</div>
                <p>К сожалению, сервер сейчас не работает или не может обработать Ваш запрос</p>
                <p>Пожалуйста, зайдите позже.</p>
            </div>
        </div>
    )
}