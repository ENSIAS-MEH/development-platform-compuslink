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

  // Gestion de plusieurs fichiers d'images
  const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
  const [filePreviews, setFilePreviews] = useState<string[]>([]);

  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // --- LOGIQUE GOOGLE MAPS ---
  const searchQuery = `${formData.address}, ${formData.city}`.trim();
  const showMap = formData.address.length > 3 || formData.city.length > 3;
  // ---------------------------

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      const filesArray = Array.from(e.target.files);

      const validFiles = filesArray.filter(file => file.size <= 10 * 1024 * 1024);
      if (validFiles.length !== filesArray.length) {
        setError("Certaines images dépassent la limite de 10 Mo.");
      }

      setSelectedFiles(prev => [...prev, ...validFiles]);
      const newPreviews = validFiles.map(file => URL.createObjectURL(file));
      setFilePreviews(prev => [...prev, ...newPreviews]);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);

    try {
      const token = localStorage.getItem("accessToken");
      if (!token) throw new Error("Tu dois être connecté pour publier une annonce.");

      const response = await fetch("http://localhost:8080/api/colocations", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({
          ...formData,
          rentPerPerson: Number(formData.rentPerPerson),   
          spotsNeeded: Number(formData.spotsNeeded),      
          spotsConfirmed: 0,
          status: "OPEN"                                  
        }),
      });

      if (!response.ok) throw new Error("Échec de la création de l'annonce.");

      const createdPost = await response.json();
      const postId = createdPost.id;

      if (selectedFiles.length > 0 && postId) {
        const fileFormData = new FormData();
        selectedFiles.forEach(file => {
          fileFormData.append("files", file); 
        });

        const photoResponse = await fetch(`http://localhost:8080/api/colocations/${postId}/photos`, {
          method: "POST",
          headers: { "Authorization": `Bearer ${token}` },
          body: fileFormData
        });

        if (!photoResponse.ok) console.warn("Téléversement des photos échoué.");
      }

      navigate("/colocation");
    } catch (err: any) {
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

      {error && <div className="mt-4 p-4 bg-red-50 text-red-600 rounded-xl text-sm font-medium">Erreur : {error}</div>}

      <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
        <div className="bg-white rounded-2xl border border-gray-100 p-6 space-y-5">
          <h3 className="font-semibold text-gray-800">Informations générales</h3>
          <div>
            <label className="text-sm font-medium text-gray-700">Titre de l'annonce *</label>
            <input required type="text" value={formData.title} onChange={(e) => setFormData({ ...formData, title: e.target.value })} className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-blue-600/20 focus:border-blue-600" />
          </div>
          <div>
            <label className="text-sm font-medium text-gray-700">Description</label>
            <textarea value={formData.description} onChange={(e) => setFormData({ ...formData, description: e.target.value })} className="mt-1.5 w-full h-28 px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-blue-600/20 focus:border-blue-600" />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="text-sm font-medium text-gray-700">Ville *</label>
              <select required value={formData.city} onChange={(e) => setFormData({ ...formData, city: e.target.value })} className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-blue-600/20 focus:border-blue-600">
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
              <input required type="text" value={formData.address} onChange={(e) => setFormData({ ...formData, address: e.target.value })} className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-blue-600/20 focus:border-blue-600" />
            </div>
          </div>
        </div>

        {/* Aperçu dynamique de la carte AVEC MARQUEUR ROUGE */}
        {showMap && (
          <div className="bg-white rounded-2xl border border-gray-100 p-6 transition-all duration-300 space-y-3">
            <h3 className="font-semibold text-gray-800 flex items-center gap-2">
              <span className="material-symbols-outlined text-blue-600 text-[20px]">map</span>
              Aperçu de la localisation
            </h3>
            <p className="text-sm text-gray-500">
              Vérifiez que le repère rouge correspond bien à l'adresse saisie ci-dessus.
            </p>
            <div className="w-full h-72 rounded-xl overflow-hidden bg-gray-100 border border-gray-200">
              <iframe
                width="100%"
                height="100%"
                style={{ border: 0 }}
                loading="lazy"
                allowFullScreen
                referrerPolicy="no-referrer-when-downgrade"
                src={`https://maps.google.com/maps?q=${encodeURIComponent(searchQuery)}&z=16&output=embed&hl=fr`}
              ></iframe>
            </div>
          </div>
        )}

        <div className="bg-white rounded-2xl border border-gray-100 p-6 space-y-5">
          <h3 className="font-semibold text-gray-800">Détails du logement</h3>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="text-sm font-medium text-gray-700">Type de logement *</label>
              <select value={formData.housingType} onChange={(e) => setFormData({ ...formData, housingType: e.target.value })} className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-blue-600/20 focus:border-blue-600">
                <option value="APARTMENT">Appartement</option>
                <option value="HOUSE">Maison</option>
                <option value="STUDIO">Studio</option>
                <option value="ROOM">Chambre simple</option>
              </select>
            </div>
            <div>
              <label className="text-sm font-medium text-gray-700">Loyer par personne (DH) *</label>
              <input required type="number" value={formData.rentPerPerson} onChange={(e) => setFormData({ ...formData, rentPerPerson: e.target.value })} className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-blue-600/20 focus:border-blue-600" />
            </div>
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="text-sm font-medium text-gray-700">Places recherchées *</label>
              <input required type="number" min="1" value={formData.spotsNeeded} onChange={(e) => setFormData({ ...formData, spotsNeeded: e.target.value })} className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-blue-600/20 focus:border-blue-600" />
            </div>
            <div>
              <label className="text-sm font-medium text-gray-700">Date de début *</label>
              <input required type="date" value={formData.startDate} onChange={(e) => setFormData({ ...formData, startDate: e.target.value })} className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-blue-600/20 focus:border-blue-600" />
            </div>
          </div>
          <label className="flex items-center gap-3 cursor-pointer pt-2">
            <input type="checkbox" checked={formData.furnished} onChange={(e) => setFormData({ ...formData, furnished: e.target.checked })} className="w-4 h-4 rounded border-gray-300 text-blue-600" />
            <span className="text-sm font-medium text-gray-700">Ce logement est meublé</span>
          </label>
        </div>

        <div className="bg-white rounded-2xl border border-gray-100 p-6 space-y-5">
          <h3 className="font-semibold text-gray-800">Galerie de photos (Sélection multiple)</h3>
          <label className="block border-2 border-dashed border-gray-200 rounded-xl p-6 text-center hover:border-blue-600/50 transition-colors cursor-pointer relative">
            <input type="file" multiple accept="image/png, image/jpeg" onChange={handleFileChange} className="hidden" />
            <span className="material-symbols-outlined text-gray-400 text-3xl">collections</span>
            <p className="text-sm font-medium mt-2 text-gray-700">Cliquez pour ajouter une ou plusieurs photos</p>
            <p className="text-xs text-gray-400 mt-1">PNG, JPG • Max 10 Mo par photo</p>
          </label>

          {filePreviews.length > 0 && (
            <div className="grid grid-cols-3 gap-3 mt-4">
              {filePreviews.map((preview, index) => (
                <div key={index} className="h-24 rounded-xl overflow-hidden relative border border-gray-100">
                  <img src={preview} alt="Aperçu" className="w-full h-full object-cover" />
                  {index === 0 && <span className="absolute bottom-1 left-1 text-[9px] bg-blue-600 text-white px-1.5 py-0.5 rounded-md font-semibold">Couverture</span>}
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="flex gap-4">
          <Link to="/colocation" className="flex-1 border border-gray-200 py-3 rounded-xl text-sm text-center font-medium hover:bg-gray-50 transition-colors">Annuler</Link>
          <button type="submit" disabled={submitting} className="flex-1 bg-blue-600 text-white py-3 rounded-xl text-sm font-medium hover:bg-blue-700 transition-colors disabled:bg-gray-300 disabled:cursor-not-allowed">
            {submitting ? "Publication en cours..." : "Publier l'annonce"}
          </button>
        </div>
      </form>
    </div>
  );
}