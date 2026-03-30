import { useCart, useRemoveFromCart } from "../hooks/useCart";

import CartItem from "../components/CartItem";
import CartSummary from "../components/CartSummary";

import "@/features/cart/styles/cart.css";

function Cart() {
  const { data, isLoading, isError } = useCart();
  const removeMutation = useRemoveFromCart();
  const items = Array.isArray(data) ? data : [];

  const handleRemove = (id) => {
    removeMutation.mutate(id);
  };

  if (isLoading) {
    return <div className="cart-container">Loading cart...</div>;
  }

  if (isError) {
    return (
      <div className="cart-container">
        Unable to load cart right now.
      </div>
    );
  }

  const total = items.reduce(
    (sum, item) =>
      sum + (Number(item?.price) || 0) * (Number(item?.quantity) || 0),
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
                onRemove={handleRemove}
                deleting={
                  removeMutation.isPending &&
                  removeMutation.variables === item.id
                }
              />
            ))}
          </div>

          <CartSummary total={total} />
        </>
      )}
    </section>
  );
}

export default Cart;
