import {
  useState,
  useRef,
  useEffect,
} from "react";
import ReactDOM from "react-dom";
import { useUserLogin } from "../hooks/userLogin";
import { useUserRegister } from "../hooks/userRegister";
import { useToast } from "@/components/ui/useToast";
import { useCartStore } from "@/features/cart/store/useCartStore"; // ✅ ADDED

import "../styles/loginModal.css";

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const indianPhoneRegex = /^[6-9]\d{9}$/;
const strongPasswordRegex =
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#^()_+=-]).{8,}$/;

function LoginModal({ isOpen, onClose }) {

  const modalRef = useRef(null);
  const inputRef = useRef(null);

  const { showToast } = useToast();
  const { loadCart } = useCartStore(); // ✅ ADDED

  const loginMutation = useUserLogin();
  const registerMutation = useUserRegister();

  const [mode, setMode] = useState("login");
  const [touched, setTouched] = useState({});
  const [shake, setShake] = useState(false);
  const [submitLocked, setSubmitLocked] = useState(false);

  const [loginData, setLoginData] = useState({
    identifier: "",
    password: "",
  });

  const [registerData, setRegisterData] = useState({
    firstName: "",
    lastName: "",
    phoneNumber: "",
    email: "",
    password: "",
    confirmPassword: "",
    agree: false,
  });

  /* ================= AUTO FOCUS ================= */

  useEffect(() => {
    if (isOpen) {
      setTimeout(() => inputRef.current?.focus(), 100);
    }
  }, [isOpen, mode]);

  /* ================= ESC CLOSE ================= */

  useEffect(() => {

    const handleEsc = (e) => {
      if (e.key === "Escape") onClose();
    };

    if (isOpen)
      window.addEventListener("keydown", handleEsc);

    return () =>
      window.removeEventListener("keydown", handleEsc);

  }, [isOpen, onClose]);

  /* ================= PASSWORD STRENGTH ================= */

  const calculateStrength = (password) => {

    if (!password) return 0;

    let score = 0;

    if (password.length >= 8) score++;
    if (/[A-Z]/.test(password)) score++;
    if (/[a-z]/.test(password)) score++;
    if (/\d/.test(password)) score++;
    if (/[@$!%*?&#^()_+=-]/.test(password)) score++;

    return score;
  };

  const strength = calculateStrength(registerData.password);

  /* ================= LOGIN VALIDATION ================= */

  const loginErrors = {};

  if (touched.identifier) {
    const identifier = loginData.identifier.trim();

    if (!identifier) {

      loginErrors.identifier = "Required";

    } else if (/^\d+$/.test(identifier)) {

      if (!indianPhoneRegex.test(identifier)) {
        loginErrors.identifier = "Invalid email or phone";
      }

    } else {

      if (!emailRegex.test(identifier)) {
        loginErrors.identifier = "Invalid email or phone";
      }

    }

  }

  if (touched.password && !loginData.password) {
    loginErrors.password = "Password required";
  }

  /* ================= REGISTER VALIDATION ================= */

  const registerErrors = {};

  if (touched.firstName && !registerData.firstName)
    registerErrors.firstName = "Required";

  if (touched.email) {

    if (!registerData.email)
      registerErrors.email = "Required";

    else if (!emailRegex.test(registerData.email))
      registerErrors.email = "Invalid email";

  }

  if (touched.phoneNumber) {

    if (!registerData.phoneNumber)
      registerErrors.phoneNumber = "Required";

    else if (!indianPhoneRegex.test(registerData.phoneNumber))
      registerErrors.phoneNumber = "Invalid phone";

  }

  if (touched.password) {

    if (!registerData.password)
      registerErrors.password = "Required";

    else if (!strongPasswordRegex.test(registerData.password))
      registerErrors.password = "Weak password";

  }

  if (
    touched.confirmPassword &&
    registerData.confirmPassword !== registerData.password
  ) {

    registerErrors.confirmPassword =
      "Passwords do not match";

  }

  if (touched.agree && !registerData.agree) {

    registerErrors.agree =
      "You must agree to policy";

  }

  /* ================= LOGIN SUBMIT ================= */

  const handleLogin = (e) => {

    e.preventDefault();

    if (submitLocked) return;

    setTouched({
      identifier: true,
      password: true,
    });

    if (Object.keys(loginErrors).length) {

      setShake(true);

      setTimeout(() => setShake(false), 400);

      return;
    }

    setSubmitLocked(true);

    loginMutation.mutate(loginData, {

      onSuccess: async () => {

        /* ✅ LOAD USER CART AFTER LOGIN */

        await loadCart();

        showToast("Login successful");

        onClose();
      },

      onError: () =>
        showToast("Login failed", "error"),

      onSettled: () =>
        setSubmitLocked(false),

    });

  };

  /* ================= REGISTER SUBMIT ================= */

  const handleRegister = (e) => {

    e.preventDefault();

    if (submitLocked) return;

    setTouched({
      firstName: true,
      email: true,
      phoneNumber: true,
      password: true,
      confirmPassword: true,
      agree: true,
    });

    if (Object.keys(registerErrors).length) {

      setShake(true);

      setTimeout(() => setShake(false), 400);

      return;
    }

    setSubmitLocked(true);

    /* ✅ FIXED PAYLOAD */

    const payload = {
      firstName: registerData.firstName,
      lastName: registerData.lastName,
      phoneNumber: registerData.phoneNumber,
      email: registerData.email,
      password: registerData.password,
    };

    registerMutation.mutate(payload, {

      onSuccess: () => {

        showToast("Account created");

        onClose();

      },

      onError: () =>
        showToast("Registration failed", "error"),

      onSettled: () =>
        setSubmitLocked(false),

    });

  };

  if (!isOpen) return null;

  return ReactDOM.createPortal(

    <div
      className="modal-overlay"
      ref={modalRef}
      onClick={(e) =>
        modalRef.current === e.target && onClose()
      }
    >

      <div className="login-modal">

        <div className="login-left">

          {mode === "login" ? (

            <form
              onSubmit={handleLogin}
              className={shake ? "shake-form" : ""}
            >

              <h2>Login</h2>

              <input
                ref={inputRef}
                type="text"
                placeholder="Email or Phone Number"
                value={loginData.identifier}
                onBlur={() =>
                  setTouched((p) => ({
                    ...p,
                    identifier: true,
                  }))
                }
                onChange={(e) =>
                  setLoginData({
                    ...loginData,
                    identifier: e.target.value.trim(),
                  })
                }
              />

              {loginErrors.identifier && (
                <p className="error-text">
                  {loginErrors.identifier}
                </p>
              )}

              <input
                type="password"
                placeholder="Password"
                value={loginData.password}
                onBlur={() =>
                  setTouched((p) => ({
                    ...p,
                    password: true,
                  }))
                }
                onChange={(e) =>
                  setLoginData({
                    ...loginData,
                    password: e.target.value,
                  })
                }
              />

              {loginErrors.password && (
                <p className="error-text">
                  {loginErrors.password}
                </p>
              )}

              <button
                type="submit"
                className="login-btn"
                disabled={
                  submitLocked ||
                  loginMutation.isPending
                }
              >

                {loginMutation.isPending ? (
                  <span className="btn-loader" />
                ) : (
                  "Login"
                )}

              </button>

              <p
                className="switch-text"
                onClick={() =>
                  setMode("signup")
                }
              >
                Don’t have an account?{" "}
                <span>Sign Up</span>
              </p>

            </form>

          ) : (

            <form
              onSubmit={handleRegister}
              className={shake ? "shake-form" : ""}
            >

              <h2>Sign Up</h2>

              <input
                placeholder="First Name"
                onBlur={() =>
                  setTouched((p) => ({
                    ...p,
                    firstName: true,
                  }))
                }
                onChange={(e) =>
                  setRegisterData({
                    ...registerData,
                    firstName: e.target.value,
                  })
                }
              />

              {registerErrors.firstName && (
                <p className="error-text">
                  {registerErrors.firstName}
                </p>
              )}

              <input
                placeholder="Phone Number"
                onBlur={() =>
                  setTouched((p) => ({
                    ...p,
                    phoneNumber: true,
                  }))
                }
                onChange={(e) =>
                  setRegisterData({
                    ...registerData,
                    phoneNumber: e.target.value,
                  })
                }
              />

              {registerErrors.phoneNumber && (
                <p className="error-text">
                  {registerErrors.phoneNumber}
                </p>
              )}

              <input
                type="email"
                placeholder="Email"
                onBlur={() =>
                  setTouched((p) => ({
                    ...p,
                    email: true,
                  }))
                }
                onChange={(e) =>
                  setRegisterData({
                    ...registerData,
                    email: e.target.value,
                  })
                }
              />

              {registerErrors.email && (
                <p className="error-text">
                  {registerErrors.email}
                </p>
              )}

              <input
                type="password"
                placeholder="Password"
                onBlur={() =>
                  setTouched((p) => ({
                    ...p,
                    password: true,
                  }))
                }
                onChange={(e) =>
                  setRegisterData({
                    ...registerData,
                    password: e.target.value,
                  })
                }
              />

              {registerErrors.password && (
                <p className="error-text">
                  {registerErrors.password}
                </p>
              )}

              <div className="strength-wrapper">
                <div
                  className={`strength-bar strength-${strength}`}
                />
              </div>

              <input
                type="password"
                placeholder="Confirm Password"
                onBlur={() =>
                  setTouched((p) => ({
                    ...p,
                    confirmPassword: true,
                  }))
                }
                onChange={(e) =>
                  setRegisterData({
                    ...registerData,
                    confirmPassword:
                      e.target.value,
                  })
                }
              />

              {registerErrors.confirmPassword && (
                <p className="error-text">
                  {
                    registerErrors.confirmPassword
                  }
                </p>
              )}

              <div className="terms-container">

                <input
                  type="checkbox"
                  onBlur={() =>
                    setTouched((p) => ({
                      ...p,
                      agree: true,
                    }))
                  }
                  onChange={(e) =>
                    setRegisterData({
                      ...registerData,
                      agree: e.target.checked,
                    })
                  }
                />

                <label>
                  I agree to Privacy Policy
                </label>

              </div>

              {registerErrors.agree && (
                <p className="error-text">
                  {registerErrors.agree}
                </p>
              )}

              <button
                type="submit"
                className="login-btn"
                disabled={
                  submitLocked ||
                  registerMutation.isPending
                }
              >

                {registerMutation.isPending ? (
                  <span className="btn-loader" />
                ) : (
                  "Sign Up"
                )}

              </button>

              <p
                className="back-text"
                onClick={() =>
                  setMode("login")
                }
              >
                ← Back to Login
              </p>

            </form>

          )}

        </div>

      </div>

    </div>,

    document.body
  );
}

export default LoginModal;
