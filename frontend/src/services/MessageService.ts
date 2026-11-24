import axiosClient, { safeRequest } from '../lib/axios/axiosClient';
import type { ContactResponse, MessageRequest, MessageResponse } from '../types/api.types';

export const getMyMessages = async () => {
    return safeRequest<MessageResponse[]>(
        axiosClient.get('/api/v1/messages')
    );
};

export const getMessageContacts = async () => {
    return safeRequest<ContactResponse[]>(
        axiosClient.get('/api/v1/messages/contacts')
    );
};

export const sendMessage = async (request: MessageRequest) => {
    return safeRequest<MessageResponse[]>(
        axiosClient.post('/api/v1/messages', request)
    );
};
