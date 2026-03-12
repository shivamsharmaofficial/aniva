import { useQuery } from "@tanstack/react-query";
import { fetchOrders } from "../api/userApi";
import { userKeys } from "../api/queryKeys";

export const useOrders = (page) => {
  return useQuery({
    queryKey: userKeys.orders(page),
    queryFn: () => fetchOrders({ page }),
    keepPreviousData: true,
  });
};