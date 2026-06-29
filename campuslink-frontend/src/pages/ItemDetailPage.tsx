import { useState, useEffect } from "react";
import { Link, useParams, useNavigate } from "react-router-dom";
import api from "../services/api";
import { useAuth } from "../context/AuthContext";

const PLACEHOLDER_IMAGE = "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 400 300'%3E%3Crect fill='%23e5e7eb' width='400' height='300'/%3E%3Ctext x='50%' y='50%' font-size='20' fill='%236b7280' text-anchor='middle' dy='.3em'%3ENo Image Available%3C/text%3E%3C/svg%3E";

const conditionMap: Record<string, string> = {
  "NEW": "Neuf",
  "LIKE_NEW": "Très bon état",
  "GOOD": "Bon état",
  "FAIR": "Correct",
};

const getConditionLabel = (condition: string): string => {
  return conditionMap[condition] || condition;
};

interface Item {
  id: string;
  title: string;
  price: number;
  condition: string;
  city: string;
  category: string;
  status: string;
  description: string;
  images: Array<{ id: string; url: string }>;
  sellerId: string;
  sellerName: string;
  createdAt: string;
}

export default function ItemDetailPage() {
  const { id } = useParams<{ id: string }>();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [item, setItem] = useState<Item | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [selectedImage, setSelectedImage] = useState(0);
  const [saved, setSaved] = useState(false);
  const [savingItem, setSavingItem] = useState(false);
  const [showReportModal, setShowReportModal] = useState(false);
  const [reportingItem, setReportingItem] = useState(false);

  const reportReasons = [
    { value: "INAPPROPRIATE", label: "Contenu inapproprié" },
    { value: "SPAM", label: "Spam" },
    { value: "FAKE", label: "Faux article" },
    { value: "ALREADY_SOLD", label: "Déjà vendu" },
    { value: "OTHER", label: "Autre" }
  ];

  useEffect(() => {
    if (!id) return;

    api
      .get<Item>(`/items/${id}`)
      .then(({ data }) => {
        setItem(data);
        // Check if item is saved (only if user is logged in)
        if (user) {
          return api.get(`/saved?targetType=ITEM`).then(({ data: savedItems }) => {
            const isSaved = savedItems.some((s: any) => s.targetId === id);
            setSaved(isSaved);
          });
        }
      })
      .catch((err) => {
        if (err.response?.status === 401) {
          window.location.href = "/auth";
          return;
        }
        setError(err.response?.data?.message || "Article non trouvé");
      })
      .finally(() => setLoading(false));
  }, [id, user]);

  const handleSaveItem = async () => {
    if (!user || !item) return;

    setSavingItem(true);
    try {
      if (saved) {
        await api.delete(`/saved?targetType=ITEM&targetId=${item.id}`);
        setSaved(false);
      } else {
        await api.post(`/saved`, { targetType: "ITEM", targetId: item.id });
        setSaved(true);
      }
    } catch (err) {
      console.error("Erreur lors de la sauvegarde", err);
    } finally {
      setSavingItem(false);
    }
  };

  const handleReportItem = (reason: string) => {
    if (!user || !item) return;

    setReportingItem(true);
    api.post(`/reports`, {
      targetType: "ITEM",
      targetId: item.id,
      reason: reason,
      details: ""
    })
      .then(() => {
        alert("Merci de votre signalement");
        setShowReportModal(false);
      })
      .catch((err) => {
        console.error("Erreur lors du signalement", err);
        alert("Erreur lors du signalement");
      })
      .finally(() => {
        setReportingItem(false);
      });
  };

  if (loading) {
    return <div className="max-w-6xl mx-auto px-8 py-12 text-center">Chargement...</div>;
  }

  if (error || !item) {
    return (
      <div className="max-w-6xl mx-auto px-8 py-12 text-center">
        <p className="text-red-600">{error || "Article non trouvé"}</p>
        <Link to="/marketplace" className="text-primary hover:underline mt-4 inline-block">
          Retour au marketplace
        </Link>
      </div>
    );
  }

  const imageUrls = item.images?.map(img => img.url) || [];
  const displayImages = imageUrls.length > 0 ? imageUrls : ["data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 400 300'%3E%3Crect fill='%23e5e7eb' width='400' height='300'/%3E%3Ctext x='50%' y='50%' font-size='20' fill='%236b7280' text-anchor='middle' dy='.3em'%3ENo Image Available%3C/text%3E%3C/svg%3E"];

  return (
    <div className="max-w-6xl mx-auto px-8 py-12">
      <Link to="/marketplace" className="text-sm text-gray-500 hover:text-primary flex items-center gap-1 mb-6">
        <span className="material-symbols-outlined text-[16px]">arrow_back</span> Retour au marketplace
      </Link>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Image Gallery */}
        <div className="space-y-3">
          <div className="rounded-2xl overflow-hidden h-80 bg-gray-100">
            <img
              src={displayImages[selectedImage]}
              className="w-full h-full object-cover"
              alt=""
              onError={(e) => {
                (e.target as HTMLImageElement).src = PLACEHOLDER_IMAGE;
              }}
            />
          </div>
          {displayImages.length > 1 && (
            <div className="grid grid-cols-3 gap-3">
              {displayImages.map((img, i) => (
                <button
                  key={i}
                  onClick={() => setSelectedImage(i)}
                  className={`rounded-xl overflow-hidden h-24 border-2 transition-colors ${selectedImage === i ? "border-primary" : "border-transparent"}`}
                >
                  <img
                    src={img}
                    className="w-full h-full object-cover"
                    alt=""
                    onError={(e) => {
                      (e.target as HTMLImageElement).src = PLACEHOLDER_IMAGE;
                    }}
                  />
                </button>
              ))}
            </div>
          )}
        </div>

        {/* Details */}
        <div className="space-y-6">
          <div>
            <div className="flex items-center gap-3">
              <span className="text-xs bg-green-100 text-green-700 font-medium px-2.5 py-1 rounded-full">{getConditionLabel(item.condition)}</span>
              <span className="text-xs bg-gray-100 text-gray-600 font-medium px-2.5 py-1 rounded-full">{item.category}</span>
            </div>
            <h1 className="text-2xl font-bold font-[Geist] mt-3">{item.title}</h1>
            <p className="text-3xl font-bold text-primary mt-2">{item.price.toLocaleString()} MAD</p>
            <p className="text-sm text-gray-500 flex items-center gap-1 mt-2">
              <span className="material-symbols-outlined text-[14px]">location_on</span> {item.city}
            </p>
          </div>

          <div className="bg-white rounded-2xl border border-gray-100 p-6">
            <h3 className="font-semibold mb-3">Description</h3>
            <p className="text-sm text-gray-600 leading-relaxed">{item.description}</p>
          </div>

          <div className="bg-white rounded-2xl border border-gray-100 p-6">
            <div className="flex items-center gap-3">
              <div className="w-12 h-12 rounded-full bg-primary text-white flex items-center justify-center font-bold">
                {item.sellerName.charAt(0).toUpperCase()}
              </div>
              <div>
                <p className="font-semibold">{item.sellerName}</p>
              </div>
            </div>
            {user?.userId === item.sellerId ? (
              <div className="w-full mt-5 bg-gray-100 text-gray-600 py-3 rounded-xl text-sm font-medium flex items-center justify-center gap-2">
                <span className="material-symbols-outlined text-[16px]">check_circle</span> C'est votre article
              </div>
            ) : (
              <button
                onClick={() => {
                  if (!user) {
                    window.location.href = "/auth";
                    return;
                  }
                  navigate("/messages", { state: { sellerId: item.sellerId, itemName: item.title } });
                }}
                className="w-full mt-5 bg-primary text-white py-3 rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors flex items-center justify-center gap-2"
              >
                <span className="material-symbols-outlined text-[16px]">chat_bubble_outline</span> Contacter le vendeur
              </button>
            )}
          </div>

          <div className="flex gap-3">
            <button
              onClick={handleSaveItem}
              disabled={savingItem || !user}
              className={`flex-1 py-3 rounded-xl text-sm font-medium flex items-center justify-center gap-2 disabled:opacity-50 transition-colors ${
                saved
                  ? "bg-red-500 text-white hover:bg-red-600"
                  : "border border-gray-200 hover:bg-gray-50"
              }`}
            >
              <span className="material-symbols-outlined text-[16px]">{saved ? "favorite" : "favorite_border"}</span>
              {saved ? "Sauvegardé" : "Sauvegarder"}
            </button>
            <button
              onClick={() => setShowReportModal(true)}
              disabled={!user}
              className="flex-1 border border-gray-200 py-3 rounded-xl text-sm font-medium hover:bg-gray-50 flex items-center justify-center gap-2 disabled:opacity-50 transition-colors"
            >
              <span className="material-symbols-outlined text-[16px]">flag</span> Signaler
            </button>
          </div>

          {showReportModal && (
            <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
              <div className="bg-white rounded-2xl p-6 max-w-md w-full mx-4">
                <h3 className="text-lg font-semibold mb-4">Signaler cet article</h3>
                <p className="text-sm text-gray-600 mb-6">Sélectionnez la raison du signalement:</p>

                <div className="space-y-2 mb-6">
                  {reportReasons.map((reason) => (
                    <button
                      key={reason.value}
                      onClick={() => handleReportItem(reason.value)}
                      disabled={reportingItem}
                      className="w-full text-left px-4 py-3 border border-gray-200 rounded-xl hover:bg-gray-50 transition-colors disabled:opacity-50 text-sm font-medium"
                    >
                      {reason.label}
                    </button>
                  ))}
                </div>

                <button
                  onClick={() => setShowReportModal(false)}
                  disabled={reportingItem}
                  className="w-full px-4 py-3 border border-gray-200 rounded-xl text-sm font-medium hover:bg-gray-50 transition-colors disabled:opacity-50"
                >
                  Annuler
                </button>
              </div>
            </div>
          )}

          <p className="text-xs text-gray-400">
            Publié {new Date(item.createdAt).toLocaleDateString("fr-FR")}
          </p>
        </div>
      </div>
    </div>
  );
}
