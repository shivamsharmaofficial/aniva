import { useState } from "react";
import { useOrders } from "../hooks/useOrders";
import OrderSkeleton from "../components/OrderSkeleton";
import "../styles/profile.css";

const formatStatus = (status) => {
  if (!status) return "Unknown";

  return status
    .toLowerCase()
    .replace(/_/g, " ")
    .replace(/\b\w/g, (c) => c.toUpperCase());
};

function OrderHistory() {
  const [page, setPage] = useState(0);
  const { data, isLoading } = useOrders(page);

  if (isLoading) {
    return (
      <>
        <OrderSkeleton />
        <OrderSkeleton />
      </>
    );
  }

  const orders = data?.content || [];
  const totalPages = data?.totalPages || 1;

  return (
    <div className="profile-card luxury-fade">

      <h2 className="section-title">
        Order History
      </h2>

      {orders.length === 0 ? (
        <p>No orders found.</p>
      ) : (
        orders.map((order) => (
          <div
            key={order.id}
            className="luxury-order-card"
          >
            <div>
              <h4>{order.orderNumber}</h4>
              <p>
                {new Date(order.createdAt)
                  .toLocaleDateString()}
              </p>
            </div>

            <div>
              <p className="order-price">
                ₹{order.totalAmount}
              </p>

              <span
                className={`status-badge ${order.status.toLowerCase()}`}
              >
                {formatStatus(order.status)}
              </span>
            </div>
          </div>
        ))
      )}

      <div className="pagination luxury-pagination">
        <button
          disabled={page === 0}
          onClick={() => setPage((p) => p - 1)}
        >
          Prev
        </button>

        <span>
          Page {page + 1} of {totalPages}
        </span>

        <button
          disabled={page + 1 >= totalPages}
          onClick={() => setPage((p) => p + 1)}
        >
          Next
        </button>
      </div>

    </div>
  );
}

export default OrderHistory;
