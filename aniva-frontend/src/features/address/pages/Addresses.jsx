import { useMemo, useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import AddressCard from "../components/AddressCard";
import AddressForm from "../components/AddressForm";
import { useAddresses } from "../hooks/useAddresses";
import {
  createAddress,
  deleteAddress,
  updateAddress,
} from "../api/addressApi";
import "@/features/address/styles/address.css";

const EMPTY_FORM = {
  fullName: "",
  street: "",
  city: "",
  state: "",
  pincode: "",
  phone: "",
};

function Addresses() {
  const queryClient = useQueryClient();
  const { data, isLoading, isError } = useAddresses();
  const [editingAddress, setEditingAddress] = useState(null);

  const addresses = useMemo(() => data?.data || [], [data]);

  const refreshAddresses = () =>
    queryClient.invalidateQueries({ queryKey: ["addresses"] });

  const saveMutation = useMutation({
    mutationFn: (payload) => {
      if (editingAddress?.id) {
        return updateAddress(editingAddress.id, payload);
      }

      return createAddress(payload);
    },
    onSuccess: () => {
      setEditingAddress(null);
      refreshAddresses();
    },
  });

  const deleteMutation = useMutation({
    mutationFn: deleteAddress,
    onSuccess: () => refreshAddresses(),
  });

  const handleSubmit = async (payload) => {
    await saveMutation.mutateAsync(payload);
  };

  const handleDelete = async (id) => {
    const confirmed = window.confirm("Delete this saved address?");
    if (!confirmed) return;

    await deleteMutation.mutateAsync(id);
  };

  return (
    <section className="addresses-page">
      <div className="addresses-hero">
        <div>
          <span className="addresses-hero__eyebrow">Account</span>
          <h1>My Addresses</h1>
          <p>
            Keep your delivery details updated so checkout stays fast.
          </p>
        </div>

        <div className="addresses-hero__meta">
          <strong>{addresses.length}</strong>
          <span>saved locations</span>
        </div>
      </div>

      <div className="addresses-layout">
        <div className="addresses-panel">
          <AddressForm
            onSave={handleSubmit}
            initialValues={editingAddress || EMPTY_FORM}
            submitLabel={editingAddress ? "Update Address" : "Add Address"}
            title={editingAddress ? "Edit Address" : "Add New Address"}
            loading={saveMutation.isPending}
          />

          {editingAddress && (
            <button
              type="button"
              className="addresses-cancel"
              onClick={() => setEditingAddress(null)}
            >
              Cancel editing
            </button>
          )}
        </div>

        <div className="addresses-list">
          {isLoading && (
            <div className="addresses-state-card">
              Loading your addresses...
            </div>
          )}

          {isError && (
            <div className="addresses-state-card addresses-state-card--error">
              Failed to load addresses. Refresh and try again.
            </div>
          )}

          {!isLoading && !isError && addresses.length === 0 && (
            <div className="addresses-state-card">
              No addresses saved yet. Add one to speed up checkout.
            </div>
          )}

          {!isLoading &&
            !isError &&
            addresses.map((address) => (
              <AddressCard
                key={address.id}
                address={address}
                onEdit={setEditingAddress}
                onDelete={handleDelete}
                deleting={
                  deleteMutation.isPending &&
                  deleteMutation.variables === address.id
                }
              />
            ))}
        </div>
      </div>
    </section>
  );
}

export default Addresses;
