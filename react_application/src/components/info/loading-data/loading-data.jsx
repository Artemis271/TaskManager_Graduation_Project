import './loading-data.css'

export default function LoadingData({loadingName, defaultName, defaultFont})
{
    return (
        <div className="loading-main"
            style={defaultFont ? {fontSize: "unset"} : {fontSize: "large"}}>
            <h2>{defaultName || `Загрузка ${loadingName || 'информации'}`}
                <div className="honeycomb">
                    <div></div>
                    <div></div>
                    <div></div>
                    <div></div>
                    <div></div>
                    <div></div>
                    <div></div>
                </div>
            </h2>
        </div>
    )
}