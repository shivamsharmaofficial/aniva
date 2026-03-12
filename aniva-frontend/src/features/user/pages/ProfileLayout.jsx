import { NavLink, Outlet } from "react-router-dom";
import "../styles/profile.css";

function ProfileLayout() {
  return (
    <div className="profile-wrapper">

      <div className="profile-header">
        <h1>My Account</h1>
        <div className="header-underline" />
      </div>

      <div className="profile-container">

        {/* Sidebar */}
        <aside className="profile-sidebar">
          <nav>

            <NavLink to="/profile" end>
              Overview
            </NavLink>

            <NavLink to="/profile/edit">
              Edit Profile
            </NavLink>

            <NavLink to="/profile/password">
              Change Password
            </NavLink>

            <NavLink to="/profile/addresses">
              Addresses
            </NavLink>

            <NavLink to="/profile/orders">
              Order History
            </NavLink>

            <NavLink to="/profile/wishlist">
              Wishlist
            </NavLink>

          </nav>
        </aside>

        {/* Content Area */}
        <section className="profile-content">
          <Outlet />
        </section>

      </div>
    </div>
  );
}

export default ProfileLayout;