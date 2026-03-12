import axiosInstance from "@/api/axiosInstance";

export const fetchUserProfile = async () => {
  const response = await axiosInstance.get("/auth/me");
  return response.data?.data;
};

export const updateUserProfile = async (data) => {
  const response = await axiosInstance.put("/user/profile", data);
  return response.data?.data;
};

export const fetchAddresses = async () => {
  const response = await axiosInstance.get("/user/addresses");
  return response.data?.data || [];
};

export const addAddress = async (data) => {
  const response = await axiosInstance.post(
    "/user/addresses",
    data
  );
  return response.data?.data;
};

export const updateAddress = async ({ id, data }) => {
  const response = await axiosInstance.put(
    `/user/addresses/${id}`,
    data
  );
  return response.data?.data;
};

export const deleteAddress = async (id) => {
  await axiosInstance.delete(`/user/addresses/${id}`);
  return id;
};

export const fetchOrders = async ({ page = 0 }) => {
  const response = await axiosInstance.get(
    `/user/orders?page=${page}&size=5`
  );

  return response.data?.data;
};

export const fetchWishlist = async () => {
  const response = await axiosInstance.get("/user/wishlist");
  return response.data?.data || [];
};

export const removeFromWishlist = async (id) => {
  await axiosInstance.delete(`/user/wishlist/${id}`);
  return id;
};