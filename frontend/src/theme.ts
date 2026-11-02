import { createTheme } from "@mui/material/styles";

const theme = createTheme({
  palette: {
    primary: {
      main: "#5353EE",
    },
    secondary: {
      main: "#757575",
    },
    success: {
      main: "#4caf50",
    },
    error: {
      main: "#f44336",
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
      },
    },
  },
});

export default theme;
