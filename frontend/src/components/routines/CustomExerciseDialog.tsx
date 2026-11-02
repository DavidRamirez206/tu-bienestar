import { useState } from "react";
import Button from "@mui/material/Button";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import Stack from "@mui/material/Stack";
import TextField from "@mui/material/TextField";

import type { ExerciseRequest } from "../../types/api.types";
import { buildCustomExercisePayload } from "../../utils/routine.utils";

type Props = {
  open: boolean;
  onClose: () => void;
  onSave: (payload: ExerciseRequest) => Promise<void>;
};

const CustomExerciseDialog = ({
  open,
  onClose,
  onSave,
}: Props) => {
  const [name, setName] = useState("");
  const [type, setType] = useState("");
  const [description, setDescription] = useState("");
  const [durationMin, setDurationMin] = useState("");
  const [difficulty, setDifficulty] = useState("");
  const [videoUrl, setVideoUrl] = useState("");

  const handleClose = () => {
    setName("");
    setType("");
    setDescription("");
    setDurationMin("");
    setDifficulty("");
    setVideoUrl("");
    onClose();
  };

  const handleSave = async () => {
    if (!name.trim() || !type.trim()) {
      return;
    }

    const payload = buildCustomExercisePayload({
      name,
      type,
      description,
      durationMin,
      difficulty,
      videoUrl,
    });

    await onSave(payload);
    handleClose();
  };

  return (
    <Dialog 
        open={open} 
        onClose={handleClose} 
        fullWidth 
        maxWidth="sm"
        sx={{ '& .MuiDialog-paper': { borderRadius: 3 } }}
    >
      <DialogTitle sx={{ fontWeight: 'bold' }}>Crear ejercicio personalizado</DialogTitle>

      <DialogContent dividers>
        <Stack spacing={2.5} sx={{ mt: 1 }}>
          <TextField
            label="Nombre"
            required
            value={name}
            onChange={(e) => setName(e.target.value)}
            fullWidth
          />
          <TextField
            label="Tipo"
            required
            value={type}
            onChange={(e) => setType(e.target.value)}
            fullWidth
          />
          <TextField
            label="Descripción"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            multiline
            minRows={3}
            fullWidth
          />
          <TextField
            label="Duración (min)"
            type="number"
            value={durationMin}
            onChange={(e) => setDurationMin(e.target.value)}
            fullWidth
          />
          <TextField
            label="Dificultad"
            value={difficulty}
            onChange={(e) => setDifficulty(e.target.value)}
            fullWidth
          />
          <TextField
            label="Video URL"
            value={videoUrl}
            onChange={(e) => setVideoUrl(e.target.value)}
            fullWidth
          />
        </Stack>
      </DialogContent>

      <DialogActions sx={{ p: 2.5 }}>
        <Button onClick={handleClose} sx={{ fontWeight: 600 }}>Cancelar</Button>
        <Button variant="contained" onClick={handleSave} disabled={!name.trim() || !type.trim()} sx={{ borderRadius: 2, fontWeight: 700, px: 3 }}>
          Guardar ejercicio
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default CustomExerciseDialog;
