import axiosClient, { safeRequest } from '../lib/axios/axiosClient';
import type { RoleRequest, RoleResponse } from '../types/api.types';

export const getAllRoles = async () => {
    return safeRequest<RoleResponse[]>(
        axiosClient.get('/api/v1/roles')
    );
};

export const getRoleById = async (id: number) => {
    return safeRequest<RoleResponse>(
        axiosClient.get(`/api/v1/roles/${id}`)
    );
};

export const createRole = async (request: RoleRequest) => {
    return safeRequest<RoleResponse>(
        axiosClient.post('/api/v1/roles', request)
    );
};

export const updateRole = async (id: number, request: RoleRequest) => {
    return safeRequest<RoleResponse>(
        axiosClient.put(`/api/v1/roles/${id}`, request)
    );
};

export const deleteRole = async (id: number) => {
    return safeRequest<void>(
        axiosClient.delete(`/api/v1/roles/${id}`)
    );
};
