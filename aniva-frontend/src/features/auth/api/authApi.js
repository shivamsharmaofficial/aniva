import axiosInstance from "../../../api/axiosInstance";

export const loginUser = async (data) => {

  console.log("LOGIN PAYLOAD:", data);
  const response = await axiosInstance.post("/auth/login", data);
  return response.data;
};

export const registerUser = async (data) => {
  const response = await axiosInstance.post("/auth/register", data);
  return response.data;
};