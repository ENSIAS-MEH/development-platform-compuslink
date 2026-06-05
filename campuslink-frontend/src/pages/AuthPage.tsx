import { useState } from "react";
import { useNavigate, Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { API_ORIGIN } from "../services/api";

export default function AuthPage() {
  const { user } = useAuth();
  const [tab, setTab] = useState<"signin" | "signup">("signin");

  if (user) return <Navigate to="/" replace />;

  return (
    <div className="min-h-screen bg-[#f8f9ff] flex flex-col">
      <div className="p-6">
        <h1 className="text-primary font-bold text-xl font-[Geist]">CampusLink</h1>
      </div>

      <div className="flex-1 flex items-center justify-center px-4">
        <div className="w-full max-w-md">
          <div className="text-center mb-8">
            <h2 className="text-3xl font-bold font-[Geist]">Welcome back</h2>
            <p className="text-gray-500 mt-2">Join the community of 10k+ Moroccan students.</p>
          </div>

          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-8">
            <div className="flex gap-6 mb-6 border-b border-gray-100">
              <button
                onClick={() => setTab("signin")}
                className={`pb-3 text-sm font-medium ${tab === "signin" ? "text-gray-900 border-b-2 border-primary" : "text-gray-400"}`}
              >
                Sign In
              </button>
              <button
                onClick={() => setTab("signup")}
                className={`pb-3 text-sm font-medium ${tab === "signup" ? "text-gray-900 border-b-2 border-primary" : "text-gray-400"}`}
              >
                Sign Up
              </button>
            </div>

            {tab === "signin" ? <SignInForm /> : <SignUpForm />}
          </div>

          <div className="flex items-center justify-center gap-4 mt-6 text-xs text-gray-400">
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
    </div>
  );
}

function SignInForm() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await login(email, password);
      navigate("/");
    } catch (err: any) {
      setError(err.response?.data?.message || "Invalid email or password");
    } finally {
      setLoading(false);
    }
  };

  const handleGoogle = () => {
    window.location.href = `${API_ORIGIN}/oauth2/authorization/google`;
  };

  return (
    <form className="space-y-5" onSubmit={handleSubmit}>
      {error && (
        <div className="bg-red-50 text-red-600 text-sm px-4 py-3 rounded-xl">{error}</div>
      )}

      <button type="button" onClick={handleGoogle} className="w-full flex items-center justify-center gap-3 py-3 border border-gray-200 rounded-xl text-sm font-medium hover:bg-gray-50 transition-colors">
        <img src="https://www.google.com/favicon.ico" className="w-4 h-4" alt="" />
        Continue with Google
      </button>

      <div className="flex items-center gap-3">
        <div className="flex-1 h-px bg-gray-200" />
        <span className="text-xs text-gray-400 tracking-widest">OR EMAIL</span>
        <div className="flex-1 h-px bg-gray-200" />
      </div>

      <div>
        <label className="text-sm font-medium">Student Email</label>
        <input
          type="email" required value={email} onChange={(e) => setEmail(e.target.value)}
          placeholder="nom.prenom@university.ma"
          className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
        />
      </div>

      <div>
        <label className="text-sm font-medium">Password</label>
        <input
          type="password" required value={password} onChange={(e) => setPassword(e.target.value)}
          placeholder="••••••••"
          className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
        />
      </div>

      <button
        type="submit" disabled={loading}
        className="w-full bg-primary text-white py-3 rounded-xl font-medium text-sm hover:bg-primary-dark transition-colors disabled:opacity-50 flex items-center justify-center gap-2"
      >
        {loading ? "Signing in..." : <>Sign In <span className="material-symbols-outlined text-[18px]">arrow_forward</span></>}
      </button>
    </form>
  );
}

function SignUpForm() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: "", password: "", fullName: "", university: "", city: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const set = (field: string) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm((f) => ({ ...f, [field]: e.target.value }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await register(form);
      navigate("/");
    } catch (err: any) {
      setError(err.response?.data?.message || "Registration failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form className="space-y-4" onSubmit={handleSubmit}>
      {error && (
        <div className="bg-red-50 text-red-600 text-sm px-4 py-3 rounded-xl">{error}</div>
      )}

      <div>
        <label className="text-sm font-medium">Full Name *</label>
        <input type="text" required value={form.fullName} onChange={set("fullName")} placeholder="Ahmed Mansouri" className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" />
      </div>
      <div>
        <label className="text-sm font-medium">Student Email *</label>
        <input type="email" required value={form.email} onChange={set("email")} placeholder="nom.prenom@university.ma" className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" />
      </div>
      <div>
        <label className="text-sm font-medium">Password *</label>
        <input type="password" required value={form.password} onChange={set("password")} placeholder="Min 8 characters" className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" />
      </div>
      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="text-sm font-medium">University</label>
          <input type="text" value={form.university} onChange={set("university")} placeholder="ENSIAS" className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" />
        </div>
        <div>
          <label className="text-sm font-medium">City</label>
          <input type="text" value={form.city} onChange={set("city")} placeholder="Rabat" className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" />
        </div>
      </div>
      <button
        type="submit" disabled={loading}
        className="w-full bg-primary text-white py-3 rounded-xl font-medium text-sm hover:bg-primary-dark transition-colors disabled:opacity-50"
      >
        {loading ? "Creating account..." : "Create Account"}
      </button>
    </form>
  );
}
