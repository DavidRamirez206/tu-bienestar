import { useAppSelector } from "./useSelector";
import { useAppDispatch } from "./useDispatch";
import {
  login as loginAction,
  logout as logoutAction,
  type User,
} from "../store/slices/authSlice";

export default function useAuth() {
  const dispatch = useAppDispatch();
  const { user, isAuthenticated, isCheckingSession } = useAppSelector(
    (state) => state.auth,
  );

  const login = (userData: Omit<User, "authorities">, authorities: string[]) => {
    dispatch(loginAction({ user: userData, authorities }));
  };

  const logout = () => {
    dispatch(logoutAction());
  };

  return {
    user,
    isAuthenticated,
    isCheckingSession,
    login,
    logout,
  };
}
