import axiosClient, { safeRequest } from '../lib/axios/axiosClient';
import type { RoutineExerciseRequest, RoutineExerciseResponse } from '../types/api.types';

export const getRoutineExercises = async (routineId: number) => {
    return safeRequest<RoutineExerciseResponse[]>(
        axiosClient.get(`/api/v1/routine-exercises/routine/${routineId}/exercises`)
    );
};

// Deja la rutina exactamente con estos ejercicios: agrega, actualiza y quita lo necesario.
export const replaceRoutineExercises = async (routineId: number, items: RoutineExerciseRequest[]) => {
    return safeRequest<RoutineExerciseResponse[]>(
        axiosClient.put(`/api/v1/routine-exercises/routine/${routineId}/exercises`, items)
    );
};
