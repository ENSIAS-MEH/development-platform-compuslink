import { useState } from "react";
import { Link } from "react-router-dom";

const offers = [
  { id: "1", title: "Software Engineering Intern", company: "TechMaroc", type: "INTERNSHIP", city: "Casablanca (Hybride)", description: "Join our core platform team to build scalable microservices. You'll be working with React, Node.js...", posted: "il y a 5 jours", logo: "M", logoColor: "bg-blue-600" },
  { id: "2", title: "Marketing & Comms Intern", company: "BrandSphere Media", type: "INTERNSHIP", city: "Rabat", description: "Help us create engaging digital campaigns, manage social media channels, and craft compelling content for a divers...", posted: "il y a 2 jours", logo: "B", logoColor: "bg-purple-600", badge: "HIRING" },
];

export default function OffersPage() {
  const [showModal, setShowModal] = useState(false);

  return (
    <div className="max-w-6xl mx-auto px-8 py-12">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-4xl font-bold font-[Geist]">Explore Opportunities</h1>
          <p className="text-gray-500 mt-3">Discover top internships and job offers from leading Moroccan companies tailored for ambitious students.</p>
        </div>
        <Link to="/offers/create" className="bg-primary text-white px-5 py-2.5 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors whitespace-nowrap">
          + Publier une offre
        </Link>
      </div>

      {/* Search & Filters */}
      <div className="mt-8 bg-white rounded-2xl border border-gray-100 p-2 flex flex-wrap gap-2">
        <div className="flex-1 min-w-[200px] flex items-center gap-2 px-4">
          <span className="material-symbols-outlined text-gray-400 text-[20px]">search</span>
          <input type="text" placeholder="Search jobs, companies, skills..." className="w-full py-3 text-sm outline-none" />
        </div>
        <select className="px-4 py-3 text-sm text-gray-600 bg-gray-50 rounded-xl border-none outline-none">
          <option>Type</option>
          <option>Internship</option>
          <option>PFE</option>
          <option>Job</option>
        </select>
        <select className="px-4 py-3 text-sm text-gray-600 bg-gray-50 rounded-xl border-none outline-none">
          <option>Location</option>
          <option>Remote</option>
          <option>On-site</option>
          <option>Hybrid</option>
        </select>
      </div>

      {/* Offers List */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-10">
        {offers.map((offer) => (
          <Link to={`/offers/${offer.id}`} key={offer.id} className="bg-white rounded-2xl border border-gray-100 p-6 hover:shadow-md transition-shadow block">
            <div className="flex items-start gap-4">
              <div className={`w-12 h-12 ${offer.logoColor} rounded-xl flex items-center justify-center text-white font-bold`}>
                {offer.logo}
              </div>
              <div className="flex-1">
                <div className="flex items-center justify-between">
                  <h3 className="text-lg font-bold font-[Geist]">{offer.title}</h3>
                  {offer.badge && <span className="text-xs bg-green-100 text-green-700 font-medium px-2.5 py-1 rounded-full">{offer.badge}</span>}
                </div>
                <p className="text-sm text-gray-500">{offer.company}</p>
              </div>
            </div>

            <p className="text-sm text-gray-500 flex items-center gap-1 mt-3">
              <span className="material-symbols-outlined text-[14px]">location_on</span>
              {offer.city}
            </p>

            <p className="text-sm text-gray-600 mt-3 line-clamp-2">{offer.description}</p>

            <div className="flex items-center justify-between mt-5">
              <span className="text-xs text-gray-400 flex items-center gap-1">
                <span className="material-symbols-outlined text-[14px]">schedule</span>
                {offer.posted}
              </span>
              <button
                onClick={() => setShowModal(true)}
                className="bg-primary text-white px-5 py-2 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors"
              >
                Review CV & Apply
              </button>
            </div>
          </Link>
        ))}
      </div>

      {/* Application Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50 p-4" onClick={() => setShowModal(false)}>
          <div className="bg-white rounded-2xl w-full max-w-lg p-8" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-xl font-bold font-[Geist]">Review Application</h2>
              <button onClick={() => setShowModal(false)} className="p-1 hover:bg-gray-100 rounded-lg">
                <span className="material-symbols-outlined">close</span>
              </button>
            </div>

            <div className="mb-6">
              <p className="text-sm text-gray-500">Applying for</p>
              <h3 className="text-lg font-bold">Marketing & Comms Intern</h3>
              <p className="text-sm text-primary font-medium">BrandSphere Media</p>
            </div>

            <div className="bg-gray-50 rounded-xl p-4 mb-6">
              <div className="flex items-center justify-between mb-3">
                <h4 className="font-semibold text-sm">Attached CV</h4>
                <button className="text-xs text-primary font-medium flex items-center gap-1">
                  <span className="material-symbols-outlined text-[14px]">edit</span>
                  Update
                </button>
              </div>
              <div className="flex items-center gap-3 bg-white rounded-lg p-3 border border-gray-100">
                <span className="material-symbols-outlined text-red-500">picture_as_pdf</span>
                <div>
                  <p className="text-sm font-medium">El_Amrani_Youssef_CV_2024.pdf</p>
                  <p className="text-xs text-gray-400">Updated 2 days ago • 1.2 MB</p>
                </div>
              </div>
            </div>

            <div className="mb-6">
              <label className="text-sm font-medium">Message to Recruiter (Optional)</label>
              <textarea
                placeholder="Briefly explain why you're a good fit..."
                className="mt-2 w-full h-32 px-4 py-3 border border-gray-200 rounded-xl text-sm resize-none outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
              />
            </div>

            <div className="flex items-center justify-end gap-3">
              <button onClick={() => setShowModal(false)} className="px-5 py-2.5 text-sm font-medium text-gray-600 hover:bg-gray-100 rounded-xl transition-colors">
                Cancel
              </button>
              <button className="bg-primary text-white px-5 py-2.5 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors flex items-center gap-2">
                Submit Application
                <span className="material-symbols-outlined text-[16px]">send</span>
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
