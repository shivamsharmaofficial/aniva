import { useQuery } from "@tanstack/react-query";
import { fetchCart } from "../api/cartApi";

export function useCart() {
  return useQuery({
    queryKey: ["cart"],
    queryFn: fetchCart,
  });
}