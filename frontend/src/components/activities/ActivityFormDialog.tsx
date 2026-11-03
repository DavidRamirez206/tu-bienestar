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
import type { ActivityRequest, ActivityResponse, SpaceResponse } from "../../types/api.types";

type Props = {
  activity: ActivityResponse | null;
  spaces: SpaceResponse[];
  onClose: () => void;
  onSave: (request: ActivityRequest) => void;
};

type Errors = Partial<Record<"name" | "description" | "startDate" | "endDate" | "spaceId", string>>;

const ActivityFormDialog = ({ activity, spaces, onClose, onSave }: Props) => {
  const theme = useTheme();
  const fullScreen = useMediaQuery(theme.breakpoints.down("sm"));

  const [name, setName] = useState(activity?.name ?? "");
  const [description, setDescription] = useState(activity?.description ?? "");
  const [startDate, setStartDate] = useState(activity?.startDate ?? "");
  const [endDate, setEndDate] = useState(activity?.endDate ?? "");
  const [spaceId, setSpaceId] = useState(activity?.spaceId?.toString() ?? "");
  const [errors, setErrors] = useState<Errors>({});

  const handleSave = () => {
    const found: Errors = {};
    if (!name.trim()) { found.name = "El nombre es obligatorio"; }
    if (!description.trim()) { found.description = "La descripción es obligatoria"; }
    if (!startDate) { found.startDate = "La fecha de inicio es obligatoria"; }
    if (!endDate) {
      found.endDate = "La fecha de fin es obligatoria";
    } else if (startDate && endDate < startDate) {
      found.endDate = "La fecha de fin no puede ser anterior a la de inicio";
    }
    if (!spaceId) { found.spaceId = "El lugar es obligatorio"; }
    setErrors(found);
    if (Object.keys(found).length > 0) {
      return;
    }
    onSave({
      name: name.trim(),
      description: description.trim(),
      startDate,
      endDate,
      spaceId: Number(spaceId),
    });
  };

  return (
    <Dialog open onClose={onClose} fullWidth maxWidth="sm" fullScreen={fullScreen}>
      <DialogTitle>{activity ? "Editar evento" : "Nuevo evento"}</DialogTitle>
      <DialogContent>
        <Stack spacing={2} sx={{ mt: 1 }}>
          <TextField label="Nombre" required fullWidth value={name} onChange={(e) => setName(e.target.value)} error={!!errors.name} helperText={errors.name} />
          <TextField label="Descripción" required fullWidth multiline minRows={3} value={description} onChange={(e) => setDescription(e.target.value)} error={!!errors.description} helperText={errors.description} />
          <TextField label="Fecha de inicio" required type="date" fullWidth slotProps={{ inputLabel: { shrink: true } }} value={startDate} onChange={(e) => setStartDate(e.target.value)} error={!!errors.startDate} helperText={errors.startDate} />
          <TextField label="Fecha de fin" required type="date" fullWidth slotProps={{ inputLabel: { shrink: true } }} value={endDate} onChange={(e) => setEndDate(e.target.value)} error={!!errors.endDate} helperText={errors.endDate} />
          <TextField label="Lugar" required select fullWidth value={spaceId} onChange={(e) => setSpaceId(e.target.value)} error={!!errors.spaceId} helperText={errors.spaceId}>
            {spaces.map((space) => (
              <MenuItem key={space.idSpace} value={space.idSpace.toString()}>{space.name}</MenuItem>
            ))}
          </TextField>
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancelar</Button>
        <Button variant="contained" onClick={handleSave}>Guardar</Button>
      </DialogActions>
    </Dialog>
  );
};

export default ActivityFormDialog;
