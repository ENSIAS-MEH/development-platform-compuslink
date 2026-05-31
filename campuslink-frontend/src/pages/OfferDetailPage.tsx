import { useState } from "react";
import { Link } from "react-router-dom";

const offer = {
  id: "1", title: "Software Engineering Intern", company: "TechMaroc", type: "INTERNSHIP",
  city: "Casablanca", locationType: "HYBRID", experienceLevel: "PFE Qualified",
  duration: "6 mois", domain: "Payments Infrastructure", deadline: "15 Janvier 2025",
  description: "Rejoignez notre équipe core platform pour construire des microservices scalables. Vous travaillerez avec React, Node.js, et PostgreSQL dans un environnement agile.\n\nResponsabilités:\n• Développer et maintenir des APIs RESTful\n• Participer aux code reviews\n• Collaborer avec l'équipe produit\n• Écrire des tests unitaires et d'intégration\n\nProfil recherché:\n• Étudiant en dernière année d'école d'ingénieur\n• Maîtrise de JavaScript/TypeScript\n• Connaissance de Git et méthodologies agiles\n• Bon niveau en anglais",
  posted: "il y a 5 jours", applicationCount: 12,
  logo: "M", logoColor: "bg-blue-600",
};

export default function OfferDetailPage() {
  const [showApply, setShowApply] = useState(false);

  return (
    <div className="max-w-6xl mx-auto px-8 py-12">
      <Link to="/offers" className="text-sm text-gray-500 hover:text-primary flex items-center gap-1 mb-6">
        <span className="material-symbols-outlined text-[16px]">arrow_back</span> Retour aux offres
      </Link>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Main */}
        <div className="lg:col-span-2 space-y-6">
          <div className="bg-white rounded-2xl border border-gray-100 p-8">
            <div className="flex items-start gap-4">
              <div className={`w-14 h-14 ${offer.logoColor} rounded-xl flex items-center justify-center text-white font-bold text-lg`}>
                {offer.logo}
              </div>
              <div>
                <h1 className="text-2xl font-bold font-[Geist]">{offer.title}</h1>
                <p className="text-gray-500 mt-1">{offer.company} • {offer.domain}</p>
              </div>
            </div>

            <div className="flex flex-wrap gap-3 mt-6">
              <span className="text-xs bg-blue-50 text-primary font-medium px-3 py-1.5 rounded-lg flex items-center gap-1">
                <span className="material-symbols-outlined text-[14px]">work</span> {offer.type === "INTERNSHIP" ? "Stage" : offer.type}
              </span>
              <span className="text-xs bg-gray-50 text-gray-600 font-medium px-3 py-1.5 rounded-lg flex items-center gap-1">
                <span className="material-symbols-outlined text-[14px]">location_on</span> {offer.city} ({offer.locationType === "HYBRID" ? "Hybride" : offer.locationType})
              </span>
              <span className="text-xs bg-gray-50 text-gray-600 font-medium px-3 py-1.5 rounded-lg flex items-center gap-1">
                <span className="material-symbols-outlined text-[14px]">schedule</span> {offer.duration}
              </span>
              <span className="text-xs bg-gray-50 text-gray-600 font-medium px-3 py-1.5 rounded-lg flex items-center gap-1">
                <span className="material-symbols-outlined text-[14px]">school</span> {offer.experienceLevel}
              </span>
            </div>
          </div>

          <div className="bg-white rounded-2xl border border-gray-100 p-8">
            <h3 className="font-semibold mb-4">Description du poste</h3>
            <div className="text-sm text-gray-600 leading-relaxed whitespace-pre-line">{offer.description}</div>
          </div>
        </div>

        {/* Sidebar */}
        <div className="space-y-6">
          <div className="bg-white rounded-2xl border border-gray-100 p-6">
            <button
              onClick={() => setShowApply(true)}
              className="w-full bg-primary text-white py-3 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors flex items-center justify-center gap-2"
            >
              Postuler maintenant <span className="material-symbols-outlined text-[16px]">send</span>
            </button>
            <button className="w-full mt-3 border border-gray-200 py-3 rounded-xl text-sm font-medium hover:bg-gray-50 transition-colors flex items-center justify-center gap-2">
              <span className="material-symbols-outlined text-[16px]">bookmark_border</span> Sauvegarder
            </button>
          </div>

          <div className="bg-white rounded-2xl border border-gray-100 p-6 space-y-4">
            <div className="flex justify-between text-sm">
              <span className="text-gray-500">Date limite</span>
              <span className="font-medium">{offer.deadline}</span>
            </div>
            <div className="flex justify-between text-sm">
              <span className="text-gray-500">Candidatures</span>
              <span className="font-medium">{offer.applicationCount}</span>
            </div>
            <div className="flex justify-between text-sm">
              <span className="text-gray-500">Publié</span>
              <span className="font-medium">{offer.posted}</span>
            </div>
          </div>
        </div>
      </div>

      {/* Apply Modal */}
      {showApply && (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50 p-4" onClick={() => setShowApply(false)}>
          <div className="bg-white rounded-2xl w-full max-w-lg p-8" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-xl font-bold font-[Geist]">Review Application</h2>
              <button onClick={() => setShowApply(false)} className="p-1 hover:bg-gray-100 rounded-lg">
                <span className="material-symbols-outlined">close</span>
              </button>
            </div>
            <p className="text-sm text-gray-500">Applying for</p>
            <h3 className="text-lg font-bold">{offer.title}</h3>
            <p className="text-sm text-primary font-medium">{offer.company}</p>

            <div className="bg-gray-50 rounded-xl p-4 mt-6">
              <div className="flex items-center justify-between mb-3">
                <h4 className="font-semibold text-sm">Attached CV</h4>
                <button className="text-xs text-primary font-medium">Update</button>
              </div>
              <div className="flex items-center gap-3 bg-white rounded-lg p-3 border border-gray-100">
                <span className="material-symbols-outlined text-red-500">picture_as_pdf</span>
                <div>
                  <p className="text-sm font-medium">Mon_CV_2024.pdf</p>
                  <p className="text-xs text-gray-400">1.2 MB</p>
                </div>
              </div>
            </div>

            <div className="mt-6">
              <label className="text-sm font-medium">Message to Recruiter (Optional)</label>
              <textarea placeholder="Briefly explain why you're a good fit..." className="mt-2 w-full h-28 px-4 py-3 border border-gray-200 rounded-xl text-sm resize-none outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" />
            </div>

            <div className="flex items-center justify-end gap-3 mt-6">
              <button onClick={() => setShowApply(false)} className="px-5 py-2.5 text-sm font-medium text-gray-600 hover:bg-gray-100 rounded-xl">Cancel</button>
              <button className="bg-primary text-white px-5 py-2.5 rounded-xl text-sm font-medium hover:bg-primary-dark flex items-center gap-2">
                Submit Application <span className="material-symbols-outlined text-[16px]">send</span>
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
