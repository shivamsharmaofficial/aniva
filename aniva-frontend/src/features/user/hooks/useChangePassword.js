import { useMutation } from "@tanstack/react-query";
import { changePassword } from "../api/passwordApi";
import { useToast } from "@/components/ui/useToast";

export const useChangePassword = () => {
  const { showToast } = useToast();

  return useMutation({
    mutationFn: changePassword,
    onSuccess: () => {
      showToast("Password changed successfully");
    },
    onError: (error) => {
      showToast(
        error.response?.data?.message ||
        "Failed to change password"
      );
    },
  });
};