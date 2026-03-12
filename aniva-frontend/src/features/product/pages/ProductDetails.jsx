import { useParams, Link, useNavigate } from "react-router-dom";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useCartStore } from "@/features/cart/store/useCartStore";
import { useAuthStore } from "@/features/auth/store/useAuthStore";
import axiosInstance from "@/api/axiosInstance";
import { useState } from "react";
import { useToast } from "@/components/ui/useToast";
import { getProductBySlug } from "@/features/product/api/productApi";
import Tabs from "@/components/ui/Tabs";
import RelatedProducts from "@/features/product/components/RelatedProducts";

import "@/features/product/styles/productDetails.css";

const FALLBACK_IMAGE =
  "https://images.unsplash.com/photo-1603006905003-be475563bc59";

function ProductDetails() {

  const { slug } = useParams();
  const navigate = useNavigate();

  const { addToCart } = useCartStore();
  const { isAuthenticated } = useAuthStore();

  const { showToast } = useToast();
  const queryClient = useQueryClient();

  const [quantity, setQuantity] = useState(1);
  const [selectedImage, setSelectedImage] = useState(null);
  const [selectedVariant, setSelectedVariant] = useState(null);
  const [showLightbox, setShowLightbox] = useState(false);
  const [wishlist, setWishlist] = useState(false);
  const [rating, setRating] = useState(5);
  const [reviewText, setReviewText] = useState("");

  /* ================= PRODUCT QUERY ================= */

  const { data: product, isLoading, isError } = useQuery({
    queryKey: ["product", slug],
    queryFn: () => getProductBySlug(slug),
    enabled: !!slug,
  });

  /* ================= REVIEW ================= */

  const addReviewMutation = useMutation({
    mutationFn: async () => {
      return axiosInstance.post(`/products/${product.id}/reviews`, {
        rating,
        comment: reviewText,
      });
    },
    onSuccess: () => {
      showToast("Review added ⭐");
      setReviewText("");
      queryClient.invalidateQueries({ queryKey: ["product", slug] });
    },
    onError: () => showToast("Failed to add review"),
  });

  /* ================= DERIVED DATA ================= */

  const variants = product?.variants || [];
  const images = product?.images || [];

  const activeVariant = selectedVariant || variants[0];

  const basePrice = product?.price ?? 0;
  const discountPrice = product?.discountPrice;

  const price =
    activeVariant?.variantPrice ??
    discountPrice ??
    basePrice;

  const inStock = activeVariant
    ? activeVariant.stockQuantity > 0
    : true;

  const mainImage =
    selectedImage ||
    images?.[0]?.imageUrl ||
    product?.imageUrl ||
    FALLBACK_IMAGE;

  /* ================= ADD TO CART ================= */

  const handleAddToCart = async () => {

    if (!isAuthenticated) {
      navigate("/account/login");
      return;
    }

    try {

      await addToCart(
        {
          ...product,
          selectedVariant: activeVariant || null
        },
        quantity
      );

      showToast("Added to cart ✨");

      queryClient.invalidateQueries({ queryKey: ["cart"] });

    } catch (err) {

      console.error(err);
      showToast("Error adding to cart");

    }

  };

  /* ================= BUY NOW ================= */

  const handleBuyNow = async () => {

    if (!isAuthenticated) {
      navigate("/account/login");
      return;
    }

    await handleAddToCart();
    navigate("/checkout");

  };

  /* ================= LOADING ================= */

  if (isLoading) {
    return <div className="product-details">Loading product...</div>;
  }

  if (isError || !product) {
    return <div className="product-details">Product not found</div>;
  }

  /* ================= TABS ================= */

  const tabs = [

    {
      label: "Description",
      content: (
        <div className="tab-description">
          {product.description}
        </div>
      ),
    },

    {
      label: "Specifications",
      content: (
        <div className="tab-specs">
          {variants.map((v) => (
            <div key={v.id}>
              {v.variantName} — ₹{v.variantPrice}
            </div>
          ))}
        </div>
      ),
    },

    {
      label: "Reviews",
      content: (

        <div className="reviews-section">

          <div className="review-form">

            <select
              value={rating}
              onChange={(e)=>setRating(Number(e.target.value))}
            >
              {[5,4,3,2,1].map((r)=>(
                <option key={r} value={r}>
                  {r} ⭐
                </option>
              ))}
            </select>

            <textarea
              placeholder="Write review"
              value={reviewText}
              onChange={(e)=>setReviewText(e.target.value)}
            />

            <button onClick={()=>addReviewMutation.mutate()}>
              Submit Review
            </button>

          </div>

          <div className="review-list">

            {(product.reviews || []).map((review)=>(
              <div key={review.id} className="review-item">
                <div>{"⭐".repeat(review.rating)}</div>
                <p>{review.comment}</p>
              </div>
            ))}

          </div>

        </div>

      ),
    },

  ];

  /* ================= UI ================= */

  return (

    <section className="product-details">

      <div className="breadcrumb">
        <Link to="/">Home</Link> / {product.name}
      </div>

      <div className="product-details-grid">

        {/* IMAGE SECTION */}

        <div className="product-image-section">

          <div className="image-wrapper">
            <img
              src={mainImage}
              alt={product.name}
              className="main-product-image"
              onClick={()=>setShowLightbox(true)}
              onError={(e)=>{e.target.src = FALLBACK_IMAGE}}
            />
          </div>

          <div className="thumbnail-container">

            {images.map((img, index) => (
              <img
                key={img.id || index}
                src={img.imageUrl}
                className="thumbnail"
                onClick={() => setSelectedImage(img.imageUrl)}
              />
            ))}

          </div>

        </div>

        {/* PRODUCT INFO */}

        <div className="product-info-section">

          <h2>{product.name}</h2>

          <div className="price">₹{price}</div>

          <div className={`stock ${inStock ? "in":"out"}`}>
            {inStock ? "In Stock":"Out of Stock"}
          </div>

          <button
            className={`wishlist-btn ${wishlist ? "active":""}`}
            onClick={()=>setWishlist(!wishlist)}
          >
            {wishlist ? "♥ In Wishlist":"♡ Wishlist"}
          </button>

          {variants.length > 0 && (

            <select
              value={activeVariant?.id}
              onChange={(e)=>
                setSelectedVariant(
                  variants.find(v=>v.id == e.target.value)
                )
              }
            >

              {variants.map((v)=>(
                <option key={v.id} value={v.id}>
                  {v.variantName}
                </option>
              ))}

            </select>

          )}

          <div className="quantity-selector">

            <button
              onClick={()=>setQuantity(q=>Math.max(1,q-1))}
            >
              -
            </button>

            <span>{quantity}</span>

            <button
              onClick={()=>setQuantity(q=>q+1)}
            >
              +
            </button>

          </div>

          <div className="actions">

            <button
              onClick={handleAddToCart}
              disabled={!inStock}
            >
              Add to Cart
            </button>

            <button
              onClick={handleBuyNow}
              disabled={!inStock}
            >
              Buy Now
            </button>

          </div>

        </div>

      </div>

      <Tabs tabs={tabs} />

      <RelatedProducts
        categorySlug={product.category?.slug}
        currentProductId={product.id}
      />

      {showLightbox && (

        <div
          className="lightbox"
          onClick={()=>setShowLightbox(false)}
        >
          <img src={mainImage} alt="" />
        </div>

      )}

    </section>

  );

}

export default ProductDetails;
