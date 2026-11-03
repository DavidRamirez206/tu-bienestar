import { useCallback, useEffect, useMemo, useState } from "react";
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
import MenuItem from "@mui/material/MenuItem";
import Stack from "@mui/material/Stack";
import TextField from "@mui/material/TextField";
import Typography from "@mui/material/Typography";
import useMediaQuery from "@mui/material/useMediaQuery";
import { useTheme } from "@mui/material/styles";
import AddIcon from "@mui/icons-material/Add";
import EntityCard from "../../components/common/EntityCard";
import ConfirmDialog from "../../components/common/ConfirmDialog";
import ExerciseVideo from "../../components/exercises/ExerciseVideo";
import ExerciseFormDialog, {
  EXERCISE_DIFFICULTIES,
  EXERCISE_TYPES,
} from "../../components/exercises/ExerciseFormDialog";
import {
  createExercise,
  deleteExercise,
  getAllExercises,
  updateExercise,
} from "../../services/ExerciseService";
import { useAppDispatch } from "../../hooks/useDispatch";
import useAuthorities from "../../hooks/useAuthorities";
import { showSnackbar } from "../../store/slices/uiSlice";
import type { ExerciseRequest, ExerciseResponse } from "../../types/api.types";

// Formulario abierto: null = cerrado, "new" = crear, ejercicio = editar.
type FormState = null | "new" | ExerciseResponse;

const withExisting = (base: string[], values: (string | undefined)[]) =>
  [...new Set([...base, ...values.filter((value): value is string => !!value)])];

