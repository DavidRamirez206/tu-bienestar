import Stack from '@mui/material/Stack';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';
import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import { login as loginService } from '../../services/AuthService';
import { useNavigate } from "react-router-dom";
import useAuth from '../../hooks/useAuth';
import { useState } from 'react';
import Snackbar from "@mui/material/Snackbar";
import Alert from "@mui/material/Alert";
import backgroundlogin from "../../assets/backgroundlogin.jpg";

const LoginScreen: React.FC = () => {
    const navigate = useNavigate();
    const { login } = useAuth();

    const [username, setUsername] = useState<string>("");
    const [password, setPassword] = useState<string>("");
    const [error, setError] = useState<string | null>(null);

    const handleLogin = async (event: React.FormEvent): Promise<void> => {
        event.preventDefault();

        const authRequest = {
            username,
            password,
        };

        const result = await loginService(authRequest);
        
        if (result.error || !result.data) {
            setError(
                result.status === 401
                    ? 'Usuario o contraseña incorrectos'
                    : 'No fue posible iniciar sesión. Inténtalo de nuevo.'
            );
            return;
        }

        // The backend returns the token and user data
        login(result.data.token, result.data.user);

        navigate('/');
    };

    return (
        // main container
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
            {/* Form card*/}
            <Paper 
                elevation={3} 
                sx={{ 
                    padding: 4, 
                    width: '100%', 
                    maxWidth: 400, 
                    borderRadius: 3 
                }}
            >
                <Stack spacing={3} direction="column" component="form" onSubmit={handleLogin}>
                    <Box sx={{ textAlign: 'center', mb: 1 }}>
                        <Typography variant='h4' component="h1" sx={{fontWeight:"bold"}} color="primary">
                            Bienvenido
                        </Typography>
                        <Typography variant='body2' color="text.secondary" sx={{ mt: 1 }}>
                            Ingresa tus credenciales para acceder
                        </Typography>
                    </Box>

                    <TextField 
                        label='Correo institucional' 
                        type='email'
                        autoComplete='username'
                        variant='outlined'
                        fullWidth
                        value={username} 
                        onChange={(e) => setUsername(e.target.value)}
                    />

                    <TextField 
                        label='Contraseña' 
                        type='password' 
                        autoComplete='current-password'
                        variant='outlined'
                        fullWidth
                        value={password} 
                        onChange={(e) => setPassword(e.target.value)}
                       
                    />

                    <Button 
                        type="submit"
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
                        Iniciar sesión
                    </Button>

                    <Snackbar
                        open={error !== null}
                        autoHideDuration={4000}
                        onClose={() => setError(null)}
                    >
                        <Alert severity="error" onClose={() => setError(null)}>
                        {error}
                        </Alert>
                    </Snackbar>

                    
                </Stack>
            </Paper>
        </Box>
    );
};

export default LoginScreen;