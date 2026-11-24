import { createSlice } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';
import type { MessageResponse, NotificationResponse } from '../../types/api.types';

interface RealTimeState {
  connected: boolean;
  notifications: NotificationResponse[];
  messages: MessageResponse[];
}

const initialState: RealTimeState = {
  connected: false,
  notifications: [],
  messages: [],
};

const byDateDesc = (a: NotificationResponse, b: NotificationResponse) =>
  b.dateSent.localeCompare(a.dateSent) || b.idNotification - a.idNotification;

const byDateAsc = (a: MessageResponse, b: MessageResponse) =>
  a.sentAt.localeCompare(b.sentAt) || a.id - b.id;

const realTimeSlice = createSlice({
  name: 'realTime',
  initialState,
  reducers: {
    setConnected: (state, action: PayloadAction<boolean>) => {
      state.connected = action.payload;
    },
    setNotifications: (state, action: PayloadAction<NotificationResponse[]>) => {
      state.notifications = [...action.payload].sort(byDateDesc);
    },
    addNotification: (state, action: PayloadAction<NotificationResponse>) => {
      if (!state.notifications.some((n) => n.idNotification === action.payload.idNotification)) {
        state.notifications.unshift(action.payload);
      }
    },
    markNotificationRead: (state, action: PayloadAction<number>) => {
      const notification = state.notifications.find((n) => n.idNotification === action.payload);
      if (notification) {
        notification.isRead = true;
      }
    },
    markAllNotificationsRead: (state) => {
      state.notifications.forEach((n) => {
        n.isRead = true;
      });
    },
    removeNotification: (state, action: PayloadAction<number>) => {
      state.notifications = state.notifications.filter((n) => n.idNotification !== action.payload);
    },
    setMessages: (state, action: PayloadAction<MessageResponse[]>) => {
      state.messages = [...action.payload].sort(byDateAsc);
    },
    addMessages: (state, action: PayloadAction<MessageResponse[]>) => {
      action.payload.forEach((message) => {
        if (!state.messages.some((m) => m.id === message.id)) {
          state.messages.push(message);
        }
      });
      state.messages.sort(byDateAsc);
    },
    resetRealTime: () => initialState,
  },
});

export const {
  setConnected,
  setNotifications,
  addNotification,
  markNotificationRead,
  markAllNotificationsRead,
  removeNotification,
  setMessages,
  addMessages,
  resetRealTime,
} = realTimeSlice.actions;
export default realTimeSlice.reducer;
