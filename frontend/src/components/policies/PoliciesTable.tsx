import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import {
  Box,
  Card,
  CardActions,
  CardContent,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  Typography,
  useMediaQuery,
  useTheme,
} from "@mui/material";
import type { PolicyResponse } from "../../types/api.types";

type Props = {
  policies: PolicyResponse[];
  onEdit: (policy: PolicyResponse) => void;
  onDelete: (id: number) => void;
};

const PoliciesTable = ({ policies, onEdit, onDelete }: Props) => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("md"));

  if (isMobile) {
    return (
      <Box>
        {policies.map((policy) => (
          <Card key={policy.id} sx={{ mb: 2 }}>
            <CardContent>
              <Typography variant="h6" sx={{ fontWeight: 600 }}>
                {policy.name}
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                {policy.description}
              </Typography>
              <Typography variant="body2" sx={{ mt: 1, fontFamily: 'monospace', color: 'primary.main' }}>
                {policy.resource}
              </Typography>
                <Typography variant="body2" sx={{ fontWeight: 500, mt: 1 }}>
                    {policy.action}
                </Typography>
            </CardContent>
            <CardActions>
              <IconButton color="primary" onClick={() => onEdit(policy)}>
                <EditIcon />
              </IconButton>
              <IconButton color="error" onClick={() => onDelete(policy.id)}>
                <DeleteIcon />
              </IconButton>
            </CardActions>
          </Card>
        ))}
      </Box>
    );
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
            <strong>Recurso</strong>
          </TableCell>
          <TableCell>
            <strong>Acción</strong>
          </TableCell>
          <TableCell align="right">
            <strong>Acciones</strong>
          </TableCell>
        </TableRow>
      </TableHead>

      <TableBody>
        {policies.map((policy) => (
          <TableRow key={policy.id} hover>
            <TableCell sx={{ fontWeight: 600 }}>{policy.name}</TableCell>
            <TableCell>{policy.description}</TableCell>
            <TableCell sx={{ fontFamily: 'monospace', color: 'primary.main' }}>{policy.resource}</TableCell>
            <TableCell sx={{ fontWeight: 500 }}>{policy.action}</TableCell>
            <TableCell align="right">
              <IconButton color="primary" onClick={() => onEdit(policy)} sx={{ mr: 1 }}>
                <EditIcon />
              </IconButton>

              <IconButton color="error" onClick={() => onDelete(policy.id)}>
                <DeleteIcon />
              </IconButton>
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
};

export default PoliciesTable;
