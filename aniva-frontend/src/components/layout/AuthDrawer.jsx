import { useEffect, useMemo, useState } from "react";
import { X } from "lucide-react";
import { API_BASE_URL } from "@/constants/siteConstants";
import { useToast } from "@/components/ui/useToast";
import { useCartStore } from "@/features/cart/store/useCartStore";
import { useUserLogin } from "@/features/auth/hooks/userLogin";
import { useUserRegister } from "@/features/auth/hooks/userRegister";
import "@/components/styles/authDrawer.css";
import LoginForm from "@/components/auth/LoginForm";
import SignupForm from "@/components/auth/SignupForm";

const LOGIN_FORM = {
  identifier: "",
  password: "",
  rememberMe: false,
};

const SIGNUP_FORM = {
  firstName: "",
  lastName: "",
  email: "",
  phoneNumber: "",
  password: "",
  confirmPassword: "",
  agree: false,
};

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const indianPhoneRegex = /^[6-9]\d{9}$/;
const strongPasswordRegex =
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#^()_+=-]).{8,}$/;

function AuthDrawer({ isOpen, onClose }) {
  const [view, setView] = useState("login");
  const [loginForm, setLoginForm] = useState(LOGIN_FORM);
  const [signupForm, setSignupForm] = useState(SIGNUP_FORM);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [errors, setErrors] = useState({});
  const [animationClass, setAnimationClass] = useState("auth-view-enter");
  const [animationCycle, setAnimationCycle] = useState(0);
  const [touched, setTouched] = useState({});
  const [submitLocked, setSubmitLocked] = useState(false);

  const loginMutation = useUserLogin();
  const registerMutation = useUserRegister();
  const { showToast } = useToast();
  const { loadCart } = useCartStore();

  useEffect(() => {
    if (!isOpen) {
      setErrors({});
      setTouched({});
      setSubmitLocked(false);
      setShowPassword(false);
      setShowConfirmPassword(false);
      setView("login");
      setAnimationClass("auth-view-enter");
    }
  }, [isOpen]);

  useEffect(() => {
    if (!isOpen) return undefined;

    const handleEscape = (event) => {
      if (event.key === "Escape") {
        onClose();
      }
    };

    document.body.style.overflow = "hidden";
    window.addEventListener("keydown", handleEscape);

    return () => {
      document.body.style.overflow = "";
      window.removeEventListener("keydown", handleEscape);
    };
  }, [isOpen, onClose]);

  const headerCopy = useMemo(
    () =>
      view === "login"
        ? {
            title: "Welcome Back",
            subtitle:
              "Sign in to manage your orders, wishlist, and account preferences.",
          }
        : {
            title: "Create Your Account",
            subtitle:
              "Join ANIVA for curated launches, saved addresses, and faster checkout.",
          },
    [view]
  );

  const switchView = (nextView) => {
    if (nextView === view) return;

    setErrors({});
    setTouched({});
    setAnimationClass(
      nextView === "signup" ? "auth-view-enter-left" : "auth-view-enter-right"
    );
    setView(nextView);
    setAnimationCycle((prev) => prev + 1);
  };

  const handleLoginChange = (event) => {
    const { name, value, type, checked } = event.target;
    setLoginForm((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleSignupChange = (event) => {
    const { name, value, type, checked } = event.target;
    setSignupForm((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleBlur = (field) => {
    setTouched((prev) => ({
      ...prev,
      [field]: true,
    }));
  };

  const validateLogin = () => {
    const nextErrors = {};
    const identifier = loginForm.identifier.trim();

    if (!identifier) {
      nextErrors.identifier = "Required";
    } else if (/^\d+$/.test(identifier)) {
      if (!indianPhoneRegex.test(identifier)) {
        nextErrors.identifier = "Invalid email or phone";
      }
    } else if (!emailRegex.test(identifier)) {
      nextErrors.identifier = "Invalid email or phone";
    }

    if (!loginForm.password.trim()) {
      nextErrors.password = "Password is required.";
    }

    return nextErrors;
  };

  const validateSignup = () => {
    const nextErrors = {};

    if (!signupForm.firstName.trim()) {
      nextErrors.firstName = "First name is required.";
    }
    if (!signupForm.email.trim()) {
      nextErrors.email = "Email is required.";
    } else if (!emailRegex.test(signupForm.email.trim())) {
      nextErrors.email = "Invalid email";
    }
    if (!signupForm.phoneNumber.trim()) {
      nextErrors.phoneNumber = "Phone number is required.";
    } else if (!indianPhoneRegex.test(signupForm.phoneNumber.trim())) {
      nextErrors.phoneNumber = "Invalid phone";
    }
    if (!signupForm.password.trim()) {
      nextErrors.password = "Required";
    } else if (!strongPasswordRegex.test(signupForm.password)) {
      nextErrors.password = "Weak password";
    }
    if (signupForm.password !== signupForm.confirmPassword) {
      nextErrors.confirmPassword = "Passwords do not match.";
    }
    if (!signupForm.agree) {
      nextErrors.agree = "You must agree to policy";
    }

    return nextErrors;
  };

  const handleLoginSubmit = async (event) => {
    event.preventDefault();
    if (submitLocked) return;

    const nextErrors = validateLogin();
    setTouched({
      identifier: true,
      password: true,
    });
    setErrors(nextErrors);

    if (Object.keys(nextErrors).length > 0) return;

    setSubmitLocked(true);

    try {
      await loginMutation.mutateAsync({
        identifier: loginForm.identifier.trim(),
        password: loginForm.password,
      });
      await loadCart();
      showToast("Login successful");
      onClose();
    } catch {
      showToast("Login failed", "error");
    } finally {
      setSubmitLocked(false);
    }
  };

  const handleSignupSubmit = async (event) => {
    event.preventDefault();
    if (submitLocked) return;

    const nextErrors = validateSignup();
    setTouched({
      firstName: true,
      email: true,
      phoneNumber: true,
      password: true,
      confirmPassword: true,
      agree: true,
    });
    setErrors(nextErrors);

    if (Object.keys(nextErrors).length > 0) return;

    setSubmitLocked(true);

    try {
      await registerMutation.mutateAsync({
        firstName: signupForm.firstName.trim(),
        lastName: signupForm.lastName.trim(),
        email: signupForm.email.trim(),
        phoneNumber: signupForm.phoneNumber.trim(),
        password: signupForm.password,
      });
      showToast("Account created");
      onClose();
    } catch {
      showToast("Registration failed", "error");
    } finally {
      setSubmitLocked(false);
    }
  };

  const handleGoogleLogin = () => {
    window.location.href = `${API_BASE_URL}/oauth2/authorization/google`;
  };

  const loginServerError =
    loginMutation.error?.response?.data?.message || loginMutation.error?.message;
  const signupServerError =
    registerMutation.error?.response?.data?.message ||
    registerMutation.error?.message;

  if (!isOpen) return null;

  return (
    <div className="auth-drawer-root">
      <button
        type="button"
        onClick={onClose}
        className="auth-drawer-overlay"
        aria-label="Close authentication overlay"
      />

      <aside className="auth-drawer-slide-in auth-drawer-panel">
        <div className="auth-drawer-content">
          <div className="auth-drawer-head">
            <div>
              <p className="auth-drawer-kicker">Account Access</p>
              <h2 className="auth-drawer-title">{headerCopy.title}</h2>
            </div>

            <button
              type="button"
              onClick={onClose}
              className="auth-drawer-close"
              aria-label="Close authentication drawer"
            >
              <X className="size-4" />
            </button>
          </div>

          <p className="auth-drawer-subtitle">{headerCopy.subtitle}</p>

          <div
            key={`${view}-${animationCycle}`}
            className={`auth-view-shell ${animationClass}`}
          >
            {view === "login" ? (
              <LoginForm
                form={loginForm}
                errors={errors}
                isBusy={loginMutation.isPending || submitLocked}
                showPassword={showPassword}
                onChange={handleLoginChange}
                onBlur={handleBlur}
                onSubmit={handleLoginSubmit}
                onTogglePassword={() => setShowPassword((prev) => !prev)}
                onGoogleLogin={handleGoogleLogin}
                onCreateAccount={() => switchView("signup")}
                serverError={loginServerError}
              />
            ) : (
              <SignupForm
                form={signupForm}
                errors={errors}
                isBusy={registerMutation.isPending || submitLocked}
                showPassword={showPassword}
                showConfirmPassword={showConfirmPassword}
                onChange={handleSignupChange}
                onBlur={handleBlur}
                onSubmit={handleSignupSubmit}
                onTogglePassword={() => setShowPassword((prev) => !prev)}
                onToggleConfirmPassword={() =>
                  setShowConfirmPassword((prev) => !prev)
                }
                onSignIn={() => switchView("login")}
                serverError={signupServerError}
              />
            )}
          </div>
        </div>
      </aside>
    </div>
  );
}

export default AuthDrawer;
