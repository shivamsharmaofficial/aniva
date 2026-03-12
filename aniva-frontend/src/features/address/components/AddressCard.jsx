function AddressCard({ address, onEdit, onDelete, deleting = false }) {
  return (
    <article className="address-card">
      <div className="address-card__header">
        <div>
          <span className="address-card__eyebrow">Saved Address</span>
          <h3>{address.fullName}</h3>
        </div>

        <span className="address-card__badge">{address.city}</span>
      </div>

      <div className="address-card__body">
        <p>{address.street}</p>
        <p>
          {address.city}, {address.state} {address.pincode}
        </p>
        <p>{address.phone}</p>
      </div>

      <div className="address-card__actions">
        <button
          type="button"
          className="address-card__secondary"
          onClick={() => onEdit?.(address)}
        >
          Edit
        </button>

        <button
          type="button"
          className="address-card__danger"
          onClick={() => onDelete?.(address.id)}
          disabled={deleting}
        >
          {deleting ? "Deleting..." : "Delete"}
        </button>
      </div>
    </article>
  );
}

export default AddressCard;
