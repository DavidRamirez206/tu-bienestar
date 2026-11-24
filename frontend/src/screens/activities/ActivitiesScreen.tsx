import { useCallback, useEffect, useState } from "react";
import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Chip from "@mui/material/Chip";
import CircularProgress from "@mui/material/CircularProgress";
import FormControlLabel from "@mui/material/FormControlLabel";
import Grid from "@mui/material/Grid";
import MenuItem from "@mui/material/MenuItem";
import Stack from "@mui/material/Stack";
import Switch from "@mui/material/Switch";
import Tab from "@mui/material/Tab";
import Tabs from "@mui/material/Tabs";
import TextField from "@mui/material/TextField";
import ToggleButton from "@mui/material/ToggleButton";
import ToggleButtonGroup from "@mui/material/ToggleButtonGroup";
import Typography from "@mui/material/Typography";
import AddIcon from "@mui/icons-material/Add";
import EntityCard from "../../components/common/EntityCard";
import ConfirmDialog from "../../components/common/ConfirmDialog";
import ActivityFormDialog from "../../components/activities/ActivityFormDialog";
import ActivityDetailsDialog from "../../components/activities/ActivityDetailsDialog";
import {
  createActivity,
  deleteActivity,
  getActivities,
  updateActivity,
} from "../../services/ActivityService";
import { getAllSpaces } from "../../services/SpaceService";
import { getSchedules } from "../../services/ScheduleService";
import { ACTIVITY_TYPES, DAYS, dayLabel, formatTime, today, typeLabel } from "../../utils/activity.utils";
import { cancelEnrollment, enroll, getMyEnrollments } from "../../services/EnrollmentService";
import { useAppDispatch } from "../../hooks/useDispatch";
import useAuthorities from "../../hooks/useAuthorities";
import { showSnackbar } from "../../store/slices/uiSlice";
import type {
  ActivityRequest,
  ActivityResponse,
  ActivityType,
  DayOfWeek,
  EnrollmentResponse,
  ScheduleResponse,
  SpaceResponse,
} from "../../types/api.types";

type FormState = null | "new" | ActivityResponse;

const TAB_UPCOMING = 0;
const TAB_SCHEDULES = 1;
const TAB_MINE = 2;
const TAB_SPACES = 3;

const formatDate = (iso: string) => iso.split("-").reverse().join("/");

const formatRange = (activity: ActivityResponse) =>
  activity.startDate === activity.endDate
    ? formatDate(activity.startDate)
    : `${formatDate(activity.startDate)} al ${formatDate(activity.endDate)}`;

