import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../services/api";
import { useAuth } from "../context/AuthContext";

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
  organizerId: string;
  organizerName: string;
  title: string;
  description: string;
  location: string;
  city: string;
  eventDate: string;
  category: string;
  participantCount: number;
  maxParticipants: number | null;
  isParticipating: boolean;
  coverUrl: string | null;
  cancelled: boolean;
  createdAt: string;
}

export default function EventDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [event, setEvent] = useState<Event | null>(null);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);

  useEffect(() => {
    api.get(`/events/${id}`)
      .then(({ data }) => setEvent(data))
      .finally(() => setLoading(false));
  }, [id]);

  const handleJoin = async () => {
    if (!user) { navigate("/auth"); return; }
    setActionLoading(true);
    try {
      const { data } = await api.post(`/events/${id}/join`);
      setEvent(data);
    } finally {
      setActionLoading(false);
    }
  };

  const handleLeave = async () => {
    setActionLoading(true);
    try {
      const { data } = await api.delete(`/events/${id}/leave`);
      setEvent(data);
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) return <div className="flex justify-center items-center h-64 text-gray-400">Chargement...</div>;
  if (!event) return <div className="text-center py-20 text-gray-400">Événement introuvable</div>;

  const isFull = event.maxParticipants !== null && event.participantCount >= event.maxParticipants;
  const isOrganizer = user?.userId === event.organizerId;

  return (
    <div className="max-w-4xl mx-auto px-8 py-12">
      {/* Cover */}
      <div className="relative h-64 rounded-2xl overflow-hidden bg-gray-100 mb-8">
        {event.coverUrl ? (
          <img src={event.coverUrl} className="w-full h-full object-cover" alt={event.title} />
        ) : (
          <div className="w-full h-full flex items-center justify-center bg-gradient-to-br from-primary/10 to-primary/20">
            <span className="material-symbols-outlined text-primary text-7xl">event</span>
          </div>
        )}
        <span className={`absolute top-4 left-4 text-xs font-semibold px-3 py-1.5 rounded-full ${CATEGORY_COLORS[event.category]}`}>
          {event.category}
        </span>
        {event.cancelled && (
          <span className="absolute top-4 right-4 text-xs font-semibold px-3 py-1.5 rounded-full bg-red-100 text-red-700">ANNULÉ</span>
        )}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Main */}
        <div className="lg:col-span-2 space-y-6">
          <div>
            <h1 className="text-2xl font-bold font-[Geist]">{event.title}</h1>
            <p className="text-sm text-gray-500 mt-1">Organisé par {event.organizerName}</p>
          </div>

          <div className="bg-white rounded-2xl border border-gray-100 p-6">
            <h3 className="font-semibold mb-3">Description</h3>
            <p className="text-sm text-gray-600 leading-relaxed whitespace-pre-line">{event.description}</p>
          </div>
        </div>

        {/* Sidebar */}
        <div className="space-y-4">
          <div className="bg-white rounded-2xl border border-gray-100 p-6 space-y-4">
            <div className="flex items-start gap-3">
              <span className="material-symbols-outlined text-primary text-[20px] mt-0.5">calendar_today</span>
              <div>
                <p className="text-xs text-gray-400 uppercase tracking-wide">Date</p>
                <p className="text-sm font-medium mt-0.5">
                  {new Date(event.eventDate).toLocaleDateString("fr-FR", { weekday: "long", day: "numeric", month: "long", year: "numeric" })}
                </p>
                <p className="text-sm text-gray-500">
                  {new Date(event.eventDate).toLocaleTimeString("fr-FR", { hour: "2-digit", minute: "2-digit" })}
                </p>
              </div>
            </div>

            <div className="flex items-start gap-3">
              <span className="material-symbols-outlined text-primary text-[20px] mt-0.5">location_on</span>
              <div>
                <p className="text-xs text-gray-400 uppercase tracking-wide">Lieu</p>
                <p className="text-sm font-medium mt-0.5">{event.location}</p>
                <p className="text-sm text-gray-500">{event.city}</p>
              </div>
            </div>

            <div className="flex items-start gap-3">
              <span className="material-symbols-outlined text-primary text-[20px] mt-0.5">group</span>
              <div>
                <p className="text-xs text-gray-400 uppercase tracking-wide">Participants</p>
                <p className="text-sm font-medium mt-0.5">
                  {event.participantCount}{event.maxParticipants ? ` / ${event.maxParticipants}` : ""} participant{event.participantCount !== 1 ? "s" : ""}
                </p>
                {isFull && <p className="text-xs text-red-500 mt-0.5">Complet</p>}
              </div>
            </div>

            {/* Action Button */}
            {!event.cancelled && !isOrganizer && (
              event.isParticipating ? (
                <button onClick={handleLeave} disabled={actionLoading}
                  className="w-full border border-red-200 text-red-600 py-3 rounded-xl text-sm font-medium hover:bg-red-50 transition-colors disabled:opacity-50">
                  {actionLoading ? "..." : "Se désinscrire"}
                </button>
              ) : (
                <button onClick={handleJoin} disabled={actionLoading || isFull}
                  className="w-full bg-primary text-white py-3 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors disabled:opacity-50">
                  {actionLoading ? "..." : isFull ? "Complet" : "Je participe"}
                </button>
              )
            )}

            {isOrganizer && (
              <p className="text-xs text-center text-gray-400">Vous organisez cet événement</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
