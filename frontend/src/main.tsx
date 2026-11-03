import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { RouterProvider } from 'react-router';
import { ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import theme from './theme';
import { Provider } from 'react-redux';
import { store } from './store/store';
import { login, logout } from './store/slices/authSlice';
import { setUnauthorizedHandler } from './lib/axios/axiosClient';
import { getSession } from './services/AuthService';
import router from './router/Router';
import './utils/index.css';

const rootElement = document.getElementById('root');

if (!rootElement) {
  throw new Error('Failed to find the root element');
}

// Datos de versiones anteriores, que guardaban el token en el navegador.
['token', 'user', 'persist:root'].forEach((key) => localStorage.removeItem(key));

setUnauthorizedHandler(() => store.dispatch(logout()));

void getSession().then((result) => {
  store.dispatch(result.data ? login(result.data) : logout());
});

createRoot(rootElement).render(
  <StrictMode>
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Provider store={store}>
        <RouterProvider router={router} />
      </Provider>
    </ThemeProvider>
  </StrictMode>
);