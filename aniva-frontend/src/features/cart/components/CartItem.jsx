function CartItem({ item, onRemove }) {

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
        >
          Remove
        </button>

      </div>

    </div>
  );
}

export default CartItem;