import { useEffect } from "react";
import { BrowserRouter } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import Router from "./router";
import { ToastProvider } from "@/components/ui/ToastProvider";
import ErrorBoundary from "@/components/common/ErrorBoundary";
import axiosInstance from "@/api/axiosInstance";

import {
  clearSession,
  isTokenExpired,
  scheduleAutoLogout,
} from "@/features/auth/utils/authService";

import { useAuthStore } from "@/features/auth/store/useAuthStore";

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});

function App() {
  const accessToken = useAuthStore((s) => s.accessToken);
  const setUser = useAuthStore((s) => s.setUser);
  const setAuthLoading = useAuthStore((s) => s.setAuthLoading);

  /* ===============================
     SESSION BOOTSTRAP
  =============================== */
  useEffect(() => {
    const bootstrapAuth = async () => {
      setAuthLoading(true);

      if (!accessToken) {
        setAuthLoading(false);
        return;
      }

      if (isTokenExpired(accessToken)) {
        clearSession();
        setAuthLoading(false);
        return;
      }

      try {
        const res = await axiosInstance.get("/auth/me");
        setUser(res.data?.data);
        scheduleAutoLogout(accessToken);
      } catch {
        clearSession();
      } finally {
        setAuthLoading(false);
      }
    };

    bootstrapAuth();
  }, [accessToken, setUser, setAuthLoading]);

  /* ===============================
     INACTIVITY AUTO LOGOUT
  =============================== */
  useEffect(() => {
    if (!accessToken) return;

    let timeout;

    const resetTimer = () => {
      clearTimeout(timeout);

      timeout = setTimeout(() => {
        clearSession();
      }, 15 * 60 * 1000);
    };

    ["mousemove", "keydown", "click", "scroll"].forEach((event) =>
      window.addEventListener(event, resetTimer)
    );

    resetTimer();

    return () => {
      clearTimeout(timeout);
      ["mousemove", "keydown", "click", "scroll"].forEach((event) =>
        window.removeEventListener(event, resetTimer)
      );
    };
  }, [accessToken]);

  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <ErrorBoundary>
          <ToastProvider>
            <Router />
          </ToastProvider>
        </ErrorBoundary>
      </BrowserRouter>
    </QueryClientProvider>
  );
}

export default App;