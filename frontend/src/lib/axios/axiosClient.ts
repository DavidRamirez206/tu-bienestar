import type { AxiosResponse, AxiosPromise } from 'axios';
import axios from 'axios';

const baseURL = import.meta.env.VITE_API_HOST || '/';

const axiosClient = axios.create({
    baseURL,
    headers: { 'Content-Type': 'application/json' },
});

axiosClient.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers = config.headers || {};
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

export async function safeRequest<T>(request: AxiosPromise<T>) {
    try {
        const response: AxiosResponse<T> = await request;
        return { 
            error: false, 
            data: response.data, 
            status: response.status 
        };
    } catch (error) {
        return {
            error: true,
            message: axios.isAxiosError(error) ? error.message : 'An unexpected error occurred',
            status: axios.isAxiosError(error) ? error.response?.status : undefined,
        };
    }
}

export default axiosClient;