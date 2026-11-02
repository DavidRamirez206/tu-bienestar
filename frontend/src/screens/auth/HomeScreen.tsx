import { useEffect, useState, useMemo } from 'react';
import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import Stack from '@mui/material/Stack';
import Divider from '@mui/material/Divider';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import { useNavigate } from 'react-router-dom';
import useAuth from '../../hooks/useAuth';
import { getAllRoutines } from '../../services/RoutineService';
import type { RoutineResponse } from '../../types/api.types';
import { formatDateOnly } from '../../utils/routine.utils';

const HomeScreen = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [routines, setRoutines] = useState<RoutineResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;
    const fetchRoutines = async () => {
      try {
        const res = await getAllRoutines();
        if (isMounted) {
          if (res.error) {
            setError(res.message || 'No se pudieron cargar las rutinas');
          } else if (res.data) {
            setRoutines(res.data);
          }
          setLoading(false);
        }
      } catch {
        if (isMounted) {
          setError('Error de conexión al cargar rutinas');
          setLoading(false);
        }
      }
    };
    fetchRoutines();
    return () => { isMounted = false; };
  }, []);

  const assignedRoutines = useMemo(() => {
    if (!user) {return [];}
    const userId = user.idUser;
    return routines.filter((routine) => routine.userId === Number(userId));
  }, [routines, user]);

  return (
    <Box sx={{ maxWidth: 1000, mx: 'auto' }}>
      <Paper
        elevation={0}
        sx={{
          borderRadius: 4,
          p: { xs: 3, md: 5 },
          backgroundColor: 'white',
          border: '1px solid',
          borderColor: 'divider',
          boxShadow: '0 10px 40px 0 rgba(0,0,0,0.03)',
        }}
      >
        <Box>
          <Typography
            variant="h4"
            sx={{ fontWeight: 800, letterSpacing: '-0.5px' }}
            color="primary"
          >
            Panel de inicio
          </Typography>
          <Typography variant="body1" color="text.secondary" sx={{ mt: 1, fontWeight: 500 }}>
            Visualiza tus rutinas y el estado de tu entrenamiento.
          </Typography>
        </Box>

        <Divider sx={{ my: 4 }} />

        <Typography variant="h6" sx={{ mb: 3, fontWeight: 700, display: 'flex', alignItems: 'center', gap: 1 }}>
          Mis rutinas
          <Box sx={{ width: 8, height: 8, borderRadius: '50%', bgcolor: 'primary.main' }} />
        </Typography>

        {loading ? (
          <Stack sx={{ alignItems: 'center', py: 8 }}>
            <CircularProgress thickness={5} size={50} />
          </Stack>
        ) : error ? (
          <Alert severity="error" variant="outlined" sx={{ borderRadius: 3 }}>{error}</Alert>
        ) : assignedRoutines.length === 0 ? (
          <Paper 
            variant="outlined" 
            sx={{ 
              p: 5, 
              textAlign: 'center', 
              bgcolor: 'rgba(0,0,0,0.01)', 
              borderRadius: 4,
              borderStyle: 'dashed',
              borderWidth: 2
            }}
          >
            <Typography variant="body1" color="text.secondary" sx={{ fontWeight: 500 }}>
              Todavía no tienes rutinas.
            </Typography>
            <Box sx={{ mt: 3 }}>
                <Typography variant="body2" color="text.secondary">
                    Crea tu primera rutina desde la sección Rutinas.
                </Typography>
            </Box>
          </Paper>
        ) : (
          <Stack spacing={2.5}>
            {assignedRoutines.map((routine) => (
              <Paper
                key={routine.idRoutine}
                variant="outlined"
                sx={{ 
                  p: 3, 
                  borderRadius: 4, 
                  transition: '0.2s',
                  cursor: 'pointer',
                  '&:hover': {
                    borderColor: 'primary.main',
                    boxShadow: '0 4px 20px 0 rgba(83, 83, 238, 0.1)',
                    transform: 'translateY(-2px)'
                  }
                }}
                onClick={() => navigate(`/routines/${routine.idRoutine}`)}
              >
                <Stack sx={{ flexDirection: "row", justifyContent: "space-between", alignItems: "center" }}>
                  <Box>
                    <Typography variant="h6" sx={{ fontWeight: 700, color: 'text.primary' }}>
                      {routine.name}
                    </Typography>
                    <Typography
                      variant="body2"
                      color="text.secondary"
                      sx={{ mt: 0.5, maxWidth: '600px', fontWeight: 500 }}
                    >
                      {routine.description || "Sin descripción adicional"}
                    </Typography>
                  </Box>
                  <Box sx={{ textAlign: 'right' }}>
                    <Typography variant="caption" sx={{ fontWeight: 600, color: 'text.disabled', textTransform: 'uppercase' }}>
                      Creada el
                    </Typography>
                    <Typography variant="body2" sx={{ fontWeight: 600, color: 'primary.main' }}>
                      {formatDateOnly(routine.creationDate)}
                    </Typography>
                  </Box>
                </Stack>
              </Paper>
            ))}
          </Stack>
        )}
      </Paper>
    </Box>
  );
};

export default HomeScreen;
