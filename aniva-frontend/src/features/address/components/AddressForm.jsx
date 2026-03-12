import { useEffect, useState } from "react";

const EMPTY_FORM = {
  fullName: "",
  street: "",
  city: "",
  state: "",
  pincode: "",
  phone: "",
};

function AddressForm({
  onSave,
  initialValues = EMPTY_FORM,
  submitLabel = "Save Address",
  title = "Shipping Address",
  loading = false,
}) {
  const [form, setForm] = useState({
    ...EMPTY_FORM,
    ...initialValues,
  });

  useEffect(() => {
    setForm({
      ...EMPTY_FORM,
      ...initialValues,
    });
  }, [initialValues]);

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSave?.(form);
  };

  return (
    <form className="address-form" onSubmit={handleSubmit}>
      <h3>{title}</h3>

      <input
        name="fullName"
        placeholder="Full Name"
        value={form.fullName}
        onChange={handleChange}
        required
      />

      <input
        name="street"
        placeholder="Street Address"
        value={form.street}
        onChange={handleChange}
        required
      />

      <input
        name="city"
        placeholder="City"
        value={form.city}
        onChange={handleChange}
        required
      />

      <input
        name="state"
        placeholder="State"
        value={form.state}
        onChange={handleChange}
        required
      />

      <input
        name="pincode"
        placeholder="Pincode"
        value={form.pincode}
        onChange={handleChange}
        required
      />

      <input
        name="phone"
        placeholder="Phone Number"
        value={form.phone}
        onChange={handleChange}
        required
      />

      <button type="submit" disabled={loading}>
        {loading ? "Saving..." : submitLabel}
      </button>
    </form>
  );
}

export default AddressForm;
