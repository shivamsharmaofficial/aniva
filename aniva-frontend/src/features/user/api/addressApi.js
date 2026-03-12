import axiosInstance from "@/api/axiosInstance";

export const getAddresses = async () => {
  const response = await axiosInstance.get("/user/addresses");
  return response.data.data;
};

export const addAddress = async (data) => {
  const response = await axiosInstance.post("/user/addresses", data);
  return response.data.data;
};

export const updateAddress = async (id, data) => {
  const response = await axiosInstance.put(`/user/addresses/${id}`, data);
  return response.data.data;
};

export const deleteAddress = async (id) => {
  await axiosInstance.delete(`/user/addresses/${id}`);
  return id;
};

export const setDefaultAddress = async (id) => {
  const response = await axiosInstance.put(
    `/user/addresses/${id}/default`
  );
  return response.data.data;
};