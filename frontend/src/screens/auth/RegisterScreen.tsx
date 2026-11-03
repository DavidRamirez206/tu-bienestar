import Stack from '@mui/material/Stack';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';
import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import Snackbar from '@mui/material/Snackbar';
import Alert from '@mui/material/Alert';
import { useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { register as registerService } from '../../services/AuthService';
import type { RegisterRequest } from '../../types/api.types';
import backgroundlogin from "../../assets/backgroundlogin.jpg";

const RegisterScreen = () => {
    const navigate = useNavigate();

    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [institutionalEmail, setInstitutionalEmail] = useState('');
    const [password, setPassword] = useState('');
    const [age, setAge] = useState('');

    const [error, setError] = useState(false);
    const [success, setSuccess] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');

    const isValidInstitutionalEmail = (email: string) => {
        return /^[A-Za-z0-9._%+-]+@(icesi\.edu\.co|u\.icesi\.edu\.co)$/.test(email);
    };

    const handleRegister = async () => {
    if (!firstName.trim() || !lastName.trim() || !institutionalEmail.trim() || !password.trim()) {
        setErrorMessage('Completa todos los campos obligatorios');
        setError(true);
        return;
    }

    if (!isValidInstitutionalEmail(institutionalEmail)) {
        setErrorMessage('El correo debe ser institucional de Icesi');
        setError(true);
        return;
    }

    const registerRequest: RegisterRequest = {
        firstName,
        lastName,
        institutionalEmail,
        password,
        age: age ? Number(age) : 0,
    };
      
    const result = await registerService(registerRequest);
    
    if (result.error) {
        setErrorMessage(result.message || 'No se pudo completar el registro');
        setError(true);
        return;
    }

    setSuccess(true);
    setTimeout(() => {
        navigate('/login');
    }, 1500);
  };

  const handleGoLogin = () => {
    navigate('/login');
  };

  return (
    <Box
      sx={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        backgroundColor: '#f5f5f7',
        backgroundImage: `url(${backgroundlogin})`, 
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        backgroundRepeat: 'no-repeat',
      }}
    >
      <Paper
        elevation={3}
        sx={{
          padding: 4,
          width: '100%',
          maxWidth: 450,
          borderRadius: 3
        }}
      >
        <Stack spacing={3} direction="column">
          <Box sx={{ textAlign: 'center', mb: 1 }}>
            <Typography variant="h4" component="h1" sx={{ fontWeight: 'bold' }} color="primary">
              Crear cuenta
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
              Regístrate con tu correo institucional
            </Typography>
          </Box>

          <TextField
            label="Nombre"
            variant="outlined"
            fullWidth
            value={firstName}
            onChange={(e) => setFirstName(e.target.value)}
          />

          <TextField
            label="Apellido"
            variant="outlined"
            fullWidth
            value={lastName}
            onChange={(e) => setLastName(e.target.value)}
          />

          <TextField
            label="Correo institucional"
            variant="outlined"
            fullWidth
            value={institutionalEmail}
            onChange={(e) => setInstitutionalEmail(e.target.value)}
            helperText="Debe terminar en @icesi.edu.co o @u.icesi.edu.co"
          />

          <TextField
            label="Contraseña"
            type="password"
            variant="outlined"
            fullWidth
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />

          <TextField
            label="Edad"
            type="number"
            variant="outlined"
            fullWidth
            value={age}
            onChange={(e) => setAge(e.target.value)}
          />

          <Button
            onClick={handleRegister}
            variant="contained"
            size="large"
            fullWidth
            sx={{
              textTransform: 'none',
              fontWeight: 'bold',
              borderRadius: 2,
              py: 1
            }}
          >
            Registrarse
          </Button>

          <Button
            onClick={handleGoLogin}
            variant="contained"
            color="secondary"
            size="large"
            fullWidth
            sx={{
              textTransform: 'none',
              fontWeight: 'bold',
              borderRadius: 2,
              py: 1
            }}
          >
            Volver al login
          </Button>

          <Snackbar
            open={error}
            autoHideDuration={4000}
            onClose={() => setError(false)}
          >
            <Alert severity="error" onClose={() => setError(false)}>
              {errorMessage}
            </Alert>
          </Snackbar>

          <Snackbar
            open={success}
            autoHideDuration={2500}
            onClose={() => setSuccess(false)}
          >
            <Alert severity="success" onClose={() => setSuccess(false)}>
              Registro exitoso
            </Alert>
          </Snackbar>
        </Stack>
      </Paper>
    </Box>
  );
};

export default RegisterScreen;
