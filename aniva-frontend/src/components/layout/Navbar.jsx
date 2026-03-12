import { useState } from "react";
import { Link, NavLink } from "react-router-dom";
import { Menu, X } from "lucide-react";
import { useIsAdmin } from "@/features/auth/hooks/useAuthSelectors";
import { ROUTES } from "@/constants/siteConstants";

const NAV_LINKS = [
  { label: "Candles", to: ROUTES.candles },
  { label: "Best Sellers", to: ROUTES.bestSellers },
  { label: "Gift Sets", to: ROUTES.gifts },
  { label: "About", to: ROUTES.about },
];

function Navbar() {
  const [menuOpen, setMenuOpen] = useState(false);
  const isAdmin = useIsAdmin();

  return (
    <div className="border-y border-black/10 bg-[#f8f3eb]">
      <div className="mx-auto flex max-w-[1920px] items-center justify-center px-8 py-3 sm:px-10 lg:px-14">
        <nav className="hidden items-center justify-center gap-10 md:flex">
          {NAV_LINKS.map((link) => (
            <NavLink
              key={link.to}
              to={link.to}
              className={({ isActive }) =>
                `relative text-sm uppercase tracking-[0.32em] transition ${
                  isActive
                    ? "text-[#111111]"
                    : "text-[#3a352d] hover:text-[#111111]"
                }`
              }
            >
              {link.label}
            </NavLink>
          ))}
        </nav>

        <div className="absolute right-8 hidden md:block sm:right-10 lg:right-14">
          {isAdmin && (
            <Link
              to="/admin/dashboard"
              className="rounded-full border border-black/10 bg-white px-5 py-2 text-xs font-semibold uppercase tracking-[0.22em] text-[#111111] transition hover:border-[#d4af37] hover:text-[#d4af37]"
            >
              Admin Dashboard
            </Link>
          )}
        </div>

        <button
          type="button"
          onClick={() => setMenuOpen((prev) => !prev)}
          className="inline-flex items-center justify-center rounded-full border border-black/10 bg-white p-2 text-[#111111] md:hidden"
          aria-label="Toggle navigation menu"
        >
          {menuOpen ? <X className="size-5" /> : <Menu className="size-5" />}
        </button>
      </div>

      {menuOpen && (
        <div className="border-t border-black/10 bg-white md:hidden">
          <div className="mx-auto flex max-w-[1440px] flex-col px-4 py-4 sm:px-6">
            {NAV_LINKS.map((link) => (
              <NavLink
                key={link.to}
                to={link.to}
                onClick={() => setMenuOpen(false)}
                className={({ isActive }) =>
                  `rounded-2xl px-4 py-3 text-sm uppercase tracking-[0.2em] ${
                    isActive
                      ? "bg-[#111111] text-white"
                      : "text-[#3a352d] hover:bg-[#f8f3eb]"
                  }`
                }
              >
                {link.label}
              </NavLink>
            ))}

            {isAdmin && (
              <Link
                to="/admin/dashboard"
                onClick={() => setMenuOpen(false)}
                className="mt-3 rounded-2xl border border-black/10 px-4 py-3 text-sm uppercase tracking-[0.2em] text-[#111111]"
              >
                Admin Dashboard
              </Link>
            )}
          </div>
        </div>
      )}
    </div>
  );
}

export default Navbar;
