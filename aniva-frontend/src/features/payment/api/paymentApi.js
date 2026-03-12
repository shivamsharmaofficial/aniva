import axiosInstance from "@/api/axiosInstance";

/* =============================
   GET CURRENT PAYMENT MODE
============================= */

export const getPaymentMode = async () => {
  const response = await axiosInstance.get("/payments/mode");
  return response.data?.data;
};

/* =============================
   CONFIRM PAYMENT
============================= */

export const confirmPayment = async (orderId, paymentId) => {

  const response = await axiosInstance.post("/payments/confirm", {
    orderId,
    paymentId
  });

  return response.data?.data;
};

/* =============================
   ADMIN CHANGE PAYMENT MODE
============================= */

export const changePaymentMode = async (mode) => {

  const response = await axiosInstance.post(`/payments/mode?mode=${mode}`);

  return response.data?.data;
};
