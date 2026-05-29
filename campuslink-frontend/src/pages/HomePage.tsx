import { Link } from "react-router-dom";

export default function HomePage() {
  return (
    <div>
      {/* Hero */}
      <section className="bg-[#f0f4ff] py-16 lg:py-24">
        <div className="max-w-6xl mx-auto px-8 grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
          <div>
            <h1 className="text-4xl lg:text-5xl font-bold font-[Geist] leading-tight">
              Tout ce dont un étudiant a besoin,<br />
              <span className="text-primary">en un seul endroit.</span>
            </h1>
            <p className="text-gray-600 mt-5 text-lg leading-relaxed max-w-lg">
              Rejoignez la plus grande communauté étudiante du Maroc. Trouvez votre colocation idéale à Casablanca, dénichez des offres exclusives à Rabat, ou participez aux événements incontournables de Marrakech.
            </p>
            <div className="flex gap-4 mt-8">
              <Link to="/colocation" className="bg-primary text-white px-6 py-3 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors flex items-center gap-2">
                Commencer l'expérience
                <span className="material-symbols-outlined text-[18px]">arrow_forward</span>
              </Link>
              <Link to="/offers" className="border border-gray-300 px-6 py-3 rounded-xl text-sm font-medium hover:bg-white transition-colors">
                Explorer les offres
              </Link>
            </div>
          </div>
          <div className="relative">
            <img
              src="https://images.unsplash.com/photo-1523240795612-9a054b0db644?w=600&h=400&fit=crop"
              className="rounded-2xl shadow-lg w-full object-cover"
              alt="Students"
            />
            <div className="absolute bottom-4 left-4 bg-white/90 backdrop-blur-sm rounded-xl px-4 py-3 flex items-center gap-3 shadow-sm">
              <span className="material-symbols-outlined text-primary">home</span>
              <div>
                <p className="text-xs text-gray-500">NOUVEAU</p>
                <p className="text-sm font-semibold">Colocation Casablanca</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Featured Section */}
      <section className="max-w-6xl mx-auto px-8 py-16">
        <h2 className="text-2xl font-bold font-[Geist] mb-8">À la une sur CampusLink</h2>
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Large Colocation Card */}
          <Link to="/colocation/1" className="lg:row-span-2 relative rounded-2xl overflow-hidden group cursor-pointer block">
            <img
              src="https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=500&h=700&fit=crop"
              className="w-full h-full min-h-[400px] object-cover group-hover:scale-105 transition-transform duration-300"
              alt=""
            />
            <div className="absolute inset-0 bg-gradient-to-t from-black/70 via-transparent to-transparent" />
            <div className="absolute bottom-6 left-6 right-6 text-white">
              <span className="bg-primary text-xs font-semibold px-2.5 py-1 rounded-md">COLOCATION</span>
              <h3 className="text-xl font-bold mt-3">Chambre lumineuse à Agdal, Rabat</h3>
              <p className="text-sm text-white/80 mt-1">Recherche colocataire pour appartement moderne, 3 chambres, proche tramway et universités.</p>
            </div>
            <button className="absolute bottom-6 right-6 w-10 h-10 bg-white/20 backdrop-blur-sm rounded-full flex items-center justify-center hover:bg-white/40 transition-colors">
              <span className="material-symbols-outlined text-white text-[20px]">arrow_outward</span>
            </button>
          </Link>

          {/* Marketplace Card */}
          <Link to="/marketplace/1" className="bg-white rounded-2xl border border-gray-100 p-6 hover:shadow-md transition-shadow cursor-pointer block">
            <div className="w-10 h-10 bg-blue-50 rounded-xl flex items-center justify-center mb-4">
              <span className="material-symbols-outlined text-primary">computer</span>
            </div>
            <span className="text-xs bg-green-100 text-green-700 font-medium px-2 py-0.5 rounded-full float-right">Top Deal</span>
            <p className="text-xs text-gray-500 uppercase tracking-wide">Marketplace</p>
            <h3 className="font-bold mt-1">MacBook Pro M1 (Étudiant)</h3>
            <p className="text-primary text-xl font-bold mt-2">8 500 DH</p>
          </Link>

          {/* Offer Card */}
          <Link to="/offers/1" className="bg-white rounded-2xl border border-gray-100 p-6 hover:shadow-md transition-shadow cursor-pointer block">
            <div className="w-10 h-10 bg-blue-50 rounded-xl flex items-center justify-center mb-4">
              <span className="material-symbols-outlined text-primary">work</span>
            </div>
            <span className="text-xs bg-blue-100 text-blue-700 font-medium px-2 py-0.5 rounded-full float-right">Actif</span>
            <p className="text-xs text-gray-500 uppercase tracking-wide">Stage PFE</p>
            <h3 className="font-bold mt-1">Développeur Frontend React</h3>
            <p className="text-sm text-gray-500 mt-2 flex items-center gap-1">
              <span className="material-symbols-outlined text-[14px]">location_on</span>
              Casablanca (Hybride)
            </p>
          </Link>

          {/* Event Card */}
          <div className="lg:col-span-2 bg-gradient-to-br from-primary to-blue-700 rounded-2xl p-8 text-white relative overflow-hidden cursor-pointer hover:shadow-lg transition-shadow">
            <span className="bg-white/20 text-xs font-semibold px-2.5 py-1 rounded-md">ÉVÉNEMENT</span>
            <span className="absolute top-6 right-6 text-sm font-medium">Ce weekend</span>
            <h3 className="text-2xl font-bold mt-4">Campus Startup Weekend Marrakech</h3>
            <p className="text-white/80 mt-2">Rejoignez 200 étudiants pour 48h de création et d'innovation.</p>
          </div>
        </div>
      </section>
    </div>
  );
}
