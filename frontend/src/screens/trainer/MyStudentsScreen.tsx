import { useEffect, useState } from "react";
import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import CircularProgress from "@mui/material/CircularProgress";
import Grid from "@mui/material/Grid";
import Stack from "@mui/material/Stack";
import Typography from "@mui/material/Typography";
import EntityCard from "../../components/common/EntityCard";
import { getMyStudents } from "../../services/AssignmentService";
import type { AssignmentResponse } from "../../types/api.types";

const formatDate = (iso: string) => iso.split("-").reverse().join("/");

const MyStudentsScreen = () => {
  const [students, setStudents] = useState<AssignmentResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

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
              </EntityCard>
            </Grid>
          ))}
        </Grid>
      )}
    </Stack>
  );
};

export default MyStudentsScreen;
