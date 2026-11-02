import { createSlice } from "@reduxjs/toolkit";
import type { PayloadAction } from "@reduxjs/toolkit";
import { jwtDecode } from "jwt-decode";

export interface User {
  idUser: number;
  firstName: string;
  lastName: string;
  institutionalEmail: string;
  authorities?: string[];
}

interface JwtPayload {
  sub: string;
  name?: string;
  email?: string;
  authorities?: string[];
  exp?: number;
  iat?: number;
}

interface AuthState {
  token: string | null;
  user: User | null;
  isAuthenticated: boolean;
}

const getInfoFromToken = (
  token: string,
): { email: string; authorities: string[] } => {
  try {
    const decoded = jwtDecode<JwtPayload>(token);
    return {
      email: decoded.sub,
      authorities: decoded.authorities ?? [],
    };
  } catch {
    return { email: "", authorities: [] };
  }
};

const getStoredUser = (): User | null => {
  const raw = localStorage.getItem("user");

  if (!raw || raw === "undefined" || raw === "null") {
    return null;
  }

  try {
    return JSON.parse(raw) as User;
  } catch {
    return null;
  }
};

const initialToken = localStorage.getItem("token");
const initialUser = getStoredUser();

const initialState: AuthState = {
  token: initialToken,
  user: initialUser,
  isAuthenticated: !!initialToken,
};

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    login: (
      state,
      action: PayloadAction<{
        token: string;
        user?: Omit<User, "authorities">;
      }>,
    ) => {
      const { token, user } = action.payload;
      const { email, authorities } = getInfoFromToken(token);

      state.token = token;
      state.user = {
        idUser: user?.idUser ?? 0,
        firstName: user?.firstName ?? '',
        lastName: user?.lastName ?? '',
        institutionalEmail: user?.institutionalEmail ?? email,
        authorities,
      };
      state.isAuthenticated = true;
      localStorage.setItem("token", token);
      localStorage.setItem("user", JSON.stringify(state.user));
    },
    logout: (state) => {
      state.token = null;
      state.user = null;
      state.isAuthenticated = false;
      localStorage.removeItem("token");
      localStorage.removeItem("user");
    },
  },
});

export const { login, logout } = authSlice.actions;
export default authSlice.reducer;
