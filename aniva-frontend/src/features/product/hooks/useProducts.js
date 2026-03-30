import { useCallback, useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import { fetchProducts } from "@/services/productService";

export function useProducts(filters = {}) {
  const stableFilters = useMemo(
    () => ({
      category: Array.isArray(filters.category) ? filters.category : [],
      search: filters.search || "",
      sort: filters.sort || "createdAt",
      direction: filters.direction || "desc",
      page: filters.page ?? 0,
      size: filters.size ?? 12,
      minPrice: filters.minPrice ?? 0,
      maxPrice: filters.maxPrice ?? 5000,
    }),
    [
      filters.category,
      filters.search,
      filters.sort,
      filters.direction,
      filters.page,
      filters.size,
      filters.minPrice,
      filters.maxPrice,
    ]
  );

  const queryKey = useMemo(
    () => [
      "products",
      stableFilters.category.join("|"),
      stableFilters.search,
      stableFilters.sort,
      stableFilters.direction,
      stableFilters.page,
      stableFilters.size,
      stableFilters.minPrice,
      stableFilters.maxPrice,
    ],
    [stableFilters]
  );

  const fetchProductsQuery = useCallback(
    () => fetchProducts(stableFilters),
    [stableFilters]
  );

  return useQuery({
    queryKey,
    queryFn: fetchProductsQuery,
    keepPreviousData: true,
    staleTime: 1000 * 60 * 2,
  });
}
