import Alert from "@mui/material/Alert";
import Chip from "@mui/material/Chip";
import IconButton from "@mui/material/IconButton";
import Stack from "@mui/material/Stack";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";

import type { RoutineResponse } from "../../types/api.types";

type Props = {
  routines: RoutineResponse[];
  onEdit: (routine: RoutineResponse) => void;
  onDelete: (routine: RoutineResponse) => void;
};

const RoutineTable = ({ routines, onEdit, onDelete }: Props) => {
  if (routines.length === 0) {
    return <Alert severity="info">No tienes rutinas registradas.</Alert>;
  }

  return (
    <Table>
      <TableHead>
        <TableRow>
          <TableCell>
            <strong>Nombre</strong>
          </TableCell>
          <TableCell>
            <strong>Descripción</strong>
          </TableCell>
          <TableCell>
            <strong>Fecha</strong>
          </TableCell>
          <TableCell>
            <strong>Tipo</strong>
          </TableCell>
          <TableCell align="right">
            <strong>Acciones</strong>
          </TableCell>
        </TableRow>
      </TableHead>

      <TableBody>
        {routines.map((routine) => (
          <TableRow key={routine.idRoutine}>
            <TableCell>{routine.name}</TableCell>
            <TableCell>{routine.description || "-"}</TableCell>
            <TableCell>{routine.creationDate}</TableCell>
            <TableCell>
              <Stack direction="row" spacing={1}>
                <Chip
                  size="small"
                  color={routine.isPredesigned ? "secondary" : "primary"}
                  label={routine.isPredesigned ? "Plantilla" : "Personal"}
                />
              </Stack>
            </TableCell>
            <TableCell align="right">
              <IconButton color="primary" onClick={() => onEdit(routine)}>
                <EditIcon />
              </IconButton>

              <IconButton color="error" onClick={() => onDelete(routine)}>
                <DeleteIcon />
              </IconButton>
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
};

export default RoutineTable;
