import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import api from "../services/api";

const CATEGORY_COLORS: Record<string, string> = {
  TECH: "bg-blue-100 text-blue-700",
  CAREER: "bg-purple-100 text-purple-700",
  SOCIAL: "bg-pink-100 text-pink-700",
  SPORT: "bg-green-100 text-green-700",
  CULTURE: "bg-yellow-100 text-yellow-700",
  WORKSHOP: "bg-orange-100 text-orange-700",
  OTHER: "bg-gray-100 text-gray-600",
};

interface Event {
  id: string;
  title: string;
  location: string;
  city: string;
  eventDate: string;
  category: string;
  participantCount: number;
  maxParticipants: number | null;
  coverUrl: string | null;
  organizerName: string;
  cancelled: boolean;
}

export default function EventsPage() {
  const [events, setEvents] = useState<Event[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeCategory, setActiveCategory] = useState("ALL");
  const [city, setCity] = useState("");
  const [search, setSearch] = useState("");

  useEffect(() => {
    setLoading(true);
    const params = new URLSearchParams();
    if (activeCategory !== "ALL") params.set("category", activeCategory);
    if (city) params.set("city", city);
    const query = params.toString() ? `?${params.toString()}` : "";
    api.get(`/events${query}`)
      .then(({ data }) => setEvents(data))
      .finally(() => setLoading(false));
  }, [activeCategory, city]);

  const filteredEvents = search
    ? events.filter((e) => e.title.toLowerCase().includes(search.toLowerCase()) || e.city.toLowerCase().includes(search.toLowerCase()))
    : events;

  return (
    <div className="max-w-6xl mx-auto px-8 py-12">
      {/* Header */}
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-3xl font-bold font-[Geist]">Événements</h1>
          <p className="text-gray-500 mt-1">Découvrez les événements sur votre campus</p>
        </div>
        <Link
          to="/events/create"
          className="bg-primary text-white px-5 py-2.5 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors flex items-center gap-2"
        >
          <span className="material-symbols-outlined text-[18px]">add</span>
          Créer un événement
        </Link>
      </div>

      {/* Search & Filters */}
      <div className="mt-8 bg-white rounded-2xl border border-gray-100 p-2 flex flex-wrap gap-2 mb-8">
        <div className="flex-1 min-w-[200px] flex items-center gap-2 px-4">
          <span className="material-symbols-outlined text-gray-400 text-[20px]">search</span>
          <input
            type="text"
            placeholder="Rechercher un événement..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full py-3 text-sm outline-none"
          />
        </div>
        <select
          value={activeCategory}
          onChange={(e) => setActiveCategory(e.target.value)}
          className="px-4 py-3 text-sm text-gray-600 bg-gray-50 rounded-xl border-none outline-none"
        >
          <option value="ALL">Toutes catégories</option>
          <option value="TECH">Tech</option>
          <option value="CAREER">Career</option>
          <option value="SOCIAL">Social</option>
          <option value="SPORT">Sport</option>
          <option value="CULTURE">Culture</option>
          <option value="WORKSHOP">Workshop</option>
          <option value="OTHER">Autre</option>
        </select>
        <select
          value={city}
          onChange={(e) => setCity(e.target.value)}
          className="px-4 py-3 text-sm text-gray-600 bg-gray-50 rounded-xl border-none outline-none"
        >
          <option value="">Toutes les villes</option>
          <option value="Casablanca">Casablanca</option>
          <option value="Rabat">Rabat</option>
          <option value="Marrakech">Marrakech</option>
          <option value="Fès">Fès</option>
          <option value="Tanger">Tanger</option>
          <option value="Agadir">Agadir</option>
          <option value="Oujda">Oujda</option>
        </select>
      </div>

      {/* Events Grid */}
      {loading ? (
        <div className="text-center py-20 text-gray-400">Chargement...</div>
      ) : filteredEvents.length === 0 ? (
        <div className="text-center py-20 text-gray-400">
          <span className="material-symbols-outlined text-5xl block mb-3">event</span>
          Aucun événement trouvé
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredEvents.map((event) => (
            <Link
              key={event.id}
              to={`/events/${event.id}`}
              className="bg-white rounded-2xl border border-gray-100 overflow-hidden hover:shadow-md transition-shadow"
            >
              {/* Cover */}
              <div className="relative h-44 bg-gray-100">
                {event.coverUrl ? (
                  <img src={event.coverUrl} className="w-full h-full object-cover" alt={event.title} />
                ) : (
                  <div className="w-full h-full flex items-center justify-center bg-gradient-to-br from-primary/10 to-primary/20">
                    <span className="material-symbols-outlined text-primary text-5xl">event</span>
                  </div>
                )}
                <span className={`absolute top-3 left-3 text-xs font-semibold px-2.5 py-1 rounded-full ${CATEGORY_COLORS[event.category]}`}>
                  {event.category}
                </span>
              </div>

              {/* Content */}
              <div className="p-4">
                <h3 className="font-semibold text-sm leading-snug line-clamp-2">{event.title}</h3>

                <div className="mt-3 space-y-1.5">
                  <p className="text-xs text-gray-500 flex items-center gap-1.5">
                    <span className="material-symbols-outlined text-[14px] text-gray-400">calendar_today</span>
                    {new Date(event.eventDate).toLocaleDateString("fr-FR", { day: "numeric", month: "short", year: "numeric", hour: "2-digit", minute: "2-digit" })}
                  </p>
                  <p className="text-xs text-gray-500 flex items-center gap-1.5">
                    <span className="material-symbols-outlined text-[14px] text-gray-400">location_on</span>
                    {event.location}, {event.city}
                  </p>
                  <p className="text-xs text-gray-500 flex items-center gap-1.5">
                    <span className="material-symbols-outlined text-[14px] text-gray-400">group</span>
                    {event.participantCount} participant{event.participantCount !== 1 ? "s" : ""}
                    {event.maxParticipants ? ` / ${event.maxParticipants}` : ""}
                  </p>
                </div>

                <div className="mt-3 pt-3 border-t border-gray-100 flex items-center justify-between">
                  <p className="text-xs text-gray-400">Par {event.organizerName}</p>
                  {event.maxParticipants && event.participantCount >= event.maxParticipants && (
                    <span className="text-xs bg-red-100 text-red-600 font-medium px-2 py-0.5 rounded-full">Complet</span>
                  )}
                </div>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
