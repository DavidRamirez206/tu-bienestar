import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import {
  Box,
  Card,
  CardActions,
  CardContent,
  Chip,
  IconButton,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  Typography,
  useMediaQuery,
  useTheme,
} from "@mui/material";
import type { PolicyResponse, RoleResponse } from "../../types/api.types";

type Props = {
  roles: RoleResponse[];
  policyMap: Map<number, PolicyResponse>;
  onEdit: (role: RoleResponse) => void;
  onDelete: (id: number) => void;
};

const RolesTable = ({ roles, policyMap, onEdit, onDelete }: Props) => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("md"));

  if (isMobile) {
    return (
      <Box>
        {roles.map((role) => (
          <Card key={role.id} sx={{ mb: 2 }}>
            <CardContent>
              <Typography variant="h6" sx={{ fontWeight: 600 }}>
                {role.name}
              </Typography>
              <Stack direction="row" spacing={1} sx={{ flexWrap: "wrap", mt: 1 }}>
                {role.policyIds?.length ? (
                  role.policyIds.map((policyId) => {
                    const policy = policyMap.get(policyId);

                    return (
                      <Chip
                        key={policyId}
                        label={policy ? policy.name : `Policy ${policyId}`}
                        size="small"
                        sx={{ mb: 1 }}
                      />
                    );
                  })
                ) : (
                  <Typography variant="body2" color="text.secondary">
                    Sin permisos asociados
                  </Typography>
                )}
              </Stack>
            </CardContent>
            <CardActions>
              <IconButton color="primary" onClick={() => onEdit(role)}>
                <EditIcon />
              </IconButton>
              <IconButton color="error" onClick={() => onDelete(role.id)}>
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
            <strong>Permisos</strong>
          </TableCell>
          <TableCell align="right">
            <strong>Acciones</strong>
          </TableCell>
        </TableRow>
      </TableHead>

      <TableBody>
        {roles.map((role) => (
          <TableRow key={role.id} hover>
            <TableCell sx={{ fontWeight: 600 }}>{role.name}</TableCell>

            <TableCell>
              <Stack direction="row" spacing={1} sx={{ flexWrap: "wrap" }}>
                {role.policyIds?.length ? (
                  role.policyIds.map((policyId) => {
                    const policy = policyMap.get(policyId);

                    return (
                      <Chip
                        key={policyId}
                        label={policy ? policy.name : `Policy ${policyId}`}
                        size="small"
                        sx={{ mb: 1 }}
                      />
                    );
                  })
                ) : (
                  <Typography variant="body2" color="text.secondary">
                    Sin permisos asociados
                  </Typography>
                )}
              </Stack>
            </TableCell>

            <TableCell align="right">
              <IconButton color="primary" onClick={() => onEdit(role)} sx={{ mr: 1 }}>
                <EditIcon />
              </IconButton>

              <IconButton color="error" onClick={() => onDelete(role.id)}>
                <DeleteIcon />
              </IconButton>
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
};

export default RolesTable;
