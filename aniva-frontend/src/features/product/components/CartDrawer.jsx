import { useNavigate } from "react-router-dom";
import { useCart, useRemoveFromCart } from "@/features/cart/hooks/useCart";
import CartItem from "@/features/cart/components/CartItem";

import "../styles/cartDrawer.css";

function CartDrawer({ open, onClose }) {
  const navigate = useNavigate();
  const { data, isLoading, isError } = useCart();
  const removeMutation = useRemoveFromCart();
  const items = Array.isArray(data) ? data : [];

  const total = items.reduce(
    (sum, item) =>
      sum + (Number(item?.price) || 0) * (Number(item?.quantity) || 0),
    0
  );

  return (
    <div className={`cart-drawer ${open ? "open" : ""}`}>
      <div className="drawer-header">
        <h3>Your Cart</h3>
        <button onClick={onClose}>×</button>
      </div>

      <div className="drawer-items">
        {isLoading ? (
          <p>Loading cart...</p>
        ) : isError ? (
          <p>Unable to load cart.</p>
        ) : items.length === 0 ? (
          <p>Your cart is empty</p>
        ) : (
          items.map((item) => (
            <CartItem
              key={item.id}
              item={item}
              onRemove={(id) => removeMutation.mutate(id)}
              deleting={
                removeMutation.isPending &&
                removeMutation.variables === item.id
              }
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
          disabled={isLoading || items.length === 0}
        >
          Checkout
        </button>
      </div>
    </div>
  );
}

export default CartDrawer;
