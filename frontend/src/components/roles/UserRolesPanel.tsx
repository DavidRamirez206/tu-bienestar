import { useCallback, useEffect, useMemo, useState } from "react";
import {
  Alert,
  Box,
  Button,
  Chip,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  FormControl,
  InputLabel,
  MenuItem,
  Paper,
  Select,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import { getAllUsers } from "../../services/UserService";
import {
  createUserRole,
  deleteUserRole,
  getAllUserRoles,
} from "../../services/UserRoleService";
import type {
  RoleResponse,
  UserResponse,
  UserRoleResponse,
} from "../../types/api.types";
import useAuth from "../../hooks/useAuth";

type Props = {
  roles: RoleResponse[];
  notify: (message: string, severity: "success" | "error") => void;
};

type PendingRemoval = { user: UserResponse; role: RoleResponse };

// Asigna y quita roles a cada usuario. El cambio aplica en su siguiente inicio de sesion,
// porque los permisos viajan en el token que se emite al iniciar sesion.
const UserRolesPanel = ({ roles, notify }: Props) => {
  const { user: currentUser } = useAuth();
  const [users, setUsers] = useState<UserResponse[]>([]);
  const [userRoles, setUserRoles] = useState<UserRoleResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [selectedRole, setSelectedRole] = useState<Record<number, number | "">>({});
  const [pendingRemoval, setPendingRemoval] = useState<PendingRemoval | null>(null);
  const [saving, setSaving] = useState(false);

  const loadData = useCallback(async () => {
    const [usersRes, userRolesRes] = await Promise.all([
      getAllUsers(),
      getAllUserRoles(),
    ]);
    if (usersRes.error || userRolesRes.error) {
      notify("No se pudo cargar la asignación de roles", "error");
      return;
    }
    setUsers(usersRes.data ?? []);
    setUserRoles(userRolesRes.data ?? []);
  }, [notify]);

  useEffect(() => {
    let isMounted = true;
    const fetchData = async () => {
      try {
        await loadData();
      } catch {
        if (isMounted) {
          notify("No se pudo cargar la asignación de roles", "error");
        }
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    };
    void fetchData();
    return () => {
      isMounted = false;
    };
  }, [loadData, notify]);

  const roleMap = useMemo(() => new Map(roles.map((r) => [r.id, r])), [roles]);

  const rolesByUser = useMemo(() => {
    const map = new Map<number, RoleResponse[]>();
    for (const ur of userRoles) {
      const role = roleMap.get(ur.roleId);
      if (!role) {
        continue;
      }
      map.set(ur.userId, [...(map.get(ur.userId) ?? []), role]);
    }
    return map;
  }, [userRoles, roleMap]);

  const filteredUsers = useMemo(() => {
    const term = search.trim().toLowerCase();
    const sorted = [...users].sort((a, b) =>
      `${a.firstName} ${a.lastName}`.localeCompare(`${b.firstName} ${b.lastName}`),
    );
    if (!term) {
      return sorted;
    }
    return sorted.filter((u) =>
      `${u.firstName} ${u.lastName} ${u.institutionalEmail}`.toLowerCase().includes(term),
    );
  }, [users, search]);

  const handleAssign = async (target: UserResponse) => {
    const roleId = selectedRole[target.idUser];
    if (!roleId) {
      return;
    }
    setSaving(true);
    try {
      const res = await createUserRole({ userId: target.idUser, roleId });
      if (res.error) {
        notify(res.message || "No se pudo asignar el rol", "error");
        return;
      }
      notify(
        `Rol ${roleMap.get(roleId)?.name ?? ""} asignado. Aplica en su siguiente inicio de sesión.`,
        "success",
      );
      setSelectedRole((prev) => ({ ...prev, [target.idUser]: "" }));
      await loadData();
    } catch {
      notify("No se pudo asignar el rol", "error");
    } finally {
      setSaving(false);
    }
  };

  const handleConfirmRemoval = async () => {
    if (!pendingRemoval) {
      return;
    }
    const { user: target, role } = pendingRemoval;
    setSaving(true);
    try {
      const res = await deleteUserRole(target.idUser, role.id);
      if (res.error) {
        notify(res.message || "No se pudo quitar el rol", "error");
        return;
      }
      notify(`Rol ${role.name} retirado. Aplica en su siguiente inicio de sesión.`, "success");
      await loadData();
    } catch {
      notify("No se pudo quitar el rol", "error");
    } finally {
      setSaving(false);
      setPendingRemoval(null);
    }
  };

  if (loading) {
    return (
      <Stack sx={{ alignItems: "center", py: 4 }}>
        <CircularProgress thickness={5} aria-label="Cargando usuarios" />
      </Stack>
    );
  }

  return (
    <Paper
      elevation={0}
      sx={{
        p: { xs: 2, md: 3 },
        mt: 3,
        borderRadius: 4,
        border: "1px solid",
        borderColor: "divider",
        boxShadow: "0 4px 20px 0 rgba(0,0,0,0.05)",
      }}
    >
      <Typography variant="h5" component="h2" sx={{ fontWeight: "bold", mb: 1 }}>
        Roles por usuario
      </Typography>
      <Alert severity="info" sx={{ mb: 2, borderRadius: 2 }}>
        Los cambios de rol aplican en el siguiente inicio de sesión del usuario.
      </Alert>

      <TextField
        label="Buscar usuario"
        size="small"
        fullWidth
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        sx={{ mb: 2 }}
      />

      {filteredUsers.length === 0 ? (
        <Typography color="text.secondary" sx={{ py: 3, textAlign: "center" }}>
          No hay usuarios que coincidan con la búsqueda.
        </Typography>
      ) : (
        <Stack spacing={1.5}>
          {filteredUsers.map((u) => {
            const assigned = rolesByUser.get(u.idUser) ?? [];
            const available = roles.filter((r) => !assigned.some((a) => a.id === r.id));
            const isSelf = u.idUser === currentUser?.idUser;
            return (
              <Box
                key={u.idUser}
                sx={{
                  display: "flex",
                  flexDirection: { xs: "column", md: "row" },
                  alignItems: { xs: "stretch", md: "center" },
                  justifyContent: "space-between",
                  gap: 2,
                  p: 2,
                  border: "1px solid",
                  borderColor: "divider",
                  borderRadius: 2,
                }}
              >
                <Box sx={{ minWidth: 0 }}>
                  <Typography sx={{ fontWeight: 700 }}>
                    {u.firstName} {u.lastName}
                  </Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ wordBreak: "break-all" }}>
                    {u.institutionalEmail}
                  </Typography>
                  <Stack direction="row" spacing={1} useFlexGap sx={{ mt: 1, flexWrap: "wrap" }}>
                    {assigned.length === 0 ? (
                      <Typography variant="caption" color="text.secondary">
                        Sin roles asignados
                      </Typography>
                    ) : (
                      assigned.map((role) => {
                        // Evita que el administrador se quite a si mismo el rol ADMIN.
                        const locked = isSelf && role.name === "ADMIN";
                        return (
                          <Chip
                            key={role.id}
                            label={role.name}
                            color="primary"
                            variant="outlined"
                            size="small"
                            onDelete={locked || saving ? undefined : () => setPendingRemoval({ user: u, role })}
                          />
                        );
                      })
                    )}
                  </Stack>
                </Box>

                <Stack direction="row" spacing={1} sx={{ flexShrink: 0 }}>
                  <FormControl size="small" sx={{ minWidth: 160 }} disabled={available.length === 0}>
                    <InputLabel id={`role-select-${u.idUser}`}>Asignar rol</InputLabel>
                    <Select
                      labelId={`role-select-${u.idUser}`}
                      label="Asignar rol"
                      value={selectedRole[u.idUser] ?? ""}
                      onChange={(e) =>
                        setSelectedRole((prev) => ({ ...prev, [u.idUser]: Number(e.target.value) }))
                      }
                    >
                      {available.map((r) => (
                        <MenuItem key={r.id} value={r.id}>
                          {r.name}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                  <Button
                    variant="contained"
                    disabled={!selectedRole[u.idUser] || saving}
                    onClick={() => void handleAssign(u)}
                    sx={{ fontWeight: 700 }}
                  >
                    Asignar
                  </Button>
                </Stack>
              </Box>
            );
          })}
        </Stack>
      )}

      <Dialog open={pendingRemoval !== null} onClose={() => setPendingRemoval(null)}>
        <DialogTitle>Quitar rol</DialogTitle>
        <DialogContent>
          <DialogContentText>
            ¿Quitar el rol {pendingRemoval?.role.name} a {pendingRemoval?.user.firstName}{" "}
            {pendingRemoval?.user.lastName}? Perderá esos permisos en su siguiente inicio de sesión.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setPendingRemoval(null)}>Cancelar</Button>
          <Button color="error" variant="contained" disabled={saving} onClick={() => void handleConfirmRemoval()}>
            Quitar
          </Button>
        </DialogActions>
      </Dialog>
    </Paper>
  );
};

export default UserRolesPanel;
