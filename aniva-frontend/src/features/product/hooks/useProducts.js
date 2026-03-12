import { useQuery } from "@tanstack/react-query";
import { getProducts } from "@/features/product/api/productApi";

/**
 * Enterprise Product Hook
 * Stable cache key
 * Smart pagination
 */

export function useProducts(filters) {
  return useQuery({
    queryKey: [
      "products",
      filters.page,
      filters.size,
      filters.search,
      filters.status,
      filters.includeDeleted,
      filters.sort,
      filters.direction,
      JSON.stringify(filters.category ?? [])
    ],
    queryFn: () => getProducts(filters),
    keepPreviousData: true,
    staleTime: 1000 * 60 * 2,
  });
}