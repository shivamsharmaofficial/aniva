import { useQuery } from "@tanstack/react-query";
import { getCategories } from "@/features/product/api/categoryApi";

export function useCategories() {
  return useQuery({
    queryKey: ["categories"],
    queryFn: getCategories,
    staleTime: 1000 * 60 * 10,
  });
}