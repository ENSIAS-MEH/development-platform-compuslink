import { useState } from "react";

const tabs = ["Mes Annonces Marketplace", "Mes Colocations", "Favoris"];

export default function ProfilePage() {
  const [activeTab, setActiveTab] = useState(tabs[0]);

  return (
    <div className="max-w-6xl mx-auto px-8 py-12">
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left - Profile Card */}
        <div className="space-y-6">
          <div className="bg-white rounded-2xl border border-gray-100 p-8 text-center">
            <img
              src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face"
              className="w-28 h-28 rounded-full object-cover mx-auto"
              alt=""
            />
            <h2 className="text-xl font-bold font-[Geist] mt-4">Anas El Mansouri</h2>
            <span className="inline-block mt-2 text-xs bg-green-100 text-green-700 font-semibold px-3 py-1 rounded-full">ÉTUDIANT</span>

            <div className="mt-6 space-y-3 text-left border-t border-gray-100 pt-6">
              <p className="text-sm text-gray-600 flex items-center gap-2">
                <span className="material-symbols-outlined text-[16px] text-gray-400">school</span>
                UM6P, Benguerir
              </p>
              <p className="text-sm text-gray-600 flex items-center gap-2">
                <span className="material-symbols-outlined text-[16px] text-gray-400">location_on</span>
                Benguerir, Maroc
              </p>
              <p className="text-sm text-gray-600 flex items-center gap-2">
                <span className="material-symbols-outlined text-[16px] text-gray-400">calendar_today</span>
                Membre depuis Sept 2023
              </p>
            </div>

            <button className="w-full mt-6 bg-primary text-white py-3 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors">
              Modifier le Profil
            </button>
          </div>

          {/* Stats */}
          <div className="grid grid-cols-2 gap-4">
            <div className="bg-white rounded-2xl border border-gray-100 p-4 text-center">
              <p className="text-2xl font-bold text-primary">12</p>
              <p className="text-xs text-gray-500 uppercase tracking-wide mt-1">Annonces</p>
            </div>
            <div className="bg-white rounded-2xl border border-gray-100 p-4 text-center">
              <p className="text-2xl font-bold text-primary">5</p>
              <p className="text-xs text-gray-500 uppercase tracking-wide mt-1">Colocations</p>
            </div>
          </div>
        </div>

        {/* Right - CV & Listings */}
        <div className="lg:col-span-2 space-y-6">
          {/* CV Section */}
          <div className="bg-white rounded-2xl border border-gray-100 p-6">
            <div className="flex items-center justify-between mb-2">
              <h3 className="text-lg font-bold font-[Geist]">Curriculum Vitae</h3>
              <span className="text-xs bg-gray-100 text-gray-600 font-medium px-2.5 py-1 rounded-full">Privé</span>
            </div>
            <p className="text-sm text-gray-500 mb-6">Téléchargez votre CV pour postuler plus rapidement aux offres de stage et d'emploi sur le campus.</p>

            <div className="border-2 border-dashed border-gray-200 rounded-xl p-8 text-center hover:border-primary/50 transition-colors cursor-pointer">
              <span className="material-symbols-outlined text-gray-400 text-3xl">cloud_upload</span>
              <p className="text-sm font-medium mt-2">Cliquez pour uploader ou glissez votre fichier ici</p>
              <p className="text-xs text-gray-400 mt-1">PDF, DOCX jusqu'à 5MB</p>
            </div>

            <div className="flex items-center justify-between mt-4 bg-gray-50 rounded-xl p-4">
              <div className="flex items-center gap-3">
                <span className="material-symbols-outlined text-red-500">picture_as_pdf</span>
                <div>
                  <p className="text-sm font-medium">Anas_El_Mansouri_CV_2024.pdf</p>
                  <p className="text-xs text-gray-400">Mis à jour il y a 2 jours • 1.2 MB</p>
                </div>
              </div>
              <div className="flex items-center gap-2">
                <button className="p-1.5 hover:bg-gray-200 rounded-lg"><span className="material-symbols-outlined text-[18px] text-gray-500">visibility</span></button>
                <button className="p-1.5 hover:bg-gray-200 rounded-lg"><span className="material-symbols-outlined text-[18px] text-gray-500">delete</span></button>
              </div>
            </div>
          </div>

          {/* Tabs & Listings */}
          <div>
            <div className="flex gap-6 border-b border-gray-100 mb-6">
              {tabs.map((tab) => (
                <button
                  key={tab}
                  onClick={() => setActiveTab(tab)}
                  className={`pb-3 text-sm font-medium transition-colors ${
                    activeTab === tab ? "text-primary border-b-2 border-primary" : "text-gray-500 hover:text-gray-900"
                  }`}
                >
                  {tab}
                </button>
              ))}
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="bg-white rounded-2xl border border-gray-100 overflow-hidden">
                <div className="relative h-40">
                  <img src="https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400&h=200&fit=crop" className="w-full h-full object-cover" alt="" />
                  <span className="absolute top-3 right-3 bg-white/90 text-xs font-semibold px-2 py-1 rounded-md">4500 MAD</span>
                </div>
                <div className="p-4">
                  <div className="flex items-center justify-between">
                    <h4 className="font-semibold text-sm">iPad Pro M1 11" 128GB</h4>
                    <span className="text-xs bg-blue-100 text-blue-700 font-medium px-2 py-0.5 rounded-full">ACTIF</span>
                  </div>
                  <p className="text-xs text-gray-500 mt-1">Parfait état, utilisé uniquement pour la prise de notes. Vendu avec Apple Pencil.</p>
                  <p className="text-xs text-gray-400 mt-3">Publié il y a 3 jours</p>
                </div>
              </div>

              <div className="bg-white rounded-2xl border border-gray-100 overflow-hidden">
                <div className="relative h-40">
                  <img src="https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=200&fit=crop" className="w-full h-full object-cover" alt="" />
                  <span className="absolute top-3 right-3 bg-white/90 text-xs font-semibold px-2 py-1 rounded-md">300 MAD</span>
                </div>
                <div className="p-4">
                  <div className="flex items-center justify-between">
                    <h4 className="font-semibold text-sm">Livres Prépa ECT</h4>
                    <span className="text-xs bg-red-100 text-red-700 font-medium px-2 py-0.5 rounded-full">VENDU</span>
                  </div>
                  <p className="text-xs text-gray-500 mt-1">Lot de livres de management et économie pour préparation aux grandes écoles.</p>
                  <p className="text-xs text-gray-400 mt-3">Publié le 12 Oct</p>
                </div>
              </div>

              {/* Create CTA */}
              <div className="border-2 border-dashed border-gray-200 rounded-2xl flex flex-col items-center justify-center p-8 text-center hover:border-primary/50 transition-colors cursor-pointer min-h-[200px]">
                <span className="material-symbols-outlined text-gray-400 text-3xl">add</span>
                <p className="text-sm font-medium text-gray-600 mt-2">Créer une annonce</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
