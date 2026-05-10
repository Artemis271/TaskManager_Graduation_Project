import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import reportWebVitals from './reportWebVitals';
import {BrowserRouter} from "react-router-dom";
import Navbar from "./components/navbar/navbar";
import {AuthProvider} from "./hooks/auth";
import {Toaster} from "react-hot-toast";
import ErrorBoundary from "./components/error-boundary/ErrorBoundary";
import {useAxiosErrorInterceptor} from "./hooks/useAxiosErrorInterceptor";

function AppRoot() {
    useAxiosErrorInterceptor();
    return (
        <>
            <Toaster
                position="top-right"
                toastOptions={{
                    duration: 4000,
                    style: {
                        background: '#1a1a1a',
                        color: '#e0e0e0',
                        border: '1px solid #2e2e2e',
                    },
                    error: {
                        iconTheme: { primary: '#e05a5a', secondary: '#1a1a1a' },
                    },
                    success: {
                        iconTheme: { primary: '#9cdbcb', secondary: '#1a1a1a' },
                    },
                }}
            />
            <BrowserRouter>
                <Navbar/>
            </BrowserRouter>
        </>
    );
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <React.StrictMode>
        <ErrorBoundary>
            <AuthProvider>
                <AppRoot/>
            </AuthProvider>
        </ErrorBoundary>
    </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();