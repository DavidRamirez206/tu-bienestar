import { createBrowserRouter } from "react-router";
import LoginScreen from "../screens/auth/LoginScreen";
import ProtectedRoute from "../components/wrapper/ProtectedRoute";
import RegisterScreen from "../screens/auth/RegisterScreen";
import MainLayout from "../screens/auth/MainLayout";
import HomeScreen from "../screens/auth/HomeScreen";
import ProfileScreen from "../screens/profile/ProfileScreen";
import EditProfileScreen from "../screens/profile/EditProfileScreen";
import NotFoundScreen from "../screens/common/NotFoundScreen";
import RoutinesScreen from "../screens/routines/RoutinesScreen";
import RoutineDetailScreen from "../screens/routines/RoutineDetailScreen";
import ProgressScreen from "../screens/progress/ProgressScreen";
import ExercisesScreen from "../screens/exercises/ExercisesScreen";
import ActivitiesScreen from "../screens/activities/ActivitiesScreen";
import UnauthorizedScreen from "../screens/common/UnauthorizedScreen";

// Cada historia agrega aqui sus rutas.
const router = createBrowserRouter(
  [
    {
      path: "/login",
      element: <LoginScreen />,
    },
    {
      path: "/register",
      element: <RegisterScreen />,
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
            { path: "/profile", element: <ProfileScreen /> },
            { path: "/profile/edit", element: <EditProfileScreen /> },
            { path: "/routines", element: <RoutinesScreen /> },
            { path: "/routines/:id", element: <RoutineDetailScreen /> },
            { path: "/progress", element: <ProgressScreen /> },
            { path: "/exercises", element: <ExercisesScreen /> },
            { path: "/activities", element: <ActivitiesScreen /> },
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
