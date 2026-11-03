import { useState } from "react";
import Button from "@mui/material/Button";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import MenuItem from "@mui/material/MenuItem";
import Stack from "@mui/material/Stack";
import TextField from "@mui/material/TextField";
import useMediaQuery from "@mui/material/useMediaQuery";
import { useTheme } from "@mui/material/styles";
import type { ExerciseRequest, ExerciseResponse } from "../../types/api.types";

export const EXERCISE_TYPES = ["Cardio", "Fuerza", "Movilidad"];
export const EXERCISE_DIFFICULTIES = ["Baja", "Media", "Alta"];

type Props = {
  exercise: ExerciseResponse | null;
  onClose: () => void;
  onSave: (request: ExerciseRequest) => void;
};

type Errors = Partial<Record<"name" | "type" | "difficulty" | "durationMin", string>>;

const ExerciseFormDialog = ({ exercise, onClose, onSave }: Props) => {
  const theme = useTheme();
  const fullScreen = useMediaQuery(theme.breakpoints.down("sm"));

  const [name, setName] = useState(exercise?.name ?? "");
  const [type, setType] = useState(exercise?.type ?? "");
  const [difficulty, setDifficulty] = useState(exercise?.difficulty ?? "");
  const [durationMin, setDurationMin] = useState(exercise?.durationMin?.toString() ?? "");
  const [description, setDescription] = useState(exercise?.description ?? "");
  const [videoUrl, setVideoUrl] = useState(exercise?.videoUrl ?? "");
  const [errors, setErrors] = useState<Errors>({});

  const handleSave = () => {
    const found: Errors = {};
    if (!name.trim()) { found.name = "El nombre es obligatorio"; }
    if (!type) { found.type = "El tipo es obligatorio"; }
    if (!difficulty) { found.difficulty = "La dificultad es obligatoria"; }
    if (!durationMin) {
      found.durationMin = "La duración es obligatoria";
    } else if (Number(durationMin) < 1) {
      found.durationMin = "La duración debe ser mayor que cero";
    }
    setErrors(found);
    if (Object.keys(found).length > 0) {
      return;
    }
    onSave({
      name: name.trim(),
      type,
      difficulty,
      durationMin: Number(durationMin),
      description: description.trim(),
      videoUrl: videoUrl.trim() || undefined,
      isPredefined: exercise?.isPredefined ?? true,
    });
  };

  // Conserva valores antiguos que no estan en las listas para que se puedan ver al editar.
  const typeOptions = [...new Set([...EXERCISE_TYPES, ...(exercise?.type ? [exercise.type] : [])])];
  const difficultyOptions = [...new Set([...EXERCISE_DIFFICULTIES, ...(exercise?.difficulty ? [exercise.difficulty] : [])])];

  return (
    <Dialog open onClose={onClose} fullWidth maxWidth="sm" fullScreen={fullScreen}>
      <DialogTitle>{exercise ? "Editar ejercicio" : "Nuevo ejercicio"}</DialogTitle>
      <DialogContent>
        <Stack spacing={2} sx={{ mt: 1 }}>
          <TextField label="Nombre" required fullWidth value={name} onChange={(e) => setName(e.target.value)} error={!!errors.name} helperText={errors.name} />
          <TextField label="Tipo" required select fullWidth value={type} onChange={(e) => setType(e.target.value)} error={!!errors.type} helperText={errors.type}>
            {typeOptions.map((option) => <MenuItem key={option} value={option}>{option}</MenuItem>)}
          </TextField>
          <TextField label="Dificultad" required select fullWidth value={difficulty} onChange={(e) => setDifficulty(e.target.value)} error={!!errors.difficulty} helperText={errors.difficulty}>
            {difficultyOptions.map((option) => <MenuItem key={option} value={option}>{option}</MenuItem>)}
          </TextField>
          <TextField label="Duración (min)" required type="number" fullWidth value={durationMin} onChange={(e) => setDurationMin(e.target.value)} error={!!errors.durationMin} helperText={errors.durationMin} />
          <TextField label="Descripción" fullWidth multiline minRows={3} value={description} onChange={(e) => setDescription(e.target.value)} />
          <TextField label="URL del video" fullWidth value={videoUrl} onChange={(e) => setVideoUrl(e.target.value)} helperText="Enlace de YouTube o de un archivo de video" />
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancelar</Button>
        <Button variant="contained" onClick={handleSave}>Guardar</Button>
      </DialogActions>
    </Dialog>
  );
};

export default ExerciseFormDialog;
