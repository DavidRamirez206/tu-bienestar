import { useAppSelector } from "./useSelector";
import { useAppDispatch } from "./useDispatch";
import {
  login as loginAction,
  logout as logoutAction,
  type User,
} from "../store/slices/authSlice";

export default function useAuth() {
  const dispatch = useAppDispatch();
  const { token, user, isAuthenticated } = useAppSelector(
    (state) => state.auth,
  );

  const login = (token: string, userData?: Omit<User, "authorities">) => {
    dispatch(loginAction({ token, user: userData }));
  };

  const logout = () => {
    dispatch(logoutAction());
  };

  return {
    token,
    user,
    isAuthenticated,
    login,
    logout,
  };
}
