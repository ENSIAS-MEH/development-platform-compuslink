import { useState, useEffect } from "react";
import { API_ORIGIN } from "../services/api";
import { Link } from "react-router-dom";

const cities = ["Toutes", "Casablanca", "Rabat", "Marrakech", "Fès", "Tanger"];

export default function ColocationPage() {
  const [activeCity, setActiveCity] = useState("Toutes");
  const [furnishedFilter, setFurnishedFilter] = useState("");
  const [spotsFilter, setSpotsFilter] = useState("");
  const [colocations, setColocations] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchColocations = async () => {
      setLoading(true);
      setError(null);
      try {
        const token = localStorage.getItem("accessToken");
        const headers: HeadersInit = { "Content-Type": "application/json" };

        if (token && token.trim() !== "") {
          headers["Authorization"] = `Bearer ${token}`;
        }

        const params = new URLSearchParams();
        if (activeCity !== "Toutes") params.append("city", activeCity);
        if (furnishedFilter !== "") params.append("furnished", furnishedFilter);
        if (spotsFilter !== "") params.append("spotsNeeded", spotsFilter);

        const response = await fetch(`${API_ORIGIN}/api/coloc?${params.toString()}`, {
          method: "GET",
          headers: headers
        });

        if (!response.ok) throw new Error(`Erreur HTTP: ${response.status}`);

        const data = await response.json();
        setColocations(data.content || data);
      } catch (err: any) {
        console.error(err);
        setError("Impossible de charger les annonces de colocation.");
      } finally {
        setLoading(false);
      }
    };

    fetchColocations();
  }, [activeCity, furnishedFilter, spotsFilter]);

  return (
    <div className="max-w-6xl mx-auto px-8 py-12">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-4xl font-bold font-[Geist]">Find your next flatmate.</h1>
          <p className="text-gray-500 mt-3 max-w-xl">
            Discover available rooms, connect with ambitious students, and secure your ideal living space.
          </p>
        </div>
        <Link to="/colocation/create" className="bg-primary text-white px-5 py-3 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors">
          + Publier une annonce
        </Link>
      </div>

      {/* Barre de filtres de recherche */}
      <div className="bg-gray-50 border border-gray-100 rounded-2xl p-5 mt-8 space-y-4">
        <div className="flex items-center gap-2 text-sm font-semibold text-gray-700">
          <span className="material-symbols-outlined text-[18px]">tune</span>
          Filtres de recherche
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {/* Ville */}
          <div>
            <label className="block text-xs font-medium text-gray-500 mb-1">Ville</label>
            <select
              value={activeCity}
              onChange={(e) => setActiveCity(e.target.value)}
              className="w-full px-3 py-2 bg-white border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
            >
              {cities.map(c => <option key={c} value={c}>{c === "Toutes" ? "Toutes les villes" : c}</option>)}
            </select>
          </div>

          {/* Ameublement */}
          <div>
            <label className="block text-xs font-medium text-gray-500 mb-1">État du logement</label>
            <select
              value={furnishedFilter}
              onChange={(e) => setFurnishedFilter(e.target.value)}
              className="w-full px-3 py-2 bg-white border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
            >
              <option value="">Tous les types</option>
              <option value="true">Meublé</option>
              <option value="false">Non Meublé</option>
            </select>
          </div>

          {/* Capacité Totale */}
          <div>
            <label className="block text-xs font-medium text-gray-500 mb-1">Capacité maximale</label>
            <input
              type="number"
              min="1"
              placeholder="Ex: 3"
              value={spotsFilter}
              onChange={(e) => setSpotsFilter(e.target.value)}
              className="w-full px-3 py-2 bg-white border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
            />
          </div>
        </div>
      </div>

      {loading && <div className="text-center py-12 text-gray-500">Chargement des annonces...</div>}
      {error && <div className="text-center py-12 text-red-500 font-medium">{error}</div>}

      {!loading && !error && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mt-10">
          {colocations.length === 0 ? (
            <div className="col-span-full text-center text-gray-400 py-12">
              Aucune annonce ne correspond à vos critères de recherche.
            </div>
          ) : (
              colocations.map((coloc: any) => {
              const spotsLeft = coloc.spotsNeeded - (coloc.spotsConfirmed || 0);

              return (
                <Link to={`/colocation/${coloc.id}`} key={coloc.id} className="bg-white rounded-2xl border border-gray-100 overflow-hidden hover:shadow-lg transition-shadow">
                  <div className="relative h-52 bg-gray-100 flex items-center justify-center">
                    {/* CORRECTION : Affichage de la VRAIE photo, sinon case vide */}
                    {coloc.coverUrl ? (
                      <img src={coloc.coverUrl} className="w-full h-full object-cover" alt="Cover" />
                    ) : (
                      <span className="material-symbols-outlined text-gray-300 text-4xl">image_not_supported</span>
                    )}

                    <span className="absolute top-3 right-3 text-xs font-semibold px-3 py-1 rounded-full bg-green-100 text-green-700">
                      {coloc.status}
                    </span>
                  </div>
                  <div className="p-5">
                    <h3 className="text-lg font-bold font-[Geist] truncate">{coloc.title}</h3>

                    <p className="text-xs text-primary font-medium mt-1 bg-primary/5 px-2 py-0.5 rounded inline-block">
                      Par : {coloc.posterName || "Anonyme"}
                    </p>
                    <p className="text-sm text-gray-500 flex items-center gap-1 mt-3">
                      <span className="material-symbols-outlined text-[14px]">location_on</span>
                      {coloc.city}
                    </p>

                    <div className="flex items-center gap-4 mt-4 py-3 border-t border-gray-100 text-xs text-gray-600">
                      <span className="flex items-center gap-1">
                        <span className="material-symbols-outlined text-[16px]">bed</span>
                        {spotsLeft > 0 ? `${spotsLeft} place${spotsLeft > 1 ? "s" : ""} dispo` : "Complet"}
                      </span>
                      <span className="flex items-center gap-1">
                        <span className="material-symbols-outlined text-[16px]">chair</span>
                        {coloc.furnished ? "Meublé" : "Non meublé"}
                      </span>
                    </div>

                    <div className="flex items-center justify-between mt-2 pt-2 border-t border-dashed border-gray-100 text-xs text-gray-400">
                      <span className="flex items-center gap-1">
                        <span className="material-symbols-outlined text-[14px]">payments</span>
                        <strong className="text-gray-700">{coloc.rentPerPerson} DH</strong>/mois
                      </span>
                      <span>Dispo : {coloc.startDate}</span>
                    </div>
                  </div>
                </Link>
              );
            })
          )}
        </div>
      )}
    </div>
  );
}