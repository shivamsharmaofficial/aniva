import { useQuery } from "@tanstack/react-query";
import { getMyOrders } from "../api/orderApi";

export const useOrders = () => {

  return useQuery({
    queryKey: ["orders"],
    queryFn: getMyOrders,
  });

};