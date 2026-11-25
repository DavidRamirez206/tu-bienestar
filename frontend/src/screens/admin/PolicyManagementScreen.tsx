import { useCallback, useMemo, useState, useEffect } from "react";
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
  createPolicy,
  deletePolicy,
  getAllPolicies,
  updatePolicy,
} from "../../services/PolicyService";
import type { PolicyRequest, PolicyResponse } from "../../types/api.types";
import { useAppDispatch } from "../../hooks/useDispatch";
import { showSnackbar } from "../../store/slices/uiSlice";
import PoliciesTable from "../../components/policies/PoliciesTable";
import PolicyFormDialog from "../../components/policies/PolicyFormDialog";

const PolicyManagementScreen = () => {
  const dispatch = useAppDispatch();

  const [policies, setPolicies] = useState<PolicyResponse[]>([]);
  const [loading, setLoading] = useState(true);

  const [openForm, setOpenForm] = useState(false);
  const [editingPolicy, setEditingPolicy] = useState<PolicyResponse | null>(
    null,
  );

  const [policyName, setPolicyName] = useState("");
  const [policyDescription, setPolicyDescription] = useState("");
  const [policyResource, setPolicyResource] = useState("");
  const [policyAction, setPolicyAction] = useState("");

  const notify = useCallback(
    (message: string, severity: "success" | "error") => {
      dispatch(showSnackbar({ message, severity }));
    },
    [dispatch],
  );

  const loadData = useCallback(async () => {
    try {
      setLoading(true);
      const res = await getAllPolicies();
      if (res.error) {
        notify(res.message || "No se pudo cargar la información de permisos", "error");
        return;
      }
      setPolicies(res.data ?? []);
    } catch {
      notify("No se pudo cargar la información de permisos", "error");
    } finally {
      setLoading(false);
    }
  }, [notify]);

  useEffect(() => {
    let isMounted = true;

    const initFetch = async () => {
      if (isMounted) {
        await loadData();
      }
    };

    void initFetch();

    return () => {
      isMounted = false;
    };
  }, [loadData]);

  const sortedPolicies = useMemo(() => {
    return [...policies].sort((a, b) => a.name.localeCompare(b.name));
  }, [policies]);

  const resetForm = useCallback(() => {
    setEditingPolicy(null);
    setPolicyName("");
    setPolicyDescription("");
    setPolicyResource("");
    setPolicyAction("");
  }, []);

  const handleOpenCreate = useCallback(() => {
    resetForm();
    setOpenForm(true);
  }, [resetForm]);

  const handleOpenEdit = useCallback((policy: PolicyResponse) => {
    setEditingPolicy(policy);
    setPolicyName(policy.name);
    setPolicyDescription(policy.description ?? "");
    setPolicyResource(policy.resource ?? "");
    setPolicyAction(policy.action ?? "");
    setOpenForm(true);
  }, []);

  const handleCloseForm = useCallback(() => {
    setOpenForm(false);
    resetForm();
  }, [resetForm]);

  const handleSavePolicy = useCallback(async () => {
    if (
      !policyName.trim() ||
      !policyDescription.trim() ||
      !policyResource.trim() ||
      !policyAction.trim()
    ) {
      notify("Todos los campos son obligatorios", "error");
      return;
    }

    const payload: PolicyRequest = {
      name: policyName.trim(),
      description: policyDescription.trim(),
      resource: policyResource.trim(),
      action: policyAction.trim(),
    };

    try {
      let res;
      if (editingPolicy) {
        res = await updatePolicy(editingPolicy.id, payload);
      } else {
        res = await createPolicy(payload);
      }

      if (res.error) {
        notify(res.message || "No se pudo guardar el permiso", "error");
        return;
      }

      notify(editingPolicy ? "Permiso actualizado correctamente" : "Permiso creado correctamente", "success");
      handleCloseForm();
      await loadData();
    } catch {
      notify("No se pudo guardar el permiso", "error");
    }
  }, [
    policyName,
    policyDescription,
    policyResource,
    policyAction,
    editingPolicy,
    notify,
    handleCloseForm,
    loadData,
  ]);

  const handleDeletePolicy = useCallback(
    async (id: number) => {
      const confirmed = window.confirm(
        "¿Seguro que deseas eliminar este permiso?",
      );
      if (!confirmed) {
        return;
      }

      try {
        const res = await deletePolicy(id);
        if (res.error) {
          notify(res.message || "No se pudo eliminar el permiso", "error");
          return;
        }
        notify("Permiso eliminado correctamente", "success");
        await loadData();
      } catch {
        notify("No se pudo eliminar el permiso", "error");
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
            Gestión de Permisos
          </Typography>

          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={handleOpenCreate}
            sx={{ borderRadius: 2, fontWeight: 700 }}
          >
            Crear permiso
          </Button>
        </Stack>

        <PoliciesTable
          policies={sortedPolicies}
          onEdit={handleOpenEdit}
          onDelete={handleDeletePolicy}
        />
      </Paper>

      <PolicyFormDialog
        open={openForm}
        editing={Boolean(editingPolicy)}
        policyName={policyName}
        setPolicyName={setPolicyName}
        policyDescription={policyDescription}
        setPolicyDescription={setPolicyDescription}
        policyResource={policyResource}
        setPolicyResource={setPolicyResource}
        policyAction={policyAction}
        setPolicyAction={setPolicyAction}
        onClose={handleCloseForm}
        onSave={handleSavePolicy}
      />
    </Box>
  );
};

export default PolicyManagementScreen;