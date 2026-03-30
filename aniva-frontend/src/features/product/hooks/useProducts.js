import { useQuery } from "@tanstack/react-query";
import { fetchProducts } from "@/services/productService";

export function useProducts(filters = {}) {
return useQuery({
queryKey: ["products", filters],
queryFn: () => fetchProducts(filters),
keepPreviousData: true,
staleTime: 1000 * 60 * 2,
enabled: true,
});
}
