import { Checkbox, FormControlLabel, Stack, Typography, Box } from "@mui/material";
import type { PolicyResponse } from "../../types/api.types";

type Props = {
  policies: PolicyResponse[];
  selectedPolicyIds: number[];
  onToggle: (policyId: number) => void;
};

const RolePoliciesSelector = ({
  policies,
  selectedPolicyIds,
  onToggle,
}: Props) => {
  return (
    <Box>
      <Typography variant="subtitle1" sx={{ mt: 2, mb: 1, fontWeight: "bold" }}>
        Permisos disponibles
      </Typography>

      <Stack sx={{ maxHeight: 300, overflowY: 'auto', pr: 1 }}>
        {policies.map((policy) => (
          <FormControlLabel
            key={policy.id}
            control={
              <Checkbox
                checked={selectedPolicyIds.includes(policy.id)}
                onChange={() => onToggle(policy.id)}
              />
            }
            label={
              <Box>
                <Typography variant="body2" sx={{ fontWeight: 600 }}>
                  {policy.name}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  {policy.resource} - {policy.action}
                </Typography>
              </Box>
            }
          />
        ))}
      </Stack>
    </Box>
  );
};

export default RolePoliciesSelector;
