const OrderSuccess = () => {
  return (
    <div style={{ padding: "40px", textAlign: "center" }}>
      <h2>🎉 Order Placed Successfully</h2>
      <p>Your payment has been verified.</p>
      <a href="/orders">Go to My Orders</a>
    </div>
  );
};

export default OrderSuccess;