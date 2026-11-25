import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import Alert from '@mui/material/Alert';
import Avatar from '@mui/material/Avatar';
import Badge from '@mui/material/Badge';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Checkbox from '@mui/material/Checkbox';
import CircularProgress from '@mui/material/CircularProgress';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import Divider from '@mui/material/Divider';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormGroup from '@mui/material/FormGroup';
import IconButton from '@mui/material/IconButton';
import List from '@mui/material/List';
import ListItemAvatar from '@mui/material/ListItemAvatar';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import SendIcon from '@mui/icons-material/Send';
import GroupsIcon from '@mui/icons-material/Groups';
import PersonIcon from '@mui/icons-material/Person';
import { useAppDispatch } from '../../hooks/useDispatch';
import { useAppSelector } from '../../hooks/useSelector';
import useAuth from '../../hooks/useAuth';
import useAuthorities from '../../hooks/useAuthorities';
import { getMessageContacts, sendMessage } from '../../services/MessageService';
import { markNotificationAsRead } from '../../services/NotificationService';
import { addMessages, markNotificationRead } from '../../store/slices/realTimeSlice';
import { showSnackbar } from '../../store/slices/uiSlice';
import { formatLocalDateTime } from '../../utils/date.utils';
import type { ContactResponse } from '../../types/api.types';

const MAX_LENGTH = 255;

const fullName = (contact: ContactResponse) => `${contact.firstName} ${contact.lastName}`;

