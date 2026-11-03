import axiosClient, { safeRequest } from '../lib/axios/axiosClient';
import type { AuthRequest, AuthResponse, RegisterRequest, RegisterResponse } from '../types/api.types';

export const login = async (request: AuthRequest) => {
    return safeRequest<AuthResponse>(
        axiosClient.post('/api/v1/auth/login', request)
    );
};

export const register = async (request: RegisterRequest) => {
    return safeRequest<RegisterResponse>(
        axiosClient.post('/api/v1/auth/register', request)
    );
};
