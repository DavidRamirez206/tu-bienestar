import { useCallback, useEffect, useState } from "react";
import Alert from "@mui/material/Alert";
import Button from "@mui/material/Button";
import Chip from "@mui/material/Chip";
import CircularProgress from "@mui/material/CircularProgress";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import Divider from "@mui/material/Divider";
import IconButton from "@mui/material/IconButton";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemText from "@mui/material/ListItemText";
import MenuItem from "@mui/material/MenuItem";
import Stack from "@mui/material/Stack";
import TextField from "@mui/material/TextField";
import Typography from "@mui/material/Typography";
import useMediaQuery from "@mui/material/useMediaQuery";
import { useTheme } from "@mui/material/styles";
import DeleteIcon from "@mui/icons-material/Delete";
import { createSchedule, deleteSchedule, getActivitySchedules } from "../../services/ScheduleService";
import { useAppDispatch } from "../../hooks/useDispatch";
import useAuthorities from "../../hooks/useAuthorities";
import { showSnackbar } from "../../store/slices/uiSlice";
import { DAYS, dayLabel, formatTime, typeLabel } from "../../utils/activity.utils";
import type { ActivityResponse, DayOfWeek, ScheduleResponse } from "../../types/api.types";

type Props = {
  activity: ActivityResponse;
  onClose: () => void;
};

const formatDate = (iso: string) => iso.split("-").reverse().join("/");

const ActivityDetailsDialog = ({ activity, onClose }: Props) => {
  const theme = useTheme();
  const fullScreen = useMediaQuery(theme.breakpoints.down("sm"));
  const dispatch = useAppDispatch();
  const can = useAuthorities();

  const [schedules, setSchedules] = useState<ScheduleResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [day, setDay] = useState<DayOfWeek>("MONDAY");
  const [startTime, setStartTime] = useState("");
  const [endTime, setEndTime] = useState("");
  const [formError, setFormError] = useState<string | null>(null);

  const load = useCallback(async () => {
    const res = await getActivitySchedules(activity.idActivity);
    setError(res.error ? res.message || "No se pudieron cargar los horarios" : null);
    if (!res.error) { setSchedules(res.data ?? []); }
    setLoading(false);
  }, [activity.idActivity]);

  useEffect(() => {
    void (async () => {
      await load();
    })();
  }, [load]);

  const notify = (message: string, severity: "success" | "error") => {
    dispatch(showSnackbar({ message, severity }));
  };

  const handleAdd = async () => {
    if (!startTime || !endTime) {
      setFormError("Indica la hora de inicio y la de fin");
      return;
    }
    if (endTime <= startTime) {
      setFormError("La hora de fin debe ser posterior a la de inicio");
      return;
    }
    setFormError(null);
    const res = await createSchedule({ activityId: activity.idActivity, dayOfWeek: day, startTime, endTime });
    notify(res.error ? res.message || "No se pudo agregar el horario" : "Horario agregado", res.error ? "error" : "success");
    if (!res.error) {
      setStartTime("");
      setEndTime("");
    }
    await load();
  };

  const handleDelete = async (schedule: ScheduleResponse) => {
    const res = await deleteSchedule(schedule.idSchedule);
    notify(res.error ? res.message || "No se pudo eliminar el horario" : "Horario eliminado", res.error ? "error" : "success");
    await load();
  };

  return (
    <Dialog open onClose={onClose} fullWidth maxWidth="sm" fullScreen={fullScreen}>
      <DialogTitle>
        <Stack direction="row" spacing={1} sx={{ alignItems: "center", flexWrap: "wrap" }} useFlexGap>
          <span>{activity.name}</span>
          <Chip size="small" color="primary" variant="outlined" label={typeLabel(activity.type)} />
        </Stack>
      </DialogTitle>
      <DialogContent>
        <Stack spacing={1.5}>
          <Typography variant="body2" color="text.secondary">{activity.description}</Typography>
          <Typography variant="body2">
            <b>Fechas:</b> {formatDate(activity.startDate)} al {formatDate(activity.endDate)}
          </Typography>
          <Typography variant="body2">
            <b>Lugar:</b> {[activity.spaceName, activity.spaceLocation].filter(Boolean).join(" - ") || "Sin definir"}
          </Typography>

          <Divider />
          <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>Horarios</Typography>

          {loading && <CircularProgress size={24} />}
          {error && <Alert severity="error">{error}</Alert>}
          {!loading && !error && schedules.length === 0 && (
            <Alert severity="info">Este evento todavía no tiene horarios.</Alert>
          )}
          {schedules.length > 0 && (
            <List dense disablePadding>
              {schedules.map((schedule) => (
                <ListItem
                  key={schedule.idSchedule}
                  disableGutters
                  secondaryAction={can("DELETE_SCHEDULE") && (
                    <IconButton
                      edge="end"
                      aria-label="Eliminar horario"
                      onClick={() => void handleDelete(schedule)}
                    >
                      <DeleteIcon />
                    </IconButton>
                  )}
                >
                  <ListItemText
                    primary={dayLabel(schedule.dayOfWeek)}
                    secondary={`${formatTime(schedule.startTime)} a ${formatTime(schedule.endTime)}`}
                  />
                </ListItem>
              ))}
            </List>
          )}

          {can("CREATE_SCHEDULE") && (
            <Stack spacing={1.5} sx={{ pt: 1 }}>
              <Typography variant="subtitle2">Agregar horario</Typography>
              <Stack direction={{ xs: "column", sm: "row" }} spacing={1.5}>
                <TextField select size="small" label="Día" value={day} onChange={(e) => setDay(e.target.value as DayOfWeek)} sx={{ minWidth: 140 }}>
                  {DAYS.map((option) => (
                    <MenuItem key={option.value} value={option.value}>{option.label}</MenuItem>
                  ))}
                </TextField>
                <TextField size="small" label="Inicio" type="time" slotProps={{ inputLabel: { shrink: true } }} value={startTime} onChange={(e) => setStartTime(e.target.value)} />
                <TextField size="small" label="Fin" type="time" slotProps={{ inputLabel: { shrink: true } }} value={endTime} onChange={(e) => setEndTime(e.target.value)} />
                <Button variant="outlined" onClick={() => void handleAdd()}>Agregar</Button>
              </Stack>
              {formError && <Alert severity="warning">{formError}</Alert>}
            </Stack>
          )}
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cerrar</Button>
      </DialogActions>
    </Dialog>
  );
};

export default ActivityDetailsDialog;
