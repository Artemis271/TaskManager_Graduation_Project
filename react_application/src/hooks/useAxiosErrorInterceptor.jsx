import { useEffect, useRef } from 'react';
import axios from 'axios';
import toast from 'react-hot-toast';

const ERROR_MESSAGES = {
    400: 'Некорректный запрос',
    401: 'Необходима авторизация',
    403: 'Доступ запрещён',
    404: 'Ресурс не найден',
    409: 'Конфликт данных',
    500: 'Ошибка сервера',
    503: 'Сервис недоступен',
};

export function useAxiosErrorInterceptor() {
    const interceptorId = useRef(null);

    useEffect(() => {
        interceptorId.current = axios.interceptors.response.use(
            response => response,
            error => {
                const status = error?.response?.status;
                const serverMessage = error?.response?.data?.message;
                const message = serverMessage || ERROR_MESSAGES[status] || 'Произошла ошибка';
                toast.error(message);
                return Promise.reject(error);
            }
        );

        return () => {
            axios.interceptors.response.eject(interceptorId.current);
        };
    }, []);
}
