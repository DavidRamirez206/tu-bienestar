import axiosClient, { safeRequest } from '../lib/axios/axiosClient';
import type { EnrollmentResponse } from '../types/api.types';

export const getMyEnrollments = async () => {
    return safeRequest<EnrollmentResponse[]>(
        axiosClient.get('/api/v1/enrollments/me')
    );
};

export const enroll = async (activityId: number) => {
    return safeRequest<EnrollmentResponse>(
        axiosClient.post('/api/v1/enrollments', { activityId })
    );
};

export const cancelEnrollment = async (userId: number, activityId: number) => {
    return safeRequest<void>(
        axiosClient.delete(`/api/v1/enrollments/${userId}/${activityId}`)
    );
};
