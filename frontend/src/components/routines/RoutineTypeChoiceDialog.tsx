import { Dialog, DialogTitle, DialogContent, DialogActions, Button, Stack, Typography, Box } from "@mui/material";
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutlined';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';

type Props = {
  open: boolean;
  onClose: () => void;
  onCreateBlank: () => void;
  onChooseTemplate: () => void;
};

const RoutineTypeChoiceDialog = ({ open, onClose, onCreateBlank, onChooseTemplate }: Props) => {
  return (
    <Dialog 
        open={open} 
        onClose={onClose} 
        fullWidth 
        maxWidth="xs"
        sx={{ '& .MuiDialog-paper': { borderRadius: 3 } }}
    >
      <DialogTitle sx={{ fontWeight: 'bold', textAlign: 'center', pt: 3 }}>¿Cómo quieres empezar?</DialogTitle>
      <DialogContent>
        <Stack spacing={2} sx={{ py: 2 }}>
          <Button
            variant="outlined"
            size="large"
            startIcon={<AddCircleOutlineIcon />}
            onClick={onCreateBlank}
            sx={{ 
                py: 2, 
                borderRadius: 3, 
                flexDirection: 'column', 
                gap: 1,
                '& .MuiButton-startIcon': { m: 0 }
            }}
          >
            <Box>
                <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>Rutina en blanco</Typography>
                <Typography variant="caption" color="text.secondary">Empieza desde cero y añade ejercicios</Typography>
            </Box>
          </Button>

          <Button
            variant="outlined"
            size="large"
            color="secondary"
            startIcon={<ContentCopyIcon />}
            onClick={onChooseTemplate}
            sx={{ 
                py: 2, 
                borderRadius: 3, 
                flexDirection: 'column', 
                gap: 1,
                '& .MuiButton-startIcon': { m: 0 }
            }}
          >
            <Box>
                <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>Usar plantilla</Typography>
                <Typography variant="caption" color="text.secondary">Copia una rutina oficial diseñada por entrenadores</Typography>
            </Box>
          </Button>
        </Stack>
      </DialogContent>
      <DialogActions sx={{ justifyContent: 'center', pb: 3 }}>
        <Button onClick={onClose} sx={{ fontWeight: 600 }}>Cerrar</Button>
      </DialogActions>
    </Dialog>
  );
};

export default RoutineTypeChoiceDialog;
