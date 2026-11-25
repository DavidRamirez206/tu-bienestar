import { useCallback, useEffect, useState } from "react";
import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Chip from "@mui/material/Chip";
import CircularProgress from "@mui/material/CircularProgress";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import Grid from "@mui/material/Grid";
import Stack from "@mui/material/Stack";
import TextField from "@mui/material/TextField";
import Typography from "@mui/material/Typography";
import EntityCard from "../../components/common/EntityCard";
import ConfirmDialog from "../../components/common/ConfirmDialog";
import { createTrainer, getTrainers, setUserActive, updateUser } from "../../services/UserService";
import { useAppDispatch } from "../../hooks/useDispatch";
import useAuthorities from "../../hooks/useAuthorities";
import { showSnackbar } from "../../store/slices/uiSlice";
import type { UserResponse } from "../../types/api.types";

type FormState = {
  firstName: string;
  lastName: string;
  institutionalEmail: string;
  age: string;
  password: string;
};

const EMPTY_FORM: FormState = {
  firstName: "",
  lastName: "",
  institutionalEmail: "",
  age: "",
  password: "",
};

const fullName = (trainer: UserResponse) => `${trainer.firstName} ${trainer.lastName}`;

const isActive = (trainer: UserResponse) => trainer.active !== false;

const hasAge = (trainer: UserResponse) => trainer.age !== null && trainer.age !== undefined;

