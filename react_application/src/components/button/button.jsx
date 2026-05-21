export default function Button({children, isActive = true, onClickFunction, className = '', ...props}) {
    return (
        <button type={props.type || "button"}
            style={props.style}
            className={`button${isActive ? '' : ' disabled'}${className ? ' ' + className : ''}`}
            onClick={() => onClickFunction?.()}>
            {children}
        </button>
    )
}