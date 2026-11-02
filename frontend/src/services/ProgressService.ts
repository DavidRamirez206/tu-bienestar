import axiosClient, { safeRequest } from '../lib/axios/axiosClient';
import type { ProgressRequest, ProgressResponse } from '../types/api.types';

export const getAllProgress = async () => {
    return safeRequest<ProgressResponse[]>(
        axiosClient.get('/api/v1/progress')
    );
};

export const getProgressByUserId = async (userId: number) => {
    return safeRequest<ProgressResponse[]>(
        axiosClient.get(`/api/v1/progress/user/${userId}`)
    );
};

export const createProgress = async (request: ProgressRequest) => {
    return safeRequest<ProgressResponse>(
        axiosClient.post('/api/v1/progress', request)
    );
};

export const deleteProgress = async (id: number) => {
    return safeRequest<void>(
        axiosClient.delete(`/api/v1/progress/${id}`)
    );
};