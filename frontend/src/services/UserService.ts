import axiosClient, { safeRequest } from '../lib/axios/axiosClient';
import type { UserRequest, UserResponse } from '../types/api.types';

export const getAllUsers = async () => {
    return safeRequest<UserResponse[]>(
        axiosClient.get('/api/v1/users')
    );
};

export const getUserById = async (id: number) => {
    return safeRequest<UserResponse>(
        axiosClient.get(`/api/v1/users/${id}`)
    );
};

export const createUser = async (request: UserRequest) => {
    return safeRequest<UserResponse>(
        axiosClient.post('/api/v1/users', request)
    );
};

export const updateUser = async (id: number, request: UserRequest) => {
    return safeRequest<UserResponse>(
        axiosClient.put(`/api/v1/users/${id}`, request)
    );
};

export const deleteUser = async (id: number) => {
    return safeRequest<void>(
        axiosClient.delete(`/api/v1/users/${id}`)
    );
};

export const getMyProfile = async (userId: number) => {
  return safeRequest<UserResponse>(
      axiosClient.get(`/api/v1/users/${userId}`)
  );
};

export const updateMyProfile = async (userId: number, request: UserRequest) => {
  return safeRequest<UserResponse>(
      axiosClient.put(`/api/v1/users/${userId}`, request)
  );
};
