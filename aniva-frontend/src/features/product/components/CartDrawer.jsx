import { useCartStore } from "../store/useCartStore";
import CartItem from "./CartItem";
import { useNavigate } from "react-router-dom";

import "../styles/cartDrawer.css";

function CartDrawer({ open, onClose }) {

  const navigate = useNavigate();

  const {
    items,
    removeFromCart
  } = useCartStore();

  const total = items.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0
  );

  return (

    <div className={`cart-drawer ${open ? "open" : ""}`}>

      <div className="drawer-header">

        <h3>Your Cart</h3>

        <button onClick={onClose}>✕</button>

      </div>

      <div className="drawer-items">

        {items.length === 0 ? (
          <p>Your cart is empty</p>
        ) : (
          items.map(item => (
            <CartItem
              key={item.id}
              item={item}
              onRemove={removeFromCart}
            />
          ))
        )}

      </div>

      <div className="drawer-footer">

        <h4>Subtotal: ₹{total}</h4>

        <button
          onClick={() => {
            navigate("/cart");
            onClose();
          }}
        >
          View Cart
        </button>

        <button
          onClick={() => {
            navigate("/checkout");
            onClose();
          }}
        >
          Checkout
        </button>

      </div>

    </div>

  );
}

export default CartDrawer;