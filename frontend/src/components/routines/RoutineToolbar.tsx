import Button from "@mui/material/Button";
import Stack from "@mui/material/Stack";
import Typography from "@mui/material/Typography";
import AddIcon from "@mui/icons-material/Add";

type Props = {
  onCreate: () => void;
};

const RoutineToolbar = ({ onCreate }: Props) => {
  return (
    <Stack
      direction={{ xs: "column", sm: "row" }}
      spacing={2}
      sx={{
        justifyContent: "space-between",
        alignItems: { xs: "stretch", sm: "center" },
      }}
    >
      <Typography variant="h5" sx={{ fontWeight: "bold" }}>
        Gestión de rutinas
      </Typography>

      <Button variant="contained" startIcon={<AddIcon />} onClick={onCreate}>
        Crear rutina
      </Button>
    </Stack>
  );
};

export default RoutineToolbar;
