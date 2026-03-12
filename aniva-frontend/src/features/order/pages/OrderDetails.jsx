import { useParams } from "react-router-dom";
import { useOrderDetails } from "@/features/order/hooks/useOrderDetails";
import "@/features/order/styles/orders.css";

function OrderDetails() {
  const { orderId } = useParams();
  const { order, items, isLoading } = useOrderDetails(orderId);

  if (isLoading) {
    return <section className="order-details-page">Loading order...</section>;
  }

  if (!order) {
    return <section className="order-details-page">Order not found.</section>;
  }

  return (
    <section className="order-details-page">
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
          {order.status}
        </div>
      </div>

      <div className="order-details-summary">
        <article>
          <span>Total Amount</span>
          <strong>Rs. {order.totalAmount}</strong>
        </article>

        <article>
          <span>Payment Status</span>
          <strong>{order.paymentStatus || "Pending"}</strong>
        </article>

        <article>
          <span>Order Date</span>
          <strong>
            {order.createdAt
              ? new Date(order.createdAt).toLocaleString()
              : "Recently"}
          </strong>
        </article>
      </div>

      <div className="order-items-panel">
        <div className="order-items-panel__header">
          <h2>Items</h2>
          <span>{items?.length || 0} line items</span>
        </div>

        <div className="order-items-list">
          {items?.map((item) => (
            <article key={item.id} className="order-item-row">
              <div>
                <strong>
                  {item.productName || `Product #${item.productId}`}
                </strong>
                <p>Product ID: {item.productId}</p>
              </div>

              <div className="order-item-row__meta">
                <span>Qty: {item.quantity}</span>
                <span>Price: Rs. {item.price}</span>
                <strong>Rs. {item.totalPrice}</strong>
              </div>
            </article>
          ))}
        </div>
      </div>
    </section>
  );
}

export default OrderDetails;
