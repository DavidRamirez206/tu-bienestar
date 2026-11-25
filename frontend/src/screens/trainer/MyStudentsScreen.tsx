import { useEffect, useState } from "react";
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
import Stack from "@mui/material/Stack";
import Typography from "@mui/material/Typography";
import EntityCard from "../../components/common/EntityCard";
import { getMyStudents } from "../../services/AssignmentService";
import { getRoutinesByUser } from "../../services/RoutineService";
import { getProgressByUserId } from "../../services/ProgressService";
import type { AssignmentResponse, ProgressResponse, RoutineResponse } from "../../types/api.types";

const formatDate = (iso: string) => iso.split("-").reverse().join("/");

const formatDateTime = (iso: string) => new Date(iso).toLocaleString("es-CO");

// Cada registro solo muestra los datos que el alumno llenó.
const field = (value: number | null | undefined, label: string) =>
  value === null || value === undefined ? null : `${value} ${label}`;

const MyStudentsScreen = () => {
  const [students, setStudents] = useState<AssignmentResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [selected, setSelected] = useState<AssignmentResponse | null>(null);
  const [routines, setRoutines] = useState<RoutineResponse[]>([]);
  const [progress, setProgress] = useState<ProgressResponse[]>([]);
  const [detailLoading, setDetailLoading] = useState(false);
  const [detailError, setDetailError] = useState<string | null>(null);

  useEffect(() => {
    void (async () => {
      const res = await getMyStudents();
      if (res.error) {
        setError(res.message || "No se pudieron cargar tus alumnos");
      } else {
        setStudents(res.data ?? []);
      }
      setLoading(false);
    })();
  }, []);

  // Detalle del alumno: sus rutinas y sus registros de progreso.
  const handleOpenDetail = async (student: AssignmentResponse) => {
    setSelected(student);
    setRoutines([]);
    setProgress([]);
    setDetailError(null);
    setDetailLoading(true);
    const [routinesRes, progressRes] = await Promise.all([
      getRoutinesByUser(student.userId),
      getProgressByUserId(student.userId),
    ]);
    const failed = [routinesRes, progressRes].find((res) => res.error);
    setDetailError(failed ? failed.message || "No se pudo cargar la información de este alumno" : null);
    if (!routinesRes.error) {
      setRoutines(routinesRes.data ?? []);
    }
    if (!progressRes.error) {
      setProgress(progressRes.data ?? []);
    }
    setDetailLoading(false);
  };

  const handleCloseDetail = () => setSelected(null);

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
        <Typography variant="h5" sx={{ fontWeight: 800 }}>Mis alumnos</Typography>
        <Typography variant="body2" color="text.secondary">
          Usuarios que el administrador te asignó para hacerles seguimiento.
        </Typography>
      </Box>

      {error && <Alert severity="error">{error}</Alert>}

      {!error && students.length === 0 ? (
        <Alert severity="info">Todavía no tienes alumnos asignados.</Alert>
      ) : (
        <Grid container spacing={2}>
          {students.map((student) => (
            <Grid key={student.userId} size={{ xs: 12, sm: 6, lg: 4 }}>
              <EntityCard>
                <Typography variant="h6" sx={{ fontWeight: 700 }}>
                  {student.userFirstName} {student.userLastName}
                </Typography>
                {student.userEmail && (
                  <Typography variant="body2" color="text.secondary" sx={{ overflowWrap: "anywhere" }}>
                    {student.userEmail}
                  </Typography>
                )}
                <Typography variant="body2">
                  <b>Asignado desde:</b> {formatDate(student.assignmentDate)}
                </Typography>
                <Stack direction="row" spacing={1} sx={{ pt: 1 }}>
                  <Button variant="contained" onClick={() => void handleOpenDetail(student)}>
                    Ver rutinas y progreso
                  </Button>
                </Stack>
              </EntityCard>
            </Grid>
          ))}
        </Grid>
      )}

      <Dialog open={selected !== null} onClose={handleCloseDetail} fullWidth maxWidth="sm">
        <DialogTitle>
          {selected?.userFirstName} {selected?.userLastName}
        </DialogTitle>
        <DialogContent dividers>
          {detailLoading && (
            <Box sx={{ display: "flex", justifyContent: "center", py: 4 }}>
              <CircularProgress />
            </Box>
          )}

          {!detailLoading && detailError && <Alert severity="error">{detailError}</Alert>}

          {!detailLoading && !detailError && (
            <Stack spacing={3}>
              <Box>
                <Typography variant="subtitle2" sx={{ fontWeight: 800, mb: 1 }}>Rutinas</Typography>
                {routines.length === 0 ? (
                  <Alert severity="info">Este alumno todavía no tiene rutinas registradas.</Alert>
                ) : (
                  <Stack spacing={2}>
                    {routines.map((routine) => (
                      <EntityCard key={routine.idRoutine}>
                        <Stack
                          direction="row"
                          spacing={1}
                          sx={{ justifyContent: "space-between", alignItems: "flex-start" }}
                        >
                          <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>{routine.name}</Typography>
                          {routine.level && <Chip size="small" label={routine.level} />}
                        </Stack>
                        {routine.description && (
                          <Typography variant="body2" color="text.secondary">{routine.description}</Typography>
                        )}
                        <Typography variant="body2">
                          <b>Creada:</b> {formatDate(routine.creationDate)}
                        </Typography>
                        <Typography variant="body2">
                          <b>Ejercicios:</b> {routine.routineExercises?.length ?? 0}
                        </Typography>
                      </EntityCard>
                    ))}
                  </Stack>
                )}
              </Box>

              <Box>
                <Typography variant="subtitle2" sx={{ fontWeight: 800, mb: 1 }}>
                  Registros de progreso
                </Typography>
                {progress.length === 0 ? (
                  <Alert severity="info">Este alumno todavía no tiene registros de progreso.</Alert>
                ) : (
                  <Stack spacing={1}>
                    {[...progress]
                      .sort((a, b) => new Date(b.dateLogged).getTime() - new Date(a.dateLogged).getTime())
                      .map((entry) => (
                        <EntityCard key={entry.idProgress}>
                          <Typography variant="body2" sx={{ fontWeight: 700 }}>
                            {formatDateTime(entry.dateLogged)}
                          </Typography>
                          <Typography variant="body2" color="text.secondary">
                            {[
                              field(entry.reps, "reps"),
                              field(entry.setNumber, "series"),
                              field(entry.weightKg, "kg"),
                              field(entry.durationMin, "min"),
                              field(entry.effortLevel, "de esfuerzo"),
                            ]
                              .filter(Boolean)
                              .join(" · ") || "Sin datos"}
                          </Typography>
                        </EntityCard>
                      ))}
                  </Stack>
                )}
              </Box>
            </Stack>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDetail}>Cerrar</Button>
        </DialogActions>
      </Dialog>
    </Stack>
  );
};

export default MyStudentsScreen;
