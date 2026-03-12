import { jwtDecode } from "jwt-decode";
import { useAuthStore } from "../store/useAuthStore";

// ==========================
// INTERNAL AUTO LOGOUT TIMER
// ==========================

let logoutTimer = null;

// ==========================
// SESSION MANAGEMENT
// ==========================

export const setSession = (accessToken, refreshToken, user = null) => {
  const { setSession } = useAuthStore.getState();
  setSession(accessToken, refreshToken, user);

  if (accessToken) {
    scheduleAutoLogout(accessToken);
  }
};

export const clearSession = () => {
  clearAutoLogoutTimer();

  const { logout } = useAuthStore.getState();
  logout();
};

export const getAccessToken = () =>
  useAuthStore.getState().accessToken;

export const getRefreshToken = () =>
  useAuthStore.getState().refreshToken;

// ==========================
// TOKEN HELPERS
// ==========================

export const decodeToken = (token) => {
  try {
    return jwtDecode(token);
  } catch {
    return null;
  }
};

export const isTokenExpired = (token) => {
  if (!token) return true;

  const decoded = decodeToken(token);
  if (!decoded?.exp) return true;

  return decoded.exp * 1000 < Date.now();
};

// ==========================
// AUTO LOGOUT LOGIC
// ==========================

export const scheduleAutoLogout = (token) => {
  clearAutoLogoutTimer();

  if (!token) return;

  const decoded = decodeToken(token);
  if (!decoded?.exp) return;

  const expiryTime = decoded.exp * 1000;
  const timeout = expiryTime - Date.now();

  if (timeout <= 0) {
    clearSession();
    return;
  }

  logoutTimer = setTimeout(() => {
    clearSession();
  }, timeout);
};

export const clearAutoLogoutTimer = () => {
  if (logoutTimer) {
    clearTimeout(logoutTimer);
    logoutTimer = null;
  }
};