import { useNavigate } from "react-router-dom";
import "@/features/admin/styles/adminDashboard.css";

import { useState, useEffect } from "react";
import axiosInstance from "@/api/axiosInstance";
import { getPaymentMode, changePaymentMode } from "@/features/payment/api/paymentApi";

function AdminDashboard() {

  const navigate = useNavigate();

  /* ===============================
     PAYMENT MODE STATE
  =============================== */

  const [paymentMode, setPaymentMode] = useState("MOCK");

  /* ===============================
     DASHBOARD STATS
  =============================== */

  const [stats, setStats] = useState({
    products: 0,
    orders: 0,
    revenue: 0
  });

  const [loadingStats, setLoadingStats] = useState(true);

  /* ===============================
     LOAD PAYMENT MODE
  =============================== */

  useEffect(() => {

    const fetchPaymentMode = async () => {

      try {

        const mode = await getPaymentMode();

        if (mode) {
          setPaymentMode(mode);
        }

      } catch (err) {

        console.error("Failed to load payment mode", err);

      }

    };

    fetchPaymentMode();

  }, []);

  /* ===============================
     LOAD DASHBOARD STATS
  =============================== */

  useEffect(() => {

    const loadStats = async () => {

      try {

        const res = await axiosInstance.get("/api/admin/dashboard");

        setStats({
          products: res.data.totalProducts || 0,
          orders: res.data.totalOrders || 0,
          revenue: res.data.totalRevenue || 0
        });

      } catch (err) {

        console.error("Failed to load dashboard stats", err);

      } finally {

        setLoadingStats(false);

      }

    };

    loadStats();

  }, []);

  /* ===============================
     SWITCH PAYMENT MODE
  =============================== */

  const handleChangeMode = async (mode) => {

    try {

      setPaymentMode(mode);

      await changePaymentMode(mode);

    } catch (err) {

      console.error("Failed to change payment mode", err);

    }

  };

  return (

    <div className="admin-dashboard">

      {/* ================= HEADER ================= */}

      <div className="dashboard-header">

        <div>

          <h1>Dashboard</h1>

          <p className="dashboard-subtitle">
            Overview of your store performance
          </p>

        </div>

        <div className="dashboard-actions">

          <button
            className="secondary-btn"
            onClick={() => navigate("/")}
          >
            🌍 View Website
          </button>

          <button
            className="primary-btn"
            onClick={() => navigate("/admin/products/create")}
          >
            + Create Product
          </button>

        </div>

      </div>

      {/* ================= STATS GRID ================= */}

      <div className="stats-grid">

        <div className="stat-card">
          <h3>Total Products</h3>
          <p>
            {loadingStats ? "..." : stats.products}
          </p>
        </div>

        <div className="stat-card">
          <h3>Total Orders</h3>
          <p>
            {loadingStats ? "..." : stats.orders}
          </p>
        </div>

        <div className="stat-card">
          <h3>Total Revenue</h3>
          <p>
            {loadingStats ? "..." : `₹${stats.revenue}`}
          </p>
        </div>

      </div>

      {/* ================= PAYMENT MODE CONTROL ================= */}

      <div className="payment-mode-card">

        <h2>Payment Mode</h2>

        <p>
          Current Mode: <strong>{paymentMode}</strong>
        </p>

        <div className="payment-mode-buttons">

          <button
            className={`secondary-btn ${paymentMode === "MOCK" ? "active-mode" : ""}`}
            onClick={() => handleChangeMode("MOCK")}
            disabled={paymentMode === "MOCK"}
          >
            MOCK MODE
          </button>

          <button
            className={`secondary-btn ${paymentMode === "RAZORPAY" ? "active-mode" : ""}`}
            onClick={() => handleChangeMode("RAZORPAY")}
            disabled={paymentMode === "RAZORPAY"}
          >
            RAZORPAY MODE
          </button>

        </div>

        <p className="payment-mode-note">
          MOCK mode is used for development and testing. Razorpay mode enables real payments.
        </p>

      </div>

    </div>

  );

}

export default AdminDashboard;