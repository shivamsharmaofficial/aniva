import axiosInstance from "@/api/axiosInstance";

export const getCategories = async () => {
  const res = await axiosInstance.get("/categories");
  return res.data;
};