import { Link } from "react-router-dom";
import { useOrders } from "@/features/order/hooks/useOrders";
import "@/features/order/styles/orders.css";

function Orders() {
  const { data, isLoading, isError } = useOrders();
  const orders = data?.content || [];

  return (
    <section className="orders-page">
      <div className="orders-header">
        <div>
          <span className="orders-header__eyebrow">Account</span>
          <h1>My Orders</h1>
          <p>Track order status, totals, and item history.</p>
        </div>

        <div className="orders-header__meta">
          <strong>{orders.length}</strong>
          <span>orders found</span>
        </div>
      </div>

      {isLoading && (
        <div className="orders-state-card">Loading orders...</div>
      )}

      {isError && (
        <div className="orders-state-card orders-state-card--error">
          Failed to load orders.
        </div>
      )}

      {!isLoading && !isError && orders.length === 0 && (
        <div className="orders-state-card">
          No orders yet. Once you place one, it will appear here.
        </div>
      )}

      {!isLoading && !isError && orders.length > 0 && (
        <div className="orders-list">
          {orders.map((order) => (
            <article key={order.id} className="order-card">
              <div className="order-card__top">
                <div>
                  <span className="order-card__label">Order Number</span>
                  <h3>{order.orderNumber}</h3>
                </div>

                <span
                  className={`order-status order-status--${String(
                    order.status
                  ).toLowerCase()}`}
                >
                  {order.status}
                </span>
              </div>

              <div className="order-card__grid">
                <div>
                  <span>Total</span>
                  <strong>Rs. {order.totalAmount}</strong>
                </div>

                <div>
                  <span>Payment</span>
                  <strong>{order.paymentStatus || "Pending"}</strong>
                </div>

                <div>
                  <span>Placed On</span>
                  <strong>
                    {order.createdAt
                      ? new Date(order.createdAt).toLocaleDateString()
                      : "Recently"}
                  </strong>
                </div>
              </div>

              <Link to={`/orders/${order.id}`} className="order-card__link">
                View Details
              </Link>
            </article>
          ))}
        </div>
      )}
    </section>
  );
}

export default Orders;
