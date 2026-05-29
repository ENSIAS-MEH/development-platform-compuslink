import { useState } from "react";

export default function AuthPage() {
  const [tab, setTab] = useState<"signin" | "signup">("signin");

  return (
    <div className="min-h-screen bg-surface flex flex-col">
      <div className="p-6">
        <h1 className="text-primary font-bold text-xl font-[Geist]">CampusLink</h1>
      </div>

      <div className="flex-1 flex items-center justify-center px-4">
        <div className="w-full max-w-md">
          <div className="text-center mb-8">
            <h2 className="text-3xl font-bold font-[Geist]">Welcome back</h2>
            <p className="text-on-surface-variant mt-2">Join the community of 10k+ Moroccan students.</p>
          </div>

          <div className="bg-white rounded-2xl shadow-sm border border-outline-variant/30 p-8">
            <div className="flex gap-6 mb-6 border-b border-outline-variant/30">
              <button
                onClick={() => setTab("signin")}
                className={`pb-3 text-sm font-medium ${tab === "signin" ? "text-on-surface border-b-2 border-primary" : "text-on-surface-variant"}`}
              >
                Sign In
              </button>
              <button
                onClick={() => setTab("signup")}
                className={`pb-3 text-sm font-medium ${tab === "signup" ? "text-on-surface border-b-2 border-primary" : "text-on-surface-variant"}`}
              >
                Sign Up
              </button>
            </div>

            {tab === "signin" ? <SignInForm /> : <SignUpForm />}
          </div>

          <div className="flex items-center justify-center gap-4 mt-6 text-xs text-on-surface-variant">
            <span className="flex items-center gap-1">
              <span className="material-symbols-outlined text-[14px]">lock</span>
              Secure 256-bit encryption
            </span>
            <span>•</span>
            <span className="flex items-center gap-1">
              <span className="material-symbols-outlined text-[14px]">verified</span>
              CNPD Compliant
            </span>
          </div>
        </div>
      </div>

      <footer className="p-6 text-center text-xs text-on-surface-variant">
        © 2024 CampusLink Morocco. Engineered for excellence.
      </footer>
    </div>
  );
}

function SignInForm() {
  return (
    <form className="space-y-5" onSubmit={(e) => e.preventDefault()}>
      <div className="grid grid-cols-2 gap-3">
        <button type="button" className="flex items-center justify-center gap-2 py-2.5 border border-outline-variant/50 rounded-xl text-sm hover:bg-surface-container-low">
          <img src="https://www.google.com/favicon.ico" className="w-4 h-4" alt="" />
          Google
        </button>
        <button type="button" className="flex items-center justify-center gap-2 py-2.5 border border-outline-variant/50 rounded-xl text-sm hover:bg-surface-container-low">
          <span className="text-[#0077B5] font-bold text-xs">in</span>
          LinkedIn
        </button>
      </div>

      <div className="flex items-center gap-3">
        <div className="flex-1 h-px bg-outline-variant/30" />
        <span className="text-xs text-on-surface-variant tracking-widest">OR EMAIL</span>
        <div className="flex-1 h-px bg-outline-variant/30" />
      </div>

      <div>
        <label className="text-sm font-medium text-on-surface">Student Email</label>
        <input
          type="email"
          placeholder="nom.prenom@university.ma"
          className="mt-1.5 w-full px-4 py-3 border border-outline-variant/50 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
        />
      </div>

      <div>
        <div className="flex justify-between">
          <label className="text-sm font-medium text-on-surface">Password</label>
          <a href="#" className="text-xs text-primary font-medium">Forgot?</a>
        </div>
        <input
          type="password"
          placeholder="••••••••"
          className="mt-1.5 w-full px-4 py-3 border border-outline-variant/50 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
        />
      </div>

      <label className="flex items-center gap-2 text-sm text-on-surface-variant">
        <input type="checkbox" className="rounded border-outline-variant" />
        Stay logged in for 30 days
      </label>

      <button className="w-full bg-primary text-white py-3 rounded-xl font-medium text-sm hover:bg-primary-dark transition-colors flex items-center justify-center gap-2">
        Sign In
        <span className="material-symbols-outlined text-[18px]">arrow_forward</span>
      </button>

      <p className="text-xs text-center text-on-surface-variant">
        By continuing, you agree to CampusLink's <a href="#" className="underline">Terms of Service</a> and <a href="#" className="underline">Privacy Policy</a>.
      </p>
    </form>
  );
}

function SignUpForm() {
  return (
    <form className="space-y-4" onSubmit={(e) => e.preventDefault()}>
      <div>
        <label className="text-sm font-medium text-on-surface">Full Name</label>
        <input type="text" placeholder="Ahmed Mansouri" className="mt-1.5 w-full px-4 py-3 border border-outline-variant/50 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" />
      </div>
      <div>
        <label className="text-sm font-medium text-on-surface">Student Email</label>
        <input type="email" placeholder="nom.prenom@university.ma" className="mt-1.5 w-full px-4 py-3 border border-outline-variant/50 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" />
      </div>
      <div>
        <label className="text-sm font-medium text-on-surface">Password</label>
        <input type="password" placeholder="Min 8 characters" className="mt-1.5 w-full px-4 py-3 border border-outline-variant/50 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" />
      </div>
      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="text-sm font-medium text-on-surface">University</label>
          <input type="text" placeholder="ENSIAS" className="mt-1.5 w-full px-4 py-3 border border-outline-variant/50 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" />
        </div>
        <div>
          <label className="text-sm font-medium text-on-surface">City</label>
          <input type="text" placeholder="Rabat" className="mt-1.5 w-full px-4 py-3 border border-outline-variant/50 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" />
        </div>
      </div>
      <button className="w-full bg-primary text-white py-3 rounded-xl font-medium text-sm hover:bg-primary-dark transition-colors">
        Create Account
      </button>
    </form>
  );
}
