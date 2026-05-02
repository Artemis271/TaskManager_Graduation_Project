import React, {useCallback, useEffect, useState} from "react";
import "./modal-window.css";
import {createPortal} from "react-dom";

export default function ModalWindow({trigger, children, onClose, ...props})
{
    const [isOpen, setIsOpen] = useState(false);
    const open  = () => setIsOpen(true);
    const close = useCallback(() => {
        setIsOpen(false);
        onClose?.();
    }, [onClose]);

    const onKeyDown = useCallback(e => {
        if (e.key === "Escape") close();
    }, [close]);

    useEffect(() => {
        if (isOpen) window.addEventListener("keydown", onKeyDown);
        else        window.removeEventListener("keydown", onKeyDown);
        return () => window.removeEventListener("keydown", onKeyDown);
    }, [isOpen, onKeyDown]);

    const triggerWithHandler = React.cloneElement(trigger, {
        onClick: open,
        onClickFunction: open,
    });

    return (
        <>
            {triggerWithHandler}
            {isOpen &&
                createPortal(
                    <div className="modal open" onClick={e => {
                        e.stopPropagation()
                        if (e.target.classList.contains("modal")) close()
                    }}
                         onKeyDown={e => e.stopPropagation()}>
                        <div className="modal__box"
                             style={props.style}
                             onClick={e => e.stopPropagation()}
                             onKeyDown={e => e.stopPropagation()}
                             tabIndex={-1}>
                            <button className="modal__close-btn" onClick={close}>&#10005;</button>
                            {typeof children === "function" ? children({close}) : children}
                        </div>
                    </div>,
                    document.body
                )
            }
        </>
    );
}