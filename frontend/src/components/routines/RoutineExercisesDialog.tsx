import { useState } from "react";
import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import FormControl from "@mui/material/FormControl";
import IconButton from "@mui/material/IconButton";
import InputLabel from "@mui/material/InputLabel";
import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select";
import Stack from "@mui/material/Stack";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TextField from "@mui/material/TextField";

import DeleteIcon from "@mui/icons-material/Delete";

import type {
  ExerciseResponse,
  RoutineExerciseRequest,
  RoutineExerciseResponse,
  RoutineResponse,
} from "../../types/api.types";

type Props = {
  open: boolean;
  routine: RoutineResponse | null;
  exercises: ExerciseResponse[];
  routineExercises: RoutineExerciseResponse[];
  exerciseMap: Map<number, ExerciseResponse>;
  onClose: () => void;
  onAddExercise: (payload: RoutineExerciseRequest) => Promise<void>;
  onRemoveExercise: (routineId: number, exerciseId: number) => Promise<void>;
};

const RoutineExercisesDialog = ({
  open,
  routine,
  exercises,
  routineExercises,
  exerciseMap,
  onClose,
  onAddExercise,
  onRemoveExercise,
}: Props) => {
  const [exerciseId, setExerciseId] = useState("");
  const [sets, setSets] = useState("");
  const [targetReps, setTargetReps] = useState("");
  const [exerciseOrder, setExerciseOrder] = useState("");

  const selectedExercisesList = routine
    ? routineExercises
        .filter((re) => re.routineId === routine.idRoutine)
        .sort((a, b) => (a.exerciseOrder ?? 9999) - (b.exerciseOrder ?? 9999))
    : [];

  const handleAdd = async () => {
    if (!routine || !exerciseId) {return;}

    const payload: RoutineExerciseRequest = {
      routineId: routine.idRoutine,
      exerciseId: Number(exerciseId),
      sets: sets.trim() ? Number(sets) : 0,
      targetReps: targetReps.trim() ? Number(targetReps) : 0,
      exerciseOrder: exerciseOrder.trim() ? Number(exerciseOrder) : 0,
    };

    await onAddExercise(payload);

    // Limpiar form
    setExerciseId("");
    setSets("");
    setTargetReps("");
    setExerciseOrder("");
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="md">
      <DialogTitle>
        {routine ? `Ejercicios de: ${routine.name}` : "Gestionar ejercicios"}
      </DialogTitle>
      <DialogContent dividers>
        <Stack spacing={2}>
          <FormControl fullWidth>
            <InputLabel>Ejercicio</InputLabel>
            <Select
              value={exerciseId}
              label="Ejercicio"
              onChange={(e) => setExerciseId(e.target.value)}
            >
              {exercises.map((ex) => (
                <MenuItem key={ex.idExercise} value={String(ex.idExercise)}>
                  {ex.name} - {ex.type}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          <Stack direction={{ xs: "column", md: "row" }} spacing={2}>
            <TextField
              label="Sets"
              type="number"
              fullWidth
              value={sets}
              onChange={(e) => setSets(e.target.value)}
            />
            <TextField
              label="Reps objetivo"
              type="number"
              fullWidth
              value={targetReps}
              onChange={(e) => setTargetReps(e.target.value)}
            />
            <TextField
              label="Orden"
              type="number"
              fullWidth
              value={exerciseOrder}
              onChange={(e) => setExerciseOrder(e.target.value)}
            />
          </Stack>

          <Box>
            <Button variant="contained" onClick={handleAdd}>
              Agregar ejercicio
            </Button>
          </Box>

          {selectedExercisesList.length === 0 ? (
            <Alert severity="info">
              Esta rutina no tiene ejercicios asociados.
            </Alert>
          ) : (
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>
                    <strong>Ejercicio</strong>
                  </TableCell>
                  <TableCell>
                    <strong>Tipo</strong>
                  </TableCell>
                  <TableCell>
                    <strong>Sets</strong>
                  </TableCell>
                  <TableCell>
                    <strong>Reps</strong>
                  </TableCell>
                  <TableCell>
                    <strong>Orden</strong>
                  </TableCell>
                  <TableCell align="right">
                    <strong>Acciones</strong>
                  </TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {selectedExercisesList.map((item) => {
                  const exercise = exerciseMap.get(item.exerciseId);
                  return (
                    <TableRow key={`${item.routineId}-${item.exerciseId}`}>
                      <TableCell>{exercise?.name ?? item.exerciseId}</TableCell>
                      <TableCell>{exercise?.type ?? "-"}</TableCell>
                      <TableCell>{item.sets ?? "-"}</TableCell>
                      <TableCell>{item.targetReps ?? "-"}</TableCell>
                      <TableCell>{item.exerciseOrder ?? "-"}</TableCell>
                      <TableCell align="right">
                        <IconButton
                          color="error"
                          onClick={() =>
                            onRemoveExercise(item.routineId, item.exerciseId)
                          }
                        >
                          <DeleteIcon />
                        </IconButton>
                      </TableCell>
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
          )}
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cerrar</Button>
      </DialogActions>
    </Dialog>
  );
};

export default RoutineExercisesDialog;
