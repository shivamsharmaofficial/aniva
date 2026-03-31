import axiosInstance from "@/api/axiosInstance";

/* =============================
   FETCH ALL ORDERS
============================= */
export const fetchAllOrders = async () => {
  const res = await axiosInstance.get("/api/admin/orders");
  return res.data?.data;
};

/* =============================
   UPDATE ORDER STATUS
============================= */
export const updateOrderStatus = async (orderId, status) => {
  const res = await axiosInstance.put(
    `/api/admin/orders/${orderId}/status?status=${status}`
  );
  return res.data?.data;
};