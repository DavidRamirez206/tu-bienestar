import axiosClient, { safeRequest } from '../lib/axios/axiosClient';
import type { UserRoleRequest, UserRoleResponse } from '../types/api.types';

export const getAllUserRoles = async () => {
    return safeRequest<UserRoleResponse[]>(
        axiosClient.get('/api/v1/user-roles')
    );
};

export const createUserRole = async (request: UserRoleRequest) => {
    return safeRequest<UserRoleResponse>(
        axiosClient.post('/api/v1/user-roles', request)
    );
};

export const deleteUserRole = async (userId: number, roleId: number) => {
    return safeRequest<void>(
        axiosClient.delete(`/api/v1/user-roles/${userId}/${roleId}`)
    );
};