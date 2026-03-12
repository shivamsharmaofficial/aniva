import { useProducts } from "@/features/product/hooks/useProducts";
import ProductCard from "@/features/product/components/ProductCard";

function GiftSets() {

  const { data } = useProducts({
    category: ["GIFT_SET"],
    page: 0,
    size: 12
  });

  const products = data?.content || [];

  return (
    <div className="page-container">

      <h1>Gift Sets</h1>

      <div className="product-grid">

        {products.map(product => (
          <ProductCard
            key={product.id}
            product={product}
          />
        ))}

      </div>

    </div>
  );
}

export default GiftSets;