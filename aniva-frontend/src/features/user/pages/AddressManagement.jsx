import { useState } from "react";
import {
  useAddresses,
  useDeleteAddress,
  useSetDefaultAddress,
} from "../hooks/useAddresses";
import AddressFormModal from "../components/AddressFormModal";
import AddressSkeleton from "../components/AddressSkeleton";
import "../styles/profile.css";

function AddressManagement() {
  const { data: addresses, isLoading } = useAddresses();
  const deleteMutation = useDeleteAddress();
  const defaultMutation = useSetDefaultAddress();

  const [openModal, setOpenModal] = useState(false);
  const [editingAddress, setEditingAddress] = useState(null);

  if (isLoading) return <AddressSkeleton />;

  return (
    <div className="profile-card luxury-fade">

      <div className="section-header">
        <h2>My Addresses</h2>
        <button
          className="luxury-outline-btn"
          onClick={() => setOpenModal(true)}
        >
          + Add Address
        </button>
      </div>

      <div className="luxury-address-grid">

        {addresses?.length === 0 && (
          <p>No saved addresses yet.</p>
        )}

        {addresses?.map((addr) => (
          <div
            key={addr.id}
            className={`luxury-address-card ${
              addr.isDefault ? "default" : ""
            }`}
          >
            <h4>{addr.fullName}</h4>
            <p>{addr.phoneNumber}</p>
            <p>{addr.addressLine}</p>
            <p>
              {addr.city}, {addr.state} - {addr.pincode}
            </p>

            {addr.isDefault && (
              <span className="default-badge">
                Default
              </span>
            )}

            <div className="address-actions">
              {!addr.isDefault && (
                <button
                  onClick={() =>
                    defaultMutation.mutate(addr.id)
                  }
                >
                  Set Default
                </button>
              )}

              <button
                onClick={() => {
                  setEditingAddress(addr);
                  setOpenModal(true);
                }}
              >
                Edit
              </button>

              <button
                onClick={() =>
                  deleteMutation.mutate(addr.id)
                }
              >
                Delete
              </button>
            </div>
          </div>
        ))}

      </div>

      {openModal && (
        <AddressFormModal
          onClose={() => {
            setOpenModal(false);
            setEditingAddress(null);
          }}
          editingAddress={editingAddress}
        />
      )}

    </div>
  );
}

export default AddressManagement;