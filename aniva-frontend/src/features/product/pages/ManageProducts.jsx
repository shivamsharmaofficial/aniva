import { useState, useEffect } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { useProducts } from "@/features/product/hooks/useProducts";
import {
  deleteProduct,
  restoreProduct,
  toggleProductActive
} from "@/features/product/api/productApi";
import { useToast } from "@/components/ui/useToast";
import { useNavigate } from "react-router-dom";
import "@/features/product/styles/manageProducts.css";

function ManageProducts() {

  const { showToast } = useToast();
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  const [page, setPage] = useState(0);
  const [searchInput, setSearchInput] = useState("");
  const [search, setSearch] = useState("");
  const [status, setStatus] = useState("ALL");
  const [showArchived, setShowArchived] = useState(false);

  const size = 10;

  useEffect(() => {
    const timer = setTimeout(() => {
      setSearch(searchInput);
      setPage(0);
    }, 500);

    return () => clearTimeout(timer);
  }, [searchInput]);

  const { data, isLoading } = useProducts({
    page,
    size,
    search,
    status,
    includeDeleted: showArchived,
    sort: "createdAt",
    direction: "desc"
  });

  const products = data?.content || [];

  const refresh = () =>
    queryClient.invalidateQueries({ queryKey: ["products"] });

  const handleDelete = async (id) => {
    if (!window.confirm("Soft delete this product?")) return;
    await deleteProduct(id);
    showToast("Product deleted");
    refresh();
  };

  const handleRestore = async (id) => {
    await restoreProduct(id);
    showToast("Product restored");
    refresh();
  };

  const handleToggle = async (id) => {
    await toggleProductActive(id);
    showToast("Status updated");
    refresh();
  };

  if (isLoading) return <div className="loading-state">Loading...</div>;

  return (
    <div className="manage-products">

      <div className="manage-header">
        <h1>Manage Products</h1>
        <button
          className="create-btn"
          onClick={() => navigate("/admin/products/create")}
        >
          + Create Product
        </button>
      </div>

      <div className="manage-toolbar">
        <input
          placeholder="Search..."
          value={searchInput}
          onChange={(e) => setSearchInput(e.target.value)}
        />

        <select value={status} onChange={(e) => setStatus(e.target.value)}>
          <option value="ALL">All</option>
          <option value="ACTIVE">Active</option>
          <option value="INACTIVE">Inactive</option>
        </select>

        <button onClick={() => setShowArchived(!showArchived)}>
          {showArchived ? "Viewing Archived" : "View Archived"}
        </button>
      </div>

      <div className="table-wrapper">
        <table className="product-table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Actual Price</th>
              <th>Discount Price</th>
              <th>Status</th>
              <th>Stock</th>
              <th>Actions</th>
            </tr>
          </thead>

          <tbody>
            {products.length === 0 ? (
              <tr>
                <td colSpan="6">No products found</td>
              </tr>
            ) : (
              products.map((p) => (
                <tr key={p.id}>
                  <td>{p.name}</td>

                  <td>₹{p.price}</td>

                  <td>
                    {p.discountPrice
                      ? `₹${p.discountPrice}`
                      : "-"}
                  </td>

                  <td>
                    {p.isDeleted ? (
                      <span className="status-badge archived">
                        Archived
                      </span>
                    ) : p.isActive ? (
                      <span className="status-badge active">
                        Active
                      </span>
                    ) : (
                      <span className="status-badge inactive">
                        Inactive
                      </span>
                    )}
                  </td>

                  {/* 🔥 UPDATED STOCK LOGIC — BACKEND DRIVEN */}
                  <td>
                    <span
                      className={
                        p.stockStatus === "LOW_STOCK"
                          ? "low-stock flash"
                          : ""
                      }
                    >
                      {p.totalStock}
                    </span>
                  </td>

                  <td className="actions">
                    <button
                      className="edit-btn"
                      onClick={() =>
                        navigate(`/admin/products/edit/${p.id}`)
                      }
                    >
                      Edit
                    </button>

                    {!p.isDeleted && (
                      <button onClick={() => handleToggle(p.id)}>
                        Toggle
                      </button>
                    )}

                    {p.isDeleted ? (
                      <button onClick={() => handleRestore(p.id)}>
                        Restore
                      </button>
                    ) : (
                      <button
                        className="delete-btn"
                        onClick={() => handleDelete(p.id)}
                      >
                        Delete
                      </button>
                    )}
                  </td>

                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

    </div>
  );
}

export default ManageProducts;