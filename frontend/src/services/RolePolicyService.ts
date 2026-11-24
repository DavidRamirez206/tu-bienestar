import axiosClient, { safeRequest } from '../lib/axios/axiosClient';
import type { RolePolicyRequest, RolePolicyResponse } from '../types/api.types';

export const getAllRolePolicies = async () => {
    return safeRequest<RolePolicyResponse[]>(
        axiosClient.get('/api/v1/role-policies')
    );
};

export const createRolePolicy = async (request: RolePolicyRequest) => {
    return safeRequest<RolePolicyResponse>(
        axiosClient.post('/api/v1/role-policies', request)
    );
};

export const deleteRolePolicy = async (id: number) => {
    return safeRequest<void>(
        axiosClient.delete(`/api/v1/role-policies/${id}`)
    );
};