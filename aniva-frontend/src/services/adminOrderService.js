import { apiGet, apiPut } from "@/lib/apiClient";

export const fetchAllOrders = () => apiGet("/admin/orders");

export const updateOrderStatus = (id, status) =>
  apiPut(`/admin/orders/${id}/status`, { status });
