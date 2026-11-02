import React, { useCallback, useEffect, useState } from 'react';
import {
  Alert,
  Box,
  Button,
  Chip,
  CircularProgress,
  Divider,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Paper,
  Stack,
  Typography,
} from '@mui/material';
import { useNavigate, useParams } from 'react-router-dom';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import EditIcon from '@mui/icons-material/Edit';
import FitnessCenterIcon from '@mui/icons-material/FitnessCenter';
import { getRoutineById, updateRoutine } from '../../services/RoutineService';
import { createExercise, getAllExercises } from '../../services/ExerciseService';
import { getRoutineExercises, replaceRoutineExercises } from '../../services/RoutineExerciseService';
import type {
  ExerciseRequest,
  ExerciseResponse,
  RoutineExerciseResponse,
  RoutineRequest,
  RoutineResponse,
} from '../../types/api.types';
import { canPublishRoutines, mapDraftToRoutineExerciseRequest, type RoutineExerciseDraft } from '../../utils/routine.utils';
import RoutineFormDialog from '../../components/routines/RoutineFormDialog';
import useAuth from '../../hooks/useAuth';
import { useAppDispatch } from '../../hooks/useDispatch';
import { showSnackbar } from '../../store/slices/uiSlice';

const RoutineDetailScreen: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const routineId = Number(id);
  const [routine, setRoutine] = useState<RoutineResponse | null>(null);
  const [items, setItems] = useState<RoutineExerciseResponse[]>([]);
  const [exercises, setExercises] = useState<ExerciseResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [openForm, setOpenForm] = useState(false);
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { user } = useAuth();

  const fetchRoutine = useCallback(async () => {
    const [routineResult, itemsResult] = await Promise.all([
      getRoutineById(routineId),
      getRoutineExercises(routineId),
    ]);
    if (routineResult.error || !routineResult.data) {
      setError(routineResult.status === 403 ? 'No tienes permiso para ver esta rutina.' : 'No se encontró la rutina.');
    } else {
      setRoutine(routineResult.data);
      setItems(itemsResult.data ?? []);
    }
    setLoading(false);
  }, [routineId]);

  const fetchExercises = useCallback(async () => {
    const result = await getAllExercises();
    if (!result.error && result.data) {
      setExercises(result.data);
    }
  }, []);

  useEffect(() => {
    void (async () => {
      await fetchRoutine();
      await fetchExercises();
    })();
  }, [fetchRoutine, fetchExercises]);

  const handleSave = async (payload: RoutineRequest, drafts: RoutineExerciseDraft[]) => {
    const updated = await updateRoutine(routineId, payload);
    const saved = updated.error
      ? updated
      : await replaceRoutineExercises(
          routineId,
          drafts.map((draft) => mapDraftToRoutineExerciseRequest(routineId, draft)),
        );

    if (saved.error) {
      dispatch(showSnackbar({ message: 'No se pudieron guardar los cambios.', severity: 'error' }));
      return;
    }

    dispatch(showSnackbar({ message: 'Rutina actualizada.', severity: 'success' }));
    setOpenForm(false);
    await fetchRoutine();
  };

  const handleCreateCustomExercise = async (payload: ExerciseRequest) => {
    const result = await createExercise(payload);
    if (result.error || !result.data) {
      dispatch(showSnackbar({ message: 'No se pudo crear el ejercicio.', severity: 'error' }));
      return null;
    }
    await fetchExercises();
    return result.data;
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  const backButton = (
    <Button startIcon={<ArrowBackIcon />} onClick={() => navigate('/routines')}>
      Volver a rutinas
    </Button>
  );

  if (error || !routine) {
    return (
      <Box sx={{ py: 4 }}>
        <Alert severity="error" sx={{ mb: 2 }}>{error ?? 'No se encontró la rutina.'}</Alert>
        {backButton}
      </Box>
    );
  }

  const canEdit = routine.userId === user?.idUser;
  const sortedItems = [...items].sort((a, b) => (a.exerciseOrder ?? 0) - (b.exerciseOrder ?? 0));

  return (
    <Box>
      <Stack direction="row" sx={{ justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        {backButton}
        {canEdit && (
          <Button variant="contained" startIcon={<EditIcon />} onClick={() => setOpenForm(true)}>
            Editar
          </Button>
        )}
      </Stack>

      <Paper elevation={3} sx={{ p: { xs: 2.5, md: 4 }, borderRadius: 4 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: 2, mb: 2 }}>
          <Box>
            <Typography variant="h4" component="h1" sx={{ fontWeight: 'bold', color: 'primary.main' }}>
              {routine.name}
            </Typography>
            <Typography variant="body1" sx={{ mt: 1 }}>
              {routine.description || 'Sin descripción detallada.'}
            </Typography>
          </Box>
          {routine.isPredesigned && (
            <Chip label={`Prediseñada · ${routine.level ?? 'Sin nivel'}`} color="secondary" sx={{ fontWeight: 'bold' }} />
          )}
        </Box>

        <Divider sx={{ my: 4 }} />

        <Typography variant="h5" component="h2" sx={{ fontWeight: 'bold', mb: 3 }}>
          Ejercicios de la rutina
        </Typography>

        {sortedItems.length > 0 ? (
          <List>
            {sortedItems.map((item) => (
              <Paper key={item.exerciseId} variant="outlined" sx={{ mb: 2, borderRadius: 2 }}>
                <ListItem>
                  <ListItemIcon>
                    <FitnessCenterIcon color="primary" />
                  </ListItemIcon>
                  <ListItemText
                    primary={
                      <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                        {item.exerciseName}
                      </Typography>
                    }
                    secondary={`Series: ${item.sets ?? 'N/A'} · Repeticiones: ${item.targetReps ?? 'N/A'} · Tipo: ${item.exerciseType ?? 'N/A'} · Dificultad: ${item.exerciseDifficulty ?? 'N/A'}`}
                  />
                </ListItem>
              </Paper>
            ))}
          </List>
        ) : (
          <Alert severity="warning">Esta rutina aún no tiene ejercicios.</Alert>
        )}
      </Paper>

      {openForm && (
        <RoutineFormDialog
          open
          routine={routine}
          exercises={exercises}
          routineExercises={items}
          canPublish={canPublishRoutines(user?.authorities)}
          onClose={() => setOpenForm(false)}
          onSave={handleSave}
          onCreateCustomExercise={handleCreateCustomExercise}
        />
      )}
    </Box>
  );
};

export default RoutineDetailScreen;
