import { useState } from "react";
import type { FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../services/api";

export default function CreateItemPage() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [images, setImages] = useState<File[]>([]);
  const [previewUrls, setPreviewUrls] = useState<string[]>([]);
  const [formData, setFormData] = useState({
    title: "",
    description: "",
    price: "",
    city: "",
    condition: "NEW",
    category: "Electronics",
  });

  const conditionMap: Record<string, string> = {
    "NEW": "Neuf",
    "LIKE_NEW": "Très bon état",
    "GOOD": "Bon état",
    "FAIR": "Correct"
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = Array.from(e.target.files || []).slice(0, 5);
    setImages(files);
    setPreviewUrls(files.map((f: File) => URL.createObjectURL(f)));
  };

  const handleRemoveImage = (index: number) => {
    setImages(images.filter((_, i) => i !== index));
    setPreviewUrls(previewUrls.filter((_, i) => i !== index));
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target as HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError("");

    if (!formData.title.trim() || !formData.price || !formData.city) {
      setError("Veuillez remplir tous les champs obligatoires");
      return;
    }

    if (images.length === 0) {
      setError("Veuillez ajouter au moins une photo");
      return;
    }

    setLoading(true);
    try {
      const data = new FormData();
      data.append("title", formData.title);
      data.append("description", formData.description);
      data.append("price", formData.price);
      data.append("city", formData.city);
      data.append("condition", formData.condition);
      data.append("category", formData.category);

      images.forEach(img => {
        data.append("images", img);
      });

      await api.post("/items", data, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      navigate("/marketplace");
    } catch (err: any) {
      setError(err.response?.data?.message || "Erreur lors de la création de l'article");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-3xl mx-auto px-8 py-12">
      <Link to="/marketplace" className="text-sm text-gray-500 hover:text-primary flex items-center gap-1 mb-6">
        <span className="material-symbols-outlined text-[16px]">arrow_back</span> Retour
      </Link>

      <h1 className="text-3xl font-bold font-[Geist]">Poster un article</h1>
      <p className="text-gray-500 mt-2">Vendez vos objets à la communauté étudiante.</p>

      {error && <div className="mt-4 p-4 bg-red-50 border border-red-200 rounded-xl text-red-700 text-sm">{error}</div>}

      <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
        <div className="bg-white rounded-2xl border border-gray-100 p-6 space-y-5">
          <h3 className="font-semibold">Photos</h3>
          <label className="border-2 border-dashed border-gray-200 rounded-xl p-8 text-center hover:border-primary/50 transition-colors cursor-pointer block">
            <input
              type="file"
              multiple
              accept="image/*"
              onChange={handleImageChange}
              className="hidden"
            />
            <span className="material-symbols-outlined text-gray-400 text-3xl">add_photo_alternate</span>
            <p className="text-sm font-medium mt-2">Ajoutez jusqu'à 5 photos</p>
            <p className="text-xs text-gray-400 mt-1">La première sera la photo de couverture</p>
          </label>
          {previewUrls.length > 0 && (
            <div className="grid grid-cols-5 gap-2 mt-4">
              {previewUrls.map((url, i) => (
                <div key={i} className="relative group">
                  <img src={url} alt="preview" className="w-full h-20 object-cover rounded-lg" />
                  <button
                    type="button"
                    onClick={() => handleRemoveImage(i)}
                    className="absolute top-0 right-0 bg-red-500 text-white rounded-full w-6 h-6 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity"
                  >
                    ×
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="bg-white rounded-2xl border border-gray-100 p-6 space-y-5">
          <h3 className="font-semibold">Informations</h3>

          <div>
            <label className="text-sm font-medium">Titre *</label>
            <input
              type="text"
              name="title"
              value={formData.title}
              onChange={handleInputChange}
              placeholder="Ex: MacBook Pro M1 2020"
              className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
            />
          </div>

          <div>
            <label className="text-sm font-medium">Description</label>
            <textarea
              name="description"
              value={formData.description}
              onChange={handleInputChange}
              placeholder="État, caractéristiques, raison de la vente..."
              className="mt-1.5 w-full h-28 px-4 py-3 border border-gray-200 rounded-xl text-sm resize-none outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="text-sm font-medium">Prix (MAD) *</label>
              <input
                type="number"
                name="price"
                value={formData.price}
                onChange={handleInputChange}
                placeholder="8500"
                className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
              />
            </div>
            <div>
              <label className="text-sm font-medium">Ville *</label>
              <select
                name="city"
                value={formData.city}
                onChange={handleInputChange}
                className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
              >
                <option value="">Sélectionner</option>
                <option>Casablanca</option><option>Rabat</option><option>Marrakech</option><option>Fès</option><option>Tanger</option>
              </select>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="text-sm font-medium">État *</label>
              <select
                name="condition"
                value={formData.condition}
                onChange={handleInputChange}
                className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
              >
                <option value="NEW">Neuf</option>
                <option value="LIKE_NEW">Très bon état</option>
                <option value="GOOD">Bon état</option>
                <option value="FAIR">Correct</option>
              </select>
            </div>
            <div>
              <label className="text-sm font-medium">Catégorie *</label>
              <select
                name="category"
                value={formData.category}
                onChange={handleInputChange}
                className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
              >
                <option>Electronics</option><option>Books</option><option>Furniture</option><option>Services</option><option>Other</option>
              </select>
            </div>
          </div>
        </div>

        <div className="flex gap-4">
          <Link to="/marketplace" className="flex-1 border border-gray-200 py-3 rounded-xl text-sm font-medium text-center hover:bg-gray-50">Annuler</Link>
          <button
            type="submit"
            disabled={loading}
            className="flex-1 bg-primary text-white py-3 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors disabled:opacity-50"
          >
            {loading ? "Publication..." : "Publier l'article"}
          </button>
        </div>
      </form>
    </div>
  );
}
