import { useMemo } from "react";
import { useAuthStore } from "../store/useAuthStore";

/* =========================================
   CORE AUTH SELECTOR (Base Hook)
========================================= */
export const useAuth = () => {
  const {
    accessToken,
    refreshToken,
    user,
    isAuthLoading,
    isAuthenticated,
    logout,
    setUser,
  } = useAuthStore();

  const isAdmin = useMemo(() => {
    return user?.roles?.includes("ROLE_ADMIN");
  }, [user]);

  return {
    accessToken,
    refreshToken,
    user,
    isAuthLoading,
    isAuthenticated,
    isAdmin,
    logout,
    setUser,
  };
};

/* =========================================
   SPECIFIC SELECTORS (Safer Abstractions)
========================================= */

export const useUser = () =>
  useAuthStore((state) => state.user);

export const useIsAuthenticated = () =>
  useAuthStore((state) => state.isAuthenticated);

export const useIsAdmin = () =>
  useAuthStore((state) =>
    state.user?.roles?.includes("ROLE_ADMIN")
  );

export const useAuthLoading = () =>
  useAuthStore((state) => state.isAuthLoading);