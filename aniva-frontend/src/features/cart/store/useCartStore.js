import { create } from "zustand";
import axiosInstance from "@/api/axiosInstance";
import { useAuthStore } from "../../auth/store/useAuthStore";

export const useCartStore = create((set, get) => ({

  items: [],
  loading: false,

  /* ===============================
     LOAD CART FROM DATABASE
  =============================== */

  loadCart: async () => {

    const user = useAuthStore.getState().user;

    if (user?.roles?.some(r => r.roleName === "ROLE_ADMIN")) {
      return;
    }

    try {

      set({ loading: true });

      const res = await axiosInstance.get("/cart");

      set({
        items: res.data?.data ?? []
      });

    } catch (err) {

      console.error("Cart load failed", err);

    } finally {

      set({ loading: false });

    }

  },

  /* ===============================
     ADD ITEM
  =============================== */

  addToCart: async (product, quantity = 1) => {

    const user = useAuthStore.getState().user;

    if (user?.roles?.some(r => r.roleName === "ROLE_ADMIN")) {
      return;
    }

    try {

      set({ loading: true });

      await axiosInstance.post("/cart/add", {
        productId: product.id,
        quantity
      });

      await get().loadCart();

    } catch (err) {

      console.error("Add to cart failed", err);

    } finally {

      set({ loading: false });

    }

  },

  /* ===============================
     REMOVE ITEM
  =============================== */

  removeFromCart: async (itemId) => {

    try {

      await axiosInstance.delete(`/cart/items/${itemId}`);

      set({
        items: get().items.filter(i => i.id !== itemId)
      });

    } catch (err) {

      console.error("Remove failed", err);

    }

  },

  /* ===============================
     CLEAR CART
  =============================== */

  clearCart: async () => {

    try {

      const items = get().items;

      await Promise.all(
        items.map(item =>
          axiosInstance.delete(`/cart/items/${item.id}`)
        )
      );

      set({ items: [] });

    } catch (err) {

      console.error("Clear cart failed", err);

    }

  }

}));