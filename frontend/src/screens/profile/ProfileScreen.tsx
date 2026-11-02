import { useEffect, useState, useCallback } from "react";
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Divider,
  Snackbar,
  Stack,
  Typography,
  Paper,
  Avatar
} from "@mui/material";
import Grid from '@mui/material/Grid';
import EditIcon from "@mui/icons-material/Edit";
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import { useNavigate } from "react-router-dom";
import { getMyProfile } from "../../services/UserService";
import useAuth from "../../hooks/useAuth";
import type { UserResponse } from "../../types/api.types";

const ProfileScreen = () => {
  const navigate = useNavigate();
  const { user: authUser } = useAuth();

  const [user, setUser] = useState<UserResponse | null>(null);
  const [loading, setLoading] = useState(true);

  const [snackbar, setSnackbar] = useState({
    open: false,
    message: "",
    severity: "success" as "success" | "error",
  });

  const showSnackbar = useCallback((message: string, severity: "success" | "error") => {
    setSnackbar({ open: true, message, severity });
  }, []);

  useEffect(() => {
    let isMounted = true;

    const loadProfile = async () => {
      const userId = authUser?.idUser ? Number(authUser.idUser) : null;
  
      if (!userId) {
        setLoading(false);
        return;
      }
  
      try {
        const res = await getMyProfile(userId);
        if (res.error) {
          showSnackbar(res.message || "No se pudo cargar el perfil", "error");
        } else if (isMounted) {
          setUser(res.data ?? null);
        }
      } catch {
        showSnackbar("No se pudo cargar el perfil", "error");
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    };

    void loadProfile();

    return () => {
      isMounted = false;
    };
  }, [authUser, showSnackbar]);

  if (loading) {
    return (
      <Stack sx={{ alignItems: "center", justifyContent: "center", py: 8 }}>
        <CircularProgress thickness={5} />
      </Stack>
    );
  }

  const displayUser = user || (authUser ? {
    firstName: authUser?.firstName.split(' ')[0] || 'Usuario',
    lastName: authUser?.lastName.split(' ').slice(1).join(' ') || '',
    institutionalEmail: authUser?.institutionalEmail,
    age: 0,
    idUser: Number(authUser?.idUser) || 0
  } : null);

  if (!displayUser) {
    return (
      <Box sx={{ maxWidth: 600, mx: 'auto', mt: 4 }}>
        <Alert severity="error" variant="outlined" sx={{ borderRadius: 3 }}>
          No fue posible obtener la información del perfil.
        </Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ maxWidth: 800, mx: 'auto' }}>
      {!user && authUser?.idUser && (
        <Alert severity="warning" variant="outlined" sx={{ mb: 3, borderRadius: 3 }}>
          Mostrando información limitada de la sesión. Algunos detalles pueden no estar disponibles.
        </Alert>
      )}
      
      <Paper 
        elevation={0} 
        sx={{ 
          p: { xs: 3, md: 5 }, 
          borderRadius: 5, 
          border: '1px solid',
          borderColor: 'divider',
          backgroundColor: 'white',
          boxShadow: '0 10px 40px 0 rgba(0,0,0,0.03)'
        }}
      >
        <Stack spacing={3}>
          <Stack
            direction={{ xs: "column", md: "row" }}
            sx={{
              justifyContent: "space-between",
              alignItems: { xs: "flex-start", md: "center" },
              gap: 3,
            }}
          >
            <Stack direction="row" spacing={3} sx={{ alignItems: 'center', mb: 3 }}>
              <Avatar 
                sx={{ 
                  width: 80, 
                  height: 80, 
                  bgcolor: 'primary.main',
                  boxShadow: '0 8px 16px 0 rgba(83, 83, 238, 0.2)'
                }}
              >
                <AccountCircleIcon sx={{ fontSize: 50 }} />
              </Avatar>
              <Box>
                <Typography variant="h4" sx={{ fontWeight: 800, letterSpacing: '-0.5px' }}>
                  {displayUser.firstName} {displayUser.lastName}
                </Typography>
                <Typography variant="body1" color="text.secondary" sx={{ fontWeight: 500 }}>
                  Información Personal
                </Typography>
              </Box>
            </Stack>

            <Button
              variant="contained"
              startIcon={<EditIcon />}
              onClick={() => navigate("/profile/edit")}
              disabled={!user}
              sx={{ borderRadius: 2, fontWeight: 700, px: 3, py: 1.2, textTransform: 'none' }}
            >
              Editar perfil
            </Button>
          </Stack>

          <Divider />

          <Grid container spacing={4}>
            <Grid size={{ xs: 12, md: 4 }}>
              <Box sx={{ textAlign: 'center' }}>
                <Typography variant="caption" sx={{ fontWeight: 700, color: 'text.disabled', textTransform: 'uppercase', letterSpacing: '1px' }}>
                  Nombre completo
                </Typography>
                <Typography variant="h6" sx={{ fontWeight: 600, mt: 0.5 }}>
                  {displayUser.firstName} {displayUser.lastName}
                </Typography>
              </Box>
            </Grid>

            <Grid size={{ xs: 12, md: 4 }}>
              <Box sx={{ textAlign: 'center' }}>
                <Typography variant="caption" sx={{ fontWeight: 700, color: 'text.disabled', textTransform: 'uppercase', letterSpacing: '1px' }}>
                  Correo electrónico
                </Typography>
                <Typography variant="h6" sx={{ fontWeight: 600, mt: 0.5 }}>
                  {displayUser.institutionalEmail}
                </Typography>
              </Box>
            </Grid>

            <Grid size={{ xs: 12, md: 4 }}>
              <Box sx={{ textAlign: 'center' }}>
                <Typography variant="caption" sx={{ fontWeight: 700, color: 'text.disabled', textTransform: 'uppercase', letterSpacing: '1px' }}>
                  Edad
                </Typography>
                <Typography variant="h6" sx={{ fontWeight: 600, mt: 0.5 }}>
                  {displayUser.age > 0 ? `${displayUser.age} años` : "No especificada"}
                </Typography>
              </Box>
            </Grid>

          </Grid>
        </Stack>
      </Paper>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={4000}
        onClose={() => setSnackbar((prev) => ({ ...prev, open: false }))}
      >
        <Alert
          severity={snackbar.severity}
          variant="filled"
          onClose={() => setSnackbar((prev) => ({ ...prev, open: false }))}
          sx={{ borderRadius: 2 }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default ProfileScreen;
