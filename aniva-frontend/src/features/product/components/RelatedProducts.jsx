import { useProducts } from "@/features/product/hooks/useProducts";
import ProductCard from "@/features/product/components/ProductCard";
import SkeletonCard from "@/components/ui/SkeletonCard";

import "@/features/product/styles/relatedProducts.css";

function RelatedProducts({ categorySlug, currentProductId }) {

  const { data, isLoading } = useProducts({
    category: categorySlug ? [categorySlug] : [],
    page: 0,
    size: 4,
    sort: "createdAt",
    direction: "desc",
  });

  const products = data?.content || [];

  const filteredProducts = products.filter(
    (p) => p.id !== currentProductId
  );

  if (isLoading) {
    return (
      <section className="related-products">
        <h2>You may also like</h2>

        <div className="related-grid">
          {Array(4)
            .fill()
            .map((_, i) => (
              <SkeletonCard key={i} />
            ))}
        </div>
      </section>
    );
  }

  if (filteredProducts.length === 0) {
    return null;
  }

  return (
    <section className="related-products">

      <h2>You may also like</h2>

      <div className="related-grid">

        {filteredProducts.map((product) => (
          <ProductCard
            key={product.id}
            product={product}
          />
        ))}

      </div>

    </section>
  );
}

export default RelatedProducts;