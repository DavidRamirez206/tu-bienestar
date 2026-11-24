import { Fragment, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import Alert from '@mui/material/Alert';
import Avatar from '@mui/material/Avatar';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Divider from '@mui/material/Divider';
import IconButton from '@mui/material/IconButton';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemAvatar from '@mui/material/ListItemAvatar';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import Tooltip from '@mui/material/Tooltip';
import Typography from '@mui/material/Typography';
import EventIcon from '@mui/icons-material/Event';
import PlaceIcon from '@mui/icons-material/Place';
import ChatIcon from '@mui/icons-material/Chat';
import DeleteIcon from '@mui/icons-material/Delete';
import MarkEmailReadIcon from '@mui/icons-material/MarkEmailRead';
import { useAppDispatch } from '../../hooks/useDispatch';
import { useAppSelector } from '../../hooks/useSelector';
import useAuthorities from '../../hooks/useAuthorities';
import {
  deleteNotification,
  markAllNotificationsAsRead,
  markNotificationAsRead,
} from '../../services/NotificationService';
import {
  markAllNotificationsRead,
  markNotificationRead,
  removeNotification,
} from '../../store/slices/realTimeSlice';
import { showSnackbar } from '../../store/slices/uiSlice';
import { formatLocalDateTime } from '../../utils/date.utils';
import { notificationLink } from '../../utils/notification.utils';
import type { NotificationResponse } from '../../types/api.types';

const iconFor = (notification: NotificationResponse) => {
  switch (notification.referenceType) {
    case 'ACTIVITY':
      return <EventIcon />;
    case 'SPACE':
      return <PlaceIcon />;
    default:
      return <ChatIcon />;
  }
};

/** HU15 · Notificaciones: las pendientes y las que llegan en vivo. */
const NotificationsScreen = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const can = useAuthorities();
  const { notifications, connected } = useAppSelector((state) => state.realTime);
  const unread = useMemo(() => notifications.filter((n) => !n.isRead).length, [notifications]);

  const notifyError = (message?: string) => {
    dispatch(showSnackbar({ message: message || 'No se pudo actualizar la notificación', severity: 'error' }));
  };

  const markRead = async (notification: NotificationResponse) => {
    if (notification.isRead) {
      return true;
    }
    const res = await markNotificationAsRead(notification.idNotification);
    if (res.error) {
      notifyError(res.message);
      return false;
    }
    dispatch(markNotificationRead(notification.idNotification));
    return true;
  };

  const open = async (notification: NotificationResponse) => {
    await markRead(notification);
    navigate(notificationLink(notification));
  };

  const markAll = async () => {
    const res = await markAllNotificationsAsRead();
    if (res.error) {
      notifyError(res.message);
      return;
    }
    dispatch(markAllNotificationsRead());
  };

  const remove = async (notification: NotificationResponse) => {
    const res = await deleteNotification(notification.idNotification);
    if (res.error) {
      notifyError(res.message);
      return;
    }
    dispatch(removeNotification(notification.idNotification));
  };

  return (
    <Stack spacing={3}>
      <Stack
        direction={{ xs: 'column', sm: 'row' }}
        spacing={2}
        sx={{ justifyContent: 'space-between', alignItems: { sm: 'center' } }}
      >
        <Box>
          <Typography variant="h5" component="h1" sx={{ fontWeight: 800 }}>Notificaciones</Typography>
          <Typography variant="body2" color="text.secondary">
            {unread === 0 ? 'Estás al día.' : `Tienes ${unread} sin leer.`}
          </Typography>
        </Box>
        <Button variant="outlined" onClick={() => void markAll()} disabled={unread === 0}>
          Marcar todas como leídas
        </Button>
      </Stack>

      {!connected && (
        <Alert severity="warning">
          Sin conexión en tiempo real. Verás las notificaciones nuevas cuando se restablezca.
        </Alert>
      )}

      {notifications.length === 0 ? (
        <Alert severity="info">No tienes notificaciones todavía.</Alert>
      ) : (
        <Paper variant="outlined" sx={{ borderRadius: 3, overflow: 'hidden' }}>
          <List disablePadding aria-label="Notificaciones">
            {notifications.map((notification, index) => (
              <Fragment key={notification.idNotification}>
                {index > 0 && <Divider component="li" />}
                <ListItem
                  disablePadding
                  secondaryAction={
                    <Stack direction="row" spacing={0.5}>
                      {!notification.isRead && (
                        <Tooltip title="Marcar como leída">
                          <IconButton aria-label="Marcar como leída" onClick={() => void markRead(notification)}>
                            <MarkEmailReadIcon />
                          </IconButton>
                        </Tooltip>
                      )}
                      {can('DELETE_NOTIFICATION') && (
                        <Tooltip title="Eliminar">
                          <IconButton aria-label="Eliminar notificación" color="error" onClick={() => void remove(notification)}>
                            <DeleteIcon />
                          </IconButton>
                        </Tooltip>
                      )}
                    </Stack>
                  }
                  sx={{ bgcolor: notification.isRead ? 'transparent' : 'rgba(83, 83, 238, 0.06)' }}
                >
                  <ListItemButton onClick={() => void open(notification)} sx={{ py: 2, pr: 14 }}>
                    <ListItemAvatar>
                      <Avatar sx={{ bgcolor: notification.isRead ? 'grey.400' : 'primary.main' }}>
                        {iconFor(notification)}
                      </Avatar>
                    </ListItemAvatar>
                    <ListItemText
                      primary={notification.message}
                      secondary={`${notification.userSourceName} · ${formatLocalDateTime(notification.dateSent)}`}
                      slotProps={{ primary: { sx: { fontWeight: notification.isRead ? 500 : 800 } } }}
                    />
                  </ListItemButton>
                </ListItem>
              </Fragment>
            ))}
          </List>
        </Paper>
      )}
    </Stack>
  );
};

export default NotificationsScreen;
