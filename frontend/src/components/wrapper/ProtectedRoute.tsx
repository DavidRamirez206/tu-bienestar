import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import useAuth from '../../hooks/useAuth';

interface ProtectedRouteProps {
  redirectPath?: string;    // Where to redirect if not authenticated
  requiredRoles?: string[]; // Optional roles required to access the route
  children?: React.ReactNode;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ 
  redirectPath = '/login', 
  requiredRoles,
  children 
}) => {
  const { isAuthenticated, user } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to={redirectPath} replace />;
  }

  if (requiredRoles && requiredRoles.length > 0) {
    const hasRequiredRole = requiredRoles.some(role => {
      // Exact match
      if (user?.authorities?.includes(role)) {return true;}

      // Clean match (no ROLE_ prefix)
      const cleanRole = role.startsWith('ROLE_') ? role.substring(5) : role;
      if (user?.authorities?.includes(cleanRole)) {return true;}

      // Admin fallback
      if (user?.authorities?.includes('ROLE_ADMIN') || user?.authorities?.includes('ADMIN')) {return true;}

      return false;
    });

    if (!hasRequiredRole) {
      return <Navigate to="/unauthorized" replace />;
    }
  }

  // If children are provided, render them, else render nested routes
  return children ? <>{children}</> : <Outlet />;
};

export default ProtectedRoute;
