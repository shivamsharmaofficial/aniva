import { useState } from "react";
import axiosInstance from "@/api/axiosInstance";
import { useCartStore } from "@/features/cart/store/useCartStore";
import { getPaymentMode, confirmPayment } from "@/features/payment/api/paymentApi";

import OrderSummary from "../components/OrderSummary";
import PaymentMethod from "../components/PaymentMethod";
import AddressForm from "@/features/address/components/AddressForm";

import "../styles/checkout.css";

const Checkout = () => {

  const { items, clearCart } = useCartStore();
  const [loading, setLoading] = useState(false);

  const totalAmount = items.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0
  );

  const handleCheckout = async () => {

    if (items.length === 0) return;

    setLoading(true);

    try {

      const orderResponse = await axiosInstance.post("/orders/checkout");

      const order = orderResponse.data.data;

      const mode = await getPaymentMode();

      /* ===============================
         MOCK PAYMENT
      =============================== */

      if (mode === "MOCK") {

        await confirmPayment(order.id, "MOCK_PAYMENT");

        await clearCart();

        alert("Mock payment successful!");

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

          await clearCart();

          window.location.href = "/orders";
        }
      };

      const rzp = new window.Razorpay(options);

      rzp.open();

    } catch (err) {

      console.error("Checkout failed", err);

      alert("Checkout failed");

    } finally {

      setLoading(false);

    }

  };

  /* ===============================
     EMPTY CART
  =============================== */

  if (items.length === 0) {

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

            {items.map((item) => (

              <div key={item.id} className="checkout-item">

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