const TrainersManagementScreen = () => {
  const dispatch = useAppDispatch();
  const can = useAuthorities();

  const [trainers, setTrainers] = useState<UserResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [search, setSearch] = useState("");

  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [editing, setEditing] = useState<UserResponse | null>(null);
  const [formOpen, setFormOpen] = useState(false);
  const [saving, setSaving] = useState(false);
  const [toDeactivate, setToDeactivate] = useState<UserResponse | null>(null);

  const load = useCallback(async () => {
    const res = await getTrainers();
    setError(res.error ? res.message || "No se pudieron cargar los entrenadores" : null);
    if (!res.error) {
      setTrainers(res.data ?? []);
    }
    setLoading(false);
  }, []);

  useEffect(() => {
    void (async () => {
      await load();
    })();
  }, [load]);

  const notify = (message: string, severity: "success" | "error") => {
    dispatch(showSnackbar({ message, severity }));
  };

  const handleNew = () => {
    setEditing(null);
    setForm(EMPTY_FORM);
    setFormOpen(true);
  };

  const handleEdit = (trainer: UserResponse) => {
    setEditing(trainer);
    setForm({
      firstName: trainer.firstName,
      lastName: trainer.lastName,
      institutionalEmail: trainer.institutionalEmail,
      age: hasAge(trainer) ? String(trainer.age) : "",
      password: "",
    });
    setFormOpen(true);
  };

  const handleCloseForm = () => setFormOpen(false);

  // Al crear se pide la contraseña; al editar solo cambian los datos.
  const canSave =
    form.firstName.trim().length > 0 &&
    form.lastName.trim().length > 0 &&
    form.institutionalEmail.trim().length > 0 &&
    (editing !== null || form.password.trim().length > 0);

  const handleSave = async () => {
    if (!canSave) {
      return;
    }
    setSaving(true);
    const data = {
      firstName: form.firstName.trim(),
      lastName: form.lastName.trim(),
      institutionalEmail: form.institutionalEmail.trim(),
      age: form.age.trim() ? Number(form.age) : undefined,
    };
    const res = editing
      ? await updateUser(editing.idUser, data)
      : await createTrainer({ ...data, password: form.password });
    setSaving(false);

    if (res.error) {
      notify(res.message || "No se pudo guardar el entrenador", "error");
      return;
    }
    notify(editing ? "Datos del entrenador actualizados" : "Entrenador creado", "success");
    setFormOpen(false);
    await load();
  };

  const handleToggleActive = async (trainer: UserResponse, active: boolean) => {
    setToDeactivate(null);
    const res = await setUserActive(trainer.idUser, active);
    if (res.error) {
      notify(res.message || "No se pudo cambiar el estado del entrenador", "error");
    } else {
      notify(`${fullName(trainer)} quedó ${active ? "activo" : "inactivo"}`, "success");
    }
    await load();
  };

  const term = search.trim().toLowerCase();
  const visible = trainers.filter(
    (trainer) =>
      !term ||
      fullName(trainer).toLowerCase().includes(term) ||
      trainer.institutionalEmail.toLowerCase().includes(term),
  );

  if (loading) {
    return (
      <Box sx={{ display: "flex", justifyContent: "center", py: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Stack spacing={3}>
      <Box>
        <Typography variant="h5" sx={{ fontWeight: 800 }}>Gestionar entrenadores</Typography>
        <Typography variant="body2" color="text.secondary">
          Crea y edita entrenadores, y revisa el estado de cada uno. Un entrenador inactivo no puede iniciar sesión,
          pero conserva sus alumnos y su historial.
        </Typography>
      </Box>

      {error && <Alert severity="error">{error}</Alert>}

      <Stack direction="row" spacing={2} useFlexGap sx={{ flexWrap: "wrap", alignItems: "center" }}>
        <TextField
          label="Buscar entrenador"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          sx={{ maxWidth: 400, flexGrow: 1 }}
        />
        {can("CREATE_USER") && (
          <Button variant="contained" onClick={handleNew}>Nuevo entrenador</Button>
        )}
      </Stack>

      {visible.length === 0 ? (
        <Alert severity="info">No hay entrenadores para mostrar.</Alert>
      ) : (
        <Grid container spacing={2}>
          {visible.map((trainer) => (
            <Grid key={trainer.idUser} size={{ xs: 12, sm: 6, lg: 4 }}>
              <EntityCard>
                <Stack direction="row" spacing={1} sx={{ justifyContent: "space-between", alignItems: "flex-start" }}>
                  <Typography variant="h6" sx={{ fontWeight: 700 }}>{fullName(trainer)}</Typography>
                  <Chip
                    size="small"
                    color={isActive(trainer) ? "success" : "default"}
                    label={isActive(trainer) ? "Activo" : "Inactivo"}
                  />
                </Stack>
                <Typography variant="body2" color="text.secondary" sx={{ overflowWrap: "anywhere" }}>
                  {trainer.institutionalEmail}
                </Typography>
                {hasAge(trainer) && (
                  <Typography variant="body2"><b>Edad:</b> {trainer.age}</Typography>
                )}
                <Stack direction="row" spacing={1} useFlexGap sx={{ flexWrap: "wrap", pt: 1 }}>
                  {can("EDIT_USER") && (
                    <Button variant="outlined" onClick={() => handleEdit(trainer)}>Editar</Button>
                  )}
                  {can("EDIT_USER") &&
                    (isActive(trainer) ? (
                      <Button color="error" onClick={() => setToDeactivate(trainer)}>Desactivar</Button>
                    ) : (
                      <Button variant="contained" onClick={() => void handleToggleActive(trainer, true)}>
                        Activar
                      </Button>
                    ))}
                </Stack>
              </EntityCard>
            </Grid>
          ))}
        </Grid>
      )}

      <Dialog open={formOpen} onClose={handleCloseForm} fullWidth maxWidth="sm">
        <DialogTitle>{editing ? "Editar entrenador" : "Nuevo entrenador"}</DialogTitle>
        <DialogContent sx={{ display: "flex", flexDirection: "column", gap: 2, pt: 2 }}>
          <TextField
            label="Nombre"
            required
            value={form.firstName}
            onChange={(e) => setForm({ ...form, firstName: e.target.value })}
          />
          <TextField
            label="Apellido"
            required
            value={form.lastName}
            onChange={(e) => setForm({ ...form, lastName: e.target.value })}
          />
          <TextField
            label="Correo institucional"
            required
            value={form.institutionalEmail}
            onChange={(e) => setForm({ ...form, institutionalEmail: e.target.value })}
            helperText="Debe terminar en @icesi.edu.co o @u.icesi.edu.co"
          />
          <TextField
            label="Edad"
            type="number"
            value={form.age}
            onChange={(e) => setForm({ ...form, age: e.target.value })}
          />
          {!editing && (
            <TextField
              label="Contraseña"
              type="password"
              required
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
            />
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseForm}>Cancelar</Button>
          <Button variant="contained" disabled={!canSave || saving} onClick={() => void handleSave()}>
            {editing ? "Guardar" : "Crear"}
          </Button>
        </DialogActions>
      </Dialog>

      <ConfirmDialog
        open={toDeactivate !== null}
        title="Desactivar entrenador"
        message={`¿Seguro que quieres desactivar a ${toDeactivate ? fullName(toDeactivate) : ""}? No podrá iniciar sesión hasta que lo actives de nuevo.`}
        confirmLabel="Desactivar"
        onConfirm={() => void handleToggleActive(toDeactivate as UserResponse, false)}
        onClose={() => setToDeactivate(null)}
      />
    </Stack>
  );
};

export default TrainersManagementScreen;
