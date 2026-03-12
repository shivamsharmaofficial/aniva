import { useNavigate } from "react-router-dom";

function CartSummary({ total, onClear }) {

  const navigate = useNavigate();

  return (

    <div className="cart-summary">

      <h3>Total: ₹{total}</h3>

      <button
        className="checkout-btn"
        onClick={() => navigate("/checkout")}
      >
        Proceed to Checkout
      </button>

      <button
        className="clear-btn"
        onClick={onClear}
      >
        Clear Cart
      </button>

    </div>

  );
}

export default CartSummary;