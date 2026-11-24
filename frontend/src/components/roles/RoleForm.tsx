import { Stack, TextField } from "@mui/material";
import type { PolicyResponse } from "../../types/api.types";
import RolePoliciesSelector from "./RolePoliciesSelector";

type Props = {
  roleName: string;
  setRoleName: (value: string) => void;
  policies: PolicyResponse[];
  selectedPolicyIds: number[];
  onTogglePolicy: (policyId: number) => void;
};

const RoleForm = ({
  roleName,
  setRoleName,
  policies,
  selectedPolicyIds,
  onTogglePolicy,
}: Props) => {
  return (
    <Stack spacing={3} sx={{ mt: 1 }}>
      <TextField
        label="Nombre del rol"
        fullWidth
        value={roleName}
        onChange={(e) => setRoleName(e.target.value)}
        placeholder="Ej. Entrenador, Administrativo..."
      />

      <RolePoliciesSelector
        policies={policies}
        selectedPolicyIds={selectedPolicyIds}
        onToggle={onTogglePolicy}
      />
    </Stack>
  );
};

export default RoleForm;
