import {createContext, useCallback, useContext, useEffect, useRef, useState} from 'react';
import axios from 'axios';
import {jwtDecode} from "jwt-decode";

const AuthContext = createContext(null);

const backHost = process.env.REACT_APP_BACKEND_PROJECT_SERVICE_HOST;
const backPort = process.env.REACT_APP_BACKEND_PORT;

export function AuthProvider({ children }) {
    const [token, setToken] = useState(() => {
        if (typeof window !== 'undefined')
            return localStorage.getItem('jwtToken');
        return null;
    });

    const [user, setUser] = useState(() => {
        try {
            return token ? jwtDecode(token) : null;
        } catch(e) {
            console.error('Error decoding token:', e);
            return null;
        }
    });

    const updateAxiosInterceptors = useCallback((currentToken) => {
        axios.interceptors.request.eject(reqInterceptor.current);

        reqInterceptor.current = axios.interceptors.request.use(
            config => {
                if (currentToken) {
                    config.headers.Authorization = `Bearer ${currentToken}`;
                }
                return config;
            },
            error => Promise.reject(error)
        );
    }, []);

    const reqInterceptor = useRef(null);

    useEffect(() => {
        updateAxiosInterceptors(token);

        return () => {
            if (reqInterceptor.current !== null)
                axios.interceptors.request.eject(reqInterceptor.current);
        };
    }, [token, updateAxiosInterceptors]);

    const login = async (credentials) => {
        const res = await axios.post(`http://${backHost}:${backPort}/api/auth/login`, credentials);
        const newToken = res.data.token;

        localStorage.setItem('jwtToken', newToken);
        setToken(newToken);
        setUser(jwtDecode(newToken));
        updateAxiosInterceptors(newToken);

        return res;
    };

    const logout = useCallback(() => {
        localStorage.removeItem('jwtToken');
        setToken(null);
        setUser(null);
        updateAxiosInterceptors(null);
    }, [updateAxiosInterceptors]);

    return (
        <AuthContext.Provider
            value={{ token, isAuthenticated: !!token, login, logout, user }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error('useAuth must be used within AuthProvider');
    return ctx;
}