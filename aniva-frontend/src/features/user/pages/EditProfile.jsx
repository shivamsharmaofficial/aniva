import { useState } from "react";
import { useUserProfile } from "../hooks/useUserProfile";
import { useUpdateProfile } from "../hooks/useUpdateProfile";
import ProfileSkeleton from "../components/ProfileSkeleton";
import "../styles/profile.css";

function EditProfile() {
  const { data: user, isLoading } = useUserProfile();
  const updateMutation = useUpdateProfile();

  // Initialize only when user changes
  const [formData, setFormData] = useState(null);

  if (isLoading) return <ProfileSkeleton />;

  // Initialize form once when user loads
  if (user && !formData) {
    setFormData({
      firstName: user.firstName || "",
      lastName: user.lastName || "",
      phoneNumber: user.phoneNumber || "",
    });
  }

  if (!formData) return null;

  const handleSubmit = (e) => {
    e.preventDefault();
    updateMutation.mutate(formData);
  };

  return (
    <div className="profile-card luxury-fade">
      <h2 className="section-title">Edit Profile</h2>

      <form onSubmit={handleSubmit} className="luxury-form">

        <div className="form-group">
          <label>First Name</label>
          <input
            type="text"
            value={formData.firstName}
            onChange={(e) =>
              setFormData((prev) => ({
                ...prev,
                firstName: e.target.value,
              }))
            }
            required
          />
        </div>

        <div className="form-group">
          <label>Last Name</label>
          <input
            type="text"
            value={formData.lastName}
            onChange={(e) =>
              setFormData((prev) => ({
                ...prev,
                lastName: e.target.value,
              }))
            }
            required
          />
        </div>

        <div className="form-group">
          <label>Phone Number</label>
          <input
            type="tel"
            value={formData.phoneNumber}
            onChange={(e) =>
              setFormData((prev) => ({
                ...prev,
                phoneNumber: e.target.value,
              }))
            }
          />
        </div>

        <button
          type="submit"
          className="luxury-btn"
          disabled={updateMutation.isPending}
        >
          {updateMutation.isPending
            ? "Updating..."
            : "Save Changes"}
        </button>

      </form>
    </div>
  );
}

export default EditProfile;