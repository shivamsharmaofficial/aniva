import { useQuery } from "@tanstack/react-query";
import { fetchUserProfile } from "../api/userApi";
import { userKeys } from "../api/queryKeys";

export const useUserProfile = () => {
  return useQuery({
    queryKey: userKeys.profile(),
    queryFn: fetchUserProfile,
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
};