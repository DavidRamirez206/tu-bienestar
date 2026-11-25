import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
} from "@mui/material";
import type { PolicyResponse } from "../../types/api.types";
import RoleForm from "./RoleForm";

type Props = {
  open: boolean;
  editing: boolean;
  roleName: string;
  setRoleName: (value: string) => void;
  policies: PolicyResponse[];
  selectedPolicyIds: number[];
  onTogglePolicy: (policyId: number) => void;
  onClose: () => void;
  onSave: () => void;
};

const RoleFormDialog = ({
  open,
  editing,
  roleName,
  setRoleName,
  policies,
  selectedPolicyIds,
  onTogglePolicy,
  onClose,
  onSave,
}: Props) => {
  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm" sx={{ '& .MuiDialog-paper': { borderRadius: 3 } }}>
      <DialogTitle sx={{ fontWeight: 'bold' }}>
        {editing ? "Editar Rol" : "Crear Nuevo Rol"}
      </DialogTitle>

      <DialogContent dividers>
        <RoleForm
          roleName={roleName}
          setRoleName={setRoleName}
          policies={policies}
          selectedPolicyIds={selectedPolicyIds}
          onTogglePolicy={onTogglePolicy}
        />
      </DialogContent>

      <DialogActions sx={{ p: 2.5 }}>
        <Button onClick={onClose} sx={{ fontWeight: 600 }}>Cancelar</Button>
        <Button variant="contained" onClick={onSave} sx={{ borderRadius: 2, fontWeight: 700, px: 3 }}>
          {editing ? "Guardar Cambios" : "Crear Rol"}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default RoleFormDialog;
