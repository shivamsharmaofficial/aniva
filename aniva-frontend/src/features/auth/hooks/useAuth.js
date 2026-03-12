import { useAuthStore } from "../store/useAuthStore";

export const useAuth = () => {
  const accessToken = useAuthStore((s) => s.accessToken);
  const user = useAuthStore((s) => s.user);
  const isAuthLoading = useAuthStore((s) => s.isAuthLoading);

  const isAuthenticated = !!accessToken;
  const isAdmin = user?.roles?.includes("ROLE_ADMIN");

  return {
    accessToken,
    user,
    isAuthenticated,
    isAdmin,
    isAuthLoading,
  };
};