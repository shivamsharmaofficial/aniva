import axiosInstance from "@/api/axiosInstance";

/* =========================
HELPER → EXTRACT ERROR
========================= */
const handleError = (err) => {
  const message =
    err?.response?.data?.message ||
    err?.response?.data?.error ||
    err?.message ||
    "Something went wrong";

  console.error("API ERROR:", message);

  return Promise.reject({
    message,
    status: err?.response?.status,
    raw: err,
  });
};

/* =========================
GET
========================= */
export const apiGet = async (url, config = {}) => {
  try {
    const res = await axiosInstance.get(url, config);
    return res.data?.data;
  } catch (err) {
    return handleError(err);
  }
};

/* =========================
POST
========================= */
export const apiPost = async (url, body, config = {}) => {
  try {
    const res = await axiosInstance.post(url, body, config);
    return res.data?.data;
  } catch (err) {
    return handleError(err);
  }
};

/* =========================
PUT
========================= */
export const apiPut = async (url, body, config = {}) => {
  try {
    const res = await axiosInstance.put(url, body, config);
    return res.data?.data;
  } catch (err) {
    return handleError(err);
  }
};

/* =========================
DELETE
========================= */
export const apiDelete = async (url, config = {}) => {
  try {
    const res = await axiosInstance.delete(url, config);
    return res.data?.data ?? true;
  } catch (err) {
    return handleError(err);
  }
};
