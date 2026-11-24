import axiosClient, { safeRequest } from '../lib/axios/axiosClient';
import type { ScheduleFilters, ScheduleRequest, ScheduleResponse } from '../types/api.types';

export const getSchedules = async (filters: ScheduleFilters = {}) => {
    return safeRequest<ScheduleResponse[]>(
        axiosClient.get('/api/v1/schedules', { params: filters })
    );
};

export const getActivitySchedules = async (activityId: number) => {
    return safeRequest<ScheduleResponse[]>(
        axiosClient.get(`/api/v1/schedules/activity/${activityId}`)
    );
};

export const createSchedule = async (request: ScheduleRequest) => {
    return safeRequest<ScheduleResponse>(
        axiosClient.post('/api/v1/schedules', request)
    );
};

export const deleteSchedule = async (id: number) => {
    return safeRequest<void>(
        axiosClient.delete(`/api/v1/schedules/${id}`)
    );
};
