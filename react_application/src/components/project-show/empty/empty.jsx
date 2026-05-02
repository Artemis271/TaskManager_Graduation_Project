import './empty.css'

export default function Empty({children, title}) {
    return (
        <div className="empty-main-container">
            <svg width="140" height="145" viewBox="0 0 140 145" xmlns="http://www.w3.org/2000/svg">
                <circle cx="70" cy="72.5" r="60" fill="none" stroke="#fc7c7c" strokeWidth="20"/>
                <line x1="30" y1="115" x2="110" y2="30" stroke="#fc7c7c" strokeWidth="10" strokeLinecap="round"
                      opacity="1"/>
            </svg>
            <h3>{title}</h3>
            {children}
        </div>
    )
}