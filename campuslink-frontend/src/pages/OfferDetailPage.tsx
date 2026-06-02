import { useState, useEffect, useRef } from "react";
import { Link, useParams, useNavigate } from "react-router-dom";
import api from "../services/api";
import { useAuth } from "../context/AuthContext";

interface Offer {
  id: string;
  posterId: string;
  title: string;
  company: string;
  type: string;
  city: string;
  locationType: string;
  experienceLevel: string;
  duration: string;
  domain: string;
  description: string;
  deadline: string;
  status: string;
  createdAt: string;
  applicationCount: number;
}

interface Application {
  id: string;
  applicantId: string;
  applicantEmail: string;
  applicantName: string;
  cvUrlSnapshot: string;
  message: string;
  status: string;
  appliedAt: string;
}

export default function OfferDetailPage() {
  const { id } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [offer, setOffer] = useState<Offer | null>(null);
  const [loading, setLoading] = useState(true);
  const [showApply, setShowApply] = useState(false);
  const [message, setMessage] = useState("");
  const [applying, setApplying] = useState(false);
  const [applied, setApplied] = useState(false);
  const [cvUrl, setCvUrl] = useState<string | null>(null);
  const [uploadingCv, setUploadingCv] = useState(false);
  const [applications, setApplications] = useState<Application[]>([]);
  const cvInputRef = useRef<HTMLInputElement>(null);

  const isOwner = offer && user && offer.posterId === user.userId;

  useEffect(() => {
    api.get(`/offers/${id}`).then(({ data }) => setOffer(data)).catch(console.error).finally(() => setLoading(false));
  }, [id]);

  useEffect(() => {
    if (isOwner) {
      api.get(`/offers/${id}/applications`).then(({ data }) => setApplications(data)).catch(console.error);
    }
  }, [isOwner, id]);

  const openApplyModal = async () => {
    if (!user) { navigate("/auth"); return; }
    try {
      const { data } = await api.get("/me/profile");
      setCvUrl(data.cvUrl);
    } catch {}
    setShowApply(true);
  };

  const [showCloseModal, setShowCloseModal] = useState(false);

  const handleClose = async () => {
    try {
      const { data } = await api.patch(`/offers/${id}/close`);
      setOffer(data);
      setShowCloseModal(false);
    } catch (err: any) {
      alert(err.response?.data?.message || "Erreur.");
    }
  };

  const handleCvUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setUploadingCv(true);
    try {
      const formData = new FormData();
      formData.append("file", file);
      const { data } = await api.post("/me/profile/cv", formData);
      setCvUrl(data.cvUrl);
    } catch {
      alert("Erreur lors de l'upload du CV.");
    } finally {
      setUploadingCv(false);
    }
  };

  const handleApply = async () => {
    if (!cvUrl) { alert("Veuillez d'abord uploader un CV."); return; }
    setApplying(true);
    try {
      await api.post(`/offers/${id}/applications`, { message: message || undefined });
      setApplied(true);
      setShowApply(false);
    } catch (err: any) {
      alert(err.response?.data?.message || "Erreur lors de la candidature.");
    } finally {
      setApplying(false);
    }
  };

  const timeAgo = (date: string) => {
    const days = Math.floor((Date.now() - new Date(date).getTime()) / 86400000);
    if (days === 0) return "Aujourd'hui";
    if (days === 1) return "Hier";
    return `il y a ${days} jours`;
  };

  const cvFileName = cvUrl ? decodeURIComponent(cvUrl.split("/").pop() || "CV") : null;

  if (loading) return <p className="text-center text-gray-400 py-20">Chargement...</p>;
  if (!offer) return <p className="text-center text-gray-500 py-20">Offre introuvable.</p>;

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
              <div className="w-14 h-14 bg-blue-600 rounded-xl flex items-center justify-center text-white font-bold text-lg">
                {offer.company?.charAt(0) || "?"}
              </div>
              <div>
                <h1 className="text-2xl font-bold font-[Geist]">{offer.title}</h1>
                <p className="text-gray-500 mt-1">{offer.company} {offer.domain && `• ${offer.domain}`}</p>
              </div>
            </div>

            <div className="flex flex-wrap gap-3 mt-6">
              <span className="text-xs bg-blue-50 text-primary font-medium px-3 py-1.5 rounded-lg flex items-center gap-1">
                <span className="material-symbols-outlined text-[14px]">work</span> {offer.type}
              </span>
              {offer.city && (
                <span className="text-xs bg-gray-50 text-gray-600 font-medium px-3 py-1.5 rounded-lg flex items-center gap-1">
                  <span className="material-symbols-outlined text-[14px]">location_on</span> {offer.city} ({offer.locationType?.replace("_", " ")})
                </span>
              )}
              {offer.duration && (
                <span className="text-xs bg-gray-50 text-gray-600 font-medium px-3 py-1.5 rounded-lg flex items-center gap-1">
                  <span className="material-symbols-outlined text-[14px]">schedule</span> {offer.duration}
                </span>
              )}
              {offer.experienceLevel && (
                <span className="text-xs bg-gray-50 text-gray-600 font-medium px-3 py-1.5 rounded-lg flex items-center gap-1">
                  <span className="material-symbols-outlined text-[14px]">school</span> {offer.experienceLevel}
                </span>
              )}
            </div>
          </div>

          <div className="bg-white rounded-2xl border border-gray-100 p-8">
            <h3 className="font-semibold mb-4">Description du poste</h3>
            <div className="text-sm text-gray-600 leading-relaxed whitespace-pre-line">{offer.description}</div>
          </div>

          {/* Owner: Applications list */}
          {isOwner && (
            <div className="bg-white rounded-2xl border border-gray-100 p-8">
              <h3 className="font-semibold mb-4">Candidatures ({applications.length})</h3>
              {applications.length === 0 ? (
                <p className="text-sm text-gray-400">Aucune candidature reçue pour le moment.</p>
              ) : (
                <div className="space-y-4">
                  {applications.map((app) => (
                    <div key={app.id} className="flex items-center justify-between border border-gray-100 rounded-xl p-4">
                      <div className="flex items-center gap-4">
                        <div className="w-10 h-10 bg-gray-100 rounded-full flex items-center justify-center">
                          <span className="material-symbols-outlined text-gray-500 text-[20px]">person</span>
                        </div>
                        <div>
                          <p className="text-sm font-medium">{app.applicantName || "Sans nom"}</p>
                          <p className="text-xs text-gray-500">{app.applicantEmail}</p>
                          {app.message && <p className="text-xs text-gray-400 line-clamp-1 mt-0.5">{app.message}</p>}
                          <p className="text-xs text-gray-400 mt-1">{new Date(app.appliedAt).toLocaleDateString("fr-FR", { day: "numeric", month: "long", year: "numeric" })}</p>
                        </div>
                      </div>
                      <div className="flex items-center gap-3">
                        <span className={`text-xs font-medium px-2.5 py-1 rounded-full ${
                          app.status === "ACCEPTED" ? "bg-green-100 text-green-700" :
                          app.status === "REJECTED" ? "bg-red-100 text-red-700" :
                          app.status === "SEEN" ? "bg-blue-100 text-blue-700" :
                          "bg-yellow-100 text-yellow-700"
                        }`}>{app.status}</span>
                        <a href={app.cvUrlSnapshot} target="_blank" rel="noopener noreferrer" className="flex items-center gap-1 text-primary text-sm font-medium hover:underline">
                          <span className="material-symbols-outlined text-[16px]">picture_as_pdf</span> CV
                        </a>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>

        {/* Sidebar */}
        <div className="space-y-6">
          {!isOwner && (
            <div className="bg-white rounded-2xl border border-gray-100 p-6">
              {offer.status === "CLOSED" ? (
                <p className="text-center text-gray-500 font-medium py-3 flex items-center justify-center gap-2">
                  <span className="material-symbols-outlined text-[18px]">lock</span> Offre clôturée
                </p>
              ) : applied ? (
                <p className="text-center text-green-600 font-medium py-3">✓ Candidature envoyée</p>
              ) : (
                <button
                  onClick={openApplyModal}
                  className="w-full bg-primary text-white py-3 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors flex items-center justify-center gap-2"
                >
                  Postuler maintenant <span className="material-symbols-outlined text-[16px]">send</span>
                </button>
              )}
            </div>
          )}

          {isOwner && (
            <div className="bg-white rounded-2xl border border-gray-100 p-6">
              {offer.status === "CLOSED" ? (
                <p className="text-center text-gray-500 font-medium py-3 flex items-center justify-center gap-2">
                  <span className="material-symbols-outlined text-[18px]">lock</span> Offre clôturée
                </p>
              ) : (
                <button
                  onClick={() => setShowCloseModal(true)}
                  className="w-full bg-red-500 text-white py-3 rounded-xl text-sm font-medium hover:bg-red-600 transition-colors flex items-center justify-center gap-2"
                >
                  <span className="material-symbols-outlined text-[16px]">close</span> Clôturer l'offre
                </button>
              )}
            </div>
          )}

          <div className="bg-white rounded-2xl border border-gray-100 p-6 space-y-4">
            {offer.deadline && (
              <div className="flex justify-between text-sm">
                <span className="text-gray-500">Date limite</span>
                <span className="font-medium">{offer.deadline}</span>
              </div>
            )}
            {offer.applicationCount >= 0 && (
              <div className="flex justify-between text-sm">
                <span className="text-gray-500">Candidatures</span>
                <span className="font-medium">{offer.applicationCount}</span>
              </div>
            )}
            <div className="flex justify-between text-sm">
              <span className="text-gray-500">Publié</span>
              <span className="font-medium">{timeAgo(offer.createdAt)}</span>
            </div>
          </div>
        </div>
      </div>

      {/* Close Offer Confirmation Modal */}
      {showCloseModal && (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50 p-4" onClick={() => setShowCloseModal(false)}>
          <div className="bg-white rounded-2xl w-full max-w-md p-8 text-center" onClick={(e) => e.stopPropagation()}>
            <div className="w-14 h-14 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-5">
              <span className="material-symbols-outlined text-red-500 text-[28px]">warning</span>
            </div>
            <h2 className="text-xl font-bold font-[Geist]">Clôturer cette offre ?</h2>
            <p className="text-sm text-gray-500 mt-3">Cette action est irréversible. Les candidats ne pourront plus postuler à cette offre.</p>
            <div className="flex gap-3 mt-8">
              <button onClick={() => setShowCloseModal(false)} className="flex-1 border border-gray-200 py-3 rounded-xl text-sm font-medium hover:bg-gray-50 transition-colors">
                Annuler
              </button>
              <button onClick={handleClose} className="flex-1 bg-red-500 text-white py-3 rounded-xl text-sm font-medium hover:bg-red-600 transition-colors">
                Oui, clôturer
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Apply Modal (only for non-owners) */}
      {showApply && !isOwner && (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50 p-4" onClick={() => setShowApply(false)}>
          <div className="bg-white rounded-2xl w-full max-w-lg p-8" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-xl font-bold font-[Geist]">Postuler</h2>
              <button onClick={() => setShowApply(false)} className="p-1 hover:bg-gray-100 rounded-lg">
                <span className="material-symbols-outlined">close</span>
              </button>
            </div>
            <p className="text-sm text-gray-500">Candidature pour</p>
            <h3 className="text-lg font-bold">{offer.title}</h3>
            <p className="text-sm text-primary font-medium">{offer.company}</p>

            {/* CV Section */}
            <div className="bg-gray-50 rounded-xl p-4 mt-6">
              <div className="flex items-center justify-between mb-3">
                <h4 className="font-semibold text-sm">CV attaché</h4>
                <button onClick={() => cvInputRef.current?.click()} className="text-xs text-primary font-medium flex items-center gap-1" disabled={uploadingCv}>
                  <span className="material-symbols-outlined text-[14px]">{cvUrl ? "edit" : "upload"}</span>
                  {uploadingCv ? "Upload..." : cvUrl ? "Mettre à jour" : "Uploader"}
                </button>
                <input ref={cvInputRef} type="file" accept=".pdf,.doc,.docx" className="hidden" onChange={handleCvUpload} />
              </div>
              {cvUrl ? (
                <a href={cvUrl} target="_blank" rel="noopener noreferrer" className="flex items-center gap-3 bg-white rounded-lg p-3 border border-gray-100 hover:border-primary transition-colors">
                  <span className="material-symbols-outlined text-red-500">picture_as_pdf</span>
                  <div className="min-w-0">
                    <p className="text-sm font-medium truncate">{cvFileName}</p>
                    <p className="text-xs text-gray-400">Cliquez pour voir</p>
                  </div>
                </a>
              ) : (
                <div className="flex items-center gap-3 bg-white rounded-lg p-3 border border-dashed border-gray-300 cursor-pointer" onClick={() => cvInputRef.current?.click()}>
                  <span className="material-symbols-outlined text-gray-400">upload_file</span>
                  <p className="text-sm text-gray-500">Aucun CV uploadé. Cliquez pour en ajouter un.</p>
                </div>
              )}
            </div>

            <div className="mt-6">
              <label className="text-sm font-medium">Message au recruteur (Optionnel)</label>
              <textarea placeholder="Expliquez brièvement pourquoi vous êtes un bon candidat..." className="mt-2 w-full h-28 px-4 py-3 border border-gray-200 rounded-xl text-sm resize-none outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary" value={message} onChange={(e) => setMessage(e.target.value)} maxLength={1000} />
            </div>

            <div className="flex items-center justify-end gap-3 mt-6">
              <button onClick={() => setShowApply(false)} className="px-5 py-2.5 text-sm font-medium text-gray-600 hover:bg-gray-100 rounded-xl">Annuler</button>
              <button onClick={handleApply} disabled={applying || !cvUrl} className="bg-primary text-white px-5 py-2.5 rounded-xl text-sm font-medium hover:bg-primary-dark flex items-center gap-2 disabled:opacity-50">
                {applying ? "Envoi..." : "Envoyer"} <span className="material-symbols-outlined text-[16px]">send</span>
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
