import { create } from "zustand";
import { persist } from "zustand/middleware";

export const useAuthStore = create(
  persist(
    (set) => ({
      accessToken: null,
      refreshToken: null,
      user: null,
      isAuthLoading: true,
      isAuthenticated: false,

      setSession: (accessToken, refreshToken, user = null) =>
        set({
          accessToken,
          refreshToken,
          user,
          isAuthenticated: !!accessToken,
        }),

      setUser: (user) =>
        set({ user }),

      setAuthLoading: (status) =>
        set({ isAuthLoading: status }),

      logout: () =>
        set({
          accessToken: null,
          refreshToken: null,
          user: null,
          isAuthenticated: false,
        }),
    }),
    {
      name: "aniva-auth",
    }
  )
);