import { useQuery } from "@tanstack/react-query";
import { fetchWishlist } from "../api/userApi";
import { userKeys } from "../api/queryKeys";

export const useWishlist = () => {
  return useQuery({
    queryKey: userKeys.wishlist(),
    queryFn: fetchWishlist,
  });
};