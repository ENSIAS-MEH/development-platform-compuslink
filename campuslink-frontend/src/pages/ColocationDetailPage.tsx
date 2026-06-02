import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";

export default function ColocationDetailPage() {
  const { id } = useParams();
  const [coloc, setColoc] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [interestMessage, setInterestMessage] = useState("Bonjour, je suis très intéressé par votre colocation !");
  const [sendingInterest, setSendingInterest] = useState(false);
  const [interestSuccess, setInterestSuccess] = useState(false);

  useEffect(() => {
    const fetchDetails = async () => {
      try {
        const token = localStorage.getItem("accessToken");
        const headers: HeadersInit = { "Content-Type": "application/json" };

        if (token && token.trim() !== "") {
          headers["Authorization"] = `Bearer ${token}`;
        }

        const response = await fetch(`http://localhost:8080/api/colocations/${id}`, {
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

  const handleExpressInterest = async () => {
    setSendingInterest(true);
    setError(null);
    try {
      const token = localStorage.getItem("accessToken");
      if (!token) throw new Error("Vous devez être connecté pour exprimer votre intérêt.");

      const response = await fetch(`http://localhost:8080/api/colocations/${id}/interests?message=${encodeURIComponent(interestMessage)}`, {
        method: "POST",
        headers: { "Authorization": `Bearer ${token}` }
      });

      if (!response.ok) throw new Error("Échec de l'envoi de votre demande d'intérêt.");

      setInterestSuccess(true);
      setColoc((prev: any) => ({ ...prev, pendingInterests: (prev.pendingInterests || 0) + 1 }));
    } catch (err: any) {
      setError(err.message);
    } finally {
      setSendingInterest(false);
    }
  };

  if (loading) return <div className="text-center py-24 text-gray-500">Chargement de la colocation...</div>;
  if (error && !coloc) return <div className="text-center py-24 text-red-500"> {error}</div>;
  if (!coloc) return null;

  const sortedImages = coloc.images ? [...coloc.images].sort((a: any, b: any) => a.sortOrder - b.sortOrder) : [];
  const mainImage = coloc.coverUrl || sortedImages[0]?.url || null;
  const remainingSpots = coloc.spotsNeeded - (coloc.spotsConfirmed || 0);

  return (
    <div className="max-w-6xl mx-auto px-8 py-12">
      <Link to="/colocation" className="text-sm text-gray-500 hover:text-primary flex items-center gap-1 mb-6">
        <span className="material-symbols-outlined text-[16px]">arrow_back</span> Retour aux annonces
      </Link>

      {mainImage && (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-3 rounded-2xl overflow-hidden mb-8">
          <div className={`${sortedImages.length > 1 ? "lg:col-span-2" : "col-span-full"} h-80`}>
            <img src={mainImage} className="w-full h-full object-cover" alt="Vue principale" />
          </div>

          {sortedImages.length > 1 && (
            <div className="hidden lg:grid grid-rows-2 gap-3">
              {sortedImages[1] && (
                <img
                  src={sortedImages[1].url}
                  className="w-full h-full object-cover"
                  alt="Vue secondaire 1"
                />
              )}
              {sortedImages[2] && (
                <img
                  src={sortedImages[2].url}
                  className="w-full h-full object-cover"
                  alt="Vue secondaire 2"
                />
              )}
            </div>
          )}
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 mt-4">
        {/* COLONNE DE GAUCHE : Détails + Description + Carte */}
        <div className="lg:col-span-2 space-y-6">
          <div>
            <div className="flex items-center gap-3">
              <h1 className="text-3xl font-bold font-[Geist]">{coloc.title}</h1>
              <span className="text-xs bg-green-100 text-green-700 font-semibold px-3 py-1 rounded-full">{coloc.status}</span>
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
            
            {interestSuccess ? (
              <div className="bg-green-50 border border-green-100 text-green-700 text-sm p-4 rounded-xl text-center font-medium">
                Intérêt envoyé avec succès !
              </div>
            ) : (
              <div className="space-y-3">
                  <label className="text-xs font-medium text-gray-500">Message d'accompagnement :</label>
                <textarea
                  value={interestMessage}
                  onChange={(e) => setInterestMessage(e.target.value)}
                    className="w-full text-xs p-3 border border-gray-200 rounded-xl resize-none h-20 outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
                  />
                <button 
                  onClick={handleExpressInterest}
                  disabled={sendingInterest}
                  className="w-full bg-primary text-white py-3 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors flex items-center justify-center gap-2 disabled:bg-gray-300"
                >
                  {sendingInterest ? "Envoi..." : "Express Interest"} 
                  <span className="material-symbols-outlined text-[16px]">arrow_forward</span>
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}