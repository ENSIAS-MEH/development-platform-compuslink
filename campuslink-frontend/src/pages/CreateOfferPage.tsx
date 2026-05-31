import { Link } from "react-router-dom";

export default function CreateOfferPage() {
  return (
    <div className="max-w-3xl mx-auto px-8 py-12">
      <Link to="/offers" className="text-sm text-gray-500 hover:text-primary flex items-center gap-1 mb-6">
        <span className="material-symbols-outlined text-[16px]">arrow_back</span> Retour
      </Link>

      <h1 className="text-3xl font-bold font-[Geist]">Publier une offre</h1>
      <p className="text-gray-500 mt-2">Partagez une opportunité avec la communauté étudiante.</p>

      <form className="mt-8 space-y-6" onSubmit={(e) => e.preventDefault()}>
        <div className="bg-white rounded-2xl border border-gray-100 p-6 space-y-5">
          <h3 className="font-semibold">Type d'offre</h3>
          <div className="grid grid-cols-3 gap-3">
            {["Stage", "PFE", "Emploi"].map((type) => (
              <label key={type} className="flex items-center justify-center gap-2 border border-gray-200 rounded-xl py-3 cursor-pointer hover:border-primary hover:bg-blue-50 transition-colors has-[:checked]:border-primary has-[:checked]:bg-blue-50">
                <input type="radio" name="type" className="sr-only" />
                <span className="text-sm font-medium">{type}</span>
              </label>
            ))}
          </div>
        </div>

        <div className="bg-white rounded-2xl border border-gray-100 p-6 space-y-5">
          <h3 className="font-semibold">Détails de l'offre</h3>

          <div>
            <label className="text-sm font-medium">Titre du poste *</label>
            <input type="text" placeholder="Ex: Développeur Frontend React" className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="text-sm font-medium">Entreprise</label>
              <input type="text" placeholder="Nom de l'entreprise" className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" />
            </div>
            <div>
              <label className="text-sm font-medium">Domaine</label>
              <input type="text" placeholder="Ex: Fintech, IA, Web..." className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="text-sm font-medium">Ville</label>
              <input type="text" placeholder="Casablanca" className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" />
            </div>
            <div>
              <label className="text-sm font-medium">Mode de travail *</label>
              <select className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary">
                <option>Sur site</option><option>Remote</option><option>Hybride</option>
              </select>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="text-sm font-medium">Niveau d'expérience</label>
              <select className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary">
                <option value="">Non spécifié</option><option>Étudiant</option><option>Junior</option><option>Senior</option>
              </select>
            </div>
            <div>
              <label className="text-sm font-medium">Durée</label>
              <input type="text" placeholder="Ex: 6 mois" className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" />
            </div>
          </div>

          <div>
            <label className="text-sm font-medium">Description *</label>
            <textarea placeholder="Décrivez le poste, les responsabilités, le profil recherché..." className="mt-1.5 w-full h-36 px-4 py-3 border border-gray-200 rounded-xl text-sm resize-none outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" />
          </div>

          <div>
            <label className="text-sm font-medium">Date limite de candidature</label>
            <input type="date" className="mt-1.5 w-full px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" />
          </div>
        </div>

        <div className="flex gap-4">
          <Link to="/offers" className="flex-1 border border-gray-200 py-3 rounded-xl text-sm font-medium text-center hover:bg-gray-50">Annuler</Link>
          <button type="submit" className="flex-1 bg-primary text-white py-3 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors">Publier l'offre</button>
        </div>
      </form>
    </div>
  );
}
