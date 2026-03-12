import { useEffect } from "react";
import { useCartStore } from "../store/useCartStore";

import CartItem from "../components/CartItem";
import CartSummary from "../components/CartSummary";

import "@/features/cart/styles/cart.css";

function Cart() {

  const {
    items,
    removeFromCart,
    clearCart,
    loadCart
  } = useCartStore();

  useEffect(() => {
    loadCart();
  }, []);

  const total = items.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0
  );

  return (

    <section className="cart-container">

      <h2 className="cart-title">
        Your Shopping Bag
      </h2>

      {items.length === 0 ? (

        <div className="empty-cart">
          <h3>Your cart is empty</h3>
          <p>Discover our luxury collection.</p>
        </div>

      ) : (

        <>

          <div className="cart-items">

            {items.map((item) => (

              <CartItem
                key={item.id}
                item={item}
                onRemove={removeFromCart}
              />

            ))}

          </div>

          <CartSummary
            total={total}
            onClear={clearCart}
          />

        </>

      )}

    </section>

  );
}

export default Cart;