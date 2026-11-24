import { createTheme } from "@mui/material/styles";

// HU13.2: todos los colores con texto blanco encima (y como texto sobre el fondo #f5f7fb)
// cumplen el contraste minimo WCAG AA de 4.5:1.
const FOCUS_RING = "#1f1fa8";

const theme = createTheme({
  palette: {
    primary: {
      main: "#5353EE", // 5.47:1 con blanco
      contrastText: "#ffffff",
    },
    secondary: {
      main: "#616161", // 6.19:1 (antes #757575: 4.30:1 sobre el fondo)
      contrastText: "#ffffff",
    },
    success: {
      main: "#2e7d32", // 5.13:1 (antes #4caf50: 2.78:1)
      contrastText: "#ffffff",
    },
    error: {
      main: "#d32f2f", // 4.98:1 (antes #f44336: 3.68:1)
      contrastText: "#ffffff",
    },
    warning: {
      main: "#b45309", // 5.02:1 (el de MUI #ed6c02: 3.11:1)
      contrastText: "#ffffff",
    },
    info: {
      main: "#01579b", // 7.40:1 (el de MUI #0288d1: 3.86:1)
      contrastText: "#ffffff",
    },
    background: {
      default: "#f5f7fb",
    },
  },
  typography: {
    fontFamily: '"Plus Jakarta Sans", sans-serif',
    button: {
      textTransform: "none",
      fontWeight: 600,
    },
  },
  components: {
    MuiCssBaseline: {
      styleOverrides: {
        body: {
          fontFamily: '"Plus Jakarta Sans", sans-serif',
          backgroundColor: "#f5f7fb",
        },
        // Foco siempre visible al navegar con el teclado.
        "*:focus-visible": {
          outline: `3px solid ${FOCUS_RING}`,
          outlineOffset: "2px",
        },
      },
    },
  },
});

export default theme;
