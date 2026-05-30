import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import api from "../services/api";
import { useAuth } from "../context/AuthContext";

interface ItemCard {
  id: string;
  title: string;
  price: number;
  condition: string;
  city: string;
  category: string;
  images: Array<{ id: string; url: string }>;
}

interface PaginatedResponse {
  content: ItemCard[];
  totalElements: number;
  totalPages: number;
}

const conditionColors: Record<string, string> = {
  "Neuf": "bg-blue-100 text-blue-700",
  "Très bon état": "bg-green-100 text-green-700",
  "Bon état": "bg-yellow-100 text-yellow-700",
  "Correct": "bg-orange-100 text-orange-700",
};

export default function MarketplacePage() {
  const { user } = useAuth();
  const [items, setItems] = useState<ItemCard[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [city, setCity] = useState("");
  const [category, setCategory] = useState("");
  const [priceRange, setPriceRange] = useState("");

  useEffect(() => {
    const fetchItems = async () => {
      setLoading(true);
      try {
        const params = new URLSearchParams();
        if (search) params.append("search", search);
        if (city) params.append("city", city);
        if (category) params.append("category", category);

        const response = await api.get<PaginatedResponse>(`/marketplace/items?${params}`);
        setItems(response.data.content || response.data);
      } catch (err) {
        console.error("Erreur lors du chargement des articles", err);
      } finally {
        setLoading(false);
      }
    };

    const timer = setTimeout(fetchItems, 300);
    return () => clearTimeout(timer);
  }, [search, city, category]);

  const filteredItems = items.filter((item) => {
    if (!priceRange) return true;
    const [min, max] = priceRange.split("-").map(Number);
    return item.price >= min && item.price <= max;
  });

  const getFirstImage = (item: ItemCard) => {
    return item.images?.length > 0 ? item.images[0].url : "https://via.placeholder.com/400x300";
  };

  return (
    <div className="max-w-6xl mx-auto px-8 py-12">
      <h1 className="text-4xl font-bold font-[Geist]">Marketplace</h1>

      {/* Search & Filters */}
      <div className="mt-8 bg-white rounded-2xl border border-gray-100 p-2 flex flex-wrap gap-2">
        <div className="flex-1 min-w-[200px] flex items-center gap-2 px-4">
          <span className="material-symbols-outlined text-gray-400 text-[20px]">search</span>
          <input
            type="text"
            placeholder="Rechercher des articles..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full py-3 text-sm outline-none"
          />
        </div>
        <select
          value={city}
          onChange={(e) => setCity(e.target.value)}
          className="px-4 py-3 text-sm text-gray-600 bg-gray-50 rounded-xl border-none outline-none min-w-[140px]"
        >
          <option value="">Toutes les villes</option>
          <option value="Casablanca">Casablanca</option>
          <option value="Rabat">Rabat</option>
          <option value="Fès">Fès</option>
          <option value="Tanger">Tanger</option>
          <option value="Marrakech">Marrakech</option>
        </select>
        <select
          value={category}
          onChange={(e) => setCategory(e.target.value)}
          className="px-4 py-3 text-sm text-gray-600 bg-gray-50 rounded-xl border-none outline-none min-w-[140px]"
        >
          <option value="">Toutes les catégories</option>
          <option value="Electronics">Electronics</option>
          <option value="Books">Books</option>
          <option value="Furniture">Furniture</option>
          <option value="Services">Services</option>
          <option value="Other">Other</option>
        </select>
        <select
          value={priceRange}
          onChange={(e) => setPriceRange(e.target.value)}
          className="px-4 py-3 text-sm text-gray-600 bg-gray-50 rounded-xl border-none outline-none min-w-[140px]"
        >
          <option value="">Tous les prix</option>
          <option value="0-500">0 - 500 MAD</option>
          <option value="500-2000">500 - 2000 MAD</option>
          <option value="2000-1000000">2000+ MAD</option>
        </select>
      </div>

      {/* Items Grid */}
      {loading ? (
        <div className="mt-10 text-center text-gray-500">Chargement des articles...</div>
      ) : filteredItems.length === 0 ? (
        <div className="mt-10 text-center text-gray-500">Aucun article trouvé</div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mt-10">
          {filteredItems.map((item) => (
            <Link
              to={`/marketplace/${item.id}`}
              key={item.id}
              className="bg-white rounded-2xl border border-gray-100 overflow-hidden hover:shadow-lg transition-shadow cursor-pointer"
            >
              <div className="h-48 bg-gray-100">
                <img src={getFirstImage(item)} className="w-full h-full object-cover" alt={item.title} />
              </div>
              <div className="p-4">
                <div className="flex items-start justify-between gap-2">
                  <h3 className="text-sm font-semibold leading-tight line-clamp-2">{item.title}</h3>
                  <span className={`text-xs font-medium px-2 py-0.5 rounded-full whitespace-nowrap ${conditionColors[item.condition] || "bg-gray-100 text-gray-600"}`}>
                    {item.condition}
                  </span>
                </div>
                <p className="text-primary text-xl font-bold mt-2">{item.price.toLocaleString()} MAD</p>
                <p className="text-xs text-gray-500 flex items-center gap-1 mt-1">
                  <span className="material-symbols-outlined text-[12px]">location_on</span>
                  {item.city}
                </p>
                <button
                  onClick={(e) => {
                    e.preventDefault();
                    if (user) {
                      alert("Redirection vers chat avec le vendeur");
                    } else {
                      alert("Veuillez vous connecter d'abord");
                    }
                  }}
                  className="w-full mt-4 py-2.5 border border-gray-200 rounded-xl text-sm font-medium flex items-center justify-center gap-2 hover:bg-gray-50 transition-colors"
                >
                  <span className="material-symbols-outlined text-[16px]">chat_bubble_outline</span>
                  Contacter
                </button>
              </div>
            </Link>
          ))}
        </div>
      )}

      {/* FAB */}
      <Link
        to={user ? "/marketplace/create" : "/auth"}
        className="fixed bottom-8 right-8 bg-primary text-white px-6 py-3 rounded-full shadow-lg hover:bg-primary-dark transition-colors flex items-center gap-2 text-sm font-medium"
      >
        <span className="material-symbols-outlined text-[18px]">add</span>
        Poster un article
      </Link>
    </div>
  );
}
