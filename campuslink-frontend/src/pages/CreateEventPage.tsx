import { useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

const CATEGORIES = ["TECH", "CAREER", "SOCIAL", "SPORT", "CULTURE", "WORKSHOP", "OTHER"];

export default function CreateEventPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    title: "", description: "", location: "", city: "",
    eventDate: "", category: "TECH", maxParticipants: "",
  });
  const [coverFile, setCoverFile] = useState<File | null>(null);
  const [coverPreview, setCoverPreview] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const fileRef = useRef<HTMLInputElement>(null);

  const set = (key: string, value: string) => setForm((f) => ({ ...f, [key]: value }));

  const handleFile = (file: File) => {
    setCoverFile(file);
    setCoverPreview(URL.createObjectURL(file));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setError("");
    try {
      const payload = {
        ...form,
        eventDate: new Date(form.eventDate).toISOString(),
        maxParticipants: form.maxParticipants ? parseInt(form.maxParticipants) : null,
      };
      const { data } = await api.post("/events", payload);

      if (coverFile) {
        const fd = new FormData();
        fd.append("file", coverFile);
        await api.post(`/events/${data.id}/cover`, fd);
      }

      navigate(`/events/${data.id}`);
    } catch (err: any) {
      setError(err.response?.data?.message || "Une erreur est survenue");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto px-8 py-12">
      <h1 className="text-2xl font-bold font-[Geist] mb-8">Créer un événement</h1>

      <form onSubmit={handleSubmit} className="bg-white rounded-2xl border border-gray-100 p-8 space-y-5">
        {error && <p className="text-sm text-red-500 bg-red-50 px-4 py-3 rounded-xl">{error}</p>}

        {/* Cover upload */}
        <div>
          <label className="text-xs font-medium text-gray-500 uppercase tracking-wide">Photo de couverture</label>
          <div
            onClick={() => fileRef.current?.click()}
            className="mt-1 border-2 border-dashed border-gray-200 rounded-xl overflow-hidden cursor-pointer hover:border-primary/50 transition-colors"
          >
            {coverPreview ? (
              <img src={coverPreview} className="w-full h-40 object-cover" alt="preview" />
            ) : (
              <div className="h-40 flex flex-col items-center justify-center text-gray-400">
                <span className="material-symbols-outlined text-3xl">add_photo_alternate</span>
                <p className="text-sm mt-1">Cliquez pour ajouter une image</p>
              </div>
            )}
          </div>
          <input ref={fileRef} type="file" accept="image/*" className="hidden"
            onChange={(e) => e.target.files?.[0] && handleFile(e.target.files[0])} />
        </div>

        <div>
          <label className="text-xs font-medium text-gray-500 uppercase tracking-wide">Titre *</label>
          <input required className="w-full mt-1 border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-primary/30"
            value={form.title} onChange={(e) => set("title", e.target.value)} />
        </div>

        <div>
          <label className="text-xs font-medium text-gray-500 uppercase tracking-wide">Description *</label>
          <textarea required rows={4} className="w-full mt-1 border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-primary/30 resize-none"
            value={form.description} onChange={(e) => set("description", e.target.value)} />
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="text-xs font-medium text-gray-500 uppercase tracking-wide">Lieu *</label>
            <input required className="w-full mt-1 border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-primary/30"
              value={form.location} onChange={(e) => set("location", e.target.value)} />
          </div>
          <div>
            <label className="text-xs font-medium text-gray-500 uppercase tracking-wide">Ville *</label>
            <input required className="w-full mt-1 border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-primary/30"
              value={form.city} onChange={(e) => set("city", e.target.value)} />
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="text-xs font-medium text-gray-500 uppercase tracking-wide">Date & Heure *</label>
            <input required type="datetime-local" className="w-full mt-1 border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-primary/30"
              value={form.eventDate} onChange={(e) => set("eventDate", e.target.value)} />
          </div>
          <div>
            <label className="text-xs font-medium text-gray-500 uppercase tracking-wide">Catégorie *</label>
            <select required className="w-full mt-1 border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-primary/30 bg-white"
              value={form.category} onChange={(e) => set("category", e.target.value)}>
              {CATEGORIES.map((c) => <option key={c} value={c}>{c}</option>)}
            </select>
          </div>
        </div>

        <div>
          <label className="text-xs font-medium text-gray-500 uppercase tracking-wide">Participants max</label>
          <input type="number" min="1" className="w-full mt-1 border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-primary/30"
            placeholder="Illimité" value={form.maxParticipants} onChange={(e) => set("maxParticipants", e.target.value)} />
        </div>

        <div className="flex gap-3 pt-2">
          <button type="button" onClick={() => navigate("/events")}
            className="flex-1 border border-gray-200 py-3 rounded-xl text-sm font-medium hover:bg-gray-50 transition-colors">
            Annuler
          </button>
          <button type="submit" disabled={submitting}
            className="flex-1 bg-primary text-white py-3 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors disabled:opacity-50">
            {submitting ? "Création..." : "Créer l'événement"}
          </button>
        </div>
      </form>
    </div>
  );
}
