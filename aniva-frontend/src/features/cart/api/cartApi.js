import { apiGet, apiPost, apiDelete } from "@/lib/apiClient";

export const fetchCart = async () => {
  return await apiGet("/cart");
};

export const addToCartApi = async (payload) => {
  return await apiPost("/cart/add", payload);
};

export const removeFromCartApi = async (itemId) => {
  return await apiDelete(`/cart/items/${itemId}`);
};
