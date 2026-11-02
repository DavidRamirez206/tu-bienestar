import React from 'react';
import { Box, Typography, Button, Container } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import ReportProblemOutlinedIcon from '@mui/icons-material/ReportProblemOutlined';

const NotFoundScreen: React.FC = () => {
  const navigate = useNavigate();

  return (
    <Container maxWidth="md">
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          minHeight: '70vh',
          textAlign: 'center',
        }}
      >
        <ReportProblemOutlinedIcon  sx={{ fontSize: 100, color: 'text.secondary', mb: 2 }} />
        <Typography variant="h2" sx={{ fontWeight: 'bold', mb: 2 }}>
          404
        </Typography>
        <Typography variant="h5" color="text.secondary" sx={{ mb: 4 }}>
          La página que buscas no existe.
        </Typography>
        <Button 
          variant="contained" 
          size="large" 
          onClick={() => navigate('/')}
        >
          Volver al Inicio
        </Button>
      </Box>
    </Container>
  );
};

export default NotFoundScreen;
