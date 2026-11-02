import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';

const HomeScreen = () => (
  <Paper elevation={0} sx={{ borderRadius: 4, p: { xs: 3, md: 5 }, border: '1px solid', borderColor: 'divider' }}>
    <Typography variant="h4" color="primary" sx={{ fontWeight: 800 }}>
      Panel de inicio
    </Typography>
    <Typography variant="body1" color="text.secondary" sx={{ mt: 1 }}>
      Aqui veras tu actividad en la plataforma.
    </Typography>
  </Paper>
);

export default HomeScreen;
