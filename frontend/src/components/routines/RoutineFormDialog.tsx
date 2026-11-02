import { useMemo, useState } from "react";
import Alert from "@mui/material/Alert";
import Button from "@mui/material/Button";
import Checkbox from "@mui/material/Checkbox";
import FormControlLabel from "@mui/material/FormControlLabel";
import MenuItem from "@mui/material/MenuItem";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import Stack from "@mui/material/Stack";
import TextField from "@mui/material/TextField";

import type {
  ExerciseRequest,
  ExerciseResponse,
  RoutineRequest,
  RoutineResponse,
  RoutineExerciseResponse,
} from "../../types/api.types";
import type { RoutineExerciseDraft } from "../../utils/routine.utils";
import {
  buildRoutinePayload,
  createDraftFromExercise,
  ROUTINE_LEVELS,
  sortExercisesByName,
  todayAsDateOnly,
} from "../../utils/routine.utils";
import RoutineExercisesBuilder from "./RoutineExercisesBuilder";
import CustomExerciseDialog from "./CustomExerciseDialog";

type Props = {
  open: boolean;
  routine: RoutineResponse | null;
  exercises: ExerciseResponse[];
  routineExercises: RoutineExerciseResponse[];
  canPublish: boolean;
  onClose: () => void;
  onSave: (
    payload: RoutineRequest,
    drafts: RoutineExerciseDraft[],
    id?: number,
  ) => Promise<void>;
  onCreateCustomExercise: (payload: ExerciseRequest) => Promise<ExerciseResponse | null>;
};

const RoutineFormDialog = ({
  open,
  routine,
  exercises,
  routineExercises,
  canPublish,
  onClose,
  onSave,
  onCreateCustomExercise,
}: Props) => {
  const [selectedExerciseId, setSelectedExerciseId] = useState("");
  const [openCustomExercise, setOpenCustomExercise] = useState(false);

  const sortedExercises = useMemo(
    () => sortExercisesByName(exercises),
    [exercises],
  );

  const initialDrafts = useMemo(() => {
    if (!routine?.idRoutine) {
      return [];
    }

    return routineExercises
      .filter((item) => item.routineId === routine.idRoutine)
      .sort((a, b) => (a.exerciseOrder ?? 9999) - (b.exerciseOrder ?? 9999))
      .map((item) => ({
        exerciseId: item.exerciseId,
        exerciseName: item.exerciseName,
        sets: item.sets ?? 3,
        targetReps: item.targetReps ?? 10,
        exerciseOrder: item.exerciseOrder ?? 0,
      }));
  }, [routine, routineExercises]);

  const [name, setName] = useState(routine?.name ?? "");
  const [description, setDescription] = useState(routine?.description ?? "");
  const [creationDate, setCreationDate] = useState(
    routine?.creationDate ?? todayAsDateOnly(),
  );
  const [drafts, setDrafts] = useState<RoutineExerciseDraft[]>(initialDrafts);
  const [isPredesigned, setIsPredesigned] = useState(routine?.isPredesigned ?? false);
  const [level, setLevel] = useState(routine?.level ?? "");
  const [formError, setFormError] = useState<string | null>(null);

  const handleCreateCustomExercise = async (payload: ExerciseRequest) => {
    const created = await onCreateCustomExercise(payload);
    if (created) {
      setDrafts((current) => [
        ...current,
        createDraftFromExercise(created.idExercise, current.length, created.name),
      ]);
    }
  };

  const handleSave = async () => {
    if (!name.trim() || !creationDate.trim()) {
      setFormError("El nombre y la fecha son obligatorios.");
      return;
    }

    if (isPredesigned && !level) {
      setFormError("Elige el nivel de la rutina prediseñada.");
      return;
    }

    if (isPredesigned && drafts.length === 0) {
      setFormError("Una rutina prediseñada necesita al menos un ejercicio.");
      return;
    }

    if (drafts.some((draft) => draft.sets < 1 || draft.targetReps < 1)) {
      setFormError("Las series y repeticiones deben ser mayores que cero.");
      return;
    }

    setFormError(null);

    const payload = buildRoutinePayload({
      name,
      description,
      creationDate,
      isPredesigned,
      level,
    });

    await onSave(payload, drafts, routine?.idRoutine);
  };

  return (
    <>
      <Dialog 
        open={open} 
        onClose={onClose} 
        fullWidth 
        maxWidth="md"
        sx={{ '& .MuiDialog-paper': { borderRadius: 3 } }}
      >
        <DialogTitle sx={{ fontWeight: 'bold' }}>{routine ? "Editar rutina" : "Crear rutina"}</DialogTitle>

        <DialogContent dividers>
          <Stack spacing={3} sx={{ mt: 1 }}>
            {formError && <Alert severity="error">{formError}</Alert>}

            <TextField
              label="Nombre de la rutina"
              required
              fullWidth
              value={name}
              onChange={(e) => setName(e.target.value)}
            />

            <TextField
              label="Descripción / Objetivo"
              fullWidth
              multiline
              minRows={3}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />

            <TextField
              label="Fecha"
              type="date"
              fullWidth
              value={creationDate}
              onChange={(e) => setCreationDate(e.target.value)}
              slotProps={{ inputLabel: { shrink: true } }}
            />

            {canPublish && (
              <Stack direction={{ xs: "column", sm: "row" }} spacing={2} sx={{ alignItems: { sm: "center" } }}>
                <FormControlLabel
                  sx={{ flexGrow: 1 }}
                  control={
                    <Checkbox
                      checked={isPredesigned}
                      onChange={(e) => setIsPredesigned(e.target.checked)}
                    />
                  }
                  label="Publicar como rutina prediseñada"
                />
                {isPredesigned && (
                  <TextField
                    select
                    required
                    label="Nivel"
                    value={level}
                    onChange={(e) => setLevel(e.target.value)}
                    sx={{ minWidth: 200 }}
                  >
                    {ROUTINE_LEVELS.map((option) => (
                      <MenuItem key={option} value={option}>
                        {option}
                      </MenuItem>
                    ))}
                  </TextField>
                )}
              </Stack>
            )}

            <RoutineExercisesBuilder
              exercises={sortedExercises}
              drafts={drafts}
              selectedExerciseId={selectedExerciseId}
              onSelectedExerciseChange={setSelectedExerciseId}
              onDraftsChange={setDrafts}
              onCreateCustomExercise={() => setOpenCustomExercise(true)}
            />
          </Stack>
        </DialogContent>

        <DialogActions sx={{ p: 2.5 }}>
          <Button onClick={onClose} sx={{ fontWeight: 600 }}>Cancelar</Button>
          <Button variant="contained" onClick={handleSave} sx={{ borderRadius: 2, fontWeight: 700, px: 3 }}>
            {routine ? "Guardar cambios" : "Crear rutina"}
          </Button>
        </DialogActions>
      </Dialog>

      <CustomExerciseDialog
        open={openCustomExercise}
        onClose={() => setOpenCustomExercise(false)}
        onSave={handleCreateCustomExercise}
      />
    </>
  );
};

export default RoutineFormDialog;
