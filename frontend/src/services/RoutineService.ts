import axiosClient, { safeRequest } from '../lib/axios/axiosClient';
import type { RoutineRequest, RoutineResponse } from '../types/api.types';

export const getAllRoutines = async () => {
    return safeRequest<RoutineResponse[]>(
        axiosClient.get('/api/v1/routines')
    );
};

export const getRoutineById = async (id: number) => {
    return safeRequest<RoutineResponse>(
        axiosClient.get(`/api/v1/routines/${id}`)
    );
};

export const createRoutine = async (request: RoutineRequest) => {
    return safeRequest<RoutineResponse>(
        axiosClient.post('/api/v1/routines', request)
    );
};

export const updateRoutine = async (id: number, request: RoutineRequest) => {
    return safeRequest<RoutineResponse>(
        axiosClient.put(`/api/v1/routines/${id}`, request)
    );
};

export const adoptRoutine = async (id: number) => {
    return safeRequest<RoutineResponse>(
        axiosClient.post(`/api/v1/routines/${id}/adopt`)
    );
};

export const deleteRoutine = async (id: number) => {
    return safeRequest<void>(
        axiosClient.delete(`/api/v1/routines/${id}`)
    );
};
