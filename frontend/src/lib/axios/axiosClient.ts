import type { AxiosResponse, AxiosPromise } from 'axios';
import axios from 'axios';

const baseURL = import.meta.env.VITE_API_HOST || '/';

const axiosClient = axios.create({
    baseURL,
    headers: { 'Content-Type': 'application/json' },
    // La sesion viaja en una cookie HttpOnly que JavaScript no puede leer.
    withCredentials: true,
});

let onUnauthorized = () => {};

export const setUnauthorizedHandler = (handler: () => void) => {
    onUnauthorized = handler;
};

axiosClient.interceptors.response.use(
    (response) => response,
    (error) => {
        if (axios.isAxiosError(error) && error.response?.status === 401) {
            onUnauthorized();
        }
        return Promise.reject(error);
    }
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
            message: axios.isAxiosError(error)
                ? ((error.response?.data as { message?: string } | undefined)?.message ?? error.message)
                : 'An unexpected error occurred',
            status: axios.isAxiosError(error) ? error.response?.status : undefined,
        };
    }
}

export default axiosClient;