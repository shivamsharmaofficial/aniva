import axiosInstance from "@/api/axiosInstance";

export const getMyOrders = async () => {
  const response = await axiosInstance.get("/orders/my-orders");
  return response.data;
};

export const getOrderById = async (orderId) => {
  const response = await axiosInstance.get(`/orders/${orderId}`);
  return response.data;
};

export const getOrderItems = async (orderId) => {
  const response = await axiosInstance.get(`/orders/${orderId}/items`);
  return response.data;
};