import "@/components/styles/skeleton.css";

function SkeletonCard() {
  return (
    <div className="skeleton-card">
      <div className="skeleton-image shimmer" />

      <div className="skeleton-content">
        <div className="skeleton-title shimmer" />
        <div className="skeleton-text shimmer" />
        <div className="skeleton-price shimmer" />
        <div className="skeleton-button shimmer" />
      </div>
    </div>
  );
}

export default SkeletonCard;