import { useState } from "react";
import { ToastContext } from "./ToastContext";
import "@/components/styles/toast.css";

export function ToastProvider({ children }) {
  const [message, setMessage] = useState("");

  const showToast = (msg) => {
    setMessage(msg);
    setTimeout(() => setMessage(""), 3000);
  };

  return (
    <ToastContext.Provider value={{ showToast }}>
      {children}
      {message && <div className="toast">{message}</div>}
    </ToastContext.Provider>
  );
}