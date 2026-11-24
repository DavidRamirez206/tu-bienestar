import { 
  Box, 
  Typography, 
  Paper, 
  List, 
  ListItem, 
  ListItemText, 
  ListItemAvatar, 
  Avatar, 
  CircularProgress, 
  Alert, 
  Divider,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  IconButton,
  FormControl,
  InputLabel,
  Select,
  MenuItem
} from '@mui/material';
import TipsAndUpdatesIcon from '@mui/icons-material/TipsAndUpdates';
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from '@mui/icons-material/Add';
import { getAllRecommendations, createRecommendation, deleteRecommendation } from '../../services/RecommendationService';
import { getAssignmentsByTrainer } from '../../services/AssignmentService';
import type { RecommendationResponse, RecommendationRequest, AssignmentResponse } from '../../types/api.types';
import useAuth from '../../hooks/useAuth';
import React, {useEffect, useState} from "react";

const RecommendationsScreen: React.FC = () => {
  const [recommendations, setRecommendations] = useState<RecommendationResponse[]>([]);
  const [trainees, setTrainees] = useState<AssignmentResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { user } = useAuth();
  const isAdmin = user?.authorities?.includes('ADMIN');
  const isTrainer = user?.authorities?.includes('TRAINER');

  // Dialog state
  const [open, setOpen] = useState(false);
  const [formData, setFormData] = useState<RecommendationRequest>({
    trainerId: user?.idUser || 0,
    userId: 0,
    description: '',
    dateCreated: new Date().toISOString().split('T')[0]
  });

  const loadData = async () => {
    setLoading(true);
    const result = await getAllRecommendations();
    if (result.error) {
      setError(result.message || 'Error al cargar recomendaciones');
    } else if (result.data) {
      setRecommendations(result.data);
    }

    if (isTrainer && user?.idUser) {
      const traineesResult = await getAssignmentsByTrainer(user.idUser);
      if (!traineesResult.error && traineesResult.data) {
        setTrainees(traineesResult.data);
      }
    }
    setLoading(false);
  };

  useEffect(() => {
    void (async () => { await loadData(); })();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);


  const handleOpen = () => {
    setFormData({
      trainerId: user?.idUser || 0,
      userId: 0,
      description: '',
      dateCreated: new Date().toISOString().split('T')[0]
    });
    setOpen(true);
  };

  const handleClose = () => setOpen(false);

  const handleSubmit = async () => {
    if (!formData.userId) {
        alert("Selecciona un usuario");
        return;
    }
    const result = await createRecommendation(formData);
    if (result.error) {
      alert(result.message);
    } else {
      handleClose();
      await loadData();
    }
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('¿Deseas eliminar esta recomendación?')) {
      const result = await deleteRecommendation(id);
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
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Typography variant="h4" sx={{ fontWeight: 'bold' }}>
          Recomendaciones
        </Typography>
        {(isAdmin || isTrainer) && (
          <Button variant="contained" startIcon={<AddIcon />} onClick={handleOpen}>
            Nueva Recomendación
          </Button>
        )}
      </Box>

      {error && <Alert severity="error" sx={{ mb: 3 }}>{error}</Alert>}

      <Paper sx={{ borderRadius: 4, overflow: 'hidden' }}>
        <List sx={{ p: 0 }}>
          {recommendations.length === 0 ? (
            <ListItem sx={{ py: 4, justifyContent: 'center' }}>
              <Typography color="text.secondary">No hay recomendaciones todavía.</Typography>
            </ListItem>
          ) : (
            recommendations.map((rec, index) => (
              <React.Fragment key={rec.idRecommendation}>
                <ListItem 
                  sx={{ py: 3 }}
                  secondaryAction={
                    (isAdmin || (isTrainer && rec.trainerId === user?.idUser)) && (
                      <IconButton edge="end" color="error" onClick={() => handleDelete(rec.idRecommendation)}>
                        <DeleteIcon />
                      </IconButton>
                    )
                  }
                >
                  <ListItemAvatar>
                    <Avatar sx={{ bgcolor: 'secondary.main' }}>
                      <TipsAndUpdatesIcon />
                    </Avatar>
                  </ListItemAvatar>
                  <ListItemText
                      primary={
                        <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                          {rec.description}
                        </Typography>
                      }
                      secondary={
                        <Box component="span" sx={{ mt: 1, display: 'block' }}>
                          <Typography variant="body2" color="text.secondary" component="span">
                            Enviada el: {new Date(rec.dateCreated).toLocaleDateString()} | Para Usuario ID: {rec.userId}
                          </Typography>
                        </Box>
                      }
                      slotProps={{
                        secondary: { component: 'span' }
                      }}
                  />
                </ListItem>
                {index < recommendations.length - 1 && <Divider />}
              </React.Fragment>
            ))
          )}
        </List>
      </Paper>

      {/* Create Dialog */}
      <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
        <DialogTitle>Nueva Recomendación</DialogTitle>
        <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 2 }}>
          {isTrainer ? (
            <FormControl fullWidth>
              <InputLabel>Usuario Asignado</InputLabel>
              <Select
                value={formData.userId || ''}
                label="Usuario Asignado"
                onChange={(e) => setFormData({ ...formData, userId: Number(e.target.value) })}
              >
                {trainees.map((t) => (
                  <MenuItem key={t.userId} value={t.userId}>
                    {t.userFirstName} {t.userLastName}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          ) : (
            <TextField
              label="ID Usuario"
              type="number"
              fullWidth
              value={formData.userId || ''}
              onChange={(e) => setFormData({ ...formData, userId: Number(e.target.value) })}
            />
          )}
          <TextField
            label="Recomendación"
            multiline
            rows={4}
            fullWidth
            value={formData.description}
            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancelar</Button>
          <Button onClick={handleSubmit} variant="contained" color="primary">
            Enviar
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default RecommendationsScreen;
