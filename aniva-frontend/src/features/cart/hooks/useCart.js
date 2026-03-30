import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  fetchCart,
  addToCartApi,
  removeFromCartApi,
} from "../api/cartApi";

/* ==============================
GET CART
============================== */

export function useCart() {
  return useQuery({
    queryKey: ["cart"],
    queryFn: fetchCart,
    staleTime: 1000 * 60,
  });
}

/* ==============================
ADD TO CART
============================== */

export function useAddToCart() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: addToCartApi,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["cart"] });
    },
  });
}

/* ==============================
REMOVE FROM CART
============================== */

export function useRemoveFromCart() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: removeFromCartApi,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["cart"] });
    },
  });
}
