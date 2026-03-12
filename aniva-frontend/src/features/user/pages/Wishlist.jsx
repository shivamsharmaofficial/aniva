import { useWishlist } from "../hooks/useWishlist";
import { useRemoveFromWishlist } from "../hooks/useRemoveFromWishlist";
import WishlistSkeleton from "../components/WishlistSkeleton";
import "../styles/profile.css";

function Wishlist() {
  const { data: items, isLoading } = useWishlist();
  const removeMutation = useRemoveFromWishlist();

  if (isLoading) {
    return (
      <>
        <WishlistSkeleton />
        <WishlistSkeleton />
      </>
    );
  }

  return (
    <div className="profile-card luxury-fade">

      <h2 className="section-title">
        Wishlist
      </h2>

      {!items || items.length === 0 ? (
        <p>Your wishlist is empty.</p>
      ) : (
        items.map((item) => (
          <div
            key={item.id}
            className="luxury-wishlist-card"
          >
            <div>
              <h4>{item.name}</h4>
              <p>₹{item.price}</p>
            </div>

            <button
              className="luxury-outline-btn"
              onClick={() =>
                removeMutation.mutate(item.id)
              }
            >
              Remove
            </button>
          </div>
        ))
      )}

    </div>
  );
}

export default Wishlist;