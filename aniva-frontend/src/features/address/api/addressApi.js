import axiosInstance from "@/api/axiosInstance";

export const fetchAddresses = () => {
  return axiosInstance.get("/api/addresses");
};

export const createAddress = (data) => {
  return axiosInstance.post("/api/addresses", data);
};

export const updateAddress = (id, data) => {
  return axiosInstance.put(`/api/addresses/${id}`, data);
};

export const deleteAddress = (id) => {
  return axiosInstance.delete(`/api/addresses/${id}`);
};