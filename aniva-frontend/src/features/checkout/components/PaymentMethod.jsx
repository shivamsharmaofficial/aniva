import { useEffect, useState } from "react";
import { getPaymentMode } from "@/features/payment/api/paymentApi";

function PaymentMethod() {

  const [mode, setMode] = useState(null);

  useEffect(() => {

    const loadMode = async () => {
      const paymentMode = await getPaymentMode();
      setMode(paymentMode);
    };

    loadMode();

  }, []);

  return (

    <div className="payment-method">

      <h3>Payment Method</h3>

      {mode === "MOCK" && (
        <p>Mock Payment (Testing Mode)</p>
      )}

      {mode === "RAZORPAY" && (
        <p>Pay securely using Razorpay</p>
      )}

    </div>

  );
}

export default PaymentMethod;