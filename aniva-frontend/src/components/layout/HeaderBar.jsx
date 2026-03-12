import { useEffect, useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  Heart,
  LogOut,
  Settings,
  ShoppingBag,
  ShoppingCart,
  UserRound,
} from "lucide-react";
import AuthDrawer from "@/components/layout/AuthDrawer";
import { BRAND_NAME, ROUTES } from "@/constants/siteConstants";
import { clearSession } from "@/features/auth/utils/authService";
import { useAuthStore } from "@/features/auth/store/useAuthStore";
import { useCartStore } from "@/features/cart/store/useCartStore";
import "@/components/styles/headerBar.css";

const getStoredAuthStatus = () => {
  try {
    const persistedAuth = localStorage.getItem("aniva-auth");
    if (!persistedAuth) return false;

    const parsedAuth = JSON.parse(persistedAuth);
    return Boolean(parsedAuth?.state?.accessToken);
  } catch {
    return false;
  }
};

function HeaderBar() {
  const navigate = useNavigate();
  const dropdownRef = useRef(null);

  const [isDrawerOpen, setIsDrawerOpen] = useState(false);
  const [accountMenuOpen, setAccountMenuOpen] = useState(false);

  const accessToken = useAuthStore((state) => state.accessToken);
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  const isLoggedIn =
    isAuthenticated || Boolean(accessToken) || getStoredAuthStatus();

  const { items } = useCartStore();
  const cartCount = items.reduce(
    (total, item) => total + item.quantity,
    0
  );

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target)
      ) {
        setAccountMenuOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () =>
      document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleLogout = () => {
    clearSession();
    localStorage.removeItem("aniva-auth");
    setAccountMenuOpen(false);
    navigate(ROUTES.login);
  };

  return (
    <>
      <header className="header-bar">
        <div className="header-bar__inner">
          <div className="header-bar__spacer" />

          <div className="header-bar__brand-wrap">
            <Link
              to={ROUTES.home}
              className="header-bar__brand"
            >
              {BRAND_NAME}
            </Link>
          </div>

          <div className="header-bar__actions-wrap">
            <div className="header-bar__actions">
            <Link
              to={ROUTES.wishlist}
              className="header-bar__icon-button"
              aria-label="Wishlist"
            >
              <Heart className="size-5" />
            </Link>

            <Link
              to={ROUTES.cart}
              className="header-bar__icon-button header-bar__cart"
              aria-label="Cart"
            >
              <ShoppingBag className="size-5" />
              {cartCount > 0 && (
                <span className="header-bar__badge">
                  {cartCount}
                </span>
              )}
            </Link>

            {!isLoggedIn ? (
              <button
                type="button"
                onClick={() => setIsDrawerOpen(true)}
                className="header-bar__icon-button"
                aria-label="Open login drawer"
              >
                <UserRound className="size-5" />
              </button>
            ) : (
              <div className="header-bar__account" ref={dropdownRef}>
                <button
                  type="button"
                  onClick={() => setAccountMenuOpen((prev) => !prev)}
                  className="header-bar__icon-button header-bar__account-trigger"
                  aria-label="Open account menu"
                >
                  <UserRound className="size-5" />
                  <span className="header-bar__account-label">
                    Account
                  </span>
                </button>

                {accountMenuOpen && (
                  <div className="header-bar__menu">
                    <button
                      type="button"
                      onClick={() => {
                        setAccountMenuOpen(false);
                        navigate(ROUTES.accountProfile);
                      }}
                      className="header-bar__menu-item"
                    >
                      <UserRound className="size-4" />
                      Profile
                    </button>
                    <button
                      type="button"
                      onClick={() => {
                        setAccountMenuOpen(false);
                        navigate(ROUTES.orders);
                      }}
                      className="header-bar__menu-item"
                    >
                      <ShoppingCart className="size-4" />
                      My Orders
                    </button>
                    <button
                      type="button"
                      onClick={() => {
                        setAccountMenuOpen(false);
                        navigate(ROUTES.accountSettings);
                      }}
                      className="header-bar__menu-item"
                    >
                      <Settings className="size-4" />
                      Settings
                    </button>
                    <button
                      type="button"
                      onClick={handleLogout}
                      className="header-bar__menu-item"
                    >
                      <LogOut className="size-4" />
                      Logout
                    </button>
                  </div>
                )}
              </div>
            )}
            </div>
          </div>
        </div>
      </header>

      <AuthDrawer
        isOpen={isDrawerOpen}
        onClose={() => setIsDrawerOpen(false)}
      />
    </>
  );
}

export default HeaderBar;
