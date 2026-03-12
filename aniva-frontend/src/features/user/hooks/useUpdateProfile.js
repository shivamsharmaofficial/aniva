import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateUserProfile } from "../api/userApi";
import { userKeys } from "../api/queryKeys";
import { useToast } from "@/components/ui/useToast";

export const useUpdateProfile = () => {
  const queryClient = useQueryClient();
  const { showToast } = useToast();

  return useMutation({
    mutationFn: updateUserProfile,

    onMutate: async (newData) => {
      await queryClient.cancelQueries({
        queryKey: userKeys.profile(),
      });

      const previousUser = queryClient.getQueryData(
        userKeys.profile()
      );

      queryClient.setQueryData(
        userKeys.profile(),
        (old) => ({
          ...old,
          ...newData,
        })
      );

      return { previousUser };
    },

    onError: (err, newData, context) => {
      queryClient.setQueryData(
        userKeys.profile(),
        context.previousUser
      );
      showToast("Failed to update profile");
    },

    onSuccess: () => {
      showToast("Profile updated successfully");
    },

    onSettled: () => {
      queryClient.invalidateQueries({
        queryKey: userKeys.profile(),
      });
    },
  });
};