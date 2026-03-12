import { Link } from "react-router-dom";
import { Eye, EyeOff, LockKeyhole, Mail } from "lucide-react";

function LoginForm({
  form,
  errors,
  isBusy,
  showPassword,
  onChange,
  onBlur,
  onSubmit,
  onTogglePassword,
  onGoogleLogin,
  onCreateAccount,
  serverError,
}) {
  return (
    <form className="space-y-0" onSubmit={onSubmit}>
      <div className="mb-4">
        <div className="group flex items-center rounded-[14px] border border-[#d8d0c4] bg-[#f4f0ea] px-4 transition duration-300 focus-within:border-[#b9ac98] focus-within:bg-white">
          <Mail className="mr-2.5 size-4 shrink-0 text-[#8f897f]" />
          <input
            type="text"
            name="identifier"
            value={form.identifier}
            onChange={onChange}
            onBlur={() => onBlur("identifier")}
            className="h-[48px] w-full bg-transparent py-2 text-[16px] text-[#1f1f1f] placeholder:text-[#8f897f] outline-none"
            placeholder="Email or Phone Number"
          />
        </div>
        {errors.identifier && (
          <p className="mt-2 text-sm text-[#b42318]">{errors.identifier}</p>
        )}
      </div>

      <div className="mb-3.5">
        <div className="group flex items-center rounded-[14px] border border-[#d8d0c4] bg-[#f4f0ea] px-4 transition duration-300 focus-within:border-[#b9ac98] focus-within:bg-white">
          <LockKeyhole className="mr-2.5 size-4 shrink-0 text-[#8f897f]" />
          <input
            type={showPassword ? "text" : "password"}
            name="password"
            value={form.password}
            onChange={onChange}
            onBlur={() => onBlur("password")}
            className="h-[48px] w-full bg-transparent py-2 text-[16px] text-[#1f1f1f] placeholder:text-[#8f897f] outline-none"
            placeholder="Password"
          />
          <button
            type="button"
            onClick={onTogglePassword}
            className="text-[#8a857e] transition hover:text-black"
            aria-label={showPassword ? "Hide password" : "Show password"}
          >
            {showPassword ? (
              <EyeOff className="size-[18px]" />
            ) : (
              <Eye className="size-[18px]" />
            )}
          </button>
        </div>
        {errors.password && (
          <p className="mt-2 text-sm text-[#b42318]">{errors.password}</p>
        )}
      </div>

      <div className="mb-4 flex items-center justify-between gap-4 text-[13px] text-[#6f695f]">
        <label className="inline-flex items-center gap-2.5">
          <input
            type="checkbox"
            name="rememberMe"
            checked={form.rememberMe}
            onChange={onChange}
            className="size-4 rounded-[4px] border border-[#cfc7bb] bg-white text-black"
          />
          <span>Remember me</span>
        </label>

        <Link
          to="/forgot-password"
          className="font-medium text-[#6f695f] transition hover:text-black"
        >
          Forgot password
        </Link>
      </div>

      {serverError && (
        <p className="mb-4 rounded-[16px] border border-[#f3d0cc] bg-[#fff5f4] px-4 py-3 text-sm text-[#b42318]">
          {serverError}
        </p>
      )}

      <button
        type="submit"
        disabled={isBusy}
        className="mb-4 inline-flex h-[46px] w-full items-center justify-center gap-2 rounded-[14px] bg-[#191919] text-[14px] font-medium uppercase tracking-[0.18em] text-white transition hover:-translate-y-px hover:bg-black disabled:cursor-not-allowed disabled:opacity-60"
      >
        {isBusy ? "Logging In..." : "Login"}
        {!isBusy && <span className="text-base leading-none">→</span>}
      </button>

      <button
        type="button"
        onClick={onGoogleLogin}
        className="mb-8 inline-flex h-[42px] w-full items-center justify-center gap-3 rounded-full border border-[#d8d1c6] bg-white/70 text-[13px] font-medium text-[#1f1f1f] transition hover:bg-white"
      >
        <span className="inline-flex size-8 items-center justify-center rounded-full bg-white text-sm font-semibold text-black shadow-[inset_0_0_0_1px_rgba(216,209,198,0.8)]">
          G
        </span>
        Login with Google
      </button>

      <p className="text-center text-[13px] text-[#6f695f]">
        Don’t have an account?{" "}
        <button
          type="button"
          onClick={onCreateAccount}
          className="font-semibold text-[#c79d36] transition hover:text-[#a57b1d]"
        >
          Sign Up
        </button>
      </p>
    </form>
  );
}

export default LoginForm;
