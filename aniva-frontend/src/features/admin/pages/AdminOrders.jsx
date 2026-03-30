import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useToast } from "@/components/ui/useToast";
import {
  fetchAllOrders,
  updateOrderStatus,
} from "@/services/adminOrderService";

const formatStatus = (status) => {
  if (!status) return "Unknown";

  return status
    .toLowerCase()
    .replace(/_/g, " ")
    .replace(/\b\w/g, (c) => c.toUpperCase());
};

function AdminOrders() {
  const queryClient = useQueryClient();
  const { showToast } = useToast();

  const { data, isLoading, isError } = useQuery({
    queryKey: ["admin-orders"],
    queryFn: fetchAllOrders,
  });

  const mutation = useMutation({
    mutationFn: ({ id, status }) => updateOrderStatus(id, status),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["admin-orders"] });
      showToast("Order status updated");
    },
    onError: () => {
      showToast("Failed to update order status");
    },
  });

  const orders = Array.isArray(data)
    ? data
    : Array.isArray(data?.content)
      ? data.content
      : [];

  if (isLoading) {
    return <section className="orders-page">Loading orders...</section>;
  }

  if (isError) {
    return (
      <section className="orders-page">
        <div className="orders-state-card orders-state-card--error">
          Failed to load admin orders.
        </div>
      </section>
    );
  }

  return (
    <section className="orders-page">
      <div className="orders-header">
        <div>
          <span className="orders-header__eyebrow">Admin</span>
          <h1>Manage Orders</h1>
          <p>Review incoming orders and update fulfillment status.</p>
        </div>

        <div className="orders-header__meta">
          <strong>{orders.length}</strong>
          <span>orders found</span>
        </div>
      </div>

      {orders.length === 0 ? (
        <div className="orders-state-card">
          No orders available right now.
        </div>
      ) : (
        <div className="orders-list">
          {orders.map((order) => {
            const isUpdating =
              mutation.isPending && mutation.variables?.id === order.id;

            return (
              <article key={order.id} className="order-card">
                <div className="order-card__top">
                  <div>
                    <span className="order-card__label">Order Number</span>
                    <h3>{order.orderNumber || `Order #${order.id}`}</h3>
                  </div>

                  <span
                    className={`order-status order-status--${String(
                      order.status
                    ).toLowerCase()}`}
                  >
                    {formatStatus(order.status)}
                  </span>
                </div>

                <div className="order-card__grid">
                  <div>
                    <span>Customer</span>
                    <strong>{order.user?.name || order.customerName || "Unknown"}</strong>
                  </div>

                  <div>
                    <span>Total</span>
                    <strong>
                      ₹{order.totalAmount?.toLocaleString("en-IN")}
                    </strong>
                  </div>

                  <div>
                    <span>Placed On</span>
                    <strong>
                      {order.createdAt
                        ? new Date(order.createdAt).toLocaleDateString("en-IN", {
                            day: "numeric",
                            month: "short",
                            year: "numeric",
                          })
                        : "Recently"}
                    </strong>
                  </div>
                </div>

                <label className="order-card__label" htmlFor={`status-${order.id}`}>
                  Update Status
                </label>
                <select
                  id={`status-${order.id}`}
                  value={order.status}
                  onChange={(e) =>
                    mutation.mutate({ id: order.id, status: e.target.value })
                  }
                  disabled={isUpdating}
                >
                  <option value="PROCESSING">PROCESSING</option>
                  <option value="SHIPPED">SHIPPED</option>
                  <option value="DELIVERED">DELIVERED</option>
                </select>
              </article>
            );
          })}
        </div>
      )}
    </section>
  );
}

export default AdminOrders;
