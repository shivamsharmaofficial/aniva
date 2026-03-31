import { memo } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAddToCart } from "@/features/cart/hooks/useCart";
import { useAuthStore } from "@/features/auth/store/useAuthStore";
import { useToast } from "@/components/ui/useToast";
import Button from "@/components/ui/Button";

import "@/features/product/styles/ProductCard.css";

const FALLBACK_IMAGE =
  "https://images.unsplash.com/photo-1603006905003-be475563bc59";

function ProductCard({ product }) {
  const navigate = useNavigate();
  const addToCartMutation = useAddToCart();
  const { isAuthenticated } = useAuthStore();
  const { showToast } = useToast();

  const imageUrl =
    product?.imageUrl ||
    product?.images?.[0]?.imageUrl ||
    FALLBACK_IMAGE;

  const handleAddToCart = (e) => {
    e.preventDefault();

    if (!isAuthenticated) {
      showToast("Please login first");
      navigate("/account/login");
      return;
    }

    const productId = product?.id;

    if (!productId) {
      showToast("Product unavailable");
      return;
    }

    addToCartMutation.mutate(
      {
        productId,
        quantity: 1,
      },
      {
        onSuccess: () => {
          showToast("Added to cart ✨");
        },
        onError: (err) => {
          console.error(err);
          showToast("Failed to add to cart");
        },
      }
    );
  };

  return (
    <div className="product-card">
      <Link
        to={`/product/${product.slug}`}
        className="product-link"
      >
        <div className="product-image-wrapper">
          <img
            src={imageUrl}
            alt={product.name}
            loading="lazy"
            onError={(e) => {
              e.target.src = FALLBACK_IMAGE;
            }}
          />
        </div>

        <div className="product-content">
          <h3 className="product-title">
            {product.name}
          </h3>

          {product.description && (
            <p className="product-description">
              {product.description}
            </p>
          )}

          <div className="product-price">
            ₹{product.discountPrice || product.price}
          </div>
        </div>
      </Link>

      <Button
        onClick={handleAddToCart}
        disabled={addToCartMutation.isPending}
      >
        {addToCartMutation.isPending ? "Adding..." : "Add to Cart"}
      </Button>
    </div>
  );
}

const MemoizedProductCard = memo(ProductCard);

export default MemoizedProductCard;
