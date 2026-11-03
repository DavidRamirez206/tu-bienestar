import axiosClient, { safeRequest } from '../lib/axios/axiosClient';
import type { SpaceResponse } from '../types/api.types';

export const getAllSpaces = async () => {
    return safeRequest<SpaceResponse[]>(
        axiosClient.get('/api/v1/spaces')
    );
};
