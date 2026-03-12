import { useMutation, useQueryClient } from "@tanstack/react-query";
import { deleteAddress } from "../api/userApi";
import { userKeys } from "../api/queryKeys";
import { toast } from "react-hot-toast";

export const useDeleteAddress = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deleteAddress,

    onMutate: async (id) => {
      await queryClient.cancelQueries({
        queryKey: userKeys.addresses(),
      });

      const previous = queryClient.getQueryData(
        userKeys.addresses()
      );

      queryClient.setQueryData(
        userKeys.addresses(),
        (old = []) => old.filter((a) => a.id !== id)
      );

      return { previous };
    },

    onError: (err, id, context) => {
      queryClient.setQueryData(
        userKeys.addresses(),
        context.previous
      );
      toast.error("Failed to delete address");
    },

    onSuccess: () => {
      toast.success("Address removed");
    },
  });
};