import { useMutation } from "@tanstack/react-query";
import { loginUser } from "@/features/auth/api/authApi";
import { setSession } from "../utils/authService";

export function useUserLogin() {
  return useMutation({
    mutationFn: loginUser,
    onSuccess: (response) => {
      const accessToken = response?.data?.data?.accessToken;
      const refreshToken = response?.data?.data?.refreshToken;

      if (!accessToken || !refreshToken) {
        throw new Error("Invalid auth response");
      }

      setSession(accessToken, refreshToken);
    },
  });
}