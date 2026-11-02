import React from "react";
import { Snackbar, Alert } from "@mui/material";
import { useAppSelector } from "../../hooks/useSelector";
import { useAppDispatch } from "../../hooks/useDispatch";
import { hideSnackbar } from "../../store/slices/uiSlice";

const GlobalSnackbar: React.FC = () => {
  const dispatch = useAppDispatch();
  const { open, message, severity } = useAppSelector(
    (state) => state.ui.snackbar,
  );

  const handleClose = (
    _event?: React.SyntheticEvent | Event,
    reason?: string,
  ) => {
    if (reason === "clickaway") {
      return;
    }
    dispatch(hideSnackbar());
  };

  return (
    <Snackbar
      open={open}
      autoHideDuration={4000}
      onClose={handleClose}
      anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
    >
      <Alert
        onClose={handleClose}
        severity={severity}
        sx={{ width: "100%" }}
        variant="filled"
      >
        {message}
      </Alert>
    </Snackbar>
  );
};

export default GlobalSnackbar;
