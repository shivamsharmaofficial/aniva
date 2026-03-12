import { useMutation, useQueryClient } from "@tanstack/react-query";
import { removeFromWishlist } from "../api/userApi";
import { userKeys } from "../api/queryKeys";
import { useToast } from "@/components/ui/useToast";

export const useRemoveFromWishlist = () => {
  const queryClient = useQueryClient();
  const { showToast } = useToast();

  return useMutation({
    mutationFn: removeFromWishlist,

    onMutate: async (id) => {
      await queryClient.cancelQueries({
        queryKey: userKeys.wishlist(),
      });

      const previous = queryClient.getQueryData(
        userKeys.wishlist()
      );

      queryClient.setQueryData(
        userKeys.wishlist(),
        (old = []) =>
          old.filter((item) => item.id !== id)
      );

      return { previous };
    },

    onError: (err, id, context) => {
      queryClient.setQueryData(
        userKeys.wishlist(),
        context?.previous
      );

      showToast("Failed to remove item");
    },

    onSuccess: () => {
      showToast("Removed from wishlist");
    },
  });
};