/** HU16 · Mensajes entre el entrenador y sus usuarios asignados. */
const MessagesScreen = () => {
  const dispatch = useAppDispatch();
  const { user } = useAuth();
  const can = useAuthorities();
  const canSend = can('CREATE_MESSAGE');
  const [searchParams, setSearchParams] = useSearchParams();
  const { messages, notifications } = useAppSelector((state) => state.realTime);

  const [contacts, setContacts] = useState<ContactResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [draft, setDraft] = useState('');
  const [sending, setSending] = useState(false);
  const [broadcastOpen, setBroadcastOpen] = useState(false);
  const [broadcastIds, setBroadcastIds] = useState<number[]>([]);
  const [broadcastText, setBroadcastText] = useState('');
  const scrollRef = useRef<HTMLDivElement>(null);

  const myId = user?.idUser;
  const selectedId = Number(searchParams.get('contact')) || contacts[0]?.idUser;
  const selected = contacts.find((contact) => contact.idUser === selectedId) ?? null;
  const trainees = contacts.filter((contact) => contact.relation === 'TRAINEE');

  useEffect(() => {
    void (async () => {
      const res = await getMessageContacts();
      if (res.error) {
        setError(res.message || 'No se pudieron cargar tus contactos');
      } else {
        setContacts(res.data ?? []);
      }
      setLoading(false);
    })();
  }, []);

  const conversation = useMemo(() => {
    if (!selected || !myId) {
      return [];
    }
    return messages.filter((m) =>
      (m.senderId === myId && m.receiverId === selected.idUser)
      || (m.senderId === selected.idUser && m.receiverId === myId));
  }, [messages, selected, myId]);

  const unreadFrom = useCallback((contactId: number) =>
    notifications.filter((n) => !n.isRead && n.type === 'NEW_MESSAGE' && n.userSourceId === contactId),
  [notifications]);

  // Abrir la conversacion cuenta como leer sus avisos de mensaje nuevo.
  useEffect(() => {
    if (!selected) {
      return;
    }
    unreadFrom(selected.idUser).forEach((notification) => {
      void markNotificationAsRead(notification.idNotification).then((res) => {
        if (!res.error) {
          dispatch(markNotificationRead(notification.idNotification));
        }
      });
    });
  }, [selected, unreadFrom, dispatch]);

  useEffect(() => {
    scrollRef.current?.scrollTo({ top: scrollRef.current.scrollHeight });
  }, [conversation.length, selected?.idUser]);

  const send = async (receiverIds: number[], content: string) => {
    const text = content.trim();
    if (!text || receiverIds.length === 0) {
      return false;
    }
    setSending(true);
    const res = await sendMessage({ receiverIds, content: text });
    setSending(false);
    if (res.error) {
      dispatch(showSnackbar({ message: res.message || 'No se pudo enviar el mensaje', severity: 'error' }));
      return false;
    }
    dispatch(addMessages(res.data ?? []));
    return true;
  };

  const handleSend = async () => {
    if (selected && await send([selected.idUser], draft)) {
      setDraft('');
    }
  };

  const handleBroadcast = async () => {
    if (await send(broadcastIds, broadcastText)) {
      dispatch(showSnackbar({ message: `Mensaje enviado a ${broadcastIds.length} usuario(s)`, severity: 'success' }));
      setBroadcastOpen(false);
      setBroadcastIds([]);
      setBroadcastText('');
    }
  };

  const toggleBroadcast = (id: number) => {
    setBroadcastIds((ids) => (ids.includes(id) ? ids.filter((x) => x !== id) : [...ids, id]));
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
        <CircularProgress aria-label="Cargando mensajes" />
      </Box>
    );
  }

  return (
    <Stack spacing={3}>
      <Stack
        direction={{ xs: 'column', sm: 'row' }}
        spacing={2}
        sx={{ justifyContent: 'space-between', alignItems: { sm: 'center' } }}
      >
        <Box>
          <Typography variant="h5" component="h1" sx={{ fontWeight: 800 }}>Mensajes</Typography>
          <Typography variant="body2" color="text.secondary">
            {canSend
              ? 'Escribe a tus usuarios asignados. Reciben el mensaje al instante.'
              : 'Mensajes y alertas de tu entrenador.'}
          </Typography>
        </Box>
        {canSend && trainees.length > 0 && (
          <Button variant="contained" startIcon={<GroupsIcon />} onClick={() => setBroadcastOpen(true)}>
            Mensaje a varios
          </Button>
        )}
      </Stack>

      {error && <Alert severity="error">{error}</Alert>}

      {!error && contacts.length === 0 ? (
        <Alert severity="info">
          {canSend ? 'Todavía no tienes usuarios asignados.' : 'Todavía no tienes un entrenador asignado.'}
        </Alert>
      ) : (
        <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ minHeight: { md: 480 } }}>
          <Paper variant="outlined" sx={{ width: { md: 280 }, flexShrink: 0, borderRadius: 3, overflow: 'hidden' }}>
            <List disablePadding aria-label="Contactos">
              {contacts.map((contact) => {
                const pending = unreadFrom(contact.idUser).length;
                return (
                  <ListItemButton
                    key={contact.idUser}
                    selected={contact.idUser === selected?.idUser}
                    onClick={() => setSearchParams({ contact: String(contact.idUser) })}
                  >
                    <ListItemAvatar>
                      <Badge color="error" badgeContent={pending} overlap="circular">
                        <Avatar sx={{ bgcolor: 'primary.light' }}><PersonIcon /></Avatar>
                      </Badge>
                    </ListItemAvatar>
                    <ListItemText
                      primary={fullName(contact)}
                      secondary={contact.relation === 'TRAINER' ? 'Entrenador' : 'Usuario asignado'}
                    />
                  </ListItemButton>
                );
              })}
            </List>
          </Paper>

          <Paper variant="outlined" sx={{ flexGrow: 1, borderRadius: 3, display: 'flex', flexDirection: 'column', minWidth: 0 }}>
            {selected ? (
              <>
                <Box sx={{ p: 2 }}>
                  <Typography variant="h6" sx={{ fontWeight: 700 }}>{fullName(selected)}</Typography>
                </Box>
                <Divider />
                <Box
                  ref={scrollRef}
                  role="log"
                  aria-live="polite"
                  aria-label={`Conversación con ${fullName(selected)}`}
                  sx={{ flexGrow: 1, overflowY: 'auto', p: 2, maxHeight: 420, minHeight: 240, bgcolor: '#f8f9fb' }}
                >
                  {conversation.length === 0 ? (
                    <Typography color="text.secondary" sx={{ textAlign: 'center', mt: 6 }}>
                      Todavía no hay mensajes en esta conversación.
                    </Typography>
                  ) : (
                    <Stack spacing={1.5}>
                      {conversation.map((message) => {
                        const own = message.senderId === myId;
                        return (
                          <Box
                            key={message.id}
                            sx={{
                              alignSelf: own ? 'flex-end' : 'flex-start',
                              maxWidth: '75%',
                              bgcolor: own ? 'primary.main' : 'white',
                              color: own ? 'primary.contrastText' : 'text.primary',
                              border: own ? 'none' : '1px solid',
                              borderColor: 'divider',
                              borderRadius: 3,
                              px: 2,
                              py: 1,
                            }}
                          >
                            <Typography variant="body1" sx={{ whiteSpace: 'pre-wrap', overflowWrap: 'anywhere' }}>
                              {message.content}
                            </Typography>
                            <Typography variant="caption" sx={{ display: 'block', textAlign: 'right', opacity: 0.8 }}>
                              {formatLocalDateTime(message.sentAt)}
                            </Typography>
                          </Box>
                        );
                      })}
                    </Stack>
                  )}
                </Box>
                {canSend && selected.relation === 'TRAINEE' && (
                  <>
                    <Divider />
                    <Stack direction="row" spacing={1} sx={{ p: 2, alignItems: 'flex-start' }}>
                      <TextField
                        fullWidth
                        size="small"
                        label={`Mensaje para ${selected.firstName}`}
                        value={draft}
                        onChange={(e) => setDraft(e.target.value.slice(0, MAX_LENGTH))}
                        onKeyDown={(e) => {
                          if (e.key === 'Enter' && !e.shiftKey) {
                            e.preventDefault();
                            void handleSend();
                          }
                        }}
                        helperText={`${draft.length}/${MAX_LENGTH}`}
                        disabled={sending}
                      />
                      <IconButton
                        color="primary"
                        aria-label="Enviar mensaje"
                        onClick={() => void handleSend()}
                        disabled={!draft.trim() || sending}
                      >
                        {sending ? <CircularProgress size={24} /> : <SendIcon />}
                      </IconButton>
                    </Stack>
                  </>
                )}
              </>
            ) : (
              <Typography color="text.secondary" sx={{ m: 'auto', p: 4 }}>
                Selecciona un contacto para ver la conversación.
              </Typography>
            )}
          </Paper>
        </Stack>
      )}

      <Dialog open={broadcastOpen} onClose={() => setBroadcastOpen(false)} fullWidth maxWidth="sm">
        <DialogTitle>Mensaje a varios usuarios</DialogTitle>
        <DialogContent>
          <Stack spacing={2} sx={{ pt: 1 }}>
            <FormGroup>
              <FormControlLabel
                control={
                  <Checkbox
                    checked={broadcastIds.length === trainees.length}
                    indeterminate={broadcastIds.length > 0 && broadcastIds.length < trainees.length}
                    onChange={(e) => setBroadcastIds(e.target.checked ? trainees.map((t) => t.idUser) : [])}
                  />
                }
                label="Todos mis usuarios asignados"
              />
              {trainees.map((trainee) => (
                <FormControlLabel
                  key={trainee.idUser}
                  sx={{ ml: 2 }}
                  control={
                    <Checkbox
                      checked={broadcastIds.includes(trainee.idUser)}
                      onChange={() => toggleBroadcast(trainee.idUser)}
                    />
                  }
                  label={fullName(trainee)}
                />
              ))}
            </FormGroup>
            <TextField
              label="Mensaje o alerta"
              multiline
              minRows={3}
              value={broadcastText}
              onChange={(e) => setBroadcastText(e.target.value.slice(0, MAX_LENGTH))}
              helperText={`${broadcastText.length}/${MAX_LENGTH}`}
            />
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setBroadcastOpen(false)}>Cancelar</Button>
          <Button
            variant="contained"
            onClick={() => void handleBroadcast()}
            disabled={sending || broadcastIds.length === 0 || !broadcastText.trim()}
          >
            Enviar
          </Button>
        </DialogActions>
      </Dialog>
    </Stack>
  );
};

export default MessagesScreen;
