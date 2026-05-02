import React from "react";

import "./background.css";

export default function Background()
{
    return (
        <div id="webcrumbs">
            <div>
                <div>
                    <svg viewBox="0 0 1200 800" xmlns="http://www.w3.org/2000/svg">
                        <defs>
                            <pattern id="grid-pattern" width="50" height="50" patternUnits="userSpaceOnUse">
                                <path d="M 50 0 L 0 0 0 50" fill="none" stroke="rgba(240, 240, 240, 0.5)"
                                      strokeWidth="1"/>
                            </pattern>
                            <linearGradient id="bg-gradient" x1="0%" y1="0%" x2="100%" y2="100%">
                                <stop offset="0%" stopColor="rgba(255, 255, 255, 0.9)"/>
                                <stop offset="100%" stopColor="rgba(240, 240, 240, 0.9)"/>
                            </linearGradient>
                            <filter id="glow" x="-20%" y="-20%" width="140%" height="140%">
                                <feGaussianBlur stdDeviation="15" result="blur"/>
                                <feComposite in="SourceGraphic" in2="blur" operator="over"/>
                            </filter>
                        </defs>

                        <rect width="100%" height="100%" fill="url(https://webcrumbs.cloud/placeholder)"/>
                        <rect width="100%" height="100%" fill="url(https://webcrumbs.cloud/placeholder)"/>

                        <g filter="url(https://webcrumbs.cloud/placeholder)" opacity="0.7">
                            <circle cx="300" cy="200" r="8" fill="rgba(220, 230, 255, 0.9)"/>
                            <circle cx="500" cy="350" r="6" fill="rgba(220, 230, 255, 0.9)"/>
                            <circle cx="700" cy="250" r="10" fill="rgba(220, 230, 255, 0.9)"/>
                            <circle cx="900" cy="400" r="7" fill="rgba(220, 230, 255, 0.9)"/>
                            <circle cx="400" cy="500" r="9" fill="rgba(220, 230, 255, 0.9)"/>
                            <circle cx="600" cy="600" r="5" fill="rgba(220, 230, 255, 0.9)"/>
                            <circle cx="800" cy="450" r="8" fill="rgba(220, 230, 255, 0.9)"/>
                        </g>

                        <g opacity="0.4">
                            <path d="M 300 200 L 500 350 L 700 250 L 900 400 L 800 450 L 600 600 L 400 500 Z"
                                  fill="none" stroke="rgba(200, 210, 240, 0.8)" strokeWidth="2" strokeDasharray="5,5"/>
                        </g>

                        <g>
                            <path d="M 100 100 Q 200 50, 300 150 T 500 200" stroke="rgba(230, 240, 255, 0.8)"
                                  strokeWidth="3" fill="none"/>
                            <path d="M 700 500 Q 800 400, 900 550 T 1100 600" stroke="rgba(230, 240, 255, 0.8)"
                                  strokeWidth="3" fill="none"/>
                            <path d="M 200 700 Q 300 650, 400 700 T 600 750" stroke="rgba(230, 240, 255, 0.8)"
                                  strokeWidth="3" fill="none"/>
                        </g>

                        <g opacity="0.6">
                            <rect x="100" y="600" width="30" height="100" rx="5" fill="rgba(225, 235, 250, 0.9)"/>
                            <rect x="150" y="550" width="30" height="150" rx="5" fill="rgba(225, 235, 250, 0.9)"/>
                            <rect x="200" y="570" width="30" height="130" rx="5" fill="rgba(225, 235, 250, 0.9)"/>
                            <rect x="250" y="500" width="30" height="200" rx="5" fill="rgba(225, 235, 250, 0.9)"/>
                            <rect x="300" y="580" width="30" height="120" rx="5" fill="rgba(225, 235, 250, 0.9)"/>
                        </g>

                        <g transform="translate(950, 180)">
                            <path d="M 0 0 L 0 -60 A 60 60 0 0 1 52 -30 Z" fill="rgba(235, 245, 255, 0.9)"/>
                            <path d="M 0 0 L 52 -30 A 60 60 0 0 1 52 30 Z" fill="rgba(225, 235, 250, 0.9)"/>
                            <path d="M 0 0 L 52 30 A 60 60 0 0 1 0 60 Z" fill="rgba(215, 225, 245, 0.9)"/>
                            <path d="M 0 0 L 0 60 A 60 60 0 0 1 -60 0 Z" fill="rgba(205, 215, 240, 0.9)"/>
                            <path d="M 0 0 L -60 0 A 60 60 0 0 1 0 -60 Z" fill="rgba(195, 205, 235, 0.9)"/>
                        </g>

                        <g opacity="0.3">
                            <circle cx="100" cy="100" r="50" fill="none" stroke="rgba(220, 230, 250, 0.9)"
                                    strokeWidth="2"/>
                            <circle cx="1100" cy="700" r="70" fill="none" stroke="rgba(220, 230, 250, 0.9)"
                                    strokeWidth="2"/>
                            <rect x="900" y="100" width="100" height="100" rx="10" fill="none"
                                  stroke="rgba(220, 230, 250, 0.9)" strokeWidth="2"/>
                            <rect x="200" y="600" width="120" height="120" rx="10" fill="none"
                                  stroke="rgba(220, 230, 250, 0.9)" strokeWidth="2"/>
                        </g>

                        <g>
                            <path d="M 100 300 C 200 200, 300 400, 400 300 S 500 400, 600 300" fill="none"
                                  stroke="rgba(220, 230, 250, 0.8)" strokeWidth="3"/>
                            <path d="M 700 350 C 800 250, 900 450, 1000 350 S 1100 450, 1200 350" fill="none"
                                  stroke="rgba(210, 220, 245, 0.8)" strokeWidth="3"/>
                        </g>
                    </svg>
                </div>
            </div>
        </div>
    )
}