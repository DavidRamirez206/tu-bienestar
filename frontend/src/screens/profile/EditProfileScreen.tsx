import { useEffect, useState, useCallback } from "react";
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  CircularProgress,
  Snackbar,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import SaveIcon from "@mui/icons-material/Save";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import { useNavigate } from "react-router-dom";
import {
  getMyProfile,
  updateMyProfile,
} from "../../services/UserService";
import useAuth from "../../hooks/useAuth";
import type { UserRequest } from "../../types/api.types";

const EditProfileScreen = () => {
  const navigate = useNavigate();
  const { user: authUser } = useAuth();

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [userId, setUserId] = useState<number | null>(null);
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [institutionalEmail, setInstitutionalEmail] = useState("");
  const [age, setAge] = useState("");

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
      const authUserId = authUser?.idUser;
      
      if (!authUserId) {
        showSnackbar("No se encontró el ID de usuario en la sesión", "error");
        setLoading(false);
        return;
      }
  
      try {
        const res = await getMyProfile(authUserId);
        
        if (res.error || !res.data) {
          showSnackbar(res.message || "No se pudo cargar el perfil", "error");
        } else if (isMounted) {
          const profileData = res.data;
          setUserId(profileData.idUser);
          setFirstName(profileData.firstName);
          setLastName(profileData.lastName);
          setInstitutionalEmail(profileData.institutionalEmail);
          setAge(profileData.age?.toString() ?? "");
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

  const handleSave = async () => {
    if (!userId) {
      showSnackbar("No se encontró el usuario", "error");
      return;
    }

    if (
      !firstName.trim() ||
      !lastName.trim() ||
      !institutionalEmail.trim() ||
      !age.trim()
    ) {
      showSnackbar("Todos los campos son obligatorios", "error");
      return;
    }

    const parsedAge = Number(age);

    if (Number.isNaN(parsedAge) || parsedAge <= 0) {
      showSnackbar("La edad debe ser un número válido", "error");
      return;
    }

    const payload: UserRequest = {
      firstName: firstName.trim(),
      lastName: lastName.trim(),
      institutionalEmail: institutionalEmail.trim(),
      age: parsedAge,
    };

    try {
      setSaving(true);
      const res = await updateMyProfile(userId, payload);
      
      if (res.error) {
        showSnackbar(res.message || "No se pudo actualizar el perfil", "error");
      } else {
        showSnackbar("Perfil actualizado correctamente", "success");
        navigate("/profile");
      }
    } catch {
      showSnackbar("No se pudo actualizar el perfil", "error");
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <Stack
        sx={{
          alignItems: "center",
          justifyContent: "center",
          py: 8,
        }}
      >
        <CircularProgress />
      </Stack>
    );
  }

  if (!userId) {
    return (
      <Box sx={{ p: 4 }}>
        <Alert severity="error" action={
          <Button color="inherit" size="small" onClick={() => navigate("/profile")}>
            Volver
          </Button>
        }>
          No se puede editar el perfil porque no se ha podido identificar al usuario.
        </Alert>
      </Box>
    );
  }

  return (
    <Box>
      <Card sx={{ maxWidth: 720, mx: "auto", borderRadius: 4 }}>
        <CardContent sx={{ p: 4 }}>
          <Stack spacing={3}>
            <Box>
              <Typography variant="h4" sx={{ fontWeight: "bold" }}>
                Editar perfil
              </Typography>
              <Typography variant="body1" color="text.secondary">
                Actualiza tu información personal
              </Typography>
            </Box>

            <TextField
              label="Nombre"
              fullWidth
              value={firstName}
              onChange={(e) => setFirstName(e.target.value)}
            />

            <TextField
              label="Apellido"
              fullWidth
              value={lastName}
              onChange={(e) => setLastName(e.target.value)}
            />

            <TextField
              label="Email institucional"
              fullWidth
              value={institutionalEmail}
              onChange={(e) => setInstitutionalEmail(e.target.value)}
            />

            <TextField
              label="Edad"
              type="number"
              fullWidth
              value={age}
              onChange={(e) => setAge(e.target.value)}
            />

            <Stack direction="row" spacing={2}>
              <Button
                variant="outlined"
                startIcon={<ArrowBackIcon />}
                onClick={() => navigate("/profile")}
                disabled={saving}
              >
                Cancelar
              </Button>

              <Button
                variant="contained"
                startIcon={<SaveIcon />}
                onClick={handleSave}
                disabled={saving}
              >
                Guardar cambios
              </Button>
            </Stack>
          </Stack>
        </CardContent>
      </Card>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={4000}
        onClose={() => setSnackbar((prev) => ({ ...prev, open: false }))}
      >
        <Alert
          severity={snackbar.severity}
          onClose={() => setSnackbar((prev) => ({ ...prev, open: false }))}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default EditProfileScreen;
