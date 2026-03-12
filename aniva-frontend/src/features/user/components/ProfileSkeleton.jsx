import "../styles/profile.css";

function ProfileSkeleton() {
  return (
    <div className="profile-card skeleton">
      <div className="skeleton-line large" />
      <div className="skeleton-line" />
      <div className="skeleton-line" />
      <div className="skeleton-line" />
    </div>
  );
}

export default ProfileSkeleton;