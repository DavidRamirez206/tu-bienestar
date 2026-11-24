import { Dialog, DialogTitle, DialogContent, DialogActions, Button, List, ListItem, ListItemText, ListItemButton, Typography } from "@mui/material";
import type { RoutineResponse } from "../../types/api.types";

type Props = {
  open: boolean;
  templates: RoutineResponse[];
  onClose: () => void;
  onConfirm: (templateId: number) => void;
};

const TemplatePickerDialog = ({ open, templates, onClose, onConfirm }: Props) => {
  return (
    <Dialog 
        open={open} 
        onClose={onClose} 
        fullWidth 
        maxWidth="xs"
        sx={{ '& .MuiDialog-paper': { borderRadius: 3 } }}
    >
      <DialogTitle sx={{ fontWeight: 'bold' }}>Seleccionar plantilla</DialogTitle>
      <DialogContent dividers>
        {templates.length === 0 ? (
            <Typography sx={{ py: 2, color: 'text.secondary', textAlign: 'center' }}>
                No hay plantillas oficiales disponibles.
            </Typography>
        ) : (
            <List>
            {templates.map((template) => (
                <ListItem key={template.idRoutine} disablePadding>
                <ListItemButton 
                    onClick={() => onConfirm(template.idRoutine)}
                    sx={{ borderRadius: 2, mb: 1 }}
                >
                  <ListItemText
                      primary={template.name}
                      secondary={template.description}
                      slotProps={{ primary: { style: { fontWeight: 700 } } }}
                  />
                </ListItemButton>
                </ListItem>
            ))}
            </List>
        )}
      </DialogContent>
      <DialogActions sx={{ p: 2 }}>
        <Button onClick={onClose} sx={{ fontWeight: 600 }}>Cancelar</Button>
      </DialogActions>
    </Dialog>
  );
};

export default TemplatePickerDialog;
