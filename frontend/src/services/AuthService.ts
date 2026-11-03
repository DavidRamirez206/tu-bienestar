import axiosClient, { safeRequest } from '../lib/axios/axiosClient';
import type { AuthRequest, AuthResponse, RegisterRequest, RegisterResponse, SessionResponse } from '../types/api.types';

export const login = async (request: AuthRequest) => {
    return safeRequest<AuthResponse>(
        axiosClient.post('/api/v1/auth/login', request)
    );
};

export const getSession = async () => {
    return safeRequest<SessionResponse>(
        axiosClient.get('/api/v1/auth/me')
    );
};

export const logout = async () => {
    return safeRequest<void>(
        axiosClient.post('/api/v1/auth/logout')
    );
};

export const register = async (request: RegisterRequest) => {
    return safeRequest<RegisterResponse>(
        axiosClient.post('/api/v1/auth/register', request)
    );
};
