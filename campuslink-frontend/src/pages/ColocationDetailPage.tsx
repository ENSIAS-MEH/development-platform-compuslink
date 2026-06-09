import { useEffect, useState } from "react";
import api, { API_ORIGIN } from "../services/api";
import { Link, useParams, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function ColocationDetailPage() {
  const { user } = useAuth();
  const { id } = useParams();
  const navigate = useNavigate();
  const [coloc, setColoc] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [userToken, setUserToken] = useState<string | null>(null);
  const [imageIndex, setImageIndex] = useState(0);

  const [sendingInterest, setSendingInterest] = useState(false);
  const [interestSuccess, setInterestSuccess] = useState(false);
  const [interests, setInterests] = useState<any[]>([]);

  useEffect(() => {
    const fetchDetails = async () => {
      try {
        const token = localStorage.getItem("accessToken");
        setUserToken(token);

        const headers: HeadersInit = { "Content-Type": "application/json" };

        if (token && token.trim() !== "") {
          headers["Authorization"] = `Bearer ${token}`;
        }

        const response = await fetch(`${API_ORIGIN}/api/coloc/${id}`, {
          method: "GET",
          headers: headers
        });

        if (!response.ok) {
          throw new Error(`Erreur serveur (${response.status}) : Impossible d'accéder à l'annonce.`);
        }

        const data = await response.json();
        setColoc(data);
      } catch (err: any) {
        console.error(err);
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    if (id) fetchDetails();
  }, [id]);

  useEffect(() => {
    const fetchInterests = async () => {
      if (!coloc || !userToken || coloc.posterId !== user?.userId) return;
      try {
        const response = await fetch(`${API_ORIGIN}/api/coloc/${id}/interests`, {
          headers: { "Authorization": `Bearer ${userToken}` }
        });
        if (response.ok) {
          const data = await response.json();
          setInterests(data);
        }
      } catch (err) {
        console.error("Failed to load interests", err);
      }
    };
    fetchInterests();
  }, [coloc, userToken, user?.userId, id]);

  const handleExpressInterest = async () => {
    setSendingInterest(true);
    setError(null);
    try {
      const token = localStorage.getItem("accessToken");
      if (!token) throw new Error("Vous devez être connecté pour exprimer votre intérêt.");

      // Mirror the marketplace "Contacter le vendeur" flow: the conversation in the
      // central messaging carries the message, so the interest just uses a standard
      // greeting instead of a free-text accompanying message.
      const greeting = `Bonjour, je m'intéresse à ${coloc.title}`;
      const response = await fetch(`${API_ORIGIN}/api/coloc/${id}/interests?message=${encodeURIComponent(greeting)}`, {
        method: "POST",
        headers: { "Authorization": `Bearer ${token}` }
      });

      if (!response.ok) throw new Error("Échec de l'envoi de votre demande d'intérêt.");

      setInterestSuccess(true);
      setColoc((prev: any) => ({ ...prev, pendingInterests: (prev.pendingInterests || 0) + 1 }));

      // Send the greeting into the central messaging directly. We can't rely on
      // MessagingPage's auto-send (it only fires for brand-new conversations), so a
      // returning applicant who already has a thread with the poster would otherwise
      // get no message. Then open that conversation.
      if (coloc?.posterId) {
        try {
          await api.post(`/messages/${coloc.posterId}`, { content: greeting });
        } catch (e) {
          console.error("Failed to send interest message", e);
        }
        navigate("/messages", { state: { sellerId: coloc.posterId } });
      }
    } catch (err: any) {
      setError(err.message);
    } finally {
      setSendingInterest(false);
    }
  };

  const handleInterestStatus = async (interestId: string, newStatus: 'ACCEPTED' | 'REJECTED') => {
    try {
      const token = localStorage.getItem("accessToken");
      if (!token) return;

      const response = await fetch(`${API_ORIGIN}/api/coloc/interests/${interestId}/status?status=${newStatus}`, {
        method: "PATCH",
        headers: { "Authorization": `Bearer ${token}` }
      });

      if (!response.ok) throw new Error("Erreur lors du traitement de l'intérêt.");

      setInterests(prev => prev.map(i => i.id === interestId ? { ...i, status: newStatus } : i));
    } catch (err: any) {
      console.error("Error updating interest:", err);
    }
  };

  const handleBlockPost = async () => {
    try {
      const token = localStorage.getItem("accessToken");
      if (!token) return;

      const endpoint = coloc.isBlocked ? 'unblock' : 'block';
      const response = await fetch(`${API_ORIGIN}/api/coloc/${id}/${endpoint}`, {
        method: "PATCH",
        headers: { "Authorization": `Bearer ${token}` }
      });

      if (!response.ok) throw new Error("Erreur lors du blocage de l'offre.");

      setColoc((prev: any) => ({ ...prev, isBlocked: !prev.isBlocked }));
    } catch (err: any) {
      console.error("Error blocking post:", err);
      alert("Erreur lors du blocage de l'offre");
    }
  };

  if (loading) return <div className="text-center py-24 text-gray-500">Chargement de la colocation...</div>;
  if (error && !coloc) return <div className="text-center py-24 text-red-500"> {error}</div>;
  if (!coloc) return null;

  const sortedImages = coloc.images ? [...coloc.images].sort((a: any, b: any) => a.sortOrder - b.sortOrder) : [];
  const mainImage = sortedImages.length > 0 ? sortedImages[imageIndex]?.url : (coloc.coverUrl || null);
  const remainingSpots = coloc.spotsNeeded - (coloc.spotsConfirmed || 0);

  const prevImage = () => setImageIndex(prev => prev === 0 ? sortedImages.length - 1 : prev - 1);
  const nextImage = () => setImageIndex(prev => prev === sortedImages.length - 1 ? 0 : prev + 1);

  return (
    <div className="max-w-6xl mx-auto px-8 py-12">
      <Link to="/colocation" className="text-sm text-gray-500 hover:text-primary flex items-center gap-1 mb-6">
        <span className="material-symbols-outlined text-[16px]">arrow_back</span> Retour aux annonces
      </Link>

      {mainImage && (
        <div className="relative rounded-2xl overflow-hidden mb-8 bg-gray-100">
          <div className="h-96 flex items-center justify-center relative">
            <img src={mainImage} className="w-full h-full object-cover" alt={`Image ${imageIndex + 1}`} />

            {sortedImages.length > 1 && (
              <>
                <button
                  onClick={prevImage}
                  className="absolute left-4 top-1/2 -translate-y-1/2 bg-white/80 hover:bg-white p-2 rounded-full transition-colors"
                  aria-label="Image précédente"
                >
                  <span className="material-symbols-outlined">chevron_left</span>
                </button>
                <button
                  onClick={nextImage}
                  className="absolute right-4 top-1/2 -translate-y-1/2 bg-white/80 hover:bg-white p-2 rounded-full transition-colors"
                  aria-label="Image suivante"
                >
                  <span className="material-symbols-outlined">chevron_right</span>
                </button>
                <div className="absolute bottom-4 left-1/2 -translate-x-1/2 bg-black/50 px-3 py-1 rounded-full text-white text-xs">
                  {imageIndex + 1} / {sortedImages.length}
                </div>
              </>
            )}
          </div>

          {sortedImages.length > 1 && (
            <div className="grid grid-cols-5 md:grid-cols-8 gap-2 p-4 bg-gray-50 overflow-x-auto">
              {sortedImages.map((img, idx) => (
                <button
                  key={idx}
                  onClick={() => setImageIndex(idx)}
                  className={`h-16 rounded-lg overflow-hidden flex-shrink-0 border-2 transition-colors ${
                    imageIndex === idx ? "border-primary" : "border-gray-300"
                  }`}
                >
                  <img src={img.url} className="w-full h-full object-cover" alt={`Thumbnail ${idx + 1}`} />
                </button>
              ))}
            </div>
          )}
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 mt-4">
        {/* COLONNE DE GAUCHE : Détails + Description + Carte */}
        <div className="lg:col-span-2 space-y-6">
          <div>
            <div className="flex items-center gap-3 flex-wrap">
              <h1 className="text-3xl font-bold font-[Geist]">{coloc.title}</h1>
              {coloc.isBlocked && (
                <span className="text-xs bg-red-100 text-red-700 font-semibold px-3 py-1 rounded-full">⛔ Bloquée</span>
              )}
              {!coloc.isBlocked && (
                <span className="text-xs bg-green-100 text-green-700 font-semibold px-3 py-1 rounded-full">{coloc.status}</span>
              )}
            </div>

            <p className="text-sm font-medium text-gray-600 mt-2 flex items-center gap-1.5">
              <span className="material-symbols-outlined text-[18px] text-primary">account_circle</span>
              Publié par : <span className="text-primary font-semibold">{coloc.posterName || "Étudiant"}</span>
            </p>

            <p className="text-gray-500 mt-1 flex items-center gap-1 text-sm">
              <span className="material-symbols-outlined text-[16px]">location_on</span>
              {coloc.address}, {coloc.city}
            </p>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div className="bg-white rounded-xl border border-gray-100 p-4 text-center">
              <p className="text-xs text-gray-500 font-medium">Loyer mensuel</p>
              <p className="text-lg font-bold text-primary mt-1">{coloc.rentPerPerson} DH</p>
            </div>
            <div className="bg-white rounded-xl border border-gray-100 p-4 text-center">
              <p className="text-xs text-gray-500 font-medium">Places restantes</p>
              <p className="text-lg font-bold mt-1 text-green-600">{remainingSpots} / {coloc.spotsNeeded}</p>
            </div>
            <div className="bg-white rounded-xl border border-gray-100 p-4 text-center">
              <p className="text-xs text-gray-500 font-medium">Type de local</p>
              <p className="text-sm font-bold mt-2 truncate text-gray-700">{coloc.housingType}</p>
            </div>
            <div className="bg-white rounded-xl border border-gray-100 p-4 text-center">
              <p className="text-xs text-gray-500 font-medium">État meublé</p>
              <p className="text-lg font-bold mt-1 text-gray-700">{coloc.furnished ? "Oui" : "Non"}</p>
            </div>
          </div>

          <div className="bg-white rounded-2xl border border-gray-100 p-6">
            <h3 className="font-semibold mb-3">Description</h3>
            <p className="text-sm text-gray-600 leading-relaxed">{coloc.description || "Aucune description fournie."}</p>
          </div>

          {/* Carte Google Maps INTÉGRÉE DANS LA COLONNE DE GAUCHE AVEC MARQUEUR */}
          <div className="bg-white rounded-2xl border border-gray-100 p-6">
            <h3 className="font-semibold mb-4 flex items-center gap-2">
              <span className="material-symbols-outlined text-primary text-[20px]">map</span>
              Localisation
            </h3>
            <div className="w-full h-72 rounded-xl overflow-hidden bg-gray-100">
              <iframe
                width="100%"
                height="100%"
                style={{ border: 0 }}
                loading="lazy"
                allowFullScreen
                referrerPolicy="no-referrer-when-downgrade"
                src={`https://maps.google.com/maps?q=${encodeURIComponent(coloc.address + ', ' + coloc.city)}&z=16&output=embed&hl=fr`}
              ></iframe>
            </div>
          </div>
        </div>

        {/* COLONNE DE DROITE : Sidebar */}
        <div className="space-y-6">
          <div className="bg-white rounded-2xl border border-gray-100 p-6">
            <p className="text-sm text-gray-500 mb-1">Annonce publiée le :</p>
            <p className="text-sm font-medium text-gray-700 mb-4">
              {coloc.createdAt ? new Date(coloc.createdAt).toLocaleDateString("fr-FR") : "Récemment"}
            </p>

            {error && <div className="text-xs text-red-500 mb-2 font-medium"> {error}</div>}

            {coloc.posterId === user?.userId ? (
              <div className="space-y-3">
                <div className="bg-blue-50 border border-blue-100 p-4 rounded-xl">
                  <p className="text-sm font-medium text-blue-700 mb-2">Intérêts reçus</p>
                  <p className="text-2xl font-bold text-blue-900">{interests.length}</p>
                </div>
                <button
                  onClick={() => handleBlockPost()}
                  className={`w-full py-2 rounded-xl text-sm font-medium transition-colors ${
                    coloc.isBlocked
                      ? "bg-green-100 text-green-700 hover:bg-green-200"
                      : "bg-red-100 text-red-700 hover:bg-red-200"
                  }`}
                >
                  {coloc.isBlocked ? "✓ Débloquer l'offre" : "⛔ Bloquer l'offre"}
                </button>
              </div>
            ) : coloc.isBlocked ? (
              <div className="bg-red-50 border border-red-100 text-red-700 text-sm p-4 rounded-xl text-center font-medium">
                ⛔ Cette offre a été bloquée par l'annonceur
              </div>
            ) : interestSuccess ? (
              <div className="bg-green-50 border border-green-100 text-green-700 text-sm p-4 rounded-xl text-center font-medium">
                Intérêt envoyé avec succès !
              </div>
            ) : (
              <button
                onClick={handleExpressInterest}
                disabled={sendingInterest}
                className="w-full bg-primary text-white py-3 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors flex items-center justify-center gap-2 disabled:bg-gray-300"
              >
                {sendingInterest ? "Envoi..." : "Express Interest"}
                <span className="material-symbols-outlined text-[16px]">chat_bubble_outline</span>
              </button>
            )}
          </div>

          {coloc.posterId === user?.userId && (
            <div className="bg-white rounded-2xl border border-gray-100 p-6">
              <div className="flex items-center justify-between mb-4">
                <h3 className="font-semibold flex items-center gap-2">
                  <span className="material-symbols-outlined text-primary">people</span>
                  Personnes intéressées ({interests.length})
                </h3>
                <button
                  onClick={() => {
                    const fetchInterests = async () => {
                      try {
                        const response = await fetch(`${API_ORIGIN}/api/coloc/${id}/interests`, {
                          headers: { "Authorization": `Bearer ${userToken}` }
                        });
                        if (response.ok) {
                          const data = await response.json();
                          setInterests(data);
                        }
                      } catch (err) {
                        console.error("Failed to refresh interests", err);
                      }
                    };
                    fetchInterests();
                  }}
                  className="p-1 hover:bg-gray-100 rounded transition-colors"
                  title="Actualiser"
                >
                  <span className="material-symbols-outlined text-sm">refresh</span>
                </button>
              </div>
              {interests.length === 0 ? (
                <p className="text-sm text-gray-500 py-4 text-center">
                  Aucune personne intéressée pour le moment
                </p>
              ) : (
              <div className={`space-y-3 max-h-96 overflow-y-auto ${interests.length > 5 ? 'pr-2' : ''}`}>
                {interests.map(interest => (
                  <div key={interest.id} className="border border-gray-100 rounded-lg p-3 hover:bg-gray-50 transition-colors">
                    <div className="flex items-start justify-between">
                      <button
                        onClick={() => navigate("/messages", { state: { sellerId: interest.userId } })}
                        className="flex-1 text-left font-medium text-sm text-primary hover:underline"
                        title="Ouvrir la conversation"
                      >
                        {interest.userName}
                      </button>
                      <span className={`ml-2 text-xs font-semibold px-2 py-1 rounded flex-shrink-0 ${
                        interest.status === 'PENDING' ? 'bg-yellow-100 text-yellow-700' :
                        interest.status === 'ACCEPTED' ? 'bg-green-100 text-green-700' :
                        'bg-red-100 text-red-700'
                      }`}>
                        {interest.status === 'PENDING' ? 'En attente' :
                         interest.status === 'ACCEPTED' ? 'Accepté' : 'Rejeté'}
                      </span>
                    </div>
                    {interest.status === 'PENDING' && (
                      <div className="flex gap-2 mt-3">
                        <button
                          onClick={() => handleInterestStatus(interest.id, 'ACCEPTED')}
                          className="flex-1 text-xs bg-green-100 text-green-700 hover:bg-green-200 px-3 py-1.5 rounded font-medium transition-colors"
                        >
                          Accepter
                        </button>
                        <button
                          onClick={() => handleInterestStatus(interest.id, 'REJECTED')}
                          className="flex-1 text-xs bg-red-100 text-red-700 hover:bg-red-200 px-3 py-1.5 rounded font-medium transition-colors"
                        >
                          Rejeter
                        </button>
                      </div>
                    )}
                    <p className="text-xs text-gray-400 mt-2">
                      {new Date(interest.createdAt).toLocaleDateString("fr-FR")}
                    </p>
                  </div>
                ))}
              </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}