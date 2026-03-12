import { useQuery } from "@tanstack/react-query";
import { getOrderById, getOrderItems } from "../api/orderApi";

export const useOrderDetails = (orderId) => {

  const orderQuery = useQuery({
    queryKey: ["order", orderId],
    queryFn: () => getOrderById(orderId),
    enabled: !!orderId,
  });

  const itemsQuery = useQuery({
    queryKey: ["order-items", orderId],
    queryFn: () => getOrderItems(orderId),
    enabled: !!orderId,
  });

  return {
    order: orderQuery.data,
    items: itemsQuery.data,
    isLoading: orderQuery.isLoading || itemsQuery.isLoading,
  };
};