import { Navigate, useLocation } from "react-router-dom";
import { useIsAuthenticated } from "../hooks/useAuthSelectors";

const ProtectedRoute = ({ children }) => {
  const isAuthenticated = useIsAuthenticated();
  const location = useLocation();

  if (!isAuthenticated) {
    return (
      <Navigate
        to="/account/login"
        state={{ from: location }}
        replace
      />
    );
  }

  return children;
};

export default ProtectedRoute;
