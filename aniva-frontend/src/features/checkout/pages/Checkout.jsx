import { useState } from "react";
import { useLocation } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";
import { useCart } from "@/features/cart/hooks/useCart";
import { useToast } from "@/components/ui/useToast";
import { createOrder } from "@/services/orderService";
import axios from "axios";

import OrderSummary from "../components/OrderSummary";
import PaymentMethod from "../components/PaymentMethod";
import AddressForm from "@/features/address/components/AddressForm";

import "../styles/checkout.css";

// ✅ Razorpay Loader
const loadRazorpayScript = () => {
  return new Promise((resolve) => {
    const script = document.createElement("script");
    script.src = "https://checkout.razorpay.com/v1/checkout.js";

    script.onload = () => resolve(true);
    script.onerror = () => resolve(false);

    document.body.appendChild(script);
  });
};

const Checkout = () => {
  const location = useLocation();
  const { data, isLoading, isError } = useCart();
  const items = Array.isArray(data) ? data : [];
  const buyNowItem = location.state?.buyNowItem;
  const finalItems = buyNowItem ? [buyNowItem] : items;

  const queryClient = useQueryClient();
  const { showToast } = useToast();
  const [loading, setLoading] = useState(false);

  const totalAmount = (finalItems || []).reduce(
    (sum, item) =>
      sum + (Number(item?.price) || 0) * (Number(item?.quantity) || 0),
    0
  );

  // ✅ VERIFY PAYMENT FUNCTION
  const verifyPayment = async (response, orderId) => {
    try {
      await axios.post("/api/payments/verify", {
        orderId: orderId,
        razorpayOrderId: response.razorpay_order_id,
        razorpayPaymentId: response.razorpay_payment_id,
        razorpaySignature: response.razorpay_signature,
      });

      queryClient.invalidateQueries({ queryKey: ["cart"] });

      showToast("Payment successful 🎉");

      window.location.href = "/order-success";
    } catch (err) {
      console.error("Verification failed", err);
      showToast("Payment verification failed ❌");
    }
  };

  const handleCheckout = async () => {
    if (loading) return;

    if (finalItems.length === 0) {
      showToast("Cart is empty ❌");
      return;
    }

    setLoading(true);

    try {
      // ✅ Load Razorpay SDK
      const loaded = await loadRazorpayScript();
      if (!loaded) {
        showToast("Razorpay SDK failed to load ❌");
        return;
      }

      // ✅ Call backend
      const res = await createOrder();

      const { order, paymentData } = res || {};

      if (!order?.id || !paymentData) {
        throw new Error("Invalid checkout response");
      }

      // ✅ Parse payment data
      const payment = JSON.parse(paymentData);

      // ✅ Razorpay options
      const options = {
        key: import.meta.env.VITE_RAZORPAY_KEY,
        amount: payment.amount,
        currency: payment.currency,
        name: "ANIVA",
        description: "Order Payment",
        order_id: payment.id,

        handler: async function (response) {
          await verifyPayment(response, order.id);
        },

        prefill: {
          name: order.userName || "Customer",
          email: order.userEmail || "",
        },

        theme: {
          color: "#3399cc",
        },
      };

      const rzp = new window.Razorpay(options);

      // ✅ ERROR HANDLING
      rzp.on("payment.failed", function (response) {
        console.error("Payment Failed:", response.error);

        showToast("Payment failed ❌ Try again");
      });

      rzp.open();
    } catch (err) {
      console.error("Checkout failed", err);
      showToast("Checkout failed ❌");
    } finally {
      setLoading(false);
    }
  };

  if (isLoading) {
    return <div className="checkout-page">Loading checkout...</div>;
  }

  if (isError && !buyNowItem) {
    return <div className="checkout-page">Unable to load checkout right now.</div>;
  }

  if (finalItems.length === 0) {
    return (
      <div className="checkout-page checkout-empty">
        <h2>Your cart is empty</h2>
      </div>
    );
  }

  return (
    <section className="checkout-page">
      <h2 className="checkout-title">Checkout</h2>

      <div className="checkout-layout">
        <div className="checkout-left">
          <AddressForm />
          <PaymentMethod />

          <div className="checkout-items">
            {finalItems.map((item) => (
              <div key={item.id || item.productId} className="checkout-item">
                <img src={item.imageUrl || item.image} alt={item.name} />

                <div className="checkout-item-info">
                  <h4>{item.name}</h4>
                  <p>Quantity: {item.quantity}</p>
                  <p>₹{item.price}</p>
                </div>
              </div>
            ))}
          </div>
        </div>

        <OrderSummary
          totalAmount={totalAmount}
          loading={loading}
          onCheckout={handleCheckout}
        />
      </div>
    </section>
  );
};

export default Checkout;