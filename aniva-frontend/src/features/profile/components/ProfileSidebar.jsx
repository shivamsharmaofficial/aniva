import { Link, useLocation } from "react-router-dom";

function ProfileSidebar() {
  const location = useLocation();
  const links = [
    { to: "/profile", label: "Overview" },
    { to: "/orders", label: "Orders" },
    { to: "/addresses", label: "Addresses" },
    { to: "/cart", label: "Cart" },
  ];

  return (
    <aside className="profile-sidebar">
      <div className="profile-sidebar__header">
        <span className="profile-sidebar__eyebrow">Dashboard</span>
        <h3>My Account</h3>
      </div>

      <nav className="profile-sidebar__nav">
        {links.map((link) => {
          const isActive = location.pathname === link.to;

          return (
            <Link
              key={link.to}
              to={link.to}
              className={isActive ? "is-active" : ""}
            >
              {link.label}
            </Link>
          );
        })}
      </nav>
    </aside>
  );
}

export default ProfileSidebar;
