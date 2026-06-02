import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import api from "../services/api";

interface Application {
  id: string;
  offerId: string;
  status: string;
  message: string;
  appliedAt: string;
}

const statusMap: Record<string, { label: string; color: string }> = {
  PENDING: { label: "En attente", color: "bg-yellow-100 text-yellow-700" },
  SEEN: { label: "Vue", color: "bg-blue-100 text-blue-700" },
  ACCEPTED: { label: "Acceptée", color: "bg-green-100 text-green-700" },
  REJECTED: { label: "Refusée", color: "bg-red-100 text-red-700" },
};

export default function MyApplicationsPage() {
  const [applications, setApplications] = useState<Application[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get("/me/applications").then(({ data }) => setApplications(data)).catch(console.error).finally(() => setLoading(false));
  }, []);

  if (loading) return <p className="text-center text-gray-400 py-20">Chargement...</p>;

  return (
    <div className="max-w-4xl mx-auto px-8 py-12">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-3xl font-bold font-[Geist]">Mes Candidatures</h1>
          <p className="text-gray-500 mt-1">{applications.length} candidature{applications.length > 1 ? "s" : ""} envoyée{applications.length > 1 ? "s" : ""}</p>
        </div>
        <Link to="/offers" className="bg-primary text-white px-5 py-2.5 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors">
          Explorer les offres
        </Link>
      </div>

      <div className="space-y-4">
        {applications.map((app) => {
          const s = statusMap[app.status] || { label: app.status, color: "bg-gray-100 text-gray-700" };
          return (
            <Link to={`/offers/${app.offerId}`} key={app.id} className="bg-white rounded-2xl border border-gray-100 p-6 flex items-center justify-between hover:shadow-sm transition-shadow block">
              <div className="flex items-center gap-4">
                <div className="w-12 h-12 bg-gray-100 rounded-xl flex items-center justify-center">
                  <span className="material-symbols-outlined text-gray-500">work</span>
                </div>
                <div>
                  <p className="font-semibold">Offre #{app.offerId.slice(0, 8)}</p>
                  {app.message && <p className="text-sm text-gray-500 line-clamp-1">{app.message}</p>}
                </div>
              </div>
              <div className="flex items-center gap-6">
                <span className="text-xs text-gray-400">{new Date(app.appliedAt).toLocaleDateString("fr-FR")}</span>
                <span className={`text-xs font-semibold px-3 py-1.5 rounded-full ${s.color}`}>{s.label}</span>
              </div>
            </Link>
          );
        })}
      </div>

      {applications.length === 0 && (
        <div className="text-center py-16">
          <span className="material-symbols-outlined text-gray-300 text-5xl">inbox</span>
          <p className="text-gray-500 mt-4">Aucune candidature pour le moment.</p>
          <Link to="/offers" className="text-primary font-medium text-sm mt-2 inline-block">Découvrir les offres</Link>
        </div>
      )}
    </div>
  );
}
