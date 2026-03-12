import { useEffect, useMemo, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import ProfileSidebar from "../components/ProfileSidebar";
import { useAuthStore } from "@/features/auth/store/useAuthStore";
import { useAddresses } from "@/features/address/hooks/useAddresses";
import { useOrders } from "@/features/order/hooks/useOrders";
import { useUserProfile } from "@/features/user/hooks/useUserProfile";
import { useUpdateProfile } from "@/features/user/hooks/useUpdateProfile";
import { useChangePassword } from "@/features/user/hooks/useChangePassword";
import "@/features/profile/styles/profile.css";

const PROFILE_FORM = {
  firstName: "",
  lastName: "",
  email: "",
  phone: "",
  profileImage: "",
};

const PASSWORD_FORM = {
  currentPassword: "",
  newPassword: "",
  confirmPassword: "",
};

function Profile() {
  const location = useLocation();
  const navigate = useNavigate();
  const authUser = useAuthStore((state) => state.user);
  const { data: fetchedProfile } = useUserProfile();
  const { data: addressData } = useAddresses();
  const { data: ordersData } = useOrders();
  const updateProfileMutation = useUpdateProfile();
  const changePasswordMutation = useChangePassword();

  const [activeTab, setActiveTab] = useState("profile");
  const [profileForm, setProfileForm] = useState(PROFILE_FORM);
  const [passwordForm, setPasswordForm] = useState(PASSWORD_FORM);
  const [passwordError, setPasswordError] = useState("");

  const user = fetchedProfile || authUser || {};
  const addresses = useMemo(() => addressData?.data || [], [addressData]);
  const orders = useMemo(() => ordersData?.content || [], [ordersData]);

  useEffect(() => {
    setActiveTab(
      location.pathname === "/account/settings" ? "settings" : "profile"
    );
  }, [location.pathname]);

  useEffect(() => {
    setProfileForm({
      firstName: user?.firstName || "",
      lastName: user?.lastName || "",
      email: user?.email || "",
      phone: user?.phone || "",
      profileImage: user?.profileImage || user?.avatarUrl || "",
    });
  }, [user]);

  const stats = [
    {
      label: "Saved Addresses",
      value: addresses.length,
      helper: "Manage delivery locations",
    },
    {
      label: "Orders Placed",
      value: orders.length,
      helper: "Track every purchase",
    },
    {
      label: "Latest Status",
      value: orders[0]?.status || "No Orders",
      helper: "Recent order activity",
    },
  ];

  const displayName =
    [user?.firstName, user?.lastName].filter(Boolean).join(" ") ||
    "ANIVA Customer";

  const handleProfileChange = (event) => {
    const { name, value } = event.target;
    setProfileForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handlePasswordChange = (event) => {
    const { name, value } = event.target;
    setPasswordForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSaveProfile = async (event) => {
    event.preventDefault();
    await updateProfileMutation.mutateAsync(profileForm);
  };

  const handleChangePassword = async (event) => {
    event.preventDefault();

    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      setPasswordError("New password and confirm password must match.");
      return;
    }

    setPasswordError("");

    await changePasswordMutation.mutateAsync({
      currentPassword: passwordForm.currentPassword,
      newPassword: passwordForm.newPassword,
    });

    setPasswordForm(PASSWORD_FORM);
  };

  const openProfileTab = () => {
    setActiveTab("profile");
    navigate("/account/profile");
  };

  const openSettingsTab = () => {
    setActiveTab("settings");
    navigate("/account/settings");
  };

  return (
    <section className="profile-page">
      <div className="profile-layout">
        <ProfileSidebar />

        <div className="profile-content">
          <div className="profile-hero">
            <div>
              <span className="profile-hero__eyebrow">Welcome back</span>
              <h2>{displayName}</h2>
              <p>
                Manage profile details, addresses, account security, and
                recent orders from one account center.
              </p>
            </div>

            <div className="profile-hero__card">
              <span>Primary Email</span>
              <strong>{user?.email || "Not available"}</strong>
              <span>Primary Phone</span>
              <strong>{user?.phone || "Not available"}</strong>
            </div>
          </div>

          <div className="profile-stats">
            {stats.map((stat) => (
              <article key={stat.label} className="profile-stat-card">
                <span>{stat.label}</span>
                <strong>{stat.value}</strong>
                <p>{stat.helper}</p>
              </article>
            ))}
          </div>

          <div className="profile-tabbar">
            <button
              type="button"
              className={activeTab === "profile" ? "is-active" : ""}
              onClick={openProfileTab}
            >
              Profile
            </button>
            <button
              type="button"
              className={activeTab === "settings" ? "is-active" : ""}
              onClick={openSettingsTab}
            >
              Settings
            </button>
          </div>

          {activeTab === "profile" ? (
            <div className="profile-sections profile-sections--stacked">
              <article className="profile-panel profile-panel--wide">
                <div className="profile-panel__header">
                  <div>
                    <span className="profile-panel__eyebrow">Profile</span>
                    <h3>User details</h3>
                  </div>
                </div>

                <form className="profile-form" onSubmit={handleSaveProfile}>
                  <div className="profile-avatar-row">
                    <div className="profile-avatar">
                      {profileForm.profileImage ? (
                        <img
                          src={profileForm.profileImage}
                          alt={displayName}
                        />
                      ) : (
                        <span>{displayName.charAt(0)}</span>
                      )}
                    </div>

                    <div className="profile-avatar-meta">
                      <h4>{displayName}</h4>
                      <p>
                        Add a profile image URL and keep your contact details
                        current.
                      </p>
                    </div>
                  </div>

                  <div className="profile-form-grid">
                    <label>
                      <span>First Name</span>
                      <input
                        name="firstName"
                        value={profileForm.firstName}
                        onChange={handleProfileChange}
                        placeholder="First Name"
                      />
                    </label>

                    <label>
                      <span>Last Name</span>
                      <input
                        name="lastName"
                        value={profileForm.lastName}
                        onChange={handleProfileChange}
                        placeholder="Last Name"
                      />
                    </label>

                    <label>
                      <span>Email</span>
                      <input
                        name="email"
                        type="email"
                        value={profileForm.email}
                        onChange={handleProfileChange}
                        placeholder="Email"
                      />
                    </label>

                    <label>
                      <span>Phone</span>
                      <input
                        name="phone"
                        value={profileForm.phone}
                        onChange={handleProfileChange}
                        placeholder="Phone Number"
                      />
                    </label>

                    <label className="profile-form-grid__full">
                      <span>Profile Image URL</span>
                      <input
                        name="profileImage"
                        value={profileForm.profileImage}
                        onChange={handleProfileChange}
                        placeholder="https://example.com/profile.jpg"
                      />
                    </label>
                  </div>

                  <div className="profile-form__actions">
                    <button
                      type="submit"
                      className="profile-primary-btn"
                      disabled={updateProfileMutation.isPending}
                    >
                      {updateProfileMutation.isPending
                        ? "Saving..."
                        : "Save Profile"}
                    </button>
                  </div>
                </form>
              </article>

              <div className="profile-sections">
                <article className="profile-panel">
                  <div className="profile-panel__header">
                    <div>
                      <span className="profile-panel__eyebrow">Addresses</span>
                      <h3>Edit address book</h3>
                    </div>
                    <Link to="/addresses">Manage</Link>
                  </div>

                  {addresses.length === 0 ? (
                    <p className="profile-empty">No address saved yet.</p>
                  ) : (
                    <div className="profile-address-list">
                      {addresses.slice(0, 2).map((address) => (
                        <div key={address.id} className="profile-address-item">
                          <strong>{address.fullName}</strong>
                          <p>{address.street}</p>
                          <p>
                            {address.city}, {address.state}{" "}
                            {address.pincode}
                          </p>
                        </div>
                      ))}
                    </div>
                  )}
                </article>

                <article className="profile-panel">
                  <div className="profile-panel__header">
                    <div>
                      <span className="profile-panel__eyebrow">Orders</span>
                      <h3>My orders</h3>
                    </div>
                    <Link to="/orders">View all</Link>
                  </div>

                  {orders.length === 0 ? (
                    <p className="profile-empty">
                      You have not placed an order yet.
                    </p>
                  ) : (
                    <div className="profile-order-list">
                      {orders.slice(0, 3).map((order) => (
                        <div key={order.id} className="profile-order-item">
                          <div>
                            <strong>{order.orderNumber}</strong>
                            <span>{order.status}</span>
                          </div>
                          <p>Rs. {order.totalAmount}</p>
                        </div>
                      ))}
                    </div>
                  )}
                </article>
              </div>
            </div>
          ) : (
            <div className="profile-sections profile-sections--stacked">
              <article className="profile-panel profile-panel--wide">
                <div className="profile-panel__header">
                  <div>
                    <span className="profile-panel__eyebrow">Security</span>
                    <h3>Change password</h3>
                  </div>
                </div>

                <form
                  className="profile-form"
                  onSubmit={handleChangePassword}
                >
                  <div className="profile-form-grid">
                    <label>
                      <span>Current Password</span>
                      <input
                        type="password"
                        name="currentPassword"
                        value={passwordForm.currentPassword}
                        onChange={handlePasswordChange}
                        placeholder="Current Password"
                      />
                    </label>

                    <label>
                      <span>New Password</span>
                      <input
                        type="password"
                        name="newPassword"
                        value={passwordForm.newPassword}
                        onChange={handlePasswordChange}
                        placeholder="New Password"
                      />
                    </label>

                    <label className="profile-form-grid__full">
                      <span>Confirm Password</span>
                      <input
                        type="password"
                        name="confirmPassword"
                        value={passwordForm.confirmPassword}
                        onChange={handlePasswordChange}
                        placeholder="Confirm Password"
                      />
                    </label>
                  </div>

                  {passwordError && (
                    <p className="profile-error">{passwordError}</p>
                  )}

                  <div className="profile-form__actions">
                    <button
                      type="submit"
                      className="profile-primary-btn"
                      disabled={changePasswordMutation.isPending}
                    >
                      {changePasswordMutation.isPending
                        ? "Updating..."
                        : "Change Password"}
                    </button>
                  </div>
                </form>
              </article>

              <div className="profile-sections">
                <article className="profile-panel">
                  <div className="profile-panel__header">
                    <div>
                      <span className="profile-panel__eyebrow">Address</span>
                      <h3>Edit address details</h3>
                    </div>
                    <Link to="/addresses">Open addresses</Link>
                  </div>
                  <p className="profile-empty">
                    Update shipping details, city, phone number, and pincode
                    from your saved address book.
                  </p>
                </article>

                <article className="profile-panel">
                  <div className="profile-panel__header">
                    <div>
                      <span className="profile-panel__eyebrow">Account</span>
                      <h3>Quick account actions</h3>
                    </div>
                  </div>
                  <div className="profile-quick-links">
                    <Link to="/orders">View My Orders</Link>
                    <Link to="/addresses">Edit Addresses</Link>
                    <button type="button" onClick={openProfileTab}>
                      Back to Profile Details
                    </button>
                  </div>
                </article>
              </div>
            </div>
          )}
        </div>
      </div>
    </section>
  );
}

export default Profile;
