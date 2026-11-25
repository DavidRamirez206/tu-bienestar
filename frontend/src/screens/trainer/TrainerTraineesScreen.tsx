import React, { useEffect, useState } from 'react';
import {
  Box,
  Typography,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Avatar,
  IconButton,
  Button,
  CircularProgress,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  List,
  ListItem,
  ListItemText,
  Divider,
  Stack,
  Chip,
  TextField
} from '@mui/material';
import VisibilityIcon from '@mui/icons-material/Visibility';
import TipsAndUpdatesIcon from '@mui/icons-material/TipsAndUpdates';
import PersonIcon from '@mui/icons-material/Person';
import AddAlertIcon from '@mui/icons-material/AddAlert';
import { getAssignmentsByTrainer } from '../../services/AssignmentService';
import { getProgressByUserId } from '../../services/ProgressService';
import { getAllExercises } from '../../services/ExerciseService';
import { createNotification } from '../../services/NotificationService';
import type { AssignmentResponse, ProgressResponse, ExerciseResponse } from '../../types/api.types';
import useAuth from '../../hooks/useAuth';
import { useNavigate } from 'react-router-dom';

const TrainerTraineesScreen: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [trainees, setTrainees] = useState<AssignmentResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Progress modal state
  const [selectedTrainee, setSelectedTrainee] = useState<AssignmentResponse | null>(null);
  const [traineeProgress, setTraineeProgress] = useState<ProgressResponse[]>([]);
  const [exercises, setExercises] = useState<ExerciseResponse[]>([]);
  const [loadingProgress, setLoadingProgress] = useState(false);

  // Alert modal state
  const [alertTarget, setAlertTarget] = useState<AssignmentResponse | null>(null);
  const [alertMessage, setAlertMessage] = useState('');
  const [sendingAlert, setSendingAlert] = useState(false);

    const fetchTrainees = async () => {
        if (!user?.idUser) {return;}
        setLoading(true);
        const result = await getAssignmentsByTrainer(user.idUser);
        if (result.error) {
            setError(result.message || 'Error al cargar alumnos');
        } else if (result.data) {
            setTrainees(result.data);
        }
        setLoading(false);
    };

    useEffect(() => {
        void (async () => { await fetchTrainees(); })();
        void getAllExercises().then(res => {
            if (res.data) {setExercises(res.data);}
        });
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [user?.idUser]);

  const handleViewProgress = async (trainee: AssignmentResponse) => {
    setSelectedTrainee(trainee);
    setLoadingProgress(true);
    const result = await getProgressByUserId(trainee.userId);
    if (!result.error && result.data) {
      setTraineeProgress(result.data);
    }
    setLoadingProgress(false);
  };

  const handleSendAlert = async () => {
    if (!alertTarget || !alertMessage.trim() || !user) {return;}
    setSendingAlert(true);
    const res = await createNotification({
        userTargetId: alertTarget.userId,
        userSourceId: user.idUser,
        type: 'TRAINER_ALERT',
        message: `Mensaje de tu entrenador (${user.firstName}): ${alertMessage}`,
        referenceId: user.idUser,
        referenceType: 'TRAINER',
        dateSent: new Date().toISOString(),
        isRead: false,
    });

    if (res.error) {
        alert(res.message);
    } else {
        alert('Alerta enviada con éxito.');
        setAlertTarget(null);
        setAlertMessage('');
    }
    setSendingAlert(false);
  };

  const getExerciseName = (id: number) => {
      return exercises.find(ex => ex.idExercise === id)?.name || `Ejercicio #${id}`;
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
        <CircularProgress thickness={5} />
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 4, fontWeight: 800 }}>
        Mis Alumnos Asignados
      </Typography>

      {error && <Alert severity="error" sx={{ mb: 3 }}>{error}</Alert>}

      {trainees.length === 0 ? (
        <Paper sx={{ p: 4, textAlign: 'center', borderRadius: 4 }}>
          <Typography color="text.secondary">No tienes alumnos asignados actualmente.</Typography>
        </Paper>
      ) : (
        <TableContainer component={Paper} sx={{ borderRadius: 4, boxShadow: '0 4px 20px 0 rgba(0,0,0,0.05)' }}>
          <Table>
            <TableHead sx={{ bgcolor: 'rgba(83, 83, 238, 0.05)' }}>
              <TableRow>
                <TableCell sx={{ fontWeight: 700 }}>Alumno</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Fecha de Asignación</TableCell>
                <TableCell align="right" sx={{ fontWeight: 700 }}>Acciones</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {trainees.map((trainee) => (
                <TableRow key={trainee.userId} hover>
                  <TableCell>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                      <Avatar sx={{ bgcolor: 'primary.light' }}>
                        <PersonIcon />
                      </Avatar>
                      <Box>
                        <Typography sx={{ fontWeight: 600 }}>
                          {trainee.userFirstName} {trainee.userLastName}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          ID: {trainee.userId}
                        </Typography>
                      </Box>
                    </Box>
                  </TableCell>
                  <TableCell>
                    {new Date(trainee.assignmentDate).toLocaleDateString()}
                  </TableCell>
                  <TableCell align="right">
                    <Stack direction="row" spacing={1} sx={{ justifyContent: 'flex-end' }}>
                      <IconButton color="info" title="Enviar Alerta" onClick={() => setAlertTarget(trainee)}>
                        <AddAlertIcon />
                      </IconButton>
                      <Button
                        variant="outlined"
                        size="small"
                        startIcon={<VisibilityIcon />}
                        onClick={() => handleViewProgress(trainee)}
                        sx={{ borderRadius: 2 }}
                      >
                        Ver Progreso
                      </Button>
                      <Button
                        variant="contained"
                        size="small"
                        color="secondary"
                        startIcon={<TipsAndUpdatesIcon />}
                        onClick={() => navigate('/recommendations', { state: { userId: trainee.userId } })}
                        sx={{ borderRadius: 2 }}
                      >
                        Recomendar
                      </Button>
                    </Stack>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* Progress Dialog */}
      <Dialog 
        open={!!selectedTrainee} 
        onClose={() => setSelectedTrainee(null)}
        fullWidth
        maxWidth="md"
      >
        <DialogTitle sx={{ fontWeight: 700 }}>
          Progreso de {selectedTrainee?.userFirstName} {selectedTrainee?.userLastName}
        </DialogTitle>
        <DialogContent dividers>
          {loadingProgress ? (
            <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
              <CircularProgress />
            </Box>
          ) : traineeProgress.length === 0 ? (
            <Typography sx={{ py: 2, textAlign: 'center' }} color="text.secondary">
              Este alumno no tiene registros de progreso todavía.
            </Typography>
          ) : (
            <List>
              {traineeProgress.map((entry, idx) => (
                <React.Fragment key={entry.idProgress}>
                  <ListItem sx={{ py: 2 }}>
                    <ListItemText
                      primary={
                        <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>
                          {getExerciseName(entry.exerciseId)}
                        </Typography>
                      }
                      secondary={
                        <Stack direction="row" spacing={1} sx={{ mt: 1 }}>
                          <Chip size="small" label={`${entry.reps} Reps`} />
                          {entry.weightKg ? <Chip size="small" label={`${entry.weightKg} kg`} variant="outlined" /> : null}
                          {entry.durationMin ? <Chip size="small" label={`${entry.durationMin} min`} variant="outlined" /> : null}
                          <Typography variant="caption" sx={{ alignSelf: 'center', ml: 1 }}>
                            {new Date(entry.dateLogged).toLocaleString()}
                          </Typography>
                        </Stack>
                      }
                    />
                  </ListItem>
                  {idx < traineeProgress.length - 1 && <Divider />}
                </React.Fragment>
              ))}
            </List>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setSelectedTrainee(null)}>Cerrar</Button>
        </DialogActions>
      </Dialog>

      {/* Alert Dialog */}
      <Dialog open={!!alertTarget} onClose={() => setAlertTarget(null)} fullWidth maxWidth="xs">
          <DialogTitle sx={{ fontWeight: 700 }}>Enviar Alerta a {alertTarget?.userFirstName}</DialogTitle>
          <DialogContent>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                  Esta notificación llegará instantáneamente al usuario.
              </Typography>
              <TextField
                  fullWidth
                  multiline
                  rows={3}
                  placeholder="Ej: Recuerda hidratarte bien hoy o No olvides tu sesión de las 4pm."
                  value={alertMessage}
                  onChange={(e) => setAlertMessage(e.target.value)}
              />
          </DialogContent>
          <DialogActions sx={{ p: 2 }}>
              <Button onClick={() => setAlertTarget(null)}>Cancelar</Button>
              <Button 
                variant="contained" 
                onClick={handleSendAlert} 
                disabled={!alertMessage.trim() || sendingAlert}
              >
                  {sendingAlert ? <CircularProgress size={20} /> : 'Enviar'}
              </Button>
          </DialogActions>
      </Dialog>
    </Box>
  );
};

export default TrainerTraineesScreen;
