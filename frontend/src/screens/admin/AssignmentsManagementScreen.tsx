import { useCallback, useEffect, useState } from "react";
import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Chip from "@mui/material/Chip";
import CircularProgress from "@mui/material/CircularProgress";
import Grid from "@mui/material/Grid";
import MenuItem from "@mui/material/MenuItem";
import Stack from "@mui/material/Stack";
import TextField from "@mui/material/TextField";
import Typography from "@mui/material/Typography";
import EntityCard from "../../components/common/EntityCard";
import ConfirmDialog from "../../components/common/ConfirmDialog";
import {
  assignTrainer,
  getAssignments,
  getTrainees,
  getTrainers,
  reassignTrainer,
  removeAssignment,
} from "../../services/AssignmentService";
import { useAppDispatch } from "../../hooks/useDispatch";
import useAuthorities from "../../hooks/useAuthorities";
import { showSnackbar } from "../../store/slices/uiSlice";
import type { AssignmentResponse, UserResponse } from "../../types/api.types";

const fullName = (user: UserResponse) => `${user.firstName} ${user.lastName}`;

const formatDate = (iso: string) => iso.split("-").reverse().join("/");

const AssignmentsManagementScreen = () => {
  const dispatch = useAppDispatch();
  const can = useAuthorities();

  const [trainees, setTrainees] = useState<UserResponse[]>([]);
  const [trainers, setTrainers] = useState<UserResponse[]>([]);
  const [assignments, setAssignments] = useState<AssignmentResponse[]>([]);
  const [selected, setSelected] = useState<Record<number, number | "">>({});
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [toRemove, setToRemove] = useState<AssignmentResponse | null>(null);

  const load = useCallback(async () => {
    const [traineesRes, trainersRes, assignmentsRes] = await Promise.all([
      getTrainees(),
      getTrainers(),
      getAssignments(),
    ]);
    const failed = [traineesRes, trainersRes, assignmentsRes].find((res) => res.error);
    setError(failed ? failed.message || "No se pudieron cargar las asignaciones" : null);
    if (!traineesRes.error) { setTrainees(traineesRes.data ?? []); }
    if (!trainersRes.error) { setTrainers(trainersRes.data ?? []); }
    if (!assignmentsRes.error) { setAssignments(assignmentsRes.data ?? []); }
    setSelected({});
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

  const assignmentOf = (userId: number) =>
    assignments.find((assignment) => assignment.userId === userId);

  const trainerName = (trainerId: number) => {
    const trainer = trainers.find((candidate) => candidate.idUser === trainerId);
    return trainer ? fullName(trainer) : "Entrenador";
  };

  const handleSave = async (trainee: UserResponse) => {
    const trainerId = selected[trainee.idUser];
    if (!trainerId) {
      return;
    }
    const current = assignmentOf(trainee.idUser);
    const request = { userId: trainee.idUser, trainerId };
    const res = current ? await reassignTrainer(request) : await assignTrainer(request);
    if (res.error) {
      notify(res.message || "No se pudo guardar la asignación", "error");
    } else {
      notify(
        `${fullName(trainee)} ${current ? "fue reasignado a" : "quedó asignado a"} ${trainerName(trainerId)}`,
        "success",
      );
    }
    await load();
  };

  const handleRemove = async () => {
    if (!toRemove) {
      return;
    }
    const res = await removeAssignment(toRemove.userId, toRemove.trainerId);
    setToRemove(null);
    notify(res.error ? res.message || "No se pudo quitar la asignación" : "Asignación eliminada", res.error ? "error" : "success");
    await load();
  };

  // Un entrenador inactivo no puede recibir alumnos nuevos (HU9.1).
  const activeTrainers = trainers.filter((trainer) => trainer.active !== false);

  const term = search.trim().toLowerCase();
  const visibleTrainees = trainees.filter((trainee) =>
    !term ||
    fullName(trainee).toLowerCase().includes(term) ||
    trainee.institutionalEmail.toLowerCase().includes(term),
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
        <Typography variant="h5" sx={{ fontWeight: 800 }}>Asignar entrenadores</Typography>
        <Typography variant="body2" color="text.secondary">
          Asigna o reasigna un entrenador a cada usuario para que tenga seguimiento.
        </Typography>
      </Box>

      {error && <Alert severity="error">{error}</Alert>}

      <TextField
        label="Buscar usuario"
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        sx={{ maxWidth: 400 }}
      />

      {trainers.length === 0 && (
        <Alert severity="warning">No hay entrenadores registrados para asignar.</Alert>
      )}

      {trainers.length > 0 && activeTrainers.length === 0 && (
        <Alert severity="warning">
          Todos los entrenadores están inactivos. Actívalos en «Gestionar entrenadores» para poder asignarlos.
        </Alert>
      )}

      {visibleTrainees.length === 0 ? (
        <Alert severity="info">No hay usuarios para mostrar.</Alert>
      ) : (
        <Grid container spacing={2}>
          {visibleTrainees.map((trainee) => {
            const current = assignmentOf(trainee.idUser);
            const choice = selected[trainee.idUser] ?? "";
            const unchanged = !choice || choice === current?.trainerId;
            return (
              <Grid key={trainee.idUser} size={{ xs: 12, sm: 6, lg: 4 }}>
                <EntityCard>
                  <Stack direction="row" spacing={1} sx={{ justifyContent: "space-between", alignItems: "flex-start" }}>
                    <Typography variant="h6" sx={{ fontWeight: 700 }}>{fullName(trainee)}</Typography>
                    {!current && <Chip size="small" color="warning" label="Sin entrenador" />}
                  </Stack>
                  <Typography variant="body2" color="text.secondary" sx={{ overflowWrap: "anywhere" }}>
                    {trainee.institutionalEmail}
                  </Typography>
                  <Typography variant="body2">
                    <b>Entrenador:</b>{" "}
                    {current
                      ? `${current.trainerFirstName ?? ""} ${current.trainerLastName ?? ""} (desde ${formatDate(current.assignmentDate)})`
                      : "Ninguno"}
                  </Typography>
                  <TextField
                    select
                    size="small"
                    label={current ? "Nuevo entrenador" : "Entrenador"}
                    value={choice}
                    onChange={(e) =>
                      setSelected((prev) => ({ ...prev, [trainee.idUser]: Number(e.target.value) }))
                    }
                    disabled={activeTrainers.length === 0}
                  >
                    {activeTrainers.map((trainer) => (
                      <MenuItem key={trainer.idUser} value={trainer.idUser}>
                        {fullName(trainer)}
                        {trainer.idUser === current?.trainerId ? " (actual)" : ""}
                      </MenuItem>
                    ))}
                  </TextField>
                  <Stack direction="row" spacing={1} useFlexGap sx={{ flexWrap: "wrap", pt: 1 }}>
                    {can("CREATE_ASSIGNMENT") && (
                      <Button variant="contained" disabled={unchanged} onClick={() => void handleSave(trainee)}>
                        {current ? "Reasignar" : "Asignar"}
                      </Button>
                    )}
                    {current && can("DELETE_ASSIGNMENT") && (
                      <Button color="error" onClick={() => setToRemove(current)}>Quitar entrenador</Button>
                    )}
                  </Stack>
                </EntityCard>
              </Grid>
            );
          })}
        </Grid>
      )}

      <ConfirmDialog
        open={toRemove !== null}
        title="Quitar entrenador"
        message={`¿Seguro que quieres quitar el entrenador de ${toRemove?.userFirstName ?? ""} ${toRemove?.userLastName ?? ""}?`}
        confirmLabel="Quitar"
        onConfirm={() => void handleRemove()}
        onClose={() => setToRemove(null)}
      />
    </Stack>
  );
};

export default AssignmentsManagementScreen;
