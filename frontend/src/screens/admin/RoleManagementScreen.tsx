import { useCallback, useEffect, useMemo, useState } from "react";
import {
  Box,
  Button,
  CircularProgress,
  Paper,
  Stack,
  Typography,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import {
  createRole,
  deleteRole,
  getAllRoles,
  updateRole,
} from "../../services/RoleService";
import { getAllPolicies } from "../../services/PolicyService";
import type { PolicyResponse, RoleRequest, RoleResponse } from "../../types/api.types";
import { useAppDispatch } from "../../hooks/useDispatch";
import { showSnackbar } from "../../store/slices/uiSlice";
import RolesTable from "../../components/roles/RolesTable";
import RoleFormDialog from "../../components/roles/RoleFormDialog";

const RoleManagementScreen = () => {
  const dispatch = useAppDispatch();

  const [roles, setRoles] = useState<RoleResponse[]>([]);
  const [policies, setPolicies] = useState<PolicyResponse[]>([]);
  const [loading, setLoading] = useState(true);

  const [openForm, setOpenForm] = useState(false);
  const [editingRole, setEditingRole] = useState<RoleResponse | null>(null);

  const [roleName, setRoleName] = useState("");
  const [selectedPolicyIds, setSelectedPolicyIds] = useState<number[]>([]);

  const notify = useCallback(
    (message: string, severity: "success" | "error") => {
      dispatch(showSnackbar({ message, severity }));
    },
    [dispatch],
  );

  const loadData = useCallback(async () => {
    try {
      setLoading(true);

      const [rolesRes, policiesRes] = await Promise.all([
        getAllRoles(),
        getAllPolicies(),
      ]);

      if (rolesRes.error || policiesRes.error) {
        notify("No se pudo cargar la información de roles", "error");
        return;
      }

      setRoles(rolesRes.data ?? []);
      setPolicies(policiesRes.data ?? []);
    } catch {
      notify("No se pudo cargar la información de roles", "error");
    } finally {
      setLoading(false);
    }
  }, [notify]);

  useEffect(() => {
    let isMounted = true;

    const fetchRoles = async () => {
      try {
        setLoading(true);

        const [rolesRes, policiesRes] = await Promise.all([
          getAllRoles(),
          getAllPolicies(),
        ]);

        if (!isMounted) {return;}

        if (rolesRes.error || policiesRes.error) {
          notify("No se pudo cargar la información de roles", "error");
          return;
        }

        setRoles(rolesRes.data ?? []);
        setPolicies(policiesRes.data ?? []);
      } catch {
        if (isMounted) {
          notify("No se pudo cargar la información de roles", "error");
        }
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    };

    void fetchRoles();

    return () => {
      isMounted = false;
    };
  }, [notify]);

  const policyMap = useMemo(() => {
    return new Map(policies.map((policy) => [policy.id, policy]));
  }, [policies]);

  const sortedRoles = useMemo(() => {
    return [...roles].sort((a, b) => a.name.localeCompare(b.name));
  }, [roles]);

  const resetForm = useCallback(() => {
    setEditingRole(null);
    setRoleName("");
    setSelectedPolicyIds([]);
  }, []);

  const handleOpenCreate = useCallback(() => {
    resetForm();
    setOpenForm(true);
  }, [resetForm]);

  const handleOpenEdit = useCallback((role: RoleResponse) => {
    setEditingRole(role);
    setRoleName(role.name);
    setSelectedPolicyIds(role.policyIds ?? []);
    setOpenForm(true);
  }, []);

  const handleCloseForm = useCallback(() => {
    setOpenForm(false);
    resetForm();
  }, [resetForm]);

  const togglePolicy = useCallback((policyId: number) => {
    setSelectedPolicyIds((prev) =>
      prev.includes(policyId)
        ? prev.filter((id) => id !== policyId)
        : [...prev, policyId],
    );
  }, []);

  const handleSaveRole = useCallback(async () => {
    if (!roleName.trim()) {
      notify("El nombre del rol es obligatorio", "error");
      return;
    }

    const payload: RoleRequest = {
      name: roleName.trim(),
      policyIds: selectedPolicyIds,
    };

    try {
      const res = editingRole
        ? await updateRole(editingRole.id, payload)
        : await createRole(payload);

      if (res.error) {
        notify(res.message || "No se pudo guardar el rol", "error");
        return;
      }

      notify(editingRole ? "Rol actualizado correctamente" : "Rol creado correctamente", "success");
      handleCloseForm();
      await loadData();
    } catch {
      notify("No se pudo guardar el rol", "error");
    }
  }, [
    roleName,
    selectedPolicyIds,
    editingRole,
    notify,
    handleCloseForm,
    loadData,
  ]);

  const handleDeleteRole = useCallback(
    async (id: number) => {
      const confirmed = window.confirm("¿Seguro que deseas eliminar este rol?");

      if (!confirmed) {
        return;
      }

      try {
        const res = await deleteRole(id);

        if (res.error) {
          notify(res.message || "No se pudo eliminar el rol", "error");
          return;
        }
        notify("Rol eliminado correctamente", "success");
        await loadData();
      } catch {
        notify("No se pudo eliminar el rol", "error");
      }
    },
    [notify, loadData],
  );

  if (loading) {
    return (
      <Stack
        sx={{
          alignItems: "center",
          justifyContent: "center",
          py: 8,
        }}
      >
        <CircularProgress thickness={5} />
      </Stack>
    );
  }

  return (
    <Box>
      <Paper 
        elevation={0} 
        sx={{ 
          p: { xs: 2, md: 3 }, 
          borderRadius: 4, 
          border: '1px solid',
          borderColor: 'divider',
          boxShadow: '0 4px 20px 0 rgba(0,0,0,0.05)'
        }}
      >
        <Stack
          direction="row"
          sx={{
            justifyContent: "space-between",
            alignItems: "center",
            mb: 3,
          }}
        >
          <Typography variant="h5" sx={{ fontWeight: "bold" }}>
            Gestión de roles
          </Typography>

          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={handleOpenCreate}
            sx={{ borderRadius: 2, fontWeight: 700 }}
          >
            Crear rol
          </Button>
        </Stack>

        <RolesTable
          roles={sortedRoles}
          policyMap={policyMap}
          onEdit={handleOpenEdit}
          onDelete={handleDeleteRole}
        />
      </Paper>

      <RoleFormDialog
        open={openForm}
        editing={Boolean(editingRole)}
        roleName={roleName}
        setRoleName={setRoleName}
        policies={policies}
        selectedPolicyIds={selectedPolicyIds}
        onTogglePolicy={togglePolicy}
        onClose={handleCloseForm}
        onSave={handleSaveRole}
      />
    </Box>
  );
};

export default RoleManagementScreen;
