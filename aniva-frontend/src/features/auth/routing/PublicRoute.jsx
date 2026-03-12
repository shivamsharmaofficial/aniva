import { Navigate } from "react-router-dom";
import { useAuthStore } from "../store/useAuthStore";

const PublicRoute = ({ children }) => {
  const accessToken = useAuthStore(
    (state) => state.accessToken
  );

  if (accessToken) {
    return <Navigate to="/" replace />;
  }

  return children;
};

export default PublicRoute;