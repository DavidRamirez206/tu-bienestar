import { useEffect } from 'react';
import { useAppDispatch } from './useDispatch';
import useAuth from './useAuth';
import createStompClient from '../lib/stomp/stompClient';
import {
  addMessages,
  addNotification,
  resetRealTime,
  setConnected,
  setMessages,
  setNotifications,
} from '../store/slices/realTimeSlice';
import { showSnackbar } from '../store/slices/uiSlice';
import { getMyNotifications } from '../services/NotificationService';
import { getMyMessages } from '../services/MessageService';
import type { MessageResponse, NotificationResponse } from '../types/api.types';

/**
 * T5 · Abre una conexion STOMP mientras haya sesion.
 * Al conectar (o reconectar) trae del backend lo que llego mientras el usuario no estaba
 * conectado; despues las notificaciones y mensajes nuevos llegan en vivo.
 */
export default function useRealTime() {
  const { user, isAuthenticated } = useAuth();
  const dispatch = useAppDispatch();
  const userId = user?.idUser;

  useEffect(() => {
    if (!isAuthenticated || !userId) {
      dispatch(resetRealTime());
      return;
    }

    const loadPending = async () => {
      const [notificationsRes, messagesRes] = await Promise.all([getMyNotifications(), getMyMessages()]);
      if (!notificationsRes.error && notificationsRes.data) {
        dispatch(setNotifications(notificationsRes.data));
      }
      if (!messagesRes.error && messagesRes.data) {
        dispatch(setMessages(messagesRes.data));
      }
    };

    const client = createStompClient(
      [
        {
          destination: '/user/queue/notifications',
          onMessage: (body) => {
            const notification = body as NotificationResponse;
            dispatch(addNotification(notification));
            // Los mensajes ya se ven en su propia pantalla y en el contador; los eventos se anuncian.
            if (notification.type !== 'NEW_MESSAGE') {
              dispatch(showSnackbar({ message: notification.message, severity: 'info' }));
            }
          },
        },
        {
          destination: '/user/queue/messages',
          onMessage: (body) => {
            dispatch(addMessages([body as MessageResponse]));
          },
        },
      ],
      () => {
        dispatch(setConnected(true));
        void loadPending();
      },
    );
    client.onWebSocketClose = () => {
      dispatch(setConnected(false));
    };

    // Si el WebSocket no esta disponible, las notificaciones pendientes igual se muestran.
    void loadPending();
    client.activate();

    return () => {
      void client.deactivate();
      dispatch(setConnected(false));
    };
  }, [isAuthenticated, userId, dispatch]);
}
