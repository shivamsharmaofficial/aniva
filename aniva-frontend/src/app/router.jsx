import { Routes, Route, Navigate } from "react-router-dom";
import { Suspense, lazy } from "react";

import MainLayout from "@/components/layout/MainLayout";
import AdminLayout from "@/components/layout/AdminLayout";
import PageLoader from "@/components/common/PageLoader";

import ProtectedRoute from "@/features/auth/routing/ProtectedRoute";
import RoleBasedRoute from "@/features/auth/routing/RoleBasedRoute";
import PublicRoute from "@/features/auth/routing/PublicRoute";

/* ================= STORE MODULE ================= */

const Home = lazy(() => import("@/pages/Home.jsx"));
const Candles = lazy(() => import("@/pages/Candles.jsx"));
const BestSellers = lazy(() => import("@/pages/BestSellers.jsx"));
const GiftSets = lazy(() => import("@/pages/GiftSets.jsx"));
const About = lazy(() => import("@/pages/About.jsx"));

/* ================= PRODUCT ================= */

const ProductDetails = lazy(() =>
  import("@/features/product/pages/ProductDetails.jsx")
);

/* ================= CART ================= */

const Cart = lazy(() =>
  import("@/features/cart/pages/Cart.jsx")
);

/* ================= CHECKOUT ================= */

const CheckoutPage = lazy(() =>
  import("@/features/checkout/pages/Checkout.jsx")
);

/* ================= AUTH ================= */

const LoginPage = lazy(() =>
  import("@/pages/LoginPage.jsx")
);

const Forbidden = lazy(() =>
  import("@/pages/Forbidden.jsx")
);

/* ================= ORDERS ================= */

const OrderDetails = lazy(() =>
  import("@/features/order/pages/OrderDetails.jsx")
);

const Orders = lazy(() =>
  import("@/features/order/pages/Orders.jsx")
);

/* ================= ADDRESS ================= */

const Addresses = lazy(() =>
  import("@/features/address/pages/Addresses.jsx")
);

/* ================= PROFILE ================= */

const ProfilePage = lazy(() =>
  import("@/features/profile/pages/Profile.jsx")
);

/* ================= ADMIN MODULE ================= */

const AdminDashboard = lazy(() =>
  import("@/features/admin/pages/AdminDashboard.jsx")
);

const ManageProducts = lazy(() =>
  import("@/features/product/pages/ManageProducts.jsx")
);

const CreateProduct = lazy(() =>
  import("@/features/product/pages/CreateProduct.jsx")
);

const EditProduct = lazy(() =>
  import("@/features/product/pages/EditProduct.jsx")
);

function Router() {

  return (

    <Suspense fallback={<PageLoader />}>

      <Routes>

        {/* ================= STORE FRONT ================= */}

        <Route element={<MainLayout />}>

          <Route path="/" element={<Home />} />

          <Route path="/candles" element={<Candles />} />

          <Route path="/bestsellers" element={<BestSellers />} />

          <Route path="/gifts" element={<GiftSets />} />

          <Route path="/about" element={<About />} />

          <Route path="/cart" element={<Cart />} />

          <Route path="/product/:slug" element={<ProductDetails />} />

          <Route
            path="/orders"
            element={
              <ProtectedRoute>
                <Orders />
              </ProtectedRoute>
            }
          />

          <Route
            path="/orders/:orderId"
            element={
              <ProtectedRoute>
                <OrderDetails />
              </ProtectedRoute>
            }
          />

          <Route
            path="/addresses"
            element={
              <ProtectedRoute>
                <Addresses />
              </ProtectedRoute>
            }
          />

          <Route
            path="/profile"
            element={
              <ProtectedRoute>
                <ProfilePage />
              </ProtectedRoute>
            }
          />

          <Route
            path="/account/profile"
            element={
              <ProtectedRoute>
                <ProfilePage />
              </ProtectedRoute>
            }
          />

          <Route
            path="/account/settings"
            element={
              <ProtectedRoute>
                <ProfilePage />
              </ProtectedRoute>
            }
          />

          <Route
            path="/account/login"
            element={
              <PublicRoute>
                <LoginPage />
              </PublicRoute>
            }
          />

          <Route
            path="/login"
            element={<Navigate to="/account/login" replace />}
          />

          <Route
            path="/checkout"
            element={
              <ProtectedRoute>
                <CheckoutPage />
              </ProtectedRoute>
            }
          />

          <Route path="/403" element={<Forbidden />} />

        </Route>

        {/* ================= ADMIN SYSTEM ================= */}

        <Route
          path="/admin/*"
          element={
            <RoleBasedRoute requiredRole="ROLE_ADMIN">
              <AdminLayout />
            </RoleBasedRoute>
          }
        >

          <Route path="dashboard" element={<AdminDashboard />} />

          <Route path="products" element={<ManageProducts />} />

          <Route path="products/create" element={<CreateProduct />} />

          <Route path="products/edit/:id" element={<EditProduct />} />

        </Route>

      </Routes>

    </Suspense>

  );

}

export default Router;
