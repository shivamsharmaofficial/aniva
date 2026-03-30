import { useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useOrderDetails } from "@/features/order/hooks/useOrderDetails";
import "@/features/order/styles/orders.css";

const formatStatus = (status) => {
  if (!status) return "Unknown";

  return status
    .toLowerCase()
    .replace(/_/g, " ")
    .replace(/\b\w/g, (c) => c.toUpperCase());
};

const steps = [
  "CREATED",
  "PAID",
  "PROCESSING",
  "SHIPPED",
  "DELIVERED",
];

function OrderDetails() {
  const { orderId } = useParams();
  const navigate = useNavigate();
  const { order, items, isLoading } = useOrderDetails(orderId);

  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  if (isLoading) {
    return (
      <section className="order-details-page">
        <div className="order-skeleton">
          <div className="skeleton-header"></div>
          <div className="skeleton-line"></div>
          <div className="skeleton-line"></div>
        </div>
      </section>
    );
  }

  if (!order) {
    return <section className="order-details-page">Order not found.</section>;
  }

  return (
    <section className="order-details-page">
      <button onClick={() => navigate(-1)} className="back-btn">
        ← Back to Orders
      </button>

      <div className="order-details-header">
        <div>
          <span className="orders-header__eyebrow">Order Details</span>
          <h1>{order.orderNumber}</h1>
          <p>Review item totals, quantities, and current status.</p>
        </div>

        <div
          className={`order-status-pill order-status-pill--${String(
            order.status
          ).toLowerCase()}`}
        >
          {formatStatus(order.status)}
        </div>
      </div>

      <div className="order-details-summary">
        <article>
          <span>Total Amount</span>
          <strong>
            ₹{order.totalAmount?.toLocaleString("en-IN")}
          </strong>
        </article>

        <article>
          <span>Payment Status</span>
          <strong
            className={`payment-status payment-status--${String(
              order.paymentStatus || "PENDING"
            ).toLowerCase()}`}
          >
            {formatStatus(order.paymentStatus || "PENDING")}
          </strong>
        </article>

        <article>
          <span>Order Date</span>
          <strong>
            {order.createdAt
              ? new Date(order.createdAt).toLocaleDateString("en-IN", {
                  day: "numeric",
                  month: "short",
                  year: "numeric",
                })
              : "Recently"}
          </strong>
        </article>
      </div>

      <div className="order-tracking">
        {steps.map((step, index) => {
          const active = steps.indexOf(order.status) >= index;

          return (
            <div key={step} className={`step ${active ? "active" : ""}`}>
              {step}
            </div>
          );
        })}
      </div>

      <div className="order-items-panel">
        <div className="order-items-panel__header">
          <h2>Items</h2>
          <span>{items?.length || 0} line items</span>
        </div>

        <div className="order-items-list">
          {items && items.length > 0 ? (
            items.map((item) => (
              <article key={item.id} className="order-item-row">
                <div className="order-item-row__left">
                  <img
                    src={item.imageUrl || item.image || "https://via.placeholder.com/80"}
                    alt={item.productName}
                    className="order-item-image"
                    onError={(e) => {
                      e.target.src = "https://via.placeholder.com/80";
                    }}
                  />

                  <div>
                    <strong>
                      {item.productName || `Product #${item.productId}`}
                    </strong>
                    <p>Product ID: {item.productId}</p>
                  </div>
                </div>

                <div className="order-item-row__meta">
                  <span>Qty: {item.quantity}</span>
                  <span>
                    Price: ₹{item.price?.toLocaleString("en-IN")}
                  </span>
                  <strong>
                    ₹{item.totalPrice?.toLocaleString("en-IN")}
                  </strong>
                </div>
              </article>
            ))
          ) : (
            <div className="orders-state-card">
              No items found for this order.
            </div>
          )}
        </div>
      </div>
    </section>
  );
}

export default OrderDetails;