const localToday = () => {
  const now = new Date();
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}-${String(now.getDate()).padStart(2, "0")}`;
};

const ActivitiesScreen = () => {
  const dispatch = useAppDispatch();
  const can = useAuthorities();

  const [tab, setTab] = useState(TAB_UPCOMING);
  const [activities, setActivities] = useState<ActivityResponse[]>([]);
  const [enrollments, setEnrollments] = useState<EnrollmentResponse[]>([]);
  const [spaces, setSpaces] = useState<SpaceResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [from, setFrom] = useState("");
  const [to, setTo] = useState("");
  const [includePast, setIncludePast] = useState(false);
  const [form, setForm] = useState<FormState>(null);
  const [toDelete, setToDelete] = useState<ActivityResponse | null>(null);
  const [type, setType] = useState<ActivityType | "">("");
  const [detail, setDetail] = useState<ActivityResponse | null>(null);
  const [day, setDay] = useState<DayOfWeek>(today());
  const [schedules, setSchedules] = useState<ScheduleResponse[]>([]);
  const [schedulesError, setSchedulesError] = useState<string | null>(null);

  const rangeError = from && to && from > to ? "La fecha inicial no puede ser posterior a la final" : null;

  const load = useCallback(async () => {
    const [activitiesRes, enrollmentsRes, spacesRes] = await Promise.all([
      getActivities({
        upcoming: !includePast,
        from: from || undefined,
        to: to || undefined,
        type: type || undefined,
      }),
      getMyEnrollments(),
      getAllSpaces(),
    ]);
    const failed = [activitiesRes, enrollmentsRes, spacesRes].find((res) => res.error);
    setError(failed ? failed.message || "No se pudieron cargar las actividades" : null);
    if (!activitiesRes.error) { setActivities(activitiesRes.data ?? []); }
    if (!enrollmentsRes.error) { setEnrollments(enrollmentsRes.data ?? []); }
    if (!spacesRes.error) { setSpaces(spacesRes.data ?? []); }
    setLoading(false);
  }, [from, to, includePast, type]);

  const loadSchedules = useCallback(async () => {
    const res = await getSchedules({ day, type: type || undefined });
    setSchedulesError(res.error ? res.message || "No se pudieron cargar los horarios" : null);
    if (!res.error) { setSchedules(res.data ?? []); }
  }, [day, type]);

  useEffect(() => {
    if (tab === TAB_SCHEDULES) {
      void (async () => {
        await loadSchedules();
      })();
    }
  }, [tab, loadSchedules]);

  useEffect(() => {
    if (!rangeError) {
      void (async () => {
        await load();
      })();
    }
  }, [load, rangeError]);

  const notify = (message: string, severity: "success" | "error") => {
    dispatch(showSnackbar({ message, severity }));
  };

  const enrollmentOf = (activityId: number) =>
    enrollments.find((enrollment) => enrollment.activityId === activityId);

  const handleEnroll = async (activity: ActivityResponse) => {
    const res = await enroll(activity.idActivity);
    notify(res.error ? res.message || "No se pudo completar la inscripción" : `Te inscribiste en ${activity.name}`, res.error ? "error" : "success");
    await load();
  };

  const handleCancel = async (enrollment: EnrollmentResponse) => {
    const res = await cancelEnrollment(enrollment.userId, enrollment.activityId);
    notify(res.error ? res.message || "No se pudo cancelar la inscripción" : "Inscripción cancelada", res.error ? "error" : "success");
    await load();
  };

  const handleSave = async (request: ActivityRequest) => {
    const editing = form !== "new" && form !== null ? form : null;
    const res = editing
      ? await updateActivity(editing.idActivity, request)
      : await createActivity(request);
    if (res.error) {
      notify(res.message || "No se pudo guardar el evento", "error");
      return;
    }
    notify(editing ? "Evento actualizado" : "Evento creado", "success");
    setForm(null);
    await load();
  };

  const handleDelete = async () => {
    if (!toDelete) {
      return;
    }
    const res = await deleteActivity(toDelete.idActivity);
    setToDelete(null);
    notify(res.error ? res.message || "No se pudo eliminar el evento" : "Evento eliminado", res.error ? "error" : "success");
    await load();
  };

  const renderActivity = (activity: ActivityResponse) => {
    const enrollment = enrollmentOf(activity.idActivity);
    const finished = activity.endDate < localToday();
    return (
      <Grid key={activity.idActivity} size={{ xs: 12, sm: 6, lg: 4 }}>
        <EntityCard>
          <Stack direction="row" spacing={1} sx={{ justifyContent: "space-between", alignItems: "flex-start" }}>
            <Typography variant="h6" sx={{ fontWeight: 700 }}>{activity.name}</Typography>
            <Stack direction="row" spacing={0.5}>
              <Chip size="small" color="primary" variant="outlined" label={typeLabel(activity.type)} />
              {finished && <Chip size="small" label="Finalizado" />}
            </Stack>
          </Stack>
          <Typography variant="body2"><b>Fecha:</b> {formatRange(activity)}</Typography>
          <Typography variant="body2">
            <b>Lugar:</b> {[activity.spaceName, activity.spaceLocation].filter(Boolean).join(" - ") || "Sin definir"}
          </Typography>
          <Typography variant="body2" color="text.secondary">{activity.description}</Typography>
          <Stack direction="row" spacing={1} useFlexGap sx={{ flexWrap: "wrap", pt: 1 }}>
            <Button variant="outlined" onClick={() => setDetail(activity)}>Ver horarios</Button>
            {enrollment && can("DELETE_ENROLLMENT") && (
              <Button color="error" variant="outlined" onClick={() => void handleCancel(enrollment)}>
                Cancelar inscripción
              </Button>
            )}
            {!enrollment && !finished && can("CREATE_ENROLLMENT") && (
              <Button variant="contained" onClick={() => void handleEnroll(activity)}>Inscribirme</Button>
            )}
            {can("EDIT_ACTIVITY") && <Button onClick={() => setForm(activity)}>Editar</Button>}
            {can("DELETE_ACTIVITY") && <Button color="error" onClick={() => setToDelete(activity)}>Eliminar</Button>}
          </Stack>
        </EntityCard>
      </Grid>
    );
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
          <Typography variant="h5" sx={{ fontWeight: 800 }}>Actividades</Typography>
          <Typography variant="body2" color="text.secondary">
            Consulta los eventos y espacios e inscríbete para asegurar tu cupo.
          </Typography>
        </Box>
        {can("CREATE_ACTIVITY") && (
          <Button variant="contained" startIcon={<AddIcon />} onClick={() => setForm("new")}>
            Nuevo evento
          </Button>
        )}
      </Stack>

      <Tabs value={tab} onChange={(_, value: number) => setTab(value)} variant="scrollable" allowScrollButtonsMobile>
        <Tab label="Próximos eventos" />
        <Tab label="Horarios" />
        <Tab label="Mis actividades" />
        <Tab label="Espacios" />
      </Tabs>

      {error && <Alert severity="error">{error}</Alert>}

      {tab === TAB_UPCOMING && (
        <>
          <Stack direction={{ xs: "column", sm: "row" }} spacing={2} sx={{ alignItems: { sm: "center" } }}>
            <TextField label="Desde" type="date" slotProps={{ inputLabel: { shrink: true } }} value={from} onChange={(e) => setFrom(e.target.value)} error={!!rangeError} />
            <TextField label="Hasta" type="date" slotProps={{ inputLabel: { shrink: true } }} value={to} onChange={(e) => setTo(e.target.value)} error={!!rangeError} helperText={rangeError} />
            <TextField select label="Tipo" value={type} onChange={(e) => setType(e.target.value as ActivityType | "")} sx={{ minWidth: 160 }}>
              <MenuItem value="">Todos</MenuItem>
              {ACTIVITY_TYPES.map((option) => (
                <MenuItem key={option.value} value={option.value}>{option.label}</MenuItem>
              ))}
            </TextField>
            <Button disabled={!from && !to} onClick={() => { setFrom(""); setTo(""); }}>Limpiar fechas</Button>
            <FormControlLabel
              control={<Switch checked={includePast} onChange={(e) => setIncludePast(e.target.checked)} />}
              label="Incluir eventos pasados"
            />
          </Stack>
          {activities.length === 0 ? (
            <Alert severity="info">No hay eventos para mostrar.</Alert>
          ) : (
            <Grid container spacing={2}>{activities.map(renderActivity)}</Grid>
          )}
        </>
      )}

      {tab === TAB_SCHEDULES && (
        <>
          <Stack direction={{ xs: "column", md: "row" }} spacing={2} sx={{ alignItems: { md: "center" } }}>
            <ToggleButtonGroup
              exclusive
              size="small"
              color="primary"
              value={day}
              onChange={(_, value: DayOfWeek | null) => { if (value) { setDay(value); } }}
              sx={{ flexWrap: "wrap" }}
            >
              {DAYS.map((option) => (
                <ToggleButton key={option.value} value={option.value}>{option.label}</ToggleButton>
              ))}
            </ToggleButtonGroup>
            <TextField select size="small" label="Tipo" value={type} onChange={(e) => setType(e.target.value as ActivityType | "")} sx={{ minWidth: 160 }}>
              <MenuItem value="">Todos</MenuItem>
              {ACTIVITY_TYPES.map((option) => (
                <MenuItem key={option.value} value={option.value}>{option.label}</MenuItem>
              ))}
            </TextField>
          </Stack>
          {schedulesError && <Alert severity="error">{schedulesError}</Alert>}
          {!schedulesError && schedules.length === 0 ? (
            <Alert severity="info">No hay clases ni horarios el {dayLabel(day).toLowerCase()}.</Alert>
          ) : (
            <Grid container spacing={2}>
              {schedules.map((schedule) => (
                <Grid key={schedule.idSchedule} size={{ xs: 12, sm: 6, lg: 4 }}>
                  <EntityCard>
                    <Stack direction="row" spacing={1} sx={{ justifyContent: "space-between", alignItems: "flex-start" }}>
                      <Typography variant="h6" sx={{ fontWeight: 700 }}>
                        {formatTime(schedule.startTime)} - {formatTime(schedule.endTime)}
                      </Typography>
                      <Chip size="small" color="primary" variant="outlined" label={typeLabel(schedule.activityType)} />
                    </Stack>
                    <Typography variant="body1" sx={{ fontWeight: 600 }}>{schedule.activityName}</Typography>
                    <Typography variant="body2"><b>Lugar:</b> {schedule.spaceName || "Sin definir"}</Typography>
                  </EntityCard>
                </Grid>
              ))}
            </Grid>
          )}
        </>
      )}

      {tab === TAB_MINE && (
        enrollments.length === 0 ? (
          <Alert severity="info">Todavía no estás inscrito en ninguna actividad.</Alert>
        ) : (
          <Grid container spacing={2}>{enrollments.map((enrollment) => renderActivity(enrollment.activity))}</Grid>
        )
      )}

      {tab === TAB_SPACES && (
        <Grid container spacing={2}>
          {spaces.map((space) => (
            <Grid key={space.idSpace} size={{ xs: 12, sm: 6, lg: 4 }}>
              <EntityCard>
                <Typography variant="h6" sx={{ fontWeight: 700 }}>{space.name}</Typography>
                <Typography variant="body2"><b>Ubicación:</b> {space.location || "Sin definir"}</Typography>
                <Typography variant="body2"><b>Capacidad:</b> {space.capacity ?? "Sin definir"}</Typography>
              </EntityCard>
            </Grid>
          ))}
        </Grid>
      )}

      {form !== null && (
        <ActivityFormDialog
          activity={form === "new" ? null : form}
          spaces={spaces}
          onClose={() => setForm(null)}
          onSave={(request) => void handleSave(request)}
        />
      )}

      {detail !== null && (
        <ActivityDetailsDialog activity={detail} onClose={() => setDetail(null)} />
      )}

      <ConfirmDialog
        open={toDelete !== null}
        title="Eliminar evento"
        message={`¿Seguro que quieres eliminar "${toDelete?.name ?? ""}"? También se cancelarán sus inscripciones.`}
        confirmLabel="Eliminar"
        onConfirm={() => void handleDelete()}
        onClose={() => setToDelete(null)}
      />
    </Stack>
  );
};

export default ActivitiesScreen;
