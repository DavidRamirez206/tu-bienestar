import { Stack, TextField } from "@mui/material";

type Props = {
  policyName: string;
  setPolicyName: (value: string) => void;
  policyDescription: string;
  setPolicyDescription: (value: string) => void;
  policyResource: string;
  setPolicyResource: (value: string) => void;
  policyAction: string;
  setPolicyAction: (value: string) => void;
};

const PolicyForm = ({
  policyName,
  setPolicyName,
  policyDescription,
  setPolicyDescription,
  policyResource,
  setPolicyResource,
  policyAction,
  setPolicyAction,
}: Props) => {
  return (
    <Stack spacing={2.5} sx={{ mt: 1 }}>
      <TextField
        label="Nombre del permiso"
        fullWidth
        value={policyName}
        onChange={(e) => setPolicyName(e.target.value)}
        placeholder="Ej. LIST_USERS"
      />

      <TextField
        label="Descripción"
        fullWidth
        multiline
        minRows={3}
        value={policyDescription}
        onChange={(e) => setPolicyDescription(e.target.value)}
        placeholder="Describe el alcance de este permiso..."
      />

      <TextField
        label="Recurso"
        fullWidth
        value={policyResource}
        onChange={(e) => setPolicyResource(e.target.value)}
        placeholder="Ej. /api/users"
      />

      <TextField
        label="Acción"
        fullWidth
        value={policyAction}
        onChange={(e) => setPolicyAction(e.target.value)}
        placeholder="Ej. READ, WRITE, DELETE..."
      />
    </Stack>
  );
};

export default PolicyForm;
