import React, { useCallback, useEffect, useState } from 'react';
import { Alert, Box, Button, CircularProgress, Stack, Typography } from '@mui/material';
import Grid from '@mui/material/Grid';
import { useNavigate } from 'react-router-dom';
import AddIcon from '@mui/icons-material/Add';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import FitnessCenterIcon from '@mui/icons-material/FitnessCenter';
import { createRoutine, getAllRoutines } from '../../services/RoutineService';
import { createExercise, getAllExercises } from '../../services/ExerciseService';
import { replaceRoutineExercises } from '../../services/RoutineExerciseService';
import type { ExerciseRequest, ExerciseResponse, RoutineRequest, RoutineResponse } from '../../types/api.types';
import { formatDateOnly, mapDraftToRoutineExerciseRequest, type RoutineExerciseDraft } from '../../utils/routine.utils';
import EntityCard from '../../components/common/EntityCard';
import RoutineFormDialog from '../../components/routines/RoutineFormDialog';
import { useAppDispatch } from '../../hooks/useDispatch';
import { showSnackbar } from '../../store/slices/uiSlice';

const RoutinesScreen: React.FC = () => {
    const [routines, setRoutines] = useState<RoutineResponse[]>([]);
    const [exercises, setExercises] = useState<ExerciseResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [openForm, setOpenForm] = useState(false);
    const navigate = useNavigate();
    const dispatch = useAppDispatch();

    const fetchRoutines = useCallback(async () => {
        const result = await getAllRoutines();
        if (result.error) {
            setError('No se pudieron cargar las rutinas.');
        } else if (result.data) {
            setError(null);
            setRoutines(result.data);
        }
        setLoading(false);
    }, []);

    const fetchExercises = useCallback(async () => {
        const result = await getAllExercises();
        if (!result.error && result.data) {
            setExercises(result.data);
        }
    }, []);

    useEffect(() => {
        void (async () => {
            await fetchRoutines();
            await fetchExercises();
        })();
    }, [fetchRoutines, fetchExercises]);

    const handleSaveRoutine = async (payload: RoutineRequest, drafts: RoutineExerciseDraft[]) => {
        const created = await createRoutine(payload);
        if (created.error || !created.data) {
            dispatch(showSnackbar({ message: 'No se pudo crear la rutina.', severity: 'error' }));
            return;
        }

        const routineId = created.data.idRoutine;
        const saved = await replaceRoutineExercises(
            routineId,
            drafts.map((draft) => mapDraftToRoutineExerciseRequest(routineId, draft)),
        );
        dispatch(saved.error
            ? showSnackbar({ message: 'La rutina se creó, pero no se pudieron guardar sus ejercicios.', severity: 'warning' })
            : showSnackbar({ message: 'Rutina creada.', severity: 'success' }));

        setOpenForm(false);
        await fetchRoutines();
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
                <CircularProgress thickness={5} />
            </Box>
        );
    }

    return (
        <Box>
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ justifyContent: 'space-between', alignItems: { sm: 'flex-start' }, mb: 5 }}>
                <Box>
                    <Typography variant="h4" sx={{ fontWeight: 800, letterSpacing: '-1px' }}>
                        Mis rutinas
                    </Typography>
                    <Typography variant="body1" color="text.secondary" sx={{ mt: 1, fontWeight: 500 }}>
                        Crea tu plan de entrenamiento con ejercicios del catálogo o personalizados.
                    </Typography>
                </Box>
                <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    sx={{ borderRadius: 2, fontWeight: 700, px: 3, py: 1.2, flexShrink: 0 }}
                    onClick={() => setOpenForm(true)}
                >
                    Nueva rutina
                </Button>
            </Stack>

            {error && <Alert severity="error" sx={{ mb: 3 }}>{error}</Alert>}

            {!error && routines.length === 0 ? (
                <Alert severity="info" variant="outlined" sx={{ borderRadius: 3 }}>
                    No tienes rutinas creadas. ¡Empieza creando una nueva!
                </Alert>
            ) : (
                <Grid container spacing={4}>
                    {routines.map((routine) => (
                        <Grid key={routine.idRoutine} size={{ xs: 12, sm: 6, md: 4 }}>
                            <EntityCard onClick={() => navigate(`/routines/${routine.idRoutine}`)}>
                                <Typography variant="h6" sx={{ fontWeight: 800, color: 'primary.main', letterSpacing: '-0.3px' }}>
                                    {routine.name}
                                </Typography>
                                <Typography variant="body2" color="text.secondary" sx={{ flexGrow: 1, minHeight: '3em', fontWeight: 500 }}>
                                    {routine.description || 'Sin descripción disponible.'}
                                </Typography>
                                <Stack spacing={1} sx={{ mt: 3 }}>
                                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                        <CalendarMonthIcon fontSize="small" sx={{ color: 'text.disabled' }} />
                                        <Typography variant="caption" sx={{ fontWeight: 600, color: 'text.secondary' }}>
                                            {formatDateOnly(routine.creationDate)}
                                        </Typography>
                                    </Box>
                                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                        <FitnessCenterIcon fontSize="small" sx={{ color: 'text.disabled' }} />
                                        <Typography variant="caption" sx={{ fontWeight: 600, color: 'text.secondary' }}>
                                            Ver detalles del entrenamiento
                                        </Typography>
                                    </Box>
                                </Stack>
                            </EntityCard>
                        </Grid>
                    ))}
                </Grid>
            )}

            {openForm && (
                <RoutineFormDialog
                    open
                    routine={null}
                    exercises={exercises}
                    routineExercises={[]}
                    onClose={() => setOpenForm(false)}
                    onSave={handleSaveRoutine}
                    onCreateCustomExercise={handleCreateCustomExercise}
                />
            )}
        </Box>
    );
};

export default RoutinesScreen;
