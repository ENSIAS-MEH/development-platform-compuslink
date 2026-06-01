import { useState, useEffect, useRef } from "react";
import { Link } from "react-router-dom";
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

interface Item {
  id: string;
  title: string;
  price: number;
  city: string;
  status: string;
  coverImageUrl: string | null;
  createdAt: string;
}

interface ColocPost {
  id: string;
  title: string;
  city: string;
  rentPerPerson: number;
  status: string;
  coverUrl: string | null;
  createdAt: string;
}

interface SavedItem {
  id: string;
  targetType: string;
  targetId: string;
}

interface MyEvent {
  id: string;
  title: string;
  city: string;
  eventDate: string;
  category: string;
  participantCount: number;
  coverUrl: string | null;
  cancelled: boolean;
}

const STATUS_COLORS: Record<string, string> = {
  OPEN: "bg-blue-100 text-blue-700",
  SOLD: "bg-red-100 text-red-700",
  CLOSED: "bg-gray-100 text-gray-600",
  FULL: "bg-orange-100 text-orange-700",
};

export default function ProfilePage() {
  const [profile, setProfile] = useState<Profile | null>(null);
  const [items, setItems] = useState<Item[]>([]);
  const [colocPosts, setColocPosts] = useState<ColocPost[]>([]);
  const [savedItems, setSavedItems] = useState<SavedItem[]>([]);
  const [myEvents, setMyEvents] = useState<MyEvent[]>([]);
  const [myParticipations, setMyParticipations] = useState<MyEvent[]>([]);
  const [eventsSubTab, setEventsSubTab] = useState<"organized" | "participating">("organized");
  const [activeTab, setActiveTab] = useState<"marketplace" | "colocation" | "favoris" | "events">("marketplace");
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
    Promise.all([
      api.get("/me/profile"),
      api.get("/marketplace/my-items"),
      api.get("/colocations/my-posts"),
      api.get("/saved"),
      api.get("/events/my-events"),
      api.get("/events/my-participations"),
    ]).then(([profileRes, itemsRes, colocRes, savedRes, eventsRes, participationsRes]) => {
      setProfile(profileRes.data);
      setForm({
        fullName: profileRes.data.fullName || "",
        university: profileRes.data.university || "",
        city: profileRes.data.city || "",
        phoneNumber: profileRes.data.phoneNumber || "",
        bio: profileRes.data.bio || "",
      });
      setItems(itemsRes.data);
      setColocPosts(colocRes.data);
      setSavedItems(savedRes.data);
      setMyEvents(eventsRes.data);
      setMyParticipations(participationsRes.data);
    }).finally(() => setLoading(false));
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

  const deleteItem = async (id: string) => {
    if (!confirm("Supprimer cette annonce ?")) return;
    await api.delete(`/marketplace/items/${id}`);
    setItems((prev) => prev.filter((i) => i.id !== id));
  };

  const deleteColoc = async (id: string) => {
    if (!confirm("Supprimer cette colocation ?")) return;
    await api.delete(`/colocations/${id}`);
    setColocPosts((prev) => prev.filter((p) => p.id !== id));
  };

  const deleteEvent = async (id: string) => {
    if (!confirm("Supprimer cet événement ?")) return;
    await api.delete(`/events/${id}`);
    setMyEvents((prev) => prev.filter((e) => e.id !== id));
  };

  if (loading) return <div className="flex justify-center items-center h-64 text-gray-400">Loading...</div>;
  if (!profile) return null;

  const memberSince = new Date(profile.createdAt).toLocaleDateString("fr-FR", { month: "short", year: "numeric" });

  const tabs = [
    { key: "marketplace", label: "Mes Annonces Marketplace" },
    { key: "colocation", label: "Mes Colocations" },
    { key: "events", label: "Mes Événements" },
    { key: "favoris", label: "Favoris" },
  ] as const;

  return (
    <div className="max-w-6xl mx-auto px-8 py-12">
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left - Profile Card */}
        <div className="space-y-6">
          <div className="bg-white rounded-2xl border border-gray-100 p-8 text-center">
            {/* Avatar */}
            <div className="relative w-28 h-28 mx-auto">
              {profile.profilePicUrl ? (
                <img src={profile.profilePicUrl} className="w-28 h-28 rounded-full object-cover" alt="" />
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

            <h2 className="text-xl font-bold font-[Geist] mt-6">{profile.fullName || profile.email}</h2>
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

          {/* Stats */}
          <div className="grid grid-cols-2 gap-4">
            <div className="bg-white rounded-2xl border border-gray-100 p-4 text-center">
              <p className="text-2xl font-bold text-primary">{items.length}</p>
              <p className="text-xs text-gray-500 uppercase tracking-wide mt-1">Annonces</p>
            </div>
            <div className="bg-white rounded-2xl border border-gray-100 p-4 text-center">
              <p className="text-2xl font-bold text-primary">{colocPosts.length}</p>
              <p className="text-xs text-gray-500 uppercase tracking-wide mt-1">Colocations</p>
            </div>
          </div>
        </div>

        {/* Right */}
        <div className="lg:col-span-2 space-y-6">
          {/* CV */}
          <div className="bg-white rounded-2xl border border-gray-100 p-6">
            <div className="flex items-center justify-between mb-2">
              <h3 className="text-lg font-bold font-[Geist]">Curriculum Vitae</h3>
              <span className="text-xs bg-gray-100 text-gray-600 font-medium px-2.5 py-1 rounded-full">Privé</span>
            </div>
            <p className="text-sm text-gray-500 mb-4">Téléchargez votre CV pour postuler plus rapidement aux offres de stage et d'emploi.</p>

            {profile.cvUrl ? (
              <div className="flex items-center justify-between bg-gray-50 rounded-xl p-4">
                <div className="flex items-center gap-3">
                  <span className="material-symbols-outlined text-red-500">picture_as_pdf</span>
                  <div>
                    <p className="text-sm font-medium">CV disponible</p>
                    <a href={profile.cvUrl} target="_blank" rel="noreferrer" className="text-xs text-primary hover:underline">Voir le CV</a>
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

          {/* Tabs */}
          <div>
            <div className="flex gap-6 border-b border-gray-100 mb-6">
              {tabs.map((tab) => (
                <button
                  key={tab.key}
                  onClick={() => setActiveTab(tab.key)}
                  className={`pb-3 text-sm font-medium transition-colors whitespace-nowrap ${
                    activeTab === tab.key ? "text-primary border-b-2 border-primary" : "text-gray-500 hover:text-gray-900"
                  }`}
                >
                  {tab.label}
                </button>
              ))}
            </div>

            {/* Marketplace Items */}
            {activeTab === "marketplace" && (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {items.map((item) => (
                  <div key={item.id} className="relative bg-white rounded-2xl border border-gray-100 overflow-hidden hover:shadow-md transition-shadow">
                    <button onClick={() => deleteItem(item.id)} className="absolute top-2 right-2 z-10 w-7 h-7 bg-red-500 text-white rounded-full flex items-center justify-center hover:bg-red-600 shadow">
                      <span className="material-symbols-outlined text-[14px]">delete</span>
                    </button>
                    <Link to={`/marketplace/${item.id}`}>
                    <div className="relative h-40 bg-gray-100">
                      {item.coverImageUrl ? (
                        <img src={item.coverImageUrl} className="w-full h-full object-cover" alt={item.title} />
                      ) : (
                        <div className="w-full h-full flex items-center justify-center text-gray-300">
                          <span className="material-symbols-outlined text-4xl">image</span>
                        </div>
                      )}
                      <span className="absolute top-3 right-3 bg-white/90 text-xs font-semibold px-2 py-1 rounded-md">{item.price} MAD</span>
                    </div>
                    <div className="p-4">
                      <div className="flex items-center justify-between">
                        <h4 className="font-semibold text-sm truncate">{item.title}</h4>
                        <span className={`text-xs font-medium px-2 py-0.5 rounded-full ml-2 shrink-0 ${STATUS_COLORS[item.status] || "bg-gray-100 text-gray-600"}`}>{item.status}</span>
                      </div>
                      <p className="text-xs text-gray-400 mt-2">{new Date(item.createdAt).toLocaleDateString("fr-FR")}</p>
                    </div>
                    </Link>
                  </div>
                ))}
                <Link to="/marketplace/create" className="border-2 border-dashed border-gray-200 rounded-2xl flex flex-col items-center justify-center p-8 text-center hover:border-primary/50 transition-colors min-h-[200px]">
                  <span className="material-symbols-outlined text-gray-400 text-3xl">add</span>
                  <p className="text-sm font-medium text-gray-600 mt-2">Créer une annonce</p>
                </Link>
              </div>
            )}

            {/* Colocation Posts */}
            {activeTab === "colocation" && (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {colocPosts.map((post) => (
                  <div key={post.id} className="relative bg-white rounded-2xl border border-gray-100 overflow-hidden hover:shadow-md transition-shadow">
                    <button onClick={() => deleteColoc(post.id)} className="absolute top-2 right-2 z-10 w-7 h-7 bg-red-500 text-white rounded-full flex items-center justify-center hover:bg-red-600 shadow">
                      <span className="material-symbols-outlined text-[14px]">delete</span>
                    </button>
                    <Link to={`/colocation/${post.id}`}>
                    <div className="relative h-40 bg-gray-100">
                      {post.coverUrl ? (
                        <img src={post.coverUrl} className="w-full h-full object-cover" alt={post.title} />
                      ) : (
                        <div className="w-full h-full flex items-center justify-center text-gray-300">
                          <span className="material-symbols-outlined text-4xl">home</span>
                        </div>
                      )}
                      <span className="absolute top-3 right-3 bg-white/90 text-xs font-semibold px-2 py-1 rounded-md">{post.rentPerPerson} MAD/mois</span>
                    </div>
                    <div className="p-4">
                      <div className="flex items-center justify-between">
                        <h4 className="font-semibold text-sm truncate">{post.title}</h4>
                        <span className={`text-xs font-medium px-2 py-0.5 rounded-full ml-2 shrink-0 ${STATUS_COLORS[post.status] || "bg-gray-100 text-gray-600"}`}>{post.status}</span>
                      </div>
                      <p className="text-xs text-gray-500 mt-1">{post.city}</p>
                      <p className="text-xs text-gray-400 mt-2">{new Date(post.createdAt).toLocaleDateString("fr-FR")}</p>
                    </div>
                    </Link>
                  </div>
                ))}
                <Link to="/colocation/create" className="border-2 border-dashed border-gray-200 rounded-2xl flex flex-col items-center justify-center p-8 text-center hover:border-primary/50 transition-colors min-h-[200px]">
                  <span className="material-symbols-outlined text-gray-400 text-3xl">add</span>
                  <p className="text-sm font-medium text-gray-600 mt-2">Créer une colocation</p>
                </Link>
              </div>
            )}

            {/* Events */}
            {activeTab === "events" && (
              <div>
                {/* Sub-tabs */}
                <div className="flex gap-4 mb-5">
                  {[{ key: "organized", label: "Mes événements" }, { key: "participating", label: "Je participe" }].map((t) => (
                    <button key={t.key} onClick={() => setEventsSubTab(t.key as any)}
                      className={`text-sm font-medium pb-2 border-b-2 transition-colors ${eventsSubTab === t.key ? "border-primary text-primary" : "border-transparent text-gray-500 hover:text-gray-900"}`}>
                      {t.label}
                    </button>
                  ))}
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  {(eventsSubTab === "organized" ? myEvents : myParticipations).map((event) => (
                    <div key={event.id} className="relative bg-white rounded-2xl border border-gray-100 overflow-hidden hover:shadow-md transition-shadow">
                      {eventsSubTab === "organized" && (
                        <button onClick={() => deleteEvent(event.id)} className="absolute top-2 right-2 z-10 w-7 h-7 bg-red-500 text-white rounded-full flex items-center justify-center hover:bg-red-600 shadow">
                          <span className="material-symbols-outlined text-[14px]">delete</span>
                        </button>
                      )}
                      <Link to={`/events/${event.id}`}>
                        <div className="relative h-40 bg-gray-100">
                          {event.coverUrl ? (
                            <img src={event.coverUrl} className="w-full h-full object-cover" alt={event.title} />
                          ) : (
                            <div className="w-full h-full flex items-center justify-center bg-gradient-to-br from-primary/10 to-primary/20">
                              <span className="material-symbols-outlined text-primary text-4xl">event</span>
                            </div>
                          )}
                          <span className="absolute top-3 left-3 bg-white/90 text-xs font-semibold px-2 py-1 rounded-md">{event.category}</span>
                        </div>
                        <div className="p-4">
                          <div className="flex items-center justify-between">
                            <h4 className="font-semibold text-sm truncate">{event.title}</h4>
                            {event.cancelled && <span className="text-xs bg-red-100 text-red-600 px-2 py-0.5 rounded-full ml-2 shrink-0">Annulé</span>}
                          </div>
                          <p className="text-xs text-gray-500 mt-1">{event.city} · {new Date(event.eventDate).toLocaleDateString("fr-FR")}</p>
                          <p className="text-xs text-gray-400 mt-1">{event.participantCount} participant{event.participantCount !== 1 ? "s" : ""}</p>
                        </div>
                      </Link>
                    </div>
                  ))}
                  {eventsSubTab === "organized" && (
                    <Link to="/events/create" className="border-2 border-dashed border-gray-200 rounded-2xl flex flex-col items-center justify-center p-8 text-center hover:border-primary/50 transition-colors min-h-[200px]">
                      <span className="material-symbols-outlined text-gray-400 text-3xl">add</span>
                      <p className="text-sm font-medium text-gray-600 mt-2">Créer un événement</p>
                    </Link>
                  )}
                </div>
              </div>
            )}

            {/* Favoris */}
            {activeTab === "favoris" && (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {savedItems.length === 0 ? (
                  <p className="text-sm text-gray-400 col-span-2 text-center py-12">Aucun favori pour l'instant.</p>
                ) : savedItems.map((saved) => (
                  <Link key={saved.id} to={
                    saved.targetType === "ITEM" ? `/marketplace/${saved.targetId}` :
                    saved.targetType === "COLOC" ? `/colocation/${saved.targetId}` :
                    saved.targetType === "OFFER" ? `/offers/${saved.targetId}` :
                    `/events/${saved.targetId}`
                  } className="bg-white rounded-2xl border border-gray-100 p-4 hover:shadow-md transition-shadow flex items-center gap-3">
                    <span className="material-symbols-outlined text-primary text-2xl">
                      {saved.targetType === "ITEM" ? "shopping_bag" :
                       saved.targetType === "COLOC" ? "home" :
                       saved.targetType === "OFFER" ? "work" : "event"}
                    </span>
                    <div>
                      <p className="text-sm font-medium">{saved.targetType}</p>
                      <p className="text-xs text-gray-400">{saved.targetId}</p>
                    </div>
                  </Link>
                ))}
              </div>
            )}
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
              <button onClick={() => setEditOpen(false)} className="flex-1 border border-gray-200 py-2.5 rounded-xl text-sm font-medium hover:bg-gray-50 transition-colors">Annuler</button>
              <button onClick={handleSave} disabled={saving} className="flex-1 bg-primary text-white py-2.5 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors disabled:opacity-50">
                {saving ? "Enregistrement..." : "Enregistrer"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
