import { useState, useMemo, useCallback } from "react";
import {
  useAddAddress,
  useUpdateAddress,
} from "../hooks/useAddresses";

const indianPhoneRegex = /^[6-9]\d{9}$/;
const pincodeRegex = /^[1-9][0-9]{5}$/;

const states = [
  "Delhi",
  "Haryana",
  "Uttar Pradesh",
  "Rajasthan",
  "Punjab",
  "Maharashtra",
  "Karnataka",
  "Tamil Nadu",
  "West Bengal",
];

function AddressFormModal({ onClose, editingAddress }) {
  const isEdit = Boolean(editingAddress);

  const addMutation = useAddAddress();
  const updateMutation = useUpdateAddress();

  /* =========================================
     INITIAL FORM STATE (NO useEffect)
  ========================================= */
  const initialForm = useMemo(
    () =>
      editingAddress || {
        fullName: "",
        phoneNumber: "",
        addressLine: "",
        city: "",
        state: "",
        pincode: "",
        isDefault: false,
      },
    [editingAddress]
  );

  const [form, setForm] = useState(initialForm);
  const [touched, setTouched] = useState({});

  /* =========================================
     VALIDATION
  ========================================= */
  const errors = useMemo(() => {
    const e = {};

    if (!form.fullName.trim())
      e.fullName = "Full name is required";

    if (!indianPhoneRegex.test(form.phoneNumber))
      e.phoneNumber = "Invalid Indian mobile number";

    if (!form.addressLine.trim())
      e.addressLine = "Address is required";

    if (!form.city.trim())
      e.city = "City is required";

    if (!form.state)
      e.state = "State is required";

    if (!pincodeRegex.test(form.pincode))
      e.pincode = "Invalid pincode";

    return e;
  }, [form]);

  const isValid = Object.keys(errors).length === 0;

  /* =========================================
     HANDLE SUBMIT
  ========================================= */
  const handleSubmit = useCallback(() => {
    setTouched({
      fullName: true,
      phoneNumber: true,
      addressLine: true,
      city: true,
      state: true,
      pincode: true,
    });

    if (!isValid) return;

    if (isEdit) {
      updateMutation.mutate(
        {
          id: editingAddress.id,
          data: form,
        },
        {
          onSuccess: onClose,
        }
      );
    } else {
      addMutation.mutate(form, {
        onSuccess: onClose,
      });
    }
  }, [
    isEdit,
    form,
    editingAddress,
    updateMutation,
    addMutation,
    isValid,
    onClose,
  ]);

  /* =========================================
     HANDLE CHANGE
  ========================================= */
  const handleChange = (field, value) => {
    setForm((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  return (
    <div className="modal-overlay">
      <div className="address-modal">
        <h3>{isEdit ? "Edit Address" : "Add Address"}</h3>

        <input
          placeholder="Full Name"
          value={form.fullName}
          onChange={(e) =>
            handleChange("fullName", e.target.value)
          }
        />
        {touched.fullName && errors.fullName && (
          <p className="error-text">{errors.fullName}</p>
        )}

        <input
          placeholder="Mobile Number"
          value={form.phoneNumber}
          onChange={(e) =>
            handleChange("phoneNumber", e.target.value)
          }
        />
        {touched.phoneNumber && errors.phoneNumber && (
          <p className="error-text">{errors.phoneNumber}</p>
        )}

        <input
          placeholder="Address Line"
          value={form.addressLine}
          onChange={(e) =>
            handleChange("addressLine", e.target.value)
          }
        />
        {touched.addressLine && errors.addressLine && (
          <p className="error-text">{errors.addressLine}</p>
        )}

        <input
          placeholder="City"
          value={form.city}
          onChange={(e) =>
            handleChange("city", e.target.value)
          }
        />
        {touched.city && errors.city && (
          <p className="error-text">{errors.city}</p>
        )}

        <select
          value={form.state}
          onChange={(e) =>
            handleChange("state", e.target.value)
          }
        >
          <option value="">Select State</option>
          {states.map((s) => (
            <option key={s} value={s}>
              {s}
            </option>
          ))}
        </select>
        {touched.state && errors.state && (
          <p className="error-text">{errors.state}</p>
        )}

        <input
          placeholder="Pincode"
          value={form.pincode}
          onChange={(e) =>
            handleChange("pincode", e.target.value)
          }
        />
        {touched.pincode && errors.pincode && (
          <p className="error-text">{errors.pincode}</p>
        )}

        <label>
          <input
            type="checkbox"
            checked={form.isDefault}
            onChange={(e) =>
              handleChange("isDefault", e.target.checked)
            }
          />
          Set as default
        </label>

        <div className="modal-actions">
          <button onClick={onClose}>Cancel</button>

          <button
            onClick={handleSubmit}
            disabled={
              !isValid ||
              addMutation.isPending ||
              updateMutation.isPending
            }
          >
            {isEdit ? "Update" : "Save"}
          </button>
        </div>
      </div>
    </div>
  );
}

export default AddressFormModal;