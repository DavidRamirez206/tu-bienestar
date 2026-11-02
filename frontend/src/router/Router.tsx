import { createBrowserRouter } from "react-router";
import LoginScreen from "../screens/auth/LoginScreen";
import ProtectedRoute from "../components/wrapper/ProtectedRoute";
import MainLayout from "../screens/auth/MainLayout";
import HomeScreen from "../screens/auth/HomeScreen";
import NotFoundScreen from "../screens/common/NotFoundScreen";
import UnauthorizedScreen from "../screens/common/UnauthorizedScreen";

// Cada historia agrega aqui sus rutas.
const router = createBrowserRouter(
  [
    {
      path: "/login",
      element: <LoginScreen />,
    },
    {
      path: "/unauthorized",
      element: <UnauthorizedScreen />,
    },
    {
      path: "/",
      element: <ProtectedRoute />,
      children: [
        {
          element: <MainLayout />,
          children: [
            { index: true, element: <HomeScreen /> },
          ],
        },
      ],
    },
    {
      path: "*",
      element: <NotFoundScreen />,
    },
  ],
  {
    basename: import.meta.env.BASE_URL,
  },
);

export default router;
