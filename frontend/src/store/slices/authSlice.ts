import { createSlice } from "@reduxjs/toolkit";
import type { PayloadAction } from "@reduxjs/toolkit";

export interface User {
  idUser: number;
  firstName: string;
  lastName: string;
  institutionalEmail: string;
  authorities?: string[];
}

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  // Al abrir la aplicacion todavia no se sabe si la cookie de sesion sigue siendo valida.
  isCheckingSession: boolean;
}

const initialState: AuthState = {
  user: null,
  isAuthenticated: false,
  isCheckingSession: true,
};

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    login: (
      state,
      action: PayloadAction<{
        user: Omit<User, "authorities">;
        authorities: string[];
      }>,
    ) => {
      const { user, authorities } = action.payload;

      state.user = {
        idUser: user.idUser,
        firstName: user.firstName,
        lastName: user.lastName,
        institutionalEmail: user.institutionalEmail,
        authorities,
      };
      state.isAuthenticated = true;
      state.isCheckingSession = false;
    },
    logout: (state) => {
      state.user = null;
      state.isAuthenticated = false;
      state.isCheckingSession = false;
    },
  },
});

export const { login, logout } = authSlice.actions;
export default authSlice.reducer;
