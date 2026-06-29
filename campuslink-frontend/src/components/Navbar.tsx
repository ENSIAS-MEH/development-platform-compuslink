import { NavLink, Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const links = [
  { to: "/colocation", label: "Colocation" },
  { to: "/marketplace", label: "Marketplace" },
  { to: "/offers", label: "Offers" },
  { to: "/events", label: "Events" },
  { to: "/messages", label: "Messages" },
  { to: "/profile", label: "Profile" },
];

export default function Navbar() {
  const { user, logout } = useAuth();

  return (
    <header className="fixed top-0 left-0 right-0 h-16 bg-white border-b border-gray-100 flex items-center justify-between px-8 lg:px-16 z-50">
      <NavLink to="/" className="text-primary font-bold text-xl font-[Geist]">
        CampusLink
      </NavLink>

      <nav className="hidden md:flex items-center gap-8">
        {links.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            className={({ isActive }) =>
              `text-sm font-medium transition-colors ${
                isActive
                  ? "text-primary underline underline-offset-[20px] decoration-2"
                  : "text-gray-600 hover:text-gray-900"
              }`
            }
          >
            {link.label}
          </NavLink>
        ))}
      </nav>

      <div className="flex items-center gap-3">
        {user ? (
          <>
            <span className="text-sm text-gray-600 hidden lg:block">{user.email}</span>
            <button
              onClick={logout}
              className="text-sm text-gray-500 hover:text-red-600 transition-colors"
            >
              Logout
            </button>
          </>
        ) : (
          <Link to="/auth" className="bg-primary text-white px-4 py-2 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors">
            Sign In
          </Link>
        )}
      </div>
    </header>
  );
}
