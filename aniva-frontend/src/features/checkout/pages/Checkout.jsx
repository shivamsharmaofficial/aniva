import { useState } from "react";
import { useLocation } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";
import { useCart } from "@/features/cart/hooks/useCart";
import {
  getPaymentMode,
  confirmPayment,
} from "@/features/payment/api/paymentApi";
import { useToast } from "@/components/ui/useToast";
import { createOrder } from "@/services/orderService";

import OrderSummary from "../components/OrderSummary";
import PaymentMethod from "../components/PaymentMethod";
import AddressForm from "@/features/address/components/AddressForm";

import "../styles/checkout.css";

const Checkout = () => {
  const location = useLocation();
  const { data: items = [], isLoading } = useCart();
  const buyNowItem = location.state?.buyNowItem;
  const finalItems = buyNowItem ? [buyNowItem] : items;
  const queryClient = useQueryClient();
  const { showToast } = useToast();
  const [loading, setLoading] = useState(false);

  const totalAmount = finalItems.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0
  );

  const handleCheckout = async () => {
    if (loading) return;

    if (finalItems.length === 0) {
      showToast("Cart is empty ❌");
      return;
    }

    setLoading(true);

    try {
      const order = await createOrder();

      const mode = await getPaymentMode();

      /* ===============================
         MOCK PAYMENT
      =============================== */

      if (mode === "MOCK") {
        await confirmPayment(order.id, "MOCK_PAYMENT");

        queryClient.invalidateQueries({ queryKey: ["cart"] });

        showToast("Payment successful 🎉");

        window.location.href = "/orders";

        return;
      }

      /* ===============================
         RAZORPAY PAYMENT
      =============================== */

      const options = {
        key: import.meta.env.VITE_RAZORPAY_KEY,
        amount: order.totalAmount * 100,
        currency: "INR",
        name: "ANIVA",
        description: "Order Payment",

        handler: async function (response) {
          await confirmPayment(
            order.id,
            response.razorpay_payment_id
          );

          queryClient.invalidateQueries({ queryKey: ["cart"] });

          showToast("Payment successful 🎉");

          window.location.href = "/orders";
        },
      };

      if (!window.Razorpay) {
        showToast("Payment system not loaded ❌");
        return;
      }

      const rzp = new window.Razorpay(options);

      rzp.open();
    } catch (err) {
      console.error("Checkout failed", err);
      showToast("Checkout failed ❌");
    } finally {
      setLoading(false);
    }
  };

  /* ===============================
     LOADING
  =============================== */

  if (isLoading) {
    return <div className="checkout-page">Loading checkout...</div>;
  }

  /* ===============================
     EMPTY CART
  =============================== */

  if (finalItems.length === 0) {
    return (
      <div className="checkout-page checkout-empty">
        <h2>Your cart is empty</h2>
      </div>
    );
  }

  return (
    <section className="checkout-page">
      <h2 className="checkout-title">
        Checkout
      </h2>

      <div className="checkout-layout">
        {/* ===============================
            LEFT SIDE (ADDRESS + PAYMENT)
        =============================== */}

        <div className="checkout-left">
          <AddressForm />

          <PaymentMethod />

          {/* CART ITEMS */}

          <div className="checkout-items">
            {finalItems.map((item) => (
              <div key={item.id || item.productId} className="checkout-item">
                <img
                  src={item.imageUrl || item.image}
                  alt={item.name}
                />

                <div className="checkout-item-info">
                  <h4>{item.name}</h4>

                  <p>Quantity: {item.quantity}</p>

                  <p>₹{item.price}</p>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* ===============================
            RIGHT SIDE (ORDER SUMMARY)
        =============================== */}

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
