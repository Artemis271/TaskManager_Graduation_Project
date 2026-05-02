import './oauth2-icon.css'

export default function Oauth2Icon() {
    const serviceNames = process.env.REACT_APP_OAUTH2_SERVICES_NAME_LIST?.split(',') || [];
    const serviceLinks = process.env.REACT_APP_OAUTH2_SERVICES_LINK_LIST?.split(',') || [];
    const serviceUri = process.env.REACT_APP_OAUTH2_SERVICES_URI_LIST?.split(',') || [];

    const backHost = process.env.REACT_APP_BACKEND_PROJECT_SERVICE_HOST;
    const backPort = process.env.REACT_APP_BACKEND_PORT;

    const services = serviceNames.map((name, index) => ({
        name,
        icon: serviceLinks[index] || '',
        uri: serviceUri[index] || ''
    }));

    const handleClickOauth = (provider) => {
        window.location.href = `http://${backHost}:${backPort}/api/oauth2/authorization/${provider}`;
    }

    return (
        <>
            <div className="sign-in-with">Войти с помощью:</div>
            <div className="oauth2-service-container">
                {services.map(service => (
                    <div
                        key={service.name}
                        className="oauth2-item"
                        role="button"
                        onClick={() => handleClickOauth(service.name.toLowerCase())}>
                        <img src={service.icon} alt={service.name} className="oauth2-service-image" />
                        <div className="service-name">{service.name}</div>
                    </div>
                ))}
            </div>
        </>
    );
}