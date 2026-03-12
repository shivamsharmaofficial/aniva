import { useState, useMemo } from "react";
import { useChangePassword } from "../hooks/useChangePassword";

const strongPasswordRegex =
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#^()_+=-]).{8,}$/;

function ChangePassword() {
  const mutation = useChangePassword();

  const [form, setForm] = useState({
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  });

  const [touched, setTouched] = useState({});

  // =========================
  // VALIDATION
  // =========================
  const errors = useMemo(() => {
    const e = {};

    if (!form.currentPassword.trim())
      e.currentPassword = "Current password required";

    if (!strongPasswordRegex.test(form.newPassword))
      e.newPassword =
        "Password must be 8+ chars, uppercase, lowercase, number & special character";

    if (form.newPassword !== form.confirmPassword)
      e.confirmPassword = "Passwords do not match";

    return e;
  }, [form]);

  const isValid = Object.keys(errors).length === 0;

  const handleSubmit = () => {
    setTouched({
      currentPassword: true,
      newPassword: true,
      confirmPassword: true,
    });

    if (!isValid) return;

    mutation.mutate({
      currentPassword: form.currentPassword,
      newPassword: form.newPassword,
    });

    setForm({
      currentPassword: "",
      newPassword: "",
      confirmPassword: "",
    });
  };

  return (
    <div className="password-page">
      <h2>Change Password</h2>

      <div className="form-group">
        <input
          type="password"
          placeholder="Current Password"
          value={form.currentPassword}
          onChange={(e) =>
            setForm({
              ...form,
              currentPassword: e.target.value,
            })
          }
          onBlur={() =>
            setTouched({
              ...touched,
              currentPassword: true,
            })
          }
        />
        {touched.currentPassword &&
          errors.currentPassword && (
            <p className="error-text">
              {errors.currentPassword}
            </p>
          )}
      </div>

      <div className="form-group">
        <input
          type="password"
          placeholder="New Password"
          value={form.newPassword}
          onChange={(e) =>
            setForm({
              ...form,
              newPassword: e.target.value,
            })
          }
          onBlur={() =>
            setTouched({
              ...touched,
              newPassword: true,
            })
          }
        />
        {touched.newPassword &&
          errors.newPassword && (
            <p className="error-text">
              {errors.newPassword}
            </p>
          )}
      </div>

      <div className="form-group">
        <input
          type="password"
          placeholder="Confirm New Password"
          value={form.confirmPassword}
          onChange={(e) =>
            setForm({
              ...form,
              confirmPassword: e.target.value,
            })
          }
          onBlur={() =>
            setTouched({
              ...touched,
              confirmPassword: true,
            })
          }
        />
        {touched.confirmPassword &&
          errors.confirmPassword && (
            <p className="error-text">
              {errors.confirmPassword}
            </p>
          )}
      </div>

      <button
        onClick={handleSubmit}
        disabled={!isValid || mutation.isPending}
      >
        {mutation.isPending ? (
          <span className="spinner" />
        ) : (
          "Update Password"
        )}
      </button>
    </div>
  );
}

export default ChangePassword;