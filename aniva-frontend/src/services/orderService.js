import { apiPost } from "@/lib/apiClient";

export const createOrder = async () => {
  return await apiPost("/orders/checkout");
};
