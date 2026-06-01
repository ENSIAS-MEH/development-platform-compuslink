import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

export default function CreateColocationPage() {
  const navigate = useNavigate();
  
  const [formData, setFormData] = useState({
    title: "",
    description: "",
    city: "",
    address: "",
    housingType: "APARTMENT", 
    rentPerPerson: "",
    spotsNeeded: "",
    furnished: false,
    startDate: ""
  });

  // États pour la gestion du fichier image
  const [selectedFile, setSelectedFile] = useState(null);
  const [filePreview, setFilePreview] = useState(null);

  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  // Gestion du choix de fichier
  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      if (file.size > 10 * 1024 * 1024) {
        setError("L'image ne doit pas dépasser 10 Mo.");
        return;
      }
      setSelectedFile(file);
      setFilePreview(URL.createObjectURL(file)); // Crée un aperçu visuel temporaire
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);

    try {
      const token = localStorage.getItem("accessToken");
      if (!token) throw new Error("Tu dois être connecté avec ton compte pour publier une annonce.");

      // ÉTAPE 1 : Création textuelle de la colocation
      const response = await fetch("http://localhost:8080/api/colocations", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({
          title: formData.title,
          description: formData.description,
          city: formData.city,
          address: formData.address,
          housingType: formData.housingType,              
          rentPerPerson: Number(formData.rentPerPerson),   
          spotsNeeded: Number(formData.spotsNeeded),       
          spotsConfirmed: 0,
          furnished: formData.furnished,
          startDate: formData.startDate,                   
          status: "OPEN"                                   
        }),
      });

      if (!response.ok) throw new Error("Échec de la création de l'annonce sur le serveur backend.");

      const createdPost = await response.json(); // Ton API retourne le ColocPostDTO avec son nouvel id (UUID)
      const postId = createdPost.id;

      // ÉTAPE 2 : Si une photo est présente, on l'envoie sur l'endpoint multipart dédié
      if (selectedFile && postId) {
        const fileFormData = new FormData();
        fileFormData.append("file", selectedFile);
        fileFormData.append("isCover", "true"); // On définit cette photo comme photo de couverture

        const photoResponse = await fetch(`http://localhost:8080/api/colocations/${postId}/photos`, {
          method: "POST",
          headers: {
            "Authorization": `Bearer ${token}`
            // Note : Ne pas ajouter "Content-Type" ici, le navigateur s'en charge automatiquement pour le multipart/form-data
          },
          body: fileFormData
        });

        if (!photoResponse.ok) {
          console.warn("L'annonce a été créée mais le téléversement de la photo a échoué.");
        }
      }

      navigate("/colocation");
    } catch (err) {
      console.error(err);
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="max-w-3xl mx-auto px-8 py-12">
      <Link to="/colocation" className="text-sm text-gray-500 hover:text-primary flex items-center gap-1 mb-6">
        <span className="material-symbols-outlined text-[16px]">arrow_back</span> Retour
      </Link>

      <h1 className="text-3xl font-bold font-[Geist]">Publier une colocation</h1>
      <p className="text-gray-500 mt-2">Trouve ton colocataire idéal en quelques clics.</p>

      {error && (
        <div className="mt-4 p-4 bg-red-50 border border-red-100 text-red-600 rounded-xl text-sm font-medium">
          ⚠️ Erreur : {error}
        </div>
      )}

      <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
        {/* Section 1 : Informations Générales */}
        <div className="bg-white rounded-2xl border border-gray-100 p-6 space-y-5">
          <h3 className="font-semibold text-gray-800">Informations générales</h3>

          <div>
            <label className="text-sm font-medium text-gray-700">Titre de l'annonce *</label>
            <input 
              required 
              type="text" 
              value={formData.title} 
              onChange={(e) => setFormData({...formData, title: e.target.value})}
              placeholder="Ex: Chambre lumineuse à Agdal" 
              className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" 
            />
          </div>

          <div>
            <label className="text-sm font-medium text-gray-700">Description</label>
            <textarea 
              value={formData.description} 
              onChange={(e) => setFormData({...formData, description: e.target.value})}
              placeholder="Décrivez le logement, l'ambiance, les règles de vie de la colocation..." 
              className="mt-1.5 w-full h-28 px-4 py-3 border border-gray-200 rounded-xl text-sm resize-none outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" 
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="text-sm font-medium text-gray-700">Ville *</label>
              <select 
                required 
                value={formData.city} 
                onChange={(e) => setFormData({...formData, city: e.target.value})}
                className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
              >
                <option value="">Sélectionner</option>
                <option value="Casablanca">Casablanca</option>
                <option value="Rabat">Rabat</option>
                <option value="Marrakech">Marrakech</option>
                <option value="Fès">Fès</option>
                <option value="Tanger">Tanger</option>
              </select>
            </div>
            <div>
              <label className="text-sm font-medium text-gray-700">Adresse *</label>
              <input 
                required 
                type="text" 
                value={formData.address} 
                onChange={(e) => setFormData({...formData, address: e.target.value})}
                placeholder="Rue, quartier..." 
                className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" 
              />
            </div>
          </div>
        </div>

        {/* Section 2 : Détails Logement */}
        <div className="bg-white rounded-2xl border border-gray-100 p-6 space-y-5">
          <h3 className="font-semibold text-gray-800">Détails du logement</h3>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="text-sm font-medium text-gray-700">Type de logement *</label>
              <select 
                value={formData.housingType} 
                onChange={(e) => setFormData({...formData, housingType: e.target.value})}
                className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
              >
                <option value="APARTMENT">Appartement</option>
                <option value="HOUSE">Maison</option>
                <option value="STUDIO">Studio</option>
                <option value="ROOM">Chambre simple</option>
              </select>
            </div>
            <div>
              <label className="text-sm font-medium text-gray-700">Loyer par personne (DH) *</label>
              <input 
                required 
                type="number" 
                value={formData.rentPerPerson} 
                onChange={(e) => setFormData({...formData, rentPerPerson: e.target.value})}
                placeholder="2500" 
                className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" 
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="text-sm font-medium text-gray-700">Places recherchées *</label>
              <input 
                required 
                type="number" 
                min="1" 
                value={formData.spotsNeeded} 
                onChange={(e) => setFormData({...formData, spotsNeeded: e.target.value})}
                placeholder="2" 
                className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" 
              />
            </div>
            <div>
              <label className="text-sm font-medium text-gray-700">Date de début *</label>
              <input 
                required
                type="date" 
                value={formData.startDate}
                onChange={(e) => setFormData({...formData, startDate: e.target.value})}
                className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" 
              />
            </div>
          </div>

          <label className="flex items-center gap-3 cursor-pointer pt-2">
            <input 
              type="checkbox" 
              checked={formData.furnished}
              onChange={(e) => setFormData({...formData, furnished: e.target.checked})}
              className="w-4 h-4 rounded border-gray-300 text-primary focus:ring-primary" 
            />
            <span className="text-sm font-medium text-gray-700">Ce logement est meublé</span>
          </label>
        </div>

        {/* Section 3 : Photos reliée à l'input masqué */}
        <div className="bg-white rounded-2xl border border-gray-100 p-6 space-y-5">
          <h3 className="font-semibold text-gray-800">Photo de couverture</h3>
          <label className="block border-2 border-dashed border-gray-200 rounded-xl p-6 text-center hover:border-primary/50 transition-colors cursor-pointer relative overflow-hidden">
            <input 
              type="file" 
              accept="image/png, image/jpeg, image/jpg" 
              onChange={handleFileChange} 
              className="hidden" 
            />
            
            {filePreview ? (
              <div className="w-full h-40 relative">
                <img src={filePreview} alt="Aperçu" className="w-full h-full object-cover rounded-lg" />
                <div className="absolute inset-0 bg-black/40 flex items-center justify-center opacity-0 hover:opacity-100 transition-opacity rounded-lg">
                  <p className="text-white text-xs font-medium">Changer de photo</p>
                </div>
              </div>
            ) : (
              <>
                <span className="material-symbols-outlined text-gray-400 text-3xl">cloud_upload</span>
                <p className="text-sm font-medium mt-2 text-gray-700">Cliquez pour ajouter la photo principale</p>
                <p className="text-xs text-gray-400 mt-1">JPG, PNG • Max 10 Mo</p>
              </>
            )}
          </label>
        </div>

        {/* Liens et validation */}
        <div className="flex gap-4">
          <Link to="/colocation" className="flex-1 border border-gray-200 py-3 rounded-xl text-sm font-medium text-center hover:bg-gray-50 transition-colors">
            Annuler
          </Link>
          <button 
            type="submit" 
            disabled={submitting}
            className="flex-1 bg-primary text-white py-3 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors disabled:bg-gray-300 disabled:cursor-not-allowed"
          >
            {submitting ? "Publication en cours..." : "Publier l'annonce"}
          </button>
        </div>
      </form>
    </div>
  );
}