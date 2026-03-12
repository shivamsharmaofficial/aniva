import axios from "axios";
import {
  getAccessToken,
  getRefreshToken,
  setSession,
  clearSession,
} from "@/features/auth/utils/authService";

/* ==============================
   AXIOS INSTANCE
============================== */

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_URL, // should be http://localhost:8080/api
  withCredentials: false,
});

/* ==============================
   REFRESH CONTROL VARIABLES
============================== */

let isRefreshing = false;
let failedQueue = [];

/* ==============================
   PROCESS FAILED REQUEST QUEUE
============================== */

const processQueue = (error, token = null) => {
  failedQueue.forEach((promise) => {
    if (error) {
      promise.reject(error);
    } else {
      promise.resolve(token);
    }
  });

  failedQueue = [];
};

/* ==============================
   REFRESH ACCESS TOKEN FUNCTION
============================== */

const refreshAccessToken = async () => {
  const refreshToken = getRefreshToken();

  if (!refreshToken) {
    throw new Error("No refresh token available");
  }

  const response = await axios.post(
    `${import.meta.env.VITE_API_URL}/auth/refresh`,
    null,
    {
      params: { refreshToken },
    }
  );

  const newAccessToken = response.data?.data?.accessToken;
  const newRefreshToken = response.data?.data?.refreshToken;

  if (!newAccessToken || !newRefreshToken) {
    throw new Error("Invalid refresh response");
  }

  setSession(newAccessToken, newRefreshToken);

  return newAccessToken;
};

/* ==============================
   REQUEST INTERCEPTOR
============================== */

axiosInstance.interceptors.request.use(
  (config) => {
    const token = getAccessToken();

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

/* ==============================
   RESPONSE INTERCEPTOR
============================== */

axiosInstance.interceptors.response.use(
  (response) => response,

  async (error) => {
    const originalRequest = error.config;

    if (!error.response) {
      console.error("Network Error:", error);
      return Promise.reject(error);
    }

    const status = error.response.status;

    /* ==============================
       HANDLE 401 (TOKEN EXPIRED)
    ============================== */

    if (status === 401 && !originalRequest._retry) {
      const refreshToken = getRefreshToken();

      if (!refreshToken) {
        clearSession();
        window.location.href = "/account/login";
        return Promise.reject(error);
      }

      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then((token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`;
            return axiosInstance(originalRequest);
          })
          .catch((err) => Promise.reject(err));
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const newAccessToken = await refreshAccessToken();

        processQueue(null, newAccessToken);

        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;

        return axiosInstance(originalRequest);
      } catch (refreshError) {
        processQueue(refreshError, null);
        clearSession();
        window.location.href = "/account/login";
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    /* ==============================
       HANDLE 403
    ============================== */

    if (status === 403) {
      console.warn("Forbidden request:", originalRequest.url);
      return Promise.reject(error);
    }

    /* ==============================
       DEBUG CHECKOUT FAILURES
    ============================== */

    if (status === 500) {
      console.error("Server Error:", error.response.data);
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;
