import axiosInstance from "@/lib/axios";

export const fetchCart = () => {
  return axiosInstance.get("/cart");
};

export const addToCart = (variantId, quantity) => {
  return axiosInstance.post("/cart/items", {
    variantId,
    quantity,
  });
};

export const removeFromCart = (itemId) => {
  return axiosInstance.delete(`/cart/items/${itemId}`);
};