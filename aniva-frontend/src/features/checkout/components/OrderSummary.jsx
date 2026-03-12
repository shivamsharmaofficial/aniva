function OrderSummary({ totalAmount, loading, onCheckout }) {

  return (

    <div className="checkout-summary">

      <h3 className="summary-title">
        Order Summary
      </h3>

      <div className="summary-row">
        <span>Subtotal</span>
        <span>₹{totalAmount}</span>
      </div>

      <div className="summary-row summary-total">
        <span>Total</span>
        <span>₹{totalAmount}</span>
      </div>

      <button
        className="checkout-btn"
        onClick={onCheckout}
        disabled={loading}
      >
        {loading ? "Processing..." : "Place Order"}
      </button>

    </div>

  );
}

export default OrderSummary;