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
  coverImageUrl: string | null;
}

interface PaginatedResponse {
  content: ItemCard[];
  totalElements: number;
  totalPages: number;
}

const PLACEHOLDER_IMAGE = "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 400 300'%3E%3Crect fill='%23e5e7eb' width='400' height='300'/%3E%3Ctext x='50%' y='50%' font-size='20' fill='%236b7280' text-anchor='middle' dy='.3em'%3ENo Image Available%3C/text%3E%3C/svg%3E";

const conditionMap: Record<string, string> = {
  "NEW": "Neuf",
  "LIKE_NEW": "Très bon état",
  "GOOD": "Bon état",
  "FAIR": "Correct",
};

const conditionColors: Record<string, string> = {
  "Neuf": "bg-blue-100 text-blue-700",
  "Très bon état": "bg-green-100 text-green-700",
  "Bon état": "bg-yellow-100 text-yellow-700",
  "Correct": "bg-orange-100 text-orange-700",
};

const getConditionLabel = (condition: string): string => {
  return conditionMap[condition] || condition;
};

export default function MarketplacePage() {
  const { user } = useAuth();
  const [items, setItems] = useState<ItemCard[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [city, setCity] = useState("");
  const [category, setCategory] = useState("");
  const [priceRange, setPriceRange] = useState("");
  const [showSavedOnly, setShowSavedOnly] = useState(false);

  useEffect(() => {
    const fetchItems = async () => {
      setLoading(true);
      try {
        if (showSavedOnly) {
          // common-service only returns the saved targetId, so fetch each item's details
          const response = await api.get(`/saved?targetType=ITEM`);
          const saved = response.data as Array<{ targetId: string }>;
          const items = await Promise.all(
            saved.map((s) =>
              api.get<ItemCard>(`/items/${s.targetId}`).then((r) => r.data).catch(() => null)
            )
          );
          setItems(items.filter((i): i is ItemCard => i !== null));
        } else {
          const params = new URLSearchParams();
          if (search) params.append("search", search);
          if (city) params.append("city", city);
          if (category) params.append("category", category);

          const response = await api.get<PaginatedResponse>(`/items?${params}`);
          setItems(response.data.content || response.data);
        }
      } catch (err) {
        console.error("Erreur lors du chargement des articles", err);
      } finally {
        setLoading(false);
      }
    };

    const timer = setTimeout(fetchItems, 300);
    return () => clearTimeout(timer);
  }, [search, city, category, showSavedOnly]);

  const filteredItems = items.filter((item) => {
    if (!priceRange) return true;
    const [min, max] = priceRange.split("-").map(Number);
    return item.price >= min && item.price <= max;
  });

  const getFirstImage = (item: ItemCard) => {
    return item.coverImageUrl || "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 400 300'%3E%3Crect fill='%23e5e7eb' width='400' height='300'/%3E%3Ctext x='50%' y='50%' font-size='20' fill='%236b7280' text-anchor='middle' dy='.3em'%3ENo Image Available%3C/text%3E%3C/svg%3E";
  };

  return (
    <div className="max-w-6xl mx-auto px-8 py-12">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-4xl font-bold font-[Geist]">Marketplace</h1>
        {user && (
          <button
            onClick={() => setShowSavedOnly(!showSavedOnly)}
            className={`px-4 py-2 rounded-xl text-sm font-medium transition-colors ${
              showSavedOnly
                ? "bg-primary text-white"
                : "bg-gray-100 text-gray-600 hover:bg-gray-200"
            }`}
          >
            Mes favoris
          </button>
        )}
      </div>

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
                <img
                  src={getFirstImage(item)}
                  className="w-full h-full object-cover"
                  alt={item.title}
                  onError={(e) => {
                    (e.target as HTMLImageElement).src = PLACEHOLDER_IMAGE;
                  }}
                />
              </div>
              <div className="p-4">
                <div className="flex items-start justify-between gap-2">
                  <h3 className="text-sm font-semibold leading-tight line-clamp-2">{item.title}</h3>
                  <span className={`text-xs font-medium px-2 py-0.5 rounded-full whitespace-nowrap ${conditionColors[getConditionLabel(item.condition)] || "bg-gray-100 text-gray-600"}`}>
                    {getConditionLabel(item.condition)}
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
                    if (!user) {
                      window.location.href = "/auth";
                      return;
                    }
                    alert("Redirection vers chat avec le vendeur");
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
