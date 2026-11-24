import axiosClient, { safeRequest } from '../lib/axios/axiosClient';
import type { AssignmentRequest, AssignmentResponse, UserResponse } from '../types/api.types';

export const getAssignments = async () => {
    return safeRequest<AssignmentResponse[]>(
        axiosClient.get('/api/v1/assignments')
    );
};

export const getMyStudents = async () => {
    return safeRequest<AssignmentResponse[]>(
        axiosClient.get('/api/v1/assignments/me')
    );
};

export const getTrainers = async () => {
    return safeRequest<UserResponse[]>(
        axiosClient.get('/api/v1/assignments/trainers')
    );
};

export const getTrainees = async () => {
    return safeRequest<UserResponse[]>(
        axiosClient.get('/api/v1/assignments/trainees')
    );
};

export const assignTrainer = async (request: AssignmentRequest) => {
    return safeRequest<AssignmentResponse>(
        axiosClient.post('/api/v1/assignments', request)
    );
};

export const reassignTrainer = async (request: AssignmentRequest) => {
    return safeRequest<AssignmentResponse>(
        axiosClient.put('/api/v1/assignments', request)
    );
};

export const removeAssignment = async (userId: number, trainerId: number) => {
    return safeRequest<void>(
        axiosClient.delete(`/api/v1/assignments/${userId}/${trainerId}`)
    );
};
