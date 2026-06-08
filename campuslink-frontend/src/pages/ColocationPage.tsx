import { useState, useEffect } from "react";
import { API_ORIGIN } from "../services/api";
import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const cities = ["Toutes", "Casablanca", "Rabat", "Marrakech", "Fès", "Tanger"];

export default function ColocationPage() {
  const { user } = useAuth();
  const [activeCity, setActiveCity] = useState("Toutes");
  const [furnishedFilter, setFurnishedFilter] = useState("");
  const [spotsFilter, setSpotsFilter] = useState("");
  const [colocations, setColocations] = useState<any[]>([]);
  const [myPosts, setMyPosts] = useState<any[]>([]);
  const [myInterests, setMyInterests] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [loadingMyPosts, setLoadingMyPosts] = useState(false);
  const [loadingMyInterests, setLoadingMyInterests] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showMyPosts, setShowMyPosts] = useState(false);
  const [showMyInterests, setShowMyInterests] = useState(false);

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

  useEffect(() => {
    const fetchMyPosts = async () => {
      if (!user || !showMyPosts) return;
      const token = localStorage.getItem("accessToken");
      if (!token) return;

      setLoadingMyPosts(true);
      try {
        const response = await fetch(`${API_ORIGIN}/api/coloc/my-posts`, {
          headers: { "Authorization": `Bearer ${token}` }
        });

        if (response.ok) {
          const data = await response.json();
          setMyPosts(data);
        }
      } catch (err) {
        console.error("Failed to load my posts", err);
      } finally {
        setLoadingMyPosts(false);
      }
    };

    if (showMyPosts) fetchMyPosts();
  }, [showMyPosts, user]);

  useEffect(() => {
    const fetchMyInterests = async () => {
      if (!user || !showMyInterests) return;
      const token = localStorage.getItem("accessToken");
      if (!token) return;

      setLoadingMyInterests(true);
      try {
        const response = await fetch(`${API_ORIGIN}/api/coloc/my-interests`, {
          headers: { "Authorization": `Bearer ${token}` }
        });

        if (response.ok) {
          const data = await response.json();
          setMyInterests(data);
        }
      } catch (err) {
        console.error("Failed to load my interests", err);
      } finally {
        setLoadingMyInterests(false);
      }
    };

    if (showMyInterests) fetchMyInterests();
  }, [showMyInterests, user]);

  return (
    <div className="max-w-6xl mx-auto px-8 py-12">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-4xl font-bold font-[Geist]">Find your next flatmate.</h1>
          <p className="text-gray-500 mt-3 max-w-xl">
            Discover available rooms, connect with ambitious students, and secure your ideal living space.
          </p>
        </div>
        <div className="flex gap-2">
          {user && (
            <>
              <button
                onClick={() => { setShowMyPosts(!showMyPosts); setShowMyInterests(false); }}
                className={`px-5 py-3 rounded-xl text-sm font-medium transition-colors ${
                  showMyPosts
                    ? "bg-primary text-white"
                    : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                }`}
              >
                📋 Mes annonces
              </button>
              <button
                onClick={() => { setShowMyInterests(!showMyInterests); setShowMyPosts(false); }}
                className={`px-5 py-3 rounded-xl text-sm font-medium transition-colors ${
                  showMyInterests
                    ? "bg-primary text-white"
                    : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                }`}
              >
                💌 Mes candidatures
              </button>
            </>
          )}
          <Link to="/colocation/create" className="bg-primary text-white px-5 py-3 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors">
            + Publier une annonce
          </Link>
        </div>
      </div>

      {!showMyPosts && !showMyInterests && (
      <>
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
      </>
      )}

      {showMyPosts && (
        <div className="mt-10">
          <h2 className="text-2xl font-bold font-[Geist] mb-6 flex items-center gap-2">
            <span className="material-symbols-outlined text-primary">edit_note</span>
            Mes annonces de colocation
          </h2>

          {loadingMyPosts && <div className="text-center py-12 text-gray-500">Chargement de vos annonces...</div>}

          {!loadingMyPosts && myPosts.length === 0 && (
            <div className="bg-blue-50 border border-blue-200 rounded-2xl p-8 text-center">
              <span className="material-symbols-outlined text-blue-400 text-4xl block mb-3">home_work</span>
              <p className="text-gray-600 font-medium">Vous n'avez pas encore publié d'annonce.</p>
              <Link to="/colocation/create" className="inline-block mt-4 bg-primary text-white px-5 py-2 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors">
                Publier votre première annonce
              </Link>
            </div>
          )}

          {!loadingMyPosts && myPosts.length > 0 && (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {myPosts.map((coloc: any) => {
                const spotsLeft = coloc.spotsNeeded - (coloc.spotsConfirmed || 0);
                return (
                  <Link to={`/colocation/${coloc.id}`} key={coloc.id} className="bg-white rounded-2xl border border-gray-100 overflow-hidden hover:shadow-lg transition-shadow">
                    <div className="relative h-52 bg-gray-100 flex items-center justify-center">
                      {coloc.coverUrl ? (
                        <img src={coloc.coverUrl} className="w-full h-full object-cover" alt="Cover" />
                      ) : (
                        <span className="material-symbols-outlined text-gray-300 text-4xl">image_not_supported</span>
                      )}
                      <span className="absolute top-3 right-3 text-xs font-semibold px-3 py-1 rounded-full bg-blue-100 text-blue-700">
                        {coloc.status}
                      </span>
                    </div>
                    <div className="p-5">
                      <h3 className="text-lg font-bold font-[Geist] truncate">{coloc.title}</h3>
                      <p className="text-xs text-primary font-medium mt-1 bg-primary/5 px-2 py-0.5 rounded inline-block">
                        Par : {coloc.posterName || "Vous"}
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
                          <span className="material-symbols-outlined text-[16px]">people</span>
                          {coloc.totalInterests || 0} intérêt(s)
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
              })}
            </div>
          )}
        </div>
      )}

      {showMyInterests && (
        <div className="mt-10">
          <h2 className="text-2xl font-bold font-[Geist] mb-6 flex items-center gap-2">
            <span className="material-symbols-outlined text-primary">inbox</span>
            Mes candidatures
          </h2>

          {loadingMyInterests && <div className="text-center py-12 text-gray-500">Chargement de vos candidatures...</div>}

          {!loadingMyInterests && myInterests.length === 0 && (
            <div className="bg-blue-50 border border-blue-200 rounded-2xl p-8 text-center">
              <span className="material-symbols-outlined text-blue-400 text-4xl block mb-3">mail_outline</span>
              <p className="text-gray-600 font-medium">Vous n'avez pas encore candidaté à une colocation.</p>
              <Link to="/colocation" onClick={() => setShowMyInterests(false)} className="inline-block mt-4 bg-primary text-white px-5 py-2 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors">
                Explorer les annonces
              </Link>
            </div>
          )}

          {!loadingMyInterests && myInterests.length > 0 && (
            <div className="space-y-4">
              {myInterests.map((interest: any) => (
                <div key={interest.id} className="bg-white rounded-2xl border border-gray-100 p-6 hover:shadow-lg transition-shadow">
                  <div className="flex items-start justify-between mb-4">
                    <div>
                      <h3 className="text-lg font-bold font-[Geist]">{interest.postTitle}</h3>
                      <p className="text-sm text-primary mt-1">Par : {interest.posterName}</p>
                    </div>
                    <span className={`text-sm font-semibold px-4 py-2 rounded-full ${
                      interest.status === 'PENDING' ? 'bg-yellow-100 text-yellow-700' :
                      interest.status === 'ACCEPTED' ? 'bg-green-100 text-green-700' :
                      'bg-red-100 text-red-700'
                    }`}>
                      {interest.status === 'PENDING' ? 'En attente' :
                       interest.status === 'ACCEPTED' ? 'Accepté ✓' : 'Rejeté ✗'}
                    </span>
                  </div>

                  {interest.postBlocked && (
                    <div className="bg-red-50 border border-red-200 text-red-700 text-sm p-3 rounded-lg mb-4 font-medium">
                      ⛔ L'annonceur a bloqué cette offre
                    </div>
                  )}

                  <p className="text-sm text-gray-600 mb-4">
                    <strong>Votre message :</strong> {interest.initialMessage}
                  </p>

                  {interest.messages && interest.messages.length > 0 && (
                    <div className="bg-gray-50 rounded-xl p-4 mb-4 max-h-48 overflow-y-auto">
                      <p className="text-xs font-semibold text-gray-600 mb-3">Messages ({interest.messages.length})</p>
                      <div className="space-y-2">
                        {interest.messages.map((msg: any) => (
                          <div key={msg.id} className="text-xs">
                            <p className="font-medium text-gray-700">{msg.senderName}</p>
                            <p className="text-gray-600 mt-1">{msg.content}</p>
                            <p className="text-gray-400 mt-0.5">{new Date(msg.createdAt).toLocaleDateString("fr-FR")}</p>
                          </div>
                        ))}
                      </div>
                    </div>
                  )}

                  <div className="flex gap-3">
                    <Link
                      to={`/colocation/${interest.postId}`}
                      className="flex-1 text-center bg-primary text-white px-4 py-2 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors"
                    >
                      Voir l'annonce
                    </Link>
                    {interest.status === 'ACCEPTED' && (
                      <button
                        onClick={() => alert("Messagerie à ajouter dans la version complète")}
                        className="flex-1 bg-blue-100 text-blue-700 px-4 py-2 rounded-xl text-sm font-medium hover:bg-blue-200 transition-colors"
                      >
                        Discuter
                      </button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}