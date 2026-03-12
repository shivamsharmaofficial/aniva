import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axiosInstance from "@/api/axiosInstance";
import { useToast } from "@/components/ui/useToast";
import ProductForm from "./ProductForm";
import "@/features/product/styles/createProduct.css";

function CreateProduct() {
  const navigate = useNavigate();
  const { showToast } = useToast();
  const [loading, setLoading] = useState(false);

  const handleCreate = async (payload) => {
    try {
      setLoading(true);

      await axiosInstance.post("/admin/products", payload);

      showToast("Product created successfully ✨");
      navigate("/admin/products");
    } catch (error) {
      console.error(error);
      showToast("Failed to create product ❌");
    } finally {
      setLoading(false);
    }
  };

  return (
    <ProductForm
      mode="create"
      onSubmit={handleCreate}
      loading={loading}
    />
  );
}

export default CreateProduct;