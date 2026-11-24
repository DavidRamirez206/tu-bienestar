import type { NotificationResponse } from '../types/api.types';

/** Pantalla a la que lleva cada notificacion (HU15: "llego a su detalle"). */
export const notificationLink = (notification: NotificationResponse): string => {
  switch (notification.referenceType) {
    case 'ACTIVITY':
      return `/activities?activity=${notification.referenceId}`;
    case 'SPACE':
      return '/activities?tab=spaces';
    case 'MESSAGE':
      return notification.userSourceId ? `/messages?contact=${notification.userSourceId}` : '/messages';
    default:
      return '/notifications';
  }
};
