import axiosClient, { safeRequest } from '../lib/axios/axiosClient';
import type { ActivityFilters, ActivityRequest, ActivityResponse } from '../types/api.types';

export const getActivities = async (filters: ActivityFilters = {}) => {
    return safeRequest<ActivityResponse[]>(
        axiosClient.get('/api/v1/activities', { params: filters })
    );
};

export const createActivity = async (request: ActivityRequest) => {
    return safeRequest<ActivityResponse>(
        axiosClient.post('/api/v1/activities', request)
    );
};

export const updateActivity = async (id: number, request: ActivityRequest) => {
    return safeRequest<ActivityResponse>(
        axiosClient.put(`/api/v1/activities/${id}`, request)
    );
};

export const deleteActivity = async (id: number) => {
    return safeRequest<void>(
        axiosClient.delete(`/api/v1/activities/${id}`)
    );
};
