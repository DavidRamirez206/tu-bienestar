import axiosClient, { safeRequest } from '../lib/axios/axiosClient';
import type { PolicyRequest, PolicyResponse } from '../types/api.types';

export const getAllPolicies = async () => {
    return safeRequest<PolicyResponse[]>(
        axiosClient.get('/api/v1/policies')
    );
};

export const getPolicyById = async (id: number) => {
    return safeRequest<PolicyResponse>(
        axiosClient.get(`/api/v1/policies/${id}`)
    );
};

export const createPolicy = async (request: PolicyRequest) => {
    return safeRequest<PolicyResponse>(
        axiosClient.post('/api/v1/policies', request)
    );
};

export const updatePolicy = async (id: number, request: PolicyRequest) => {
    return safeRequest<PolicyResponse>(
        axiosClient.put(`/api/v1/policies/${id}`, request)
    );
};

export const deletePolicy = async (id: number) => {
    return safeRequest<void>(
        axiosClient.delete(`/api/v1/policies/${id}`)
    );
};
