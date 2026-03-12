import { useNavigate } from "react-router-dom";
import { useUserProfile } from "../hooks/useUserProfile";
import ProfileSkeleton from "../components/ProfileSkeleton";
import "../styles/profile.css";

function ProfileOverview() {
  const navigate = useNavigate();
  const { data: user, isLoading, isError } = useUserProfile();

  if (isLoading) return <ProfileSkeleton />;

  if (isError) {
    return (
      <div className="profile-card">
        <p className="profile-error">
          Failed to load profile.
        </p>
      </div>
    );
  }

  return (
    <div className="profile-card luxury-fade">

      <div className="profile-info">
        <h2>
          {user.firstName} {user.lastName}
        </h2>

        <div className="profile-divider" />

        <div className="profile-meta">
          <div>
            <span>Email</span>
            <p>{user.email}</p>
          </div>

          <div>
            <span>Phone</span>
            <p>{user.phoneNumber}</p>
          </div>

          <div>
            <span>Status</span>
            <p className="status-active">
              {user.status}
            </p>
          </div>
        </div>
      </div>

      <button
        className="luxury-btn"
        onClick={() => navigate("edit")}
      >
        Edit Profile
      </button>

    </div>
  );
}

export default ProfileOverview;