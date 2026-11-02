import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import IconButton from "@mui/material/IconButton";
import InputLabel from "@mui/material/InputLabel";
import MenuItem from "@mui/material/MenuItem";
import Paper from "@mui/material/Paper";
import Select from "@mui/material/Select";
import Stack from "@mui/material/Stack";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TextField from "@mui/material/TextField";
import Typography from "@mui/material/Typography";
import DeleteIcon from "@mui/icons-material/Delete";
import AddIcon from "@mui/icons-material/Add";

import type { ExerciseResponse } from "../../types/api.types";
import type { RoutineExerciseDraft } from "../../utils/routine.utils";
import { createDraftFromExercise } from "../../utils/routine.utils";

type Props = {
  exercises: ExerciseResponse[];
  drafts: RoutineExerciseDraft[];
  selectedExerciseId: string;
  onSelectedExerciseChange: (value: string) => void;
  onDraftsChange: (value: RoutineExerciseDraft[]) => void;
  onCreateCustomExercise: () => void;
};

const RoutineExercisesBuilder = ({
  exercises,
  drafts,
  selectedExerciseId,
  onSelectedExerciseChange,
  onDraftsChange,
  onCreateCustomExercise,
}: Props) => {
  const exerciseMap = new Map(
    exercises.map((exercise) => [exercise.idExercise, exercise]),
  );

  const handleAddExercise = () => {
    if (!selectedExerciseId) {
      return;
    }

    const exerciseId = Number(selectedExerciseId);
    const exists = drafts.some((draft) => draft.exerciseId === exerciseId);

    if (exists) {
      return;
    }

    onDraftsChange([
      ...drafts,
      createDraftFromExercise(exerciseId, drafts.length, exerciseMap.get(exerciseId)?.name),
    ]);
    onSelectedExerciseChange("");
  };

  const handleRemoveDraft = (exerciseId: number) => {
    const next = drafts
      .filter((draft) => draft.exerciseId !== exerciseId)
      .map((draft, index) => ({
        ...draft,
        exerciseOrder: index + 1,
      }));

    onDraftsChange(next);
  };

  const handleDraftFieldChange = (
    exerciseId: number,
    field: "sets" | "targetReps" | "exerciseOrder",
    value: number,
  ) => {
    onDraftsChange(
      drafts.map((draft) =>
        draft.exerciseId === exerciseId ? { ...draft, [field]: value } : draft,
      ),
    );
  };

  return (
    <Stack spacing={2}>
      <Typography variant="h6" sx={{ fontWeight: 800, letterSpacing: '-0.3px', color: 'primary.main' }}>
        Ejercicios de la rutina
      </Typography>

      <Stack direction={{ xs: "column", md: "row" }} spacing={2}>
        <FormControl fullWidth>
          <InputLabel>Añadir Ejercicio</InputLabel>
          <Select
            value={selectedExerciseId}
            label="Añadir Ejercicio"
            onChange={(e) => onSelectedExerciseChange(e.target.value)}
          >
            {exercises.map((exercise) => (
              <MenuItem
                key={exercise.idExercise}
                value={String(exercise.idExercise)}
              >
                {exercise.name} - {exercise.type}
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={handleAddExercise}
          sx={{ borderRadius: 2, fontWeight: 700, px: 3 }}
        >
          Agregar
        </Button>

        <Button 
          variant="outlined" 
          onClick={onCreateCustomExercise}
          sx={{ borderRadius: 2, fontWeight: 700, px: 3 }}
        >
          Personalizado
        </Button>
      </Stack>

      {drafts.length === 0 ? (
        <Alert severity="info" variant="outlined" sx={{ borderRadius: 3 }}>
          Todavía no has agregado ejercicios a esta rutina.
        </Alert>
      ) : (
        <Paper variant="outlined" sx={{ borderRadius: 3, overflow: 'hidden' }}>
          <Table size="small">
            <TableHead sx={{ bgcolor: 'rgba(83, 83, 238, 0.04)' }}>
              <TableRow>
                <TableCell>
                  <strong>Ejercicio</strong>
                </TableCell>
                <TableCell width={80}>
                  <strong>Series</strong>
                </TableCell>
                <TableCell width={80}>
                  <strong>Repeticiones</strong>
                </TableCell>
                <TableCell width={80}>
                  <strong>Orden</strong>
                </TableCell>
                <TableCell align="right" width={60}>
                  <strong>Acciones</strong>
                </TableCell>
              </TableRow>
            </TableHead>

            <TableBody>
              {drafts.map((draft) => (
                <TableRow key={draft.exerciseId}>
                  <TableCell sx={{ fontWeight: 600 }}>
                    {exerciseMap.get(draft.exerciseId)?.name ??
                      draft.exerciseName ??
                      draft.exerciseId}
                  </TableCell>

                  <TableCell>
                    <TextField
                      type="number"
                      variant="standard"
                      size="small"
                      slotProps={{ htmlInput: { min: 1 } }}
                      value={draft.sets}
                      onChange={(e) =>
                        handleDraftFieldChange(
                          draft.exerciseId,
                          "sets",
                          Number(e.target.value),
                        )
                      }
                    />
                  </TableCell>

                  <TableCell>
                    <TextField
                      type="number"
                      variant="standard"
                      size="small"
                      slotProps={{ htmlInput: { min: 1 } }}
                      value={draft.targetReps}
                      onChange={(e) =>
                        handleDraftFieldChange(
                          draft.exerciseId,
                          "targetReps",
                          Number(e.target.value),
                        )
                      }
                    />
                  </TableCell>

                  <TableCell>
                    <TextField
                      type="number"
                      variant="standard"
                      size="small"
                      slotProps={{ htmlInput: { min: 1 } }}
                      value={draft.exerciseOrder}
                      onChange={(e) =>
                        handleDraftFieldChange(
                          draft.exerciseId,
                          "exerciseOrder",
                          Number(e.target.value),
                        )
                      }
                    />
                  </TableCell>

                  <TableCell align="right">
                    <IconButton
                      color="error"
                      size="small"
                      onClick={() => handleRemoveDraft(draft.exerciseId)}
                    >
                      <DeleteIcon fontSize="small" />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </Paper>
      )}

      <Box />
    </Stack>
  );
};

export default RoutineExercisesBuilder;
