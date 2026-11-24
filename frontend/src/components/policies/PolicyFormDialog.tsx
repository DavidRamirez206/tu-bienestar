import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
} from "@mui/material";
import PolicyForm from "./PolicyForm";

type Props = {
  open: boolean;
  editing: boolean;
  policyName: string;
  setPolicyName: (value: string) => void;
  policyDescription: string;
  setPolicyDescription: (value: string) => void;
  policyResource: string;
  setPolicyResource: (value: string) => void;
  policyAction: string;
  setPolicyAction: (value: string) => void;
  onClose: () => void;
  onSave: () => void;
};

const PolicyFormDialog = ({
  open,
  editing,
  policyName,
  setPolicyName,
  policyDescription,
  setPolicyDescription,
  policyResource,
  setPolicyResource,
  policyAction,
  setPolicyAction,
  onClose,
  onSave,
}: Props) => {
  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm" sx={{ '& .MuiDialog-paper': { borderRadius: 3 } }}>
      <DialogTitle sx={{ fontWeight: 'bold' }}>
        {editing ? "Editar Permiso" : "Crear Nuevo Permiso"}
      </DialogTitle>

      <DialogContent dividers>
        <PolicyForm
          policyName={policyName}
          setPolicyName={setPolicyName}
          policyDescription={policyDescription}
          setPolicyDescription={setPolicyDescription}
          policyResource={policyResource}
          setPolicyResource={setPolicyResource}
          policyAction={policyAction}
          setPolicyAction={setPolicyAction}
        />
      </DialogContent>

      <DialogActions sx={{ p: 2.5 }}>
        <Button onClick={onClose} sx={{ fontWeight: 600 }}>Cancelar</Button>
        <Button variant="contained" onClick={onSave} sx={{ borderRadius: 2, fontWeight: 700, px: 3 }}>
          {editing ? "Guardar Cambios" : "Crear Permiso"}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default PolicyFormDialog;
