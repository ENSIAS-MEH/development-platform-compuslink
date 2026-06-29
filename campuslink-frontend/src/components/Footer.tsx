import { Link } from "react-router-dom";

export default function Footer() {
  return (
    <footer className="border-t border-gray-100 bg-white mt-16">
      <div className="max-w-6xl mx-auto px-8 py-12 grid grid-cols-1 md:grid-cols-4 gap-8">
        <div>
          <h3 className="text-primary font-bold text-lg font-[Geist]">CampusLink</h3>
          <p className="text-sm text-gray-500 mt-2">Connecting students with the best opportunities, housing, and community.</p>
        </div>
        <div>
          <h4 className="font-semibold text-sm mb-3">Platform</h4>
          <div className="space-y-2">
            <Link to="/colocation" className="block text-sm text-gray-500 hover:text-gray-900">About Us</Link>
            <Link to="/" className="block text-sm text-gray-500 hover:text-gray-900">Help Center</Link>
          </div>
        </div>
        <div>
          <h4 className="font-semibold text-sm mb-3">Legal</h4>
          <div className="space-y-2">
            <a href="#" className="block text-sm text-gray-500 hover:text-gray-900">Terms of Service</a>
            <a href="#" className="block text-sm text-gray-500 hover:text-gray-900">Privacy Policy</a>
          </div>
        </div>
        <div>
          <h4 className="font-semibold text-sm mb-3">Connect</h4>
          <div className="space-y-2">
            <a href="#" className="block text-sm text-gray-500 hover:text-gray-900">Contact</a>
          </div>
        </div>
      </div>
      <div className="border-t border-gray-100 py-6 text-center text-xs text-gray-400">
        © 2024 CampusLink Morocco. All rights reserved.
      </div>
    </footer>
  );
}
