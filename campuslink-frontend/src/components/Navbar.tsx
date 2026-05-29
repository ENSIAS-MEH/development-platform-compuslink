import { NavLink } from "react-router-dom";

const links = [
  { to: "/colocation", label: "Colocation" },
  { to: "/marketplace", label: "Marketplace" },
  { to: "/offers", label: "Offers" },
  { to: "/events", label: "Events" },
  { to: "/profile", label: "Profile" },
];

export default function Navbar() {
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

      <button className="p-2 rounded-full hover:bg-gray-100">
        <span className="material-symbols-outlined text-gray-600">search</span>
      </button>
    </header>
  );
}
