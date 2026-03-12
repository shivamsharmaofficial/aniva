import axiosInstance from "@/api/axiosInstance";

export const changePassword = async (data) => {
  const response = await axiosInstance.put(
    "/user/change-password",
    data
  );
  return response.data;
};