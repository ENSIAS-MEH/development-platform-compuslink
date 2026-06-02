import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import api from "../services/api";

interface Offer {
  id: string;
  title: string;
  company: string;
  type: string;
  city: string;
  locationType: string;
  domain: string;
  createdAt: string;
}

export default function OffersPage() {
  const [offers, setOffers] = useState<Offer[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [typeFilter, setTypeFilter] = useState("");
  const [locationFilter, setLocationFilter] = useState("");

  useEffect(() => {
    fetchOffers();
  }, [typeFilter, locationFilter]);

  const fetchOffers = async () => {
    setLoading(true);
    try {
      const params: Record<string, string> = {};
      if (typeFilter) params.type = typeFilter;
      if (locationFilter) params.locationType = locationFilter;
      const { data } = await api.get("/offers", { params });
      setOffers(data.content || []);
    } catch (err) {
      console.error("Failed to load offers", err);
    } finally {
      setLoading(false);
    }
  };

  const filtered = offers.filter(
    (o) =>
      o.title?.toLowerCase().includes(search.toLowerCase()) ||
      o.company?.toLowerCase().includes(search.toLowerCase())
  );

  const timeAgo = (date: string) => {
    const diff = Date.now() - new Date(date).getTime();
    const days = Math.floor(diff / 86400000);
    if (days === 0) return "Aujourd'hui";
    if (days === 1) return "Hier";
    return `il y a ${days} jours`;
  };

  return (
    <div className="max-w-6xl mx-auto px-8 py-12">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-4xl font-bold font-[Geist]">Explore Opportunities</h1>
          <p className="text-gray-500 mt-3">Discover top internships and job offers from leading Moroccan companies tailored for ambitious students.</p>
        </div>
        <Link to="/offers/create" className="bg-primary text-white px-5 py-2.5 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors whitespace-nowrap">
          + Publier une offre
        </Link>
      </div>

      {/* Search & Filters */}
      <div className="mt-8 bg-white rounded-2xl border border-gray-100 p-2 flex flex-wrap gap-2">
        <div className="flex-1 min-w-[200px] flex items-center gap-2 px-4">
          <span className="material-symbols-outlined text-gray-400 text-[20px]">search</span>
          <input type="text" placeholder="Search jobs, companies, skills..." className="w-full py-3 text-sm outline-none" value={search} onChange={(e) => setSearch(e.target.value)} />
        </div>
        <select className="px-4 py-3 text-sm text-gray-600 bg-gray-50 rounded-xl border-none outline-none" value={typeFilter} onChange={(e) => setTypeFilter(e.target.value)}>
          <option value="">Type</option>
          <option value="INTERNSHIP">Internship</option>
          <option value="PFE">PFE</option>
          <option value="JOB">Job</option>
        </select>
        <select className="px-4 py-3 text-sm text-gray-600 bg-gray-50 rounded-xl border-none outline-none" value={locationFilter} onChange={(e) => setLocationFilter(e.target.value)}>
          <option value="">Location</option>
          <option value="REMOTE">Remote</option>
          <option value="ON_SITE">On-site</option>
          <option value="HYBRID">Hybrid</option>
        </select>
      </div>

      {/* Offers List */}
      {loading ? (
        <p className="text-center text-gray-400 mt-16">Chargement...</p>
      ) : filtered.length === 0 ? (
        <div className="text-center py-16">
          <span className="material-symbols-outlined text-gray-300 text-5xl">work_off</span>
          <p className="text-gray-500 mt-4">Aucune offre trouvée.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-10">
          {filtered.map((offer) => (
            <Link to={`/offers/${offer.id}`} key={offer.id} className="bg-white rounded-2xl border border-gray-100 p-6 hover:shadow-md transition-shadow block">
              <div className="flex items-start gap-4">
                <div className="w-12 h-12 bg-blue-600 rounded-xl flex items-center justify-center text-white font-bold">
                  {offer.company?.charAt(0) || "?"}
                </div>
                <div className="flex-1">
                  <h3 className="text-lg font-bold font-[Geist]">{offer.title}</h3>
                  <p className="text-sm text-gray-500">{offer.company}</p>
                </div>
                <span className="text-xs bg-blue-50 text-primary font-medium px-2.5 py-1 rounded-full">{offer.type}</span>
              </div>

              <p className="text-sm text-gray-500 flex items-center gap-1 mt-3">
                <span className="material-symbols-outlined text-[14px]">location_on</span>
                {offer.city || "Non spécifié"} {offer.locationType && `(${offer.locationType.replace("_", " ")})`}
              </p>

              <div className="flex items-center justify-between mt-5">
                <span className="text-xs text-gray-400 flex items-center gap-1">
                  <span className="material-symbols-outlined text-[14px]">schedule</span>
                  {timeAgo(offer.createdAt)}
                </span>
                <span className="text-primary text-sm font-medium">Voir détails →</span>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
