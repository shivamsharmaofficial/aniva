import { useState, useEffect, useCallback, useMemo } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import ProductCard from "./ProductCard";
import SkeletonCard from "@/components/ui/SkeletonCard";
import { useProducts } from "../hooks/useProducts";
import { useCategories } from "../hooks/useCategories";
import Pagination from "@/components/ui/Pagination";
import "@/features/product/styles/productSection.css";

function ProductSection() {
  const [searchParams, setSearchParams] = useSearchParams();
  const navigate = useNavigate();
  const [showFilters, setShowFilters] = useState(false);

  const searchParamsKey = searchParams.toString();

  /* ================= URL STATE ================= */

  const categoryParams = useMemo(
    () => searchParams.getAll("category"),
    [searchParamsKey]
  );
  const search = searchParams.get("search") || "";
  const sort = searchParams.get("sort") || "createdAt";
  const direction = searchParams.get("direction") || "desc";
  const page = parseInt(searchParams.get("page") || "0", 10);

  const minPriceParam = searchParams.get("minPrice");
  const maxPriceParam = searchParams.get("maxPrice");

  const minPrice = minPriceParam ? Number(minPriceParam) : 0;
  const maxPrice = maxPriceParam ? Number(maxPriceParam) : 5000;

  /* ================= LOCK BODY SCROLL ================= */

  useEffect(() => {
    document.body.style.overflow = showFilters ? "hidden" : "auto";

    return () => {
      document.body.style.overflow = "auto";
    };
  }, [showFilters]);

  /* ================= UPDATE PARAMS ================= */

  const updateParams = useCallback(
    (newParams) => {
      const params = new URLSearchParams(searchParams);

      Object.entries(newParams).forEach(([key, value]) => {
        params.delete(key);

        if (Array.isArray(value)) {
          value.forEach((v) => params.append(key, v));
        } else if (
          value !== "" &&
          value !== null &&
          value !== undefined
        ) {
          params.set(key, value);
        }
      });

      setSearchParams(params);
    },
    [searchParamsKey, setSearchParams]
  );

  /* ================= CLEAR FILTERS ================= */

  const clearFilters = () => {
    setSearchParams({});
  };

  /* ================= SEARCH DEBOUNCE ================= */

  const [localSearch, setLocalSearch] = useState(search);

  useEffect(() => {
    setLocalSearch(search);
  }, [search]);

  useEffect(() => {
    if (localSearch === search) return;

    const timer = setTimeout(() => {
      updateParams({ search: localSearch, page: 0 });
    }, 500);

    return () => clearTimeout(timer);
  }, [localSearch, search, updateParams]);

  /* ================= FILTER OBJECT ================= */

  const filters = useMemo(
    () => ({
      category: categoryParams,
      search,
      sort,
      direction,
      page,
      size: 12,
      minPrice,
      maxPrice,
    }),
    [categoryParams, search, sort, direction, page, minPrice, maxPrice]
  );

  const { data: productResponse, isLoading } = useProducts(filters);
  const { data: categoryResponse } = useCategories();

  const products = productResponse?.content || [];
  const categories = categoryResponse?.data || [];

  /* ================= CATEGORY TOGGLE ================= */

  const toggleCategory = (slug) => {
    const newCategories = categoryParams.includes(slug)
      ? categoryParams.filter((c) => c !== slug)
      : [...categoryParams, slug];

    updateParams({ category: newCategories, page: 0 });
  };

  /* ================= PRICE HANDLER ================= */

  const handleMinChange = (value) => {
    if (value <= maxPrice) {
      updateParams({ minPrice: value, page: 0 });
    }
  };

  const handleMaxChange = (value) => {
    if (value >= minPrice) {
      updateParams({ maxPrice: value, page: 0 });
    }
  };

  /* ================= ACTIVE FILTER CHECK ================= */

  const hasActiveFilters =
    categoryParams.length > 0 ||
    search ||
    minPriceParam ||
    maxPriceParam;

  /* ================= CLOSE ON ESC ================= */

  useEffect(() => {
    const handleEsc = (e) => {
      if (e.key === "Escape") setShowFilters(false);
    };

    window.addEventListener("keydown", handleEsc);
    return () => window.removeEventListener("keydown", handleEsc);
  }, []);

  /* ================= UI ================= */

  return (
    <section className="products container">
      <div className="section-title-wrapper">
        <h2 className="section-title">Our Collection</h2>

        {hasActiveFilters && (
          <button
            className="clear-filters-btn"
            onClick={clearFilters}
          >
            Clear Filters
          </button>
        )}
      </div>

      <div className="mobile-filter-btn">
        <button
          aria-label="Open filters"
          onClick={() => setShowFilters(true)}
        >
          Filter Products
        </button>
      </div>

      <div className="product-layout">
        {showFilters && (
          <div
            className="sidebar-overlay"
            onClick={() => setShowFilters(false)}
          />
        )}

        <aside className={`sidebar ${showFilters ? "active" : ""}`}>
          <div className="sidebar-header">
            <h4>Refine Collection</h4>
            <button onClick={() => setShowFilters(false)}>×</button>
          </div>

          <div className="filter-group">
            <label>Search</label>
            <input
              type="text"
              value={localSearch}
              onChange={(e) => setLocalSearch(e.target.value)}
              placeholder="Luxury candles..."
            />
          </div>

          <div className="filter-group">
            <label>Categories</label>

            {categories.length === 0 ? (
              <p style={{ fontSize: "0.9rem", opacity: 0.6 }}>
                No categories available
              </p>
            ) : (
              <div className="checkbox-list">
                {categories.map((cat) => (
                  <label key={cat.id} className="checkbox-item">
                    <input
                      type="checkbox"
                      checked={categoryParams.includes(cat.slug)}
                      onChange={() => toggleCategory(cat.slug)}
                    />
                    <span>{cat.name}</span>
                  </label>
                ))}
              </div>
            )}
          </div>

          <div className="filter-group">
            <label>
              Price Range
              <span className="price-value">
                ₹{minPrice} — ₹{maxPrice}
              </span>
            </label>

            <div className="range-wrapper">
              <input
                type="range"
                min="0"
                max="5000"
                step="100"
                value={minPrice}
                onChange={(e) => handleMinChange(Number(e.target.value))}
              />
              <input
                type="range"
                min="0"
                max="5000"
                step="100"
                value={maxPrice}
                onChange={(e) => handleMaxChange(Number(e.target.value))}
              />
            </div>
          </div>
        </aside>

        <div className="product-content">
          {isLoading ? (
            <div className="product-grid">
              {Array(12)
                .fill(null)
                .map((_, i) => (
                  <SkeletonCard key={i} />
                ))}
            </div>
          ) : products.length === 0 ? (
            <div className="empty-state">
              <h3>No products found</h3>
              <p>Try adjusting filters or search terms.</p>
            </div>
          ) : (
            <>
              <div className="product-grid">
                {products.map((p) => (
                  <ProductCard
                    key={p.id}
                    product={p}
                    onClick={() =>
                      navigate(`/products/${p.slug}`)
                    }
                  />
                ))}
              </div>

              <Pagination
                page={page}
                totalPages={productResponse?.totalPages || 0}
                onPageChange={(newPage) =>
                  updateParams({ page: newPage })
                }
              />
            </>
          )}
        </div>
      </div>
    </section>
  );
}

export default ProductSection;
