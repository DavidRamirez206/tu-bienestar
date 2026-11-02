import axiosClient, { safeRequest } from '../lib/axios/axiosClient';
import type { ExerciseRequest, ExerciseResponse } from '../types/api.types';

export const getAllExercises = async () => {
    return safeRequest<ExerciseResponse[]>(
        axiosClient.get('/api/v1/exercises')
    );
};

export const getExerciseById = async (id: number) => {
    return safeRequest<ExerciseResponse>(
        axiosClient.get(`/api/v1/exercises/${id}`)
    );
};

export const createExercise = async (request: ExerciseRequest) => {
    return safeRequest<ExerciseResponse>(
        axiosClient.post('/api/v1/exercises', request)
    );
};

export const updateExercise = async (id: number, request: ExerciseRequest) => {
    return safeRequest<ExerciseResponse>(
        axiosClient.put(`/api/v1/exercises/${id}`, request)
    );
};

export const deleteExercise = async (id: number) => {
    return safeRequest<void>(
        axiosClient.delete(`/api/v1/exercises/${id}`)
    );
};