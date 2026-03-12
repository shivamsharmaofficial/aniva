import "@/components/styles/button.css";

function Button({
  children,
  variant = "primary",
  size = "md",
  onClick,
  disabled,
  type = "button"
}) {
  return (
    <button
      type={type}
      className={`btn btn-${variant} btn-${size}`}
      onClick={onClick}
      disabled={disabled}
    >
      {children}
    </button>
  );
}

export default Button;