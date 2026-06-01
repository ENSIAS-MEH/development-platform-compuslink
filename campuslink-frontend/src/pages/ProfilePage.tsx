import { useState, useEffect, useRef } from "react";
import api from "../services/api";

interface Profile {
  id: string;
  email: string;
  role: string;
  fullName: string | null;
  university: string | null;
  city: string | null;
  phoneNumber: string | null;
  bio: string | null;
  profilePicUrl: string | null;
  cvUrl: string | null;
  createdAt: string;
}

export default function ProfilePage() {
  const [profile, setProfile] = useState<Profile | null>(null);
  const [loading, setLoading] = useState(true);
  const [editOpen, setEditOpen] = useState(false);
  const [form, setForm] = useState({ fullName: "", university: "", city: "", phoneNumber: "", bio: "" });
  const [saving, setSaving] = useState(false);
  const picInputRef = useRef<HTMLInputElement>(null);
  const cvInputRef = useRef<HTMLInputElement>(null);

  const handleUpload = async (file: File, endpoint: string, field: "profilePicUrl" | "cvUrl") => {
    const formData = new FormData();
    formData.append("file", file);
    const { data } = await api.post(endpoint, formData);
    setProfile((p) => p ? { ...p, [field]: data[field] } : p);
  };

  useEffect(() => {
    api.get("/me/profile")
      .then(({ data }) => {
        setProfile(data);
        setForm({
          fullName: data.fullName || "",
          university: data.university || "",
          city: data.city || "",
          phoneNumber: data.phoneNumber || "",
          bio: data.bio || "",
        });
      })
      .finally(() => setLoading(false));
  }, []);

  const handleSave = async () => {
    setSaving(true);
    try {
      const { data } = await api.patch("/me/profile", form);
      setProfile(data);
      setEditOpen(false);
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div className="flex justify-center items-center h-64 text-gray-400">Loading...</div>;
  if (!profile) return null;

  const memberSince = new Date(profile.createdAt).toLocaleDateString("fr-FR", { month: "short", year: "numeric" });

  return (
    <div className="max-w-6xl mx-auto px-8 py-12">
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left - Profile Card */}
        <div className="space-y-6">
          <div className="bg-white rounded-2xl border border-gray-100 p-8 text-center">
            <div className="relative w-28 h-28 mx-auto">
              {profile.profilePicUrl ? (
                <img src={profile.profilePicUrl} className="w-28 h-28 rounded-full object-cover cursor-pointer" alt="" onClick={() => picInputRef.current?.click()} />
              ) : (
                <div className="w-28 h-28 rounded-full bg-primary/10 flex items-center justify-center text-3xl font-bold text-primary">
                  {(profile.fullName || profile.email)[0].toUpperCase()}
                </div>
              )}
              <button
                onClick={() => picInputRef.current?.click()}
                className="absolute -bottom-3 left-1/2 -translate-x-1/2 w-8 h-8 bg-primary text-white rounded-full flex items-center justify-center shadow-md hover:bg-primary-dark transition-colors border-2 border-white"
              >
                <span className="material-symbols-outlined text-[16px]">add</span>
              </button>
            </div>
            <input ref={picInputRef} type="file" accept="image/*" className="hidden" onChange={(e) => e.target.files?.[0] && handleUpload(e.target.files[0], "/me/profile/picture", "profilePicUrl")} />
            <h2 className="text-xl font-bold font-[Geist] mt-4">{profile.fullName || profile.email}</h2>
            <span className="inline-block mt-2 text-xs bg-green-100 text-green-700 font-semibold px-3 py-1 rounded-full">
              {profile.role}
            </span>

            <div className="mt-6 space-y-3 text-left border-t border-gray-100 pt-6">
              {profile.university && (
                <p className="text-sm text-gray-600 flex items-center gap-2">
                  <span className="material-symbols-outlined text-[16px] text-gray-400">school</span>
                  {profile.university}
                </p>
              )}
              {profile.city && (
                <p className="text-sm text-gray-600 flex items-center gap-2">
                  <span className="material-symbols-outlined text-[16px] text-gray-400">location_on</span>
                  {profile.city}
                </p>
              )}
              {profile.phoneNumber && (
                <p className="text-sm text-gray-600 flex items-center gap-2">
                  <span className="material-symbols-outlined text-[16px] text-gray-400">phone</span>
                  {profile.phoneNumber}
                </p>
              )}
              <p className="text-sm text-gray-600 flex items-center gap-2">
                <span className="material-symbols-outlined text-[16px] text-gray-400">calendar_today</span>
                Membre depuis {memberSince}
              </p>
            </div>

            {profile.bio && (
              <p className="mt-4 text-sm text-gray-500 text-left border-t border-gray-100 pt-4">{profile.bio}</p>
            )}

            <button
              onClick={() => setEditOpen(true)}
              className="w-full mt-6 bg-primary text-white py-3 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors"
            >
              Modifier le Profil
            </button>
          </div>
        </div>

        {/* Right - CV Section */}
        <div className="lg:col-span-2 space-y-6">
          <div className="bg-white rounded-2xl border border-gray-100 p-6">
            <div className="flex items-center justify-between mb-2">
              <h3 className="text-lg font-bold font-[Geist]">Curriculum Vitae</h3>
            </div>
            <p className="text-sm text-gray-500 mb-6">
              Téléchargez votre CV pour postuler plus rapidement aux offres de stage et d'emploi.
            </p>

            {profile.cvUrl ? (
              <div className="flex items-center justify-between bg-gray-50 rounded-xl p-4">
                <div className="flex items-center gap-3">
                  <span className="material-symbols-outlined text-red-500">picture_as_pdf</span>
                  <div>
                    <p className="text-sm font-medium">CV disponible</p>
                    <a href={profile.cvUrl} target="_blank" rel="noreferrer" className="text-xs text-primary hover:underline">
                      Voir le CV
                    </a>
                  </div>
                </div>
                <button onClick={() => cvInputRef.current?.click()} className="text-xs text-gray-500 hover:text-primary">Remplacer</button>
              </div>
            ) : (
              <div className="border-2 border-dashed border-gray-200 rounded-xl p-8 text-center cursor-pointer hover:border-primary/50 transition-colors" onClick={() => cvInputRef.current?.click()}>
                <span className="material-symbols-outlined text-gray-400 text-3xl">cloud_upload</span>
                <p className="text-sm font-medium mt-2">Cliquez pour uploader votre CV</p>
                <p className="text-xs text-gray-400 mt-1">PDF, DOCX jusqu'à 5MB</p>
              </div>
            )}
            <input ref={cvInputRef} type="file" accept=".pdf,.doc,.docx" className="hidden" onChange={(e) => e.target.files?.[0] && handleUpload(e.target.files[0], "/me/profile/cv", "cvUrl")} />
          </div>
        </div>
      </div>

      {/* Edit Modal */}
      {editOpen && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl p-8 w-full max-w-md space-y-4">
            <h3 className="text-lg font-bold font-[Geist]">Modifier le Profil</h3>

            {[
              { label: "Nom complet", key: "fullName" },
              { label: "Université", key: "university" },
              { label: "Ville", key: "city" },
              { label: "Téléphone", key: "phoneNumber" },
            ].map(({ label, key }) => (
              <div key={key}>
                <label className="text-xs font-medium text-gray-500 uppercase tracking-wide">{label}</label>
                <input
                  className="w-full mt-1 border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-primary/30"
                  value={form[key as keyof typeof form]}
                  onChange={(e) => setForm({ ...form, [key]: e.target.value })}
                />
              </div>
            ))}

            <div>
              <label className="text-xs font-medium text-gray-500 uppercase tracking-wide">Bio</label>
              <textarea
                rows={3}
                className="w-full mt-1 border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-primary/30 resize-none"
                value={form.bio}
                onChange={(e) => setForm({ ...form, bio: e.target.value })}
              />
            </div>

            <div className="flex gap-3 pt-2">
              <button
                onClick={() => setEditOpen(false)}
                className="flex-1 border border-gray-200 py-2.5 rounded-xl text-sm font-medium hover:bg-gray-50 transition-colors"
              >
                Annuler
              </button>
              <button
                onClick={handleSave}
                disabled={saving}
                className="flex-1 bg-primary text-white py-2.5 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors disabled:opacity-50"
              >
                {saving ? "Enregistrement..." : "Enregistrer"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
