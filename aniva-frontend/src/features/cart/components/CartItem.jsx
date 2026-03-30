import { useNavigate } from "react-router-dom";

function CartItem({ item, onRemove, deleting = false }) {
  const navigate = useNavigate();

  const handleBuyNow = (item) => {
    navigate("/checkout", {
      state: {
        buyNowItem: item,
      },
    });
  };

  return (
    <div className="cart-item">

      <img
        src={item.imageUrl || item.image}
        alt={item.name}
        className="cart-image"
      />

      <div className="cart-details">

        <h4>{item.name}</h4>

        <p>₹{item.price}</p>

        <p>Qty: {item.quantity}</p>

        <button
          className="remove-btn"
          onClick={() => onRemove(item.id)}
          disabled={deleting}
        >
          {deleting ? "Removing..." : "Remove"}
        </button>

        <button onClick={() => handleBuyNow(item)}>
          Buy Now
        </button>

      </div>

    </div>
  );
}

export default CartItem;
