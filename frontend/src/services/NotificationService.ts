import axiosClient, { safeRequest } from '../lib/axios/axiosClient';
import type { NotificationResponse } from '../types/api.types';

export const getMyNotifications = async (unreadOnly = false) => {
    return safeRequest<NotificationResponse[]>(
        axiosClient.get('/api/v1/notifications', { params: { unreadOnly } })
    );
};

export const markNotificationAsRead = async (id: number) => {
    return safeRequest<NotificationResponse>(
        axiosClient.patch(`/api/v1/notifications/${id}/read`)
    );
};

export const markAllNotificationsAsRead = async () => {
    return safeRequest<{ updated: number }>(
        axiosClient.patch('/api/v1/notifications/read-all')
    );
};

export const deleteNotification = async (id: number) => {
    return safeRequest<void>(
        axiosClient.delete(`/api/v1/notifications/${id}`)
    );
};
