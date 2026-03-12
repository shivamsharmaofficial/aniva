import { useNavigate, useLocation } from "react-router-dom";
import { useEffect } from "react";
import { useAuthStore } from "@/features/auth/store/useAuthStore";
import LoginModal from "@/features/auth/components/LoginModal";

const LoginPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const accessToken = useAuthStore(
    (state) => state.accessToken
  );

  const from = location.state?.from?.pathname || "/";

  useEffect(() => {
    if (accessToken) {
      navigate(from, { replace: true });
    }
  }, [accessToken, navigate, from]);

  const handleClose = () => {
    navigate("/", { replace: true });
  };

  return (
    <LoginModal
      isOpen={true}
      onClose={handleClose}
    />
  );
};

export default LoginPage;