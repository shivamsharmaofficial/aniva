import { useState } from "react";
import { useChangePassword } from "../hooks/useChangePassword";
import "../styles/profile.css";

function ChangePassword() {
  const changeMutation = useChangePassword();

  const [formData, setFormData] = useState({
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  });

  const handleSubmit = (e) => {
    e.preventDefault();

    if (formData.newPassword !== formData.confirmPassword) {
      return alert("Passwords do not match");
    }

    changeMutation.mutate({
      currentPassword: formData.currentPassword,
      newPassword: formData.newPassword,
    });
  };

  return (
    <div className="profile-card luxury-fade">
      <h2 className="section-title">Change Password</h2>

      <form onSubmit={handleSubmit} className="luxury-form">

        <div className="form-group">
          <label>Current Password</label>
          <input
            type="password"
            value={formData.currentPassword}
            onChange={(e) =>
              setFormData({
                ...formData,
                currentPassword: e.target.value,
              })
            }
            required
          />
        </div>

        <div className="form-group">
          <label>New Password</label>
          <input
            type="password"
            value={formData.newPassword}
            onChange={(e) =>
              setFormData({
                ...formData,
                newPassword: e.target.value,
              })
            }
            required
          />
        </div>

        <div className="form-group">
          <label>Confirm Password</label>
          <input
            type="password"
            value={formData.confirmPassword}
            onChange={(e) =>
              setFormData({
                ...formData,
                confirmPassword: e.target.value,
              })
            }
            required
          />
        </div>

        <button
          type="submit"
          className="luxury-btn"
          disabled={changeMutation.isPending}
        >
          {changeMutation.isPending
            ? "Updating..."
            : "Update Password"}
        </button>

      </form>
    </div>
  );
}

export default ChangePassword;