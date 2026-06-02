import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../services/api";

export default function CreateOfferPage() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [form, setForm] = useState({
    type: "INTERNSHIP",
    title: "",
    company: "",
    domain: "",
    city: "",
    locationType: "ON_SITE",
    experienceLevel: "",
    duration: "",
    description: "",
    deadline: "",
  });

  const set = (field: string, value: string) => setForm((f) => ({ ...f, [field]: value }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      const body: Record<string, unknown> = {
        type: form.type,
        title: form.title,
        company: form.company || undefined,
        domain: form.domain || undefined,
        city: form.city || undefined,
        locationType: form.locationType,
        experienceLevel: form.experienceLevel || undefined,
        duration: form.duration || undefined,
        description: form.description,
        deadline: form.deadline || undefined,
      };
      await api.post("/offers", body);
      navigate("/offers");
    } catch (err: any) {
      setError(err.response?.data?.message || "Erreur lors de la publication.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-3xl mx-auto px-8 py-12">
      <Link to="/offers" className="text-sm text-gray-500 hover:text-primary flex items-center gap-1 mb-6">
        <span className="material-symbols-outlined text-[16px]">arrow_back</span> Retour
      </Link>

      <h1 className="text-3xl font-bold font-[Geist]">Publier une offre</h1>
      <p className="text-gray-500 mt-2">Partagez une opportunité avec la communauté étudiante.</p>

      {error && <p className="mt-4 text-sm text-red-600 bg-red-50 px-4 py-2 rounded-xl">{error}</p>}

      <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
        <div className="bg-white rounded-2xl border border-gray-100 p-6 space-y-5">
          <h3 className="font-semibold">Type d'offre</h3>
          <div className="grid grid-cols-3 gap-3">
            {([["INTERNSHIP", "Stage"], ["PFE", "PFE"], ["JOB", "Emploi"]] as const).map(([val, label]) => (
              <label key={val} className={`flex items-center justify-center gap-2 border rounded-xl py-3 cursor-pointer transition-colors ${form.type === val ? "border-primary bg-blue-50" : "border-gray-200 hover:border-primary hover:bg-blue-50"}`}>
                <input type="radio" name="type" className="sr-only" checked={form.type === val} onChange={() => set("type", val)} />
                <span className="text-sm font-medium">{label}</span>
              </label>
            ))}
          </div>
        </div>

        <div className="bg-white rounded-2xl border border-gray-100 p-6 space-y-5">
          <h3 className="font-semibold">Détails de l'offre</h3>

          <div>
            <label className="text-sm font-medium">Titre du poste *</label>
            <input type="text" required placeholder="Ex: Développeur Frontend React" className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" value={form.title} onChange={(e) => set("title", e.target.value)} />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="text-sm font-medium">Entreprise</label>
              <input type="text" placeholder="Nom de l'entreprise" className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" value={form.company} onChange={(e) => set("company", e.target.value)} />
            </div>
            <div>
              <label className="text-sm font-medium">Domaine</label>
              <input type="text" placeholder="Ex: Fintech, IA, Web..." className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" value={form.domain} onChange={(e) => set("domain", e.target.value)} />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="text-sm font-medium">Ville</label>
              <input type="text" placeholder="Casablanca" className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" value={form.city} onChange={(e) => set("city", e.target.value)} />
            </div>
            <div>
              <label className="text-sm font-medium">Mode de travail *</label>
              <select className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" value={form.locationType} onChange={(e) => set("locationType", e.target.value)}>
                <option value="ON_SITE">Sur site</option>
                <option value="REMOTE">Remote</option>
                <option value="HYBRID">Hybride</option>
              </select>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="text-sm font-medium">Niveau d'expérience</label>
              <select className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" value={form.experienceLevel} onChange={(e) => set("experienceLevel", e.target.value)}>
                <option value="">Non spécifié</option>
                <option value="STUDENT">Étudiant</option>
                <option value="JUNIOR">Junior</option>
                <option value="SENIOR">Senior</option>
              </select>
            </div>
            <div>
              <label className="text-sm font-medium">Durée</label>
              <input type="text" placeholder="Ex: 6 mois" className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" value={form.duration} onChange={(e) => set("duration", e.target.value)} />
            </div>
          </div>

          <div>
            <label className="text-sm font-medium">Description *</label>
            <textarea required placeholder="Décrivez le poste, les responsabilités, le profil recherché..." className="mt-1.5 w-full h-36 px-4 py-3 border border-gray-200 rounded-xl text-sm resize-none outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" value={form.description} onChange={(e) => set("description", e.target.value)} />
          </div>

          <div>
            <label className="text-sm font-medium">Date limite de candidature</label>
            <input type="date" className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" value={form.deadline} onChange={(e) => set("deadline", e.target.value)} />
          </div>
        </div>

        <div className="flex gap-4">
          <Link to="/offers" className="flex-1 border border-gray-200 py-3 rounded-xl text-sm font-medium text-center hover:bg-gray-50">Annuler</Link>
          <button type="submit" disabled={loading} className="flex-1 bg-primary text-white py-3 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors disabled:opacity-50">
            {loading ? "Publication..." : "Publier l'offre"}
          </button>
        </div>
      </form>
    </div>
  );
}
