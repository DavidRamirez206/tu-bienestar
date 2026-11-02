import React from 'react';
import { Box, Typography, Button, Container } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import SecurityIcon from '@mui/icons-material/Security';

const UnauthorizedScreen: React.FC = () => {
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
        <SecurityIcon sx={{ fontSize: 100, color: 'error.main', mb: 2 }} />
        <Typography variant="h2" sx={{ fontWeight: 'bold', mb: 2, color: 'error.main' }}>
          Acceso Denegado
        </Typography>
        <Typography variant="h5" color="text.secondary" sx={{ mb: 4 }}>
          No tienes los permisos necesarios para acceder a esta sección.
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

export default UnauthorizedScreen;
