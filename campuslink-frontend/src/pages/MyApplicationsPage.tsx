import { Link } from "react-router-dom";

const applications = [
  { id: "1", offerTitle: "Software Engineering Intern", company: "TechMaroc", appliedAt: "22 Mai 2024", status: "PENDING", statusLabel: "En attente", statusColor: "bg-yellow-100 text-yellow-700" },
  { id: "2", offerTitle: "Marketing & Comms Intern", company: "BrandSphere Media", appliedAt: "20 Mai 2024", status: "SEEN", statusLabel: "Vue", statusColor: "bg-blue-100 text-blue-700" },
  { id: "3", offerTitle: "Data Analyst PFE", company: "OCP Group", appliedAt: "15 Mai 2024", status: "ACCEPTED", statusLabel: "Acceptée", statusColor: "bg-green-100 text-green-700" },
  { id: "4", offerTitle: "UX Designer Junior", company: "Majorel Digital", appliedAt: "10 Mai 2024", status: "REJECTED", statusLabel: "Refusée", statusColor: "bg-red-100 text-red-700" },
];

export default function MyApplicationsPage() {
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
        {applications.map((app) => (
          <div key={app.id} className="bg-white rounded-2xl border border-gray-100 p-6 flex items-center justify-between hover:shadow-sm transition-shadow">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 bg-gray-100 rounded-xl flex items-center justify-center">
                <span className="material-symbols-outlined text-gray-500">work</span>
              </div>
              <div>
                <h3 className="font-semibold">{app.offerTitle}</h3>
                <p className="text-sm text-gray-500">{app.company}</p>
              </div>
            </div>
            <div className="flex items-center gap-6">
              <span className="text-xs text-gray-400">{app.appliedAt}</span>
              <span className={`text-xs font-semibold px-3 py-1.5 rounded-full ${app.statusColor}`}>
                {app.statusLabel}
              </span>
            </div>
          </div>
        ))}
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
