import { Navigate } from "react-router-dom";
import {
  useIsAuthenticated,
  useIsAdmin,
} from "../hooks/useAuthSelectors";

const RoleBasedRoute = ({ children, requiredRole }) => {
  const isAuthenticated = useIsAuthenticated();
  const isAdmin = useIsAdmin();

  if (!isAuthenticated) {
    return <Navigate to="/account/login" replace />;
  }

  if (requiredRole === "ROLE_ADMIN" && !isAdmin) {
    return <Navigate to="/403" replace />;
  }

  return children;
};

export default RoleBasedRoute;
