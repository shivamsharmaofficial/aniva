import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axiosInstance from "@/api/axiosInstance";
import { useToast } from "@/components/ui/useToast";
import ProductForm from "./ProductForm";

function EditProduct() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { showToast } = useToast();

  const [loading, setLoading] = useState(false);
  const [initialData, setInitialData] = useState(null);
  const [fetching, setFetching] = useState(true);

  /* ================= FETCH PRODUCT ================= */

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        const res = await axiosInstance.get(
          `/admin/products/${id}`
        );

        setInitialData(res.data.data);
      } catch (error) {
        console.error(error);
        showToast("Failed to load product");
        navigate("/admin/products");
      } finally {
        setFetching(false);
      }
    };

    fetchProduct();
  }, [id, navigate, showToast]);

  /* ================= UPDATE ================= */

  const handleUpdate = async (payload) => {
    try {
      setLoading(true);

      await axiosInstance.put(`/admin/products/${id}`, payload);

      showToast("Product updated successfully ✨");
      navigate("/admin/products");
    } catch (error) {
      console.error(error);
      showToast("Failed to update product ❌");
    } finally {
      setLoading(false);
    }
  };

  if (fetching) {
    return <div className="loading-state">Loading product...</div>;
  }

  return (
    <ProductForm
      mode="edit"
      initialData={initialData}
      onSubmit={handleUpdate}
      loading={loading}
    />
  );
}

export default EditProduct;