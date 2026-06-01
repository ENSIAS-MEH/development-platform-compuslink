import { useState, useEffect } from "react";
import { Link } from "react-router-dom";

const cities = ["Casablanca", "Rabat", "Marrakech", "Fès", "Tanger"];

export default function ColocationPage() {
  const [activeCity, setActiveCity] = useState("Casablanca");
  const [colocations, setColocations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchColocations = async () => {
      setLoading(true);
      setError(null);
      try {
        const token = localStorage.getItem("accessToken");
        const headers = {};
        if (token) headers["Authorization"] = `Bearer ${token}`;

        // Appel vers ton contrôleur Spring Boot avec filtre de ville
        const response = await fetch(`http://localhost:8080/api/colocations?city=${activeCity}`, {
          headers: headers
        });

        if (!response.ok) throw new Error(`Erreur HTTP: ${response.status}`);

        const data = await response.json();
        // Extraction de la liste depuis la pagination (.content) de Spring Data
        setColocations(data.content || data);
      } catch (err) {
        console.error(err);
        setError("Impossible de charger les annonces de colocation.");
      } finally {
        setLoading(false);
      }
    };

    fetchColocations();
  }, [activeCity]);

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

      {/* Filtre par Ville */}
      <div className="flex items-center gap-3 mt-8">
        <span className="flex items-center gap-2 text-sm text-gray-500">
          <span className="material-symbols-outlined text-[18px]">tune</span>
          Filter by City:
        </span>
        {cities.map((city) => (
          <button
            key={city}
            onClick={() => setActiveCity(city)}
            className={`px-4 py-2 rounded-full text-sm font-medium border transition-colors ${
              activeCity === city ? "bg-primary text-white border-primary" : "border-gray-200 text-gray-700 hover:bg-gray-50"
            }`}
          >
            {city}
          </button>
        ))}
      </div>

      {loading && <div className="text-center py-12 text-gray-500">Chargement des annonces...</div>}
      {error && <div className="text-center py-12 text-red-500 font-medium">{error}</div>}

      {!loading && !error && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mt-10">
          {colocations.length === 0 ? (
            <div className="col-span-full text-center text-gray-400 py-12">Aucune annonce disponible à {activeCity} pour le moment.</div>
          ) : (
            colocations.map((coloc) => {
              // Calcul des places restantes basés sur ColocPostDTO
              const spotsLeft = coloc.spotsNeeded - (coloc.spotsConfirmed || 0);

              return (
                <Link to={`/colocation/${coloc.id}`} key={coloc.id} className="bg-white rounded-2xl border border-gray-100 overflow-hidden hover:shadow-lg transition-shadow">
                  <div className="relative h-52">
                    {/* Synchronisé avec 'coverUrl' de ton ColocPostDTO */}
                    <img src={coloc.coverUrl || "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=500"} className="w-full h-full object-cover" alt="" />
                    <span className={`absolute top-3 right-3 text-xs font-semibold px-3 py-1 rounded-full ${
                      coloc.status === "OPEN" ? "bg-green-100 text-green-700" : "bg-orange-100 text-orange-700"
                    }`}>
                      {coloc.status}
                    </span>
                  </div>
                  <div className="p-5">
                    <h3 className="text-lg font-bold font-[Geist]">{coloc.title}</h3>
                    <p className="text-sm text-gray-500 flex items-center gap-1 mt-1">
                      <span className="material-symbols-outlined text-[14px]">location_on</span>
                      {coloc.city}
                    </p>

                    <div className="flex items-center gap-6 mt-4 py-3 border-t border-gray-100">
                      <span className="flex items-center gap-2 text-sm text-gray-600">
                        <span className="material-symbols-outlined text-[16px]">bed</span>
                        {spotsLeft > 0 ? `${spotsLeft} place${spotsLeft > 1 ? "s" : ""} dispo` : "Complet"}
                      </span>
                      <span className="flex items-center gap-2 text-sm text-gray-600">
                        <span className="material-symbols-outlined text-[16px]">payments</span>
                        {coloc.rentPerPerson} DH / mois
                      </span>
                    </div>

                    <div className="flex items-center justify-between mt-4 pt-2 border-t border-dashed border-gray-100 text-xs text-gray-400">
                      <span className="flex items-center gap-1">
                        <span className="material-symbols-outlined text-[14px]">group</span>
                        {coloc.totalInterests || 0} demande(s)
                      </span>
                      <span>Dispo le : {coloc.startDate}</span>
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