import axiosClient, { safeRequest } from '../lib/axios/axiosClient';
import type { AuthRequest, AuthResponse } from '../types/api.types';

export const login = async (request: AuthRequest) => {
    return safeRequest<AuthResponse>(
        axiosClient.post('/api/v1/auth/login', request)
    );
};
