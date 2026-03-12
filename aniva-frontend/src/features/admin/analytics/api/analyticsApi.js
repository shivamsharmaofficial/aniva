import axiosInstance from "@/api/axiosInstance";

export const fetchDashboardStats = () => {
  return axiosInstance.get("/api/admin/analytics/dashboard");
};

export const fetchSalesChart = () => {
  return axiosInstance.get("/api/admin/analytics/sales");
};