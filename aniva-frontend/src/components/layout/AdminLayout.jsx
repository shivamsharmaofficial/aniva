import { Outlet, NavLink, useNavigate } from "react-router-dom";
import { useAuthStore } from "@/features/auth/store/useAuthStore";
import "@/components/styles/adminLayout.css";

function AdminLayout() {
  const navigate = useNavigate();
  const logout = useAuthStore((s) => s.logout);

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  return (
    <div className="admin-layout">

      {/* SIDEBAR */}
      <aside className="admin-sidebar">
        <div className="admin-sidebar-top">
          <div className="admin-logo">ĀNIVA</div>
          <div className="admin-subtitle">Admin Panel</div>

          <nav className="admin-nav">
            <NavLink to="/admin/dashboard">
              Dashboard
            </NavLink>

            <NavLink to="/admin/products">
              Manage Products
            </NavLink>

            <NavLink to="/admin/products/create">
              Create Product
            </NavLink>
          </nav>
        </div>

        <button onClick={handleLogout} className="admin-logout">
          Logout
        </button>
      </aside>

      {/* MAIN CONTENT */}
      <div className="admin-content">
        <header className="admin-header">
          <div className="admin-header-title">
            Admin Control Center
          </div>
        </header>

        <main className="admin-main">
          <Outlet />
        </main>
      </div>

    </div>
  );
}

export default AdminLayout;