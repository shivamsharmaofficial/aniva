import { useMutation, useQueryClient } from "@tanstack/react-query";
import { addAddress } from "../api/userApi";
import { userKeys } from "../api/queryKeys";
import { toast } from "react-hot-toast";

export const useAddAddress = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: addAddress,

    onMutate: async (newAddress) => {
      await queryClient.cancelQueries({
        queryKey: userKeys.addresses(),
      });

      const previous = queryClient.getQueryData(
        userKeys.addresses()
      );

      queryClient.setQueryData(
        userKeys.addresses(),
        (old = []) => [
          ...old,
          { ...newAddress, id: Date.now() },
        ]
      );

      return { previous };
    },

    onError: (err, newAddress, context) => {
      queryClient.setQueryData(
        userKeys.addresses(),
        context.previous
      );
      toast.error("Failed to add address");
    },

    onSuccess: () => {
      toast.success("Address added");
    },

    onSettled: () => {
      queryClient.invalidateQueries({
        queryKey: userKeys.addresses(),
      });
    },
  });
};