import { useQuery } from "@tanstack/react-query";
import { fetchDashboardStats } from "../api/analyticsApi";

export const useDashboardStats = () => {

  return useQuery({
    queryKey: ["dashboard-stats"],
    queryFn: fetchDashboardStats
  });

};