const ExercisesScreen = () => {
  const theme = useTheme();
  const fullScreen = useMediaQuery(theme.breakpoints.down("sm"));
  const dispatch = useAppDispatch();
  const can = useAuthorities();

  const [exercises, setExercises] = useState<ExerciseResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [typeFilter, setTypeFilter] = useState("");
  const [difficultyFilter, setDifficultyFilter] = useState("");
  const [selected, setSelected] = useState<ExerciseResponse | null>(null);
  const [form, setForm] = useState<FormState>(null);
  const [toDelete, setToDelete] = useState<ExerciseResponse | null>(null);

  const load = useCallback(async () => {
    const res = await getAllExercises();
    setError(res.error ? res.message || "No se pudieron cargar los ejercicios" : null);
    if (!res.error) {
      setExercises(res.data ?? []);
    }
    setLoading(false);
  }, []);

  useEffect(() => {
    void (async () => {
      await load();
    })();
  }, [load]);

  const types = useMemo(() => withExisting(EXERCISE_TYPES, exercises.map((e) => e.type)), [exercises]);
  const difficulties = useMemo(() => withExisting(EXERCISE_DIFFICULTIES, exercises.map((e) => e.difficulty)), [exercises]);

  const visible = exercises.filter(
    (exercise) =>
      (!typeFilter || exercise.type === typeFilter) &&
      (!difficultyFilter || exercise.difficulty === difficultyFilter),
  );

  const notify = (message: string, severity: "success" | "error") => {
    dispatch(showSnackbar({ message, severity }));
  };

  const handleSave = async (request: ExerciseRequest) => {
    const editing = form !== "new" && form !== null ? form : null;
    const res = editing
      ? await updateExercise(editing.idExercise, request)
      : await createExercise(request);
    if (res.error) {
      notify(res.message || "No se pudo guardar el ejercicio", "error");
      return;
    }
    notify(editing ? "Ejercicio actualizado" : "Ejercicio creado", "success");
    setForm(null);
    await load();
  };

  const handleDelete = async () => {
    if (!toDelete) {
      return;
    }
    const res = await deleteExercise(toDelete.idExercise);
    setToDelete(null);
    if (res.error) {
      notify(res.message || "No se pudo eliminar el ejercicio", "error");
      return;
    }
    notify("Ejercicio eliminado", "success");
    await load();
  };

  if (loading) {
    return (
      <Box sx={{ display: "flex", justifyContent: "center", py: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Stack spacing={3}>
      <Stack
        direction={{ xs: "column", sm: "row" }}
        spacing={2}
        sx={{ justifyContent: "space-between", alignItems: { sm: "center" } }}
      >
        <Box>
          <Typography variant="h5" sx={{ fontWeight: 800 }}>Ejercicios</Typography>
          <Typography variant="body2" color="text.secondary">
            Consulta el catálogo y aprende a ejecutar cada ejercicio.
          </Typography>
        </Box>
        {can("CREATE_EXERCISE") && (
          <Button variant="contained" startIcon={<AddIcon />} onClick={() => setForm("new")}>
            Nuevo ejercicio
          </Button>
        )}
      </Stack>

      <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
        <TextField select label="Tipo" value={typeFilter} onChange={(e) => setTypeFilter(e.target.value)} sx={{ minWidth: 180 }}>
          <MenuItem value="">Todos</MenuItem>
          {types.map((type) => <MenuItem key={type} value={type}>{type}</MenuItem>)}
        </TextField>
        <TextField select label="Dificultad" value={difficultyFilter} onChange={(e) => setDifficultyFilter(e.target.value)} sx={{ minWidth: 180 }}>
          <MenuItem value="">Todas</MenuItem>
          {difficulties.map((difficulty) => <MenuItem key={difficulty} value={difficulty}>{difficulty}</MenuItem>)}
        </TextField>
      </Stack>

      {error && <Alert severity="error">{error}</Alert>}

      {!error && visible.length === 0 && (
        <Alert severity="info">No hay ejercicios que cumplan esos filtros.</Alert>
      )}

      <Grid container spacing={2}>
        {visible.map((exercise) => (
          <Grid key={exercise.idExercise} size={{ xs: 12, sm: 6, lg: 4 }}>
            <EntityCard onClick={() => setSelected(exercise)}>
              <Typography variant="h6" sx={{ fontWeight: 700 }}>{exercise.name}</Typography>
              <Stack direction="row" spacing={1} useFlexGap sx={{ flexWrap: "wrap" }}>
                <Chip size="small" color="primary" label={exercise.type} />
                {exercise.difficulty && <Chip size="small" variant="outlined" label={exercise.difficulty} />}
                {exercise.durationMin !== undefined && exercise.durationMin !== null && <Chip size="small" variant="outlined" label={`${exercise.durationMin} min`} />}
              </Stack>
            </EntityCard>
          </Grid>
        ))}
      </Grid>

      {selected && (
        <Dialog open onClose={() => setSelected(null)} fullWidth maxWidth="md" fullScreen={fullScreen}>
          <DialogTitle sx={{ fontWeight: 800 }}>{selected.name}</DialogTitle>
          <DialogContent>
            <Stack spacing={2}>
              <Typography><b>Tipo:</b> {selected.type}</Typography>
              <Typography><b>Dificultad:</b> {selected.difficulty || "Sin especificar"}</Typography>
              <Typography><b>Duración:</b> {selected.durationMin ? `${selected.durationMin} min` : "Sin especificar"}</Typography>
              <Typography><b>Descripción:</b> {selected.description || "Sin descripción"}</Typography>
              <ExerciseVideo url={selected.videoUrl} title={selected.name} />
            </Stack>
          </DialogContent>
          <DialogActions sx={{ flexWrap: "wrap" }}>
            {can("DELETE_EXERCISE") && (
              <Button color="error" onClick={() => { setToDelete(selected); setSelected(null); }}>Eliminar</Button>
            )}
            {can("EDIT_EXERCISE") && (
              <Button onClick={() => { setForm(selected); setSelected(null); }}>Editar</Button>
            )}
            <Button variant="contained" onClick={() => setSelected(null)}>Cerrar</Button>
          </DialogActions>
        </Dialog>
      )}

      {form !== null && (
        <ExerciseFormDialog
          exercise={form === "new" ? null : form}
          onClose={() => setForm(null)}
          onSave={(request) => void handleSave(request)}
        />
      )}

      <ConfirmDialog
        open={toDelete !== null}
        title="Eliminar ejercicio"
        message={`¿Seguro que quieres eliminar "${toDelete?.name ?? ""}"? También saldrá de las rutinas que lo usan.`}
        confirmLabel="Eliminar"
        onConfirm={() => void handleDelete()}
        onClose={() => setToDelete(null)}
      />
    </Stack>
  );
};

export default ExercisesScreen;
