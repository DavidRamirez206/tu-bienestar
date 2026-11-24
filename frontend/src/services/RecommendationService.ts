import axiosClient, { safeRequest } from '../lib/axios/axiosClient';
import type { RecommendationRequest, RecommendationResponse } from '../types/api.types';

export const getAllRecommendations = async () => {
    return safeRequest<RecommendationResponse[]>(
        axiosClient.get('/api/v1/recommendations')
    );
};

export const getRecommendationById = async (id: number) => {
    return safeRequest<RecommendationResponse>(
        axiosClient.get(`/api/v1/recommendations/${id}`)
    );
};

export const createRecommendation = async (request: RecommendationRequest) => {
    return safeRequest<RecommendationResponse>(
        axiosClient.post('/api/v1/recommendations', request)
    );
};

export const deleteRecommendation = async (id: number) => {
    return safeRequest<void>(
        axiosClient.delete(`/api/v1/recommendations/${id}`)
    );
};