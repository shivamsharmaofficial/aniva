import {
  Eye,
  EyeOff,
} from "lucide-react";

function SignupForm({
  form,
  errors,
  isBusy,
  showPassword,
  showConfirmPassword,
  onChange,
  onBlur,
  onSubmit,
  onTogglePassword,
  onToggleConfirmPassword,
  onSignIn,
  serverError,
}) {
  return (
    <form className="space-y-0" onSubmit={onSubmit}>
      <div className="mb-6">
        <div className="group flex items-center rounded-[16px] border border-transparent bg-[#f8f7f4] px-5 shadow-[inset_0_0_0_1px_rgba(208,198,181,0.7)] transition duration-300 focus-within:border-[#c9a956] focus-within:shadow-[inset_0_0_0_1px_rgba(201,169,86,1)]">
          <input
            type="text"
            name="firstName"
            value={form.firstName}
            onChange={onChange}
            onBlur={() => onBlur("firstName")}
            className="h-[68px] w-full bg-transparent py-2 text-[16px] text-[#1f1f1f] placeholder:text-[#9a968f] outline-none"
            placeholder="First Name"
          />
        </div>
        {errors.firstName && (
          <p className="mt-2 text-sm text-[#b42318]">{errors.firstName}</p>
        )}
      </div>

      <div className="mb-6">
        <div className="group flex items-center rounded-[16px] border border-transparent bg-[#f8f7f4] px-5 shadow-[inset_0_0_0_1px_rgba(208,198,181,0.7)] transition duration-300 focus-within:border-[#c9a956] focus-within:shadow-[inset_0_0_0_1px_rgba(201,169,86,1)]">
          <input
            type="text"
            name="lastName"
            value={form.lastName}
            onChange={onChange}
            onBlur={() => onBlur("lastName")}
            className="h-[68px] w-full bg-transparent py-2 text-[16px] text-[#1f1f1f] placeholder:text-[#9a968f] outline-none"
            placeholder="Last Name"
          />
        </div>
        {errors.lastName && (
          <p className="mt-2 text-sm text-[#b42318]">{errors.lastName}</p>
        )}
      </div>

      <div className="mb-6">
        <div className="group flex items-center rounded-[16px] border border-transparent bg-[#f8f7f4] px-5 shadow-[inset_0_0_0_1px_rgba(208,198,181,0.7)] transition duration-300 focus-within:border-[#c9a956] focus-within:shadow-[inset_0_0_0_1px_rgba(201,169,86,1)]">
          <input
            type="email"
            name="email"
            value={form.email}
            onChange={onChange}
            onBlur={() => onBlur("email")}
            className="h-[68px] w-full bg-transparent py-2 text-[16px] text-[#1f1f1f] placeholder:text-[#9a968f] outline-none"
            placeholder="Email"
          />
        </div>
        {errors.email && (
          <p className="mt-2 text-sm text-[#b42318]">{errors.email}</p>
        )}
      </div>

      <div className="mb-6">
        <div className="group flex items-center rounded-[16px] border border-transparent bg-[#f8f7f4] px-5 shadow-[inset_0_0_0_1px_rgba(208,198,181,0.7)] transition duration-300 focus-within:border-[#c9a956] focus-within:shadow-[inset_0_0_0_1px_rgba(201,169,86,1)]">
          <input
            type="tel"
            name="phoneNumber"
            value={form.phoneNumber}
            onChange={onChange}
            onBlur={() => onBlur("phoneNumber")}
            className="h-[68px] w-full bg-transparent py-2 text-[16px] text-[#1f1f1f] placeholder:text-[#9a968f] outline-none"
            placeholder="Phone Number"
          />
        </div>
        {errors.phoneNumber && (
          <p className="mt-2 text-sm text-[#b42318]">{errors.phoneNumber}</p>
        )}
      </div>

      <div className="mb-6">
        <div className="group flex items-center rounded-[16px] border border-transparent bg-[#f8f7f4] px-5 shadow-[inset_0_0_0_1px_rgba(208,198,181,0.7)] transition duration-300 focus-within:border-[#c9a956] focus-within:shadow-[inset_0_0_0_1px_rgba(201,169,86,1)]">
          <input
            type={showPassword ? "text" : "password"}
            name="password"
            value={form.password}
            onChange={onChange}
            onBlur={() => onBlur("password")}
            className="h-[68px] w-full bg-transparent py-2 text-[16px] text-[#1f1f1f] placeholder:text-[#9a968f] outline-none"
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

      <div className="mb-5 h-2 rounded-full bg-[#e3ddd2]" />

      <div className="mb-6">
        <div className="group flex items-center rounded-[16px] border border-transparent bg-[#f8f7f4] px-5 shadow-[inset_0_0_0_1px_rgba(208,198,181,0.7)] transition duration-300 focus-within:border-[#c9a956] focus-within:shadow-[inset_0_0_0_1px_rgba(201,169,86,1)]">
          <input
            type={showConfirmPassword ? "text" : "password"}
            name="confirmPassword"
            value={form.confirmPassword}
            onChange={onChange}
            onBlur={() => onBlur("confirmPassword")}
            className="h-[68px] w-full bg-transparent py-2 text-[16px] text-[#1f1f1f] placeholder:text-[#9a968f] outline-none"
            placeholder="Confirm Password"
          />
          <button
            type="button"
            onClick={onToggleConfirmPassword}
            className="text-[#8a857e] transition hover:text-black"
            aria-label={
              showConfirmPassword ? "Hide confirm password" : "Show confirm password"
            }
          >
            {showConfirmPassword ? (
              <EyeOff className="size-[18px]" />
            ) : (
              <Eye className="size-[18px]" />
            )}
          </button>
        </div>
        {errors.confirmPassword && (
          <p className="mt-2 text-sm text-[#b42318]">
            {errors.confirmPassword}
          </p>
        )}
      </div>

      {serverError && (
        <p className="mb-5 rounded-[16px] border border-[#f3d0cc] bg-[#fff5f4] px-4 py-3 text-sm text-[#b42318]">
          {serverError}
        </p>
      )}

      <label className="mb-8 flex items-center justify-center gap-3 text-[13px] text-[#2d2d2d]">
        <input
          type="checkbox"
          name="agree"
          checked={form.agree}
          onChange={onChange}
          onBlur={() => onBlur("agree")}
          className="size-4 rounded-[4px] border border-[#cfc7bb] bg-white text-black"
        />
        <span>
          I agree to <span className="font-medium">Privacy Policy</span>
        </span>
      </label>
      {errors.agree && (
        <p className="-mt-5 mb-5 text-center text-sm text-[#b42318]">
          {errors.agree}
        </p>
      )}

      <button
        type="submit"
        disabled={isBusy}
        className="mb-8 inline-flex h-[68px] w-full items-center justify-center rounded-[16px] bg-[#191919] text-[17px] font-semibold text-white transition hover:-translate-y-px hover:bg-black disabled:cursor-not-allowed disabled:opacity-60"
      >
        {isBusy ? "Signing Up..." : "Sign Up"}
      </button>

      <p className="text-center text-[13px] text-[#6f695f]">
        <button
          type="button"
          onClick={onSignIn}
          className="font-medium text-[#6f695f] transition hover:text-black"
        >
          ← Back to Login
        </button>
      </p>
    </form>
  );
}

export default SignupForm;
