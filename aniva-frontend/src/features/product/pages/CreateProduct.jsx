import { useNavigate } from "react-router-dom";
import { useMutation } from "@tanstack/react-query";
import { useToast } from "@/components/ui/useToast";
import { createProduct } from "@/services/productService";
import ProductForm from "./ProductForm";
import "@/features/product/styles/createProduct.css";

function CreateProduct() {
  const navigate = useNavigate();
  const { showToast } = useToast();

  const mutation = useMutation({
    mutationFn: createProduct,
    onSuccess: () => {
      showToast("Product created successfully âœ¨");
      navigate("/admin/products");
    },
    onError: () => {
      showToast("Failed to create product âŒ");
    },
  });

  const handleCreate = async (payload) => {
    mutation.mutate(payload);
  };

  return (
    <ProductForm
      mode="create"
      onSubmit={handleCreate}
      loading={mutation.isPending}
    />
  );
}

export default CreateProduct;
