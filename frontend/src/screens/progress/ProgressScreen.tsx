import React, { useEffect, useState, useCallback, useMemo } from 'react';
import { 
  Box, 
  Typography, 
  Paper,
  CircularProgress, 
  Alert, 
  Card, 
  CardContent,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  IconButton,
  List,
  ListItem,
  ListItemText,
  Stack,
  Autocomplete,
  Divider
} from '@mui/material';
import Grid from '@mui/material/Grid';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import FitnessCenterIcon from '@mui/icons-material/FitnessCenter';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from '@mui/icons-material/Add';
import { getAllProgress, createProgress, deleteProgress } from '../../services/ProgressService';
import { getAllExercises } from '../../services/ExerciseService';
import type { ProgressResponse, ProgressRequest, ExerciseResponse } from '../../types/api.types';
import useAuth from '../../hooks/useAuth';

const ProgressScreen: React.FC = () => {
  const [progress, setProgress] = useState<ProgressResponse[]>([]);
  const [exercises, setExercises] = useState<ExerciseResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { user } = useAuth();

  // Dialog state
  const [open, setOpen] = useState(false);
  const [formData, setFormData] = useState<ProgressRequest>({
    userId: user?.idUser || 0,
    exerciseId: 0,
    dateLogged: new Date().toISOString(),
    reps: 0,
    weightKg: 0,
    durationMin: 0,
    effortLevel: 5,
    setNumber: 1
  });

  const loadData = useCallback(async () => {
    try {
      await Promise.resolve(); // Satisfy react-hooks/set-state-in-effect
      setLoading(true);
      const [progressRes, exercisesRes] = await Promise.all([
        getAllProgress(),
        getAllExercises()
      ]);

      if (progressRes.error) {
        setError(progressRes.message || 'Error al cargar el progreso');
      } else {
        setProgress(progressRes.data || []);
      }

      if (!exercisesRes.error) {
        setExercises(exercisesRes.data || []);
      }
    } catch {
      setError('Ocurrió un error inesperado al cargar los datos.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    const t = setTimeout(loadData, 0);
    return () => clearTimeout(t);
  }, [loadData]);

  const exerciseMap = useMemo(() => {
    return new Map(exercises.map(ex => [ex.idExercise, ex]));
  }, [exercises]);

  const sortedProgress = useMemo(() => {
    return [...progress].sort((a, b) => new Date(b.dateLogged).getTime() - new Date(a.dateLogged).getTime());
  }, [progress]);

  const handleOpen = () => {
    setFormData({
      userId: user?.idUser || 0,
      exerciseId: 0,
      dateLogged: new Date().toISOString(),
      reps: 0,
      weightKg: 0,
      durationMin: 0,
      effortLevel: 5,
      setNumber: 1
    });
    setOpen(true);
  };

  const handleClose = () => setOpen(false);

  const handleSubmit = async () => {
    if (!formData.exerciseId) {
      alert('Debe seleccionar un ejercicio');
      return;
    }

    const result = await createProgress(formData);
    if (result.error) {
      alert(result.message);
    } else {
      handleClose();
      await loadData();
    }
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('¿Deseas eliminar este registro de progreso?')) {
      const result = await deleteProgress(id);
      if (result.error) {
        alert(result.message);
      } else {
        await loadData();
      }
    }
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
      <Stack direction="row" sx={{ justifyContent: 'space-between', alignItems: 'flex-start', mb: 4 }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 800, letterSpacing: '-1px' }}>
            Mi Progreso
          </Typography>
          <Typography variant="body1" color="text.secondary" sx={{ mt: 0.5, fontWeight: 500 }}>
            Visualiza tus logros y mantén el registro de tus entrenamientos.
          </Typography>
        </Box>
        <Button 
            variant="contained" 
            startIcon={<AddIcon />} 
            onClick={handleOpen}
            sx={{ borderRadius: 2, fontWeight: 700, px: 3, py: 1.2 }}
        >
          Nuevo Registro
        </Button>
      </Stack>

      {error && <Alert severity="error" variant="outlined" sx={{ mb: 3, borderRadius: 3 }}>{error}</Alert>}

      <Grid container spacing={3} sx={{ mb: 5 }}>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card 
            elevation={0}
            sx={{ 
                bgcolor: 'primary.main', 
                color: 'white', 
                borderRadius: 4,
                boxShadow: '0 8px 32px 0 rgba(83, 83, 238, 0.3)'
            }}
          >
            <CardContent sx={{ textAlign: 'center', p: 3 }}>
              <TrendingUpIcon sx={{ fontSize: 40, mb: 1, opacity: 0.9 }} />
              <Typography variant="subtitle1" sx={{ fontWeight: 600, opacity: 0.9 }}>Sesiones Totales</Typography>
              <Typography variant="h2" sx={{ fontWeight: 800 }}>{progress.length}</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card 
            elevation={0}
            sx={{ 
                bgcolor: 'white', 
                color: 'text.primary', 
                borderRadius: 4,
                border: '1px solid',
                borderColor: 'divider',
                boxShadow: '0 4px 20px 0 rgba(0,0,0,0.05)'
            }}
          >
            <CardContent sx={{ textAlign: 'center', p: 3 }}>
              <FitnessCenterIcon sx={{ fontSize: 40, mb: 1, color: 'primary.main' }} />
              <Typography variant="subtitle1" sx={{ fontWeight: 600, color: 'text.secondary' }}>Ejercicios Realizados</Typography>
              <Typography variant="h2" sx={{ fontWeight: 800 }}>
                {new Set(progress.map(p => p.exerciseId)).size}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card 
            elevation={0}
            sx={{ 
                bgcolor: 'white', 
                color: 'text.primary', 
                borderRadius: 4,
                border: '1px solid',
                borderColor: 'divider',
                boxShadow: '0 4px 20px 0 rgba(0,0,0,0.05)'
            }}
          >
            <CardContent sx={{ textAlign: 'center', p: 3 }}>
              <CalendarMonthIcon sx={{ fontSize: 40, mb: 1, color: 'secondary.main' }} />
              <Typography variant="subtitle1" sx={{ fontWeight: 600, color: 'text.secondary' }}>Último Registro</Typography>
              <Typography variant="h4" sx={{ fontWeight: 800, mt: 1.5 }}>
                {progress.length > 0 
                  ? new Date(sortedProgress[0].dateLogged).toLocaleDateString() 
                  : 'Sin registros'}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Typography variant="h5" sx={{ mb: 3, fontWeight: 800 }}>Historial Reciente</Typography>
      <Paper 
        elevation={0}
        sx={{ 
            borderRadius: 4, 
            overflow: 'hidden',
            border: '1px solid',
            borderColor: 'divider',
            boxShadow: '0 4px 20px 0 rgba(0,0,0,0.05)'
        }}
      >
        <Box>
          {progress.length === 0 ? (
            <Box sx={{ py: 8, textAlign: 'center' }}>
                <Typography color="text.secondary" sx={{ fontWeight: 500 }}>No hay registros de progreso disponibles.</Typography>
                <Button variant="text" color="primary" sx={{ mt: 1, fontWeight: 700 }} onClick={handleOpen}>
                    ¡Registra tu primer ejercicio hoy!
                </Button>
            </Box>
          ) : (
            <List disablePadding>
               {sortedProgress.slice(0, 10).map((entry, index) => {
                 const exercise = exerciseMap.get(entry.exerciseId);
                 return (
                    <React.Fragment key={entry.idProgress}>
                        <ListItem 
                            sx={{ px: 3, py: 2 }}
                            secondaryAction={
                                <IconButton edge="end" color="error" onClick={() => handleDelete(entry.idProgress)}>
                                    <DeleteIcon />
                                </IconButton>
                            }
                        >
                            <ListItemText 
                                primary={
                                    <Typography variant="h6" sx={{ fontWeight: 700, color: 'primary.main' }}>
                                        {exercise?.name || `Ejercicio #${entry.exerciseId}`}
                                    </Typography>
                                }
                                secondary={
                                    <Stack direction="row" spacing={2} sx={{ mt: 0.5, alignItems: 'center' }}>
                                        <Typography component="span" variant="body2" color="text.primary" sx={{ fontWeight: 600 }}>
                                            {new Date(entry.dateLogged).toLocaleDateString()}
                                        </Typography>
                                        <Divider orientation="vertical" flexItem />
                                        <Typography component="span" variant="body2">
                                            <strong>{entry.reps}</strong> Reps
                                        </Typography>
                                        {entry.weightKg ? (
                                            <>
                                                <Divider orientation="vertical" flexItem />
                                                <Typography component="span" variant="body2">
                                                    <strong>{entry.weightKg}</strong> kg
                                                </Typography>
                                            </>
                                        ) : null}
                                        {entry.durationMin ? (
                                            <>
                                                <Divider orientation="vertical" flexItem />
                                                <Typography component="span" variant="body2">
                                                    <strong>{entry.durationMin}</strong> min
                                                </Typography>
                                            </>
                                        ) : null}
                                    </Stack>
                                }
                                slotProps={{ secondary: { component: 'div' } }}
                            />
                        </ListItem>
                        {index < sortedProgress.slice(0, 10).length - 1 && <Divider />}
                    </React.Fragment>
                 );
               })}
            </List>
          )}
        </Box>
      </Paper>

      {/* Create Dialog */}
      <Dialog 
        open={open} 
        onClose={handleClose} 
        fullWidth 
        maxWidth="xs"
        sx={{ '& .MuiDialog-paper': { borderRadius: 3 } }}
      >
        <DialogTitle sx={{ fontWeight: 'bold' }}>Registrar Progreso</DialogTitle>
        <DialogContent dividers>
          <Stack spacing={3} sx={{ mt: 1 }}>
            <Autocomplete
                options={exercises}
                getOptionLabel={(option) => `${option.name} (${option.type})`}
                renderInput={(params) => <TextField {...params} label="Seleccionar Ejercicio" fullWidth />}
                onChange={(_, newValue) => setFormData({ ...formData, exerciseId: newValue?.idExercise || 0 })}
            />
            
            <Stack direction="row" spacing={2}>
                <TextField
                    label="Repeticiones"
                    type="number"
                    fullWidth
                    value={formData.reps || ''}
                    onChange={(e) => setFormData({ ...formData, reps: Number(e.target.value) })}
                />
                <TextField
                    label="Peso (kg)"
                    type="number"
                    fullWidth
                    value={formData.weightKg || ''}
                    onChange={(e) => setFormData({ ...formData, weightKg: Number(e.target.value) })}
                />
            </Stack>

            <TextField
                label="Duración (min)"
                type="number"
                fullWidth
                value={formData.durationMin || ''}
                onChange={(e) => setFormData({ ...formData, durationMin: Number(e.target.value) })}
            />
            
            <TextField
                label="Nivel de Esfuerzo (1-10)"
                type="number"
                fullWidth
                slotProps={{ htmlInput: { min: 1, max: 10 } }}
                value={formData.effortLevel}
                onChange={(e) => setFormData({ ...formData, effortLevel: Number(e.target.value) })}
            />
          </Stack>
        </DialogContent>
        <DialogActions sx={{ p: 2.5 }}>
          <Button onClick={handleClose} sx={{ fontWeight: 600 }}>Cancelar</Button>
          <Button onClick={handleSubmit} variant="contained" color="primary" sx={{ borderRadius: 2, fontWeight: 700, px: 3 }}>
            Guardar Registro
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default ProgressScreen;
