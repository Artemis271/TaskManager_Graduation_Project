import React from 'react';
import './main.css'
import Button from "../button/button";
import {useNavigate} from "react-router-dom";

export default function Main() {
    const navigate = useNavigate()
    return (
        <>
            <svg
                className="main-circle-1"
                width="200"
                height="200"
                xmlns="http://www.w3.org/2000/svg">
                <defs>
                    <filter
                        id="drop-shadow"
                        x="-50%"
                        y="-50%"
                        width="250%"
                        height="200%"
                        colorInterpolationFilters="sRGB">
                        <feDropShadow
                            dx="0"
                            dy="0"
                            stdDeviation="45"
                            floodColor="rgba(0,0,0,1)"/>
                    </filter>
                </defs>
                <circle
                    cx="10"
                    cy="10"
                    r="80"
                    fill="grey"
                    filter="url(#drop-shadow)"
                />
            </svg>
            <svg
                className="main-circle-2"
                width="250"
                height="250"
                xmlns="http://www.w3.org/2000/svg">
                <defs>
                    <filter
                        id="drop-shadow-1"
                        x="-50%"
                        y="-50%"
                        width="200%"
                        height="200%"
                        colorInterpolationFilters="sRGB">
                        <feDropShadow
                            dx="0"
                            dy="0"
                            stdDeviation="40"
                            floodColor="#9cdbcb"/>
                    </filter>
                </defs>
                <circle
                    cx="125"
                    cy="125"
                    r="60"
                    fill="#9cdbcb"
                    filter="url(#drop-shadow-1)"
                />
            </svg>
            <svg
                className="main-circle-3"
                width="200"
                height="200"
                xmlns="http://www.w3.org/2000/svg">
                <defs>
                    <filter
                        id="drop-shadow-2"
                        x="-50%"
                        y="-50%"
                        width="200%"
                        height="200%"
                        colorInterpolationFilters="sRGB">
                        <feDropShadow
                            dx="0"
                            dy="0"
                            stdDeviation="40"
                            floodColor="#83d5e7"/>
                    </filter>
                </defs>
                <circle
                    cx="100"
                    cy="100"
                    r="50"
                    fill="#83d5e7"
                    filter="url(#drop-shadow-2)"
                />
            </svg>
            <svg
                className="main-circle-4"
                width="300"
                height="300"
                xmlns="http://www.w3.org/2000/svg">
                <defs>
                    <filter
                        id="drop-shadow-3"
                        x="-50%"
                        y="-50%"
                        width="250%"
                        height="250%"
                        colorInterpolationFilters="sRGB">
                        <feDropShadow
                            dx="0"
                            dy="0"
                            stdDeviation="40"
                            floodColor="#dbea51"/>
                    </filter>
                </defs>
                <circle
                    cx="150"
                    cy="150"
                    r="40"
                    fill="#dbea51"
                    filter="url(#drop-shadow-3)"
                />
            </svg>
            <div className="main-description">
                <strong>Task-Manager</strong><span> - Инструмент для отслеживания поставленных задач</span>
            </div>
            <section className="section-g">
                <div className="slider middle">
                    <div className="slides">
                        <input type="radio" name="r" id="r1" defaultChecked/>
                        <input type="radio" name="r" id="r2"/>
                        <input type="radio" name="r" id="r3"/>
                        <input type="radio" name="r" id="r4"/>

                        <div className="slide s1">
                            <img className="slide-image" src="/main-slider/marketing.jpg" alt="marketing"/>
                        </div>
                        <div className="slide">
                            <img className="slide-image" src="/main-slider/it.png" alt="IT development"/>
                        </div>
                        <div className="slide">
                            <img className="slide-image" src="/main-slider/business-analytics.jpg" alt="business-analytics"/>
                        </div>
                        <div className="slide">
                            <img className="slide-image" src="/main-slider/business-analytics-1.jpg" alt="business-analytics"/>
                        </div>
                    </div>
                    <div className="navigation">
                        <label htmlFor="r1" className="main-bar"></label>
                        <label htmlFor="r2" className="main-bar"></label>
                        <label htmlFor="r3" className="main-bar"></label>
                        <label htmlFor="r4" className="main-bar"></label>
                    </div>
                </div>
            </section>

            <div className="main-page">
                <h2>Проекты</h2>
                <div className="main-buttons-container">
                    <Button onClickFunction={() => navigate('/create-project')}
                            style={{width: "10%"}}>
                        Создать проект
                    </Button>
                    <Button onClickFunction={() => navigate('/projects')}
                            style={{width: "10%"}}>
                        К проектам
                    </Button>
                </div>
            </div>
        </>
    )
}