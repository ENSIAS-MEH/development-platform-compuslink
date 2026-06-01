import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";

export default function ColocationDetailPage() {
  const { id } = useParams();
  const [coloc, setColoc] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  // États pour gérer l'expression d'intérêt
  const [interestMessage, setInterestMessage] = useState("Bonjour, je suis très intéressé par votre colocation !");
  const [sendingInterest, setSendingInterest] = useState(false);
  const [interestSuccess, setInterestSuccess] = useState(false);

  useEffect(() => {
    const fetchDetails = async () => {
      try {
        const token = localStorage.getItem("accessToken");
        
        // CORRECTION DE L'EN-TÊTE : On construit les headers proprement
        const headers = {
          "Content-Type": "application/json"
        };

        // On n'injecte Bearer que si le token existe vraiment pour éviter de corrompre le JwtFilter
        if (token && token.trim() !== "") {
          headers["Authorization"] = `Bearer ${token}`;
        }

        const response = await fetch(`http://localhost:8080/api/colocations/${id}`, {
          method: "GET",
          headers: headers
        });

        if (!response.ok) {
          throw new Error(`Erreur serveur (${response.status}) : Impossible d'accéder aux spécifications.`);
        }
        
        const data = await response.json();
        setColoc(data);
      } catch (err) {
        console.error("Erreur attrapée dans fetchDetails:", err);
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    
    if (id) {
      fetchDetails();
    }
  }, [id]);

  // Fonction pour appeler l'endpoint POST /{id}/interests
  const handleExpressInterest = async () => {
    setSendingInterest(true);
    setError(null);
    try {
      const token = localStorage.getItem("accessToken");
      if (!token) throw new Error("Vous devez être connecté pour exprimer votre intérêt.");

      // CORRECTION : Ajout du 'const' manquant pour sécuriser la déclaration
      const response = await fetch(`http://localhost:8080/api/colocations/${id}/interests?message=${encodeURIComponent(interestMessage)}`, {
        method: "POST",
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });

      if (!response.ok) throw new Error("Échec de l'envoi de votre demande d'intérêt.");

      setInterestSuccess(true);
      setColoc(prev => ({ ...prev, pendingInterests: (prev.pendingInterests || 0) + 1 }));
    } catch (err) {
      setError(err.message);
    } finally {
      setSendingInterest(false);
    }
  };

  if (loading) return <div className="text-center py-24 text-gray-500">Chargement de la colocation...</div>;
  if (error && !coloc) return <div className="text-center py-24 text-red-500"> {error}</div>;
  if (!coloc) return null;

  const sortedImages = coloc.images ? [...coloc.images].sort((a, b) => a.sortOrder - b.sortOrder) : [];
  const mainImage = coloc.coverUrl || sortedImages[0]?.url || "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800";

  return (
    <div className="max-w-6xl mx-auto px-8 py-12">
      <Link to="/colocation" className="text-sm text-gray-500 hover:text-primary flex items-center gap-1 mb-6">
        <span className="material-symbols-outlined text-[16px]">arrow_back</span> Retour aux annonces
      </Link>

      {/* Grid d'images */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-3 rounded-2xl overflow-hidden">
        <div className="lg:col-span-2 h-80">
          <img src={mainImage} className="w-full h-full object-cover" alt="" />
        </div>
        <div className="hidden lg:grid grid-rows-2 gap-3">
          <img src={sortedImages[1]?.url || "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=400"} className="w-full h-full object-cover" alt="" />
          <img src={sortedImages[2]?.url || "https://images.unsplash.com/photo-1484154218962-a197022b5858?w=400"} className="w-full h-full object-cover" alt="" />
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 mt-8">
        {/* Colonne Principale */}
        <div className="lg:col-span-2 space-y-6">
          <div>
            <div className="flex items-center gap-3">
              <h1 className="text-3xl font-bold font-[Geist]">{coloc.title}</h1>
              <span className="text-xs bg-green-100 text-green-700 font-semibold px-3 py-1 rounded-full">{coloc.status}</span>
            </div>
            <p className="text-gray-500 mt-1 flex items-center gap-1">
              <span className="material-symbols-outlined text-[16px]">location_on</span>
              {coloc.address}, {coloc.city}
            </p>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div className="bg-white rounded-xl border border-gray-100 p-4 text-center">
              <p className="text-xs text-gray-500">Loyer mensuel</p>
              <p className="text-lg font-bold text-primary mt-1">{coloc.rentPerPerson} DH</p>
            </div>
            <div className="bg-white rounded-xl border border-gray-100 p-4 text-center">
              <p className="text-xs text-gray-500">Places dispo</p>
              <p className="text-lg font-bold mt-1">{coloc.spotsNeeded - (coloc.spotsConfirmed || 0)}</p>
            </div>
            <div className="bg-white rounded-xl border border-gray-100 p-4 text-center">
              <p className="text-xs text-gray-500">Logement</p>
              <p className="text-md font-bold mt-1.5">{coloc.housingType}</p>
            </div>
            <div className="bg-white rounded-xl border border-gray-100 p-4 text-center">
              <p className="text-xs text-gray-500">Meublé</p>
              <p className="text-lg font-bold mt-1">{coloc.furnished ? "Oui" : "Non"}</p>
            </div>
          </div>

          <div className="bg-white rounded-2xl border border-gray-100 p-6">
            <h3 className="font-semibold mb-3">Description</h3>
            <p className="text-sm text-gray-600 leading-relaxed">{coloc.description || "Aucune description fournie."}</p>
          </div>

          <div className="bg-white rounded-2xl border border-gray-100 p-6">
            <h3 className="font-semibold mb-3">Équipements</h3>
            <div className="flex flex-wrap gap-2">
              {coloc.amenities && coloc.amenities.length > 0 ? (
                coloc.amenities.map((amenity) => (
                  <span key={amenity.id} className="text-sm bg-gray-50 border border-gray-100 px-3 py-1.5 rounded-lg font-medium text-gray-700">
                    {amenity.amenityType}
                  </span>
                ))
              ) : (
                <p className="text-sm text-gray-400 italic">Aucun équipement renseigné.</p>
              )}
            </div>
          </div>
        </div>

        {/* Sidebar d'action */}
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
                <label className="text-xs font-medium text-gray-500">Ajouter un message d'accompagnement :</label>
                <textarea
                  value={interestMessage}
                  onChange={(e) => setInterestMessage(e.target.value)}
                  className="w-full text-xs p-3 border border-gray-200 rounded-xl resize-none h-20 outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
                  placeholder="Présentez-vous brièvement..."
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

            <div className="mt-4 text-center text-xs text-gray-400 flex items-center justify-center gap-1">
              <span className="material-symbols-outlined text-[14px]">schedule</span>
              {coloc.pendingInterests || 0} demande(s) en attente
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}