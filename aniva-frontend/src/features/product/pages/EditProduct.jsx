import { useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useToast } from "@/components/ui/useToast";
import {
  fetchProductById,
  updateProduct,
} from "@/services/productService";
import ProductForm from "./ProductForm";

function EditProduct() {
  const { id } = useParams();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { showToast } = useToast();

  const { data: initialData, isLoading: fetching, isError } = useQuery({
    queryKey: ["product", id],
    queryFn: () => fetchProductById(id),
    enabled: !!id,
  });

  useEffect(() => {
    if (isError) {
      showToast("Failed to load product");
      navigate("/admin/products");
    }
  }, [isError, navigate, showToast]);

  const mutation = useMutation({
    mutationFn: ({ id, payload }) => updateProduct(id, payload),
    onSuccess: () => {
      showToast("Product updated successfully âœ¨");
      queryClient.invalidateQueries({ queryKey: ["products"] });
      queryClient.invalidateQueries({ queryKey: ["product", id] });
      navigate("/admin/products");
    },
    onError: () => {
      showToast("Failed to update product âŒ");
    },
  });

  const handleUpdate = async (payload) => {
    mutation.mutate({ id, payload });
  };

  if (fetching) {
    return <div className="loading-state">Loading product...</div>;
  }

  return (
    <ProductForm
      mode="edit"
      initialData={initialData}
      onSubmit={handleUpdate}
      loading={mutation.isPending}
    />
  );
}

export default EditProduct;
