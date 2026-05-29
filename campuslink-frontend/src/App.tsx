import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import MainLayout from "./components/MainLayout";
import AuthPage from "./pages/AuthPage";
import HomePage from "./pages/HomePage";
import ColocationPage from "./pages/ColocationPage";
import ColocationDetailPage from "./pages/ColocationDetailPage";
import CreateColocationPage from "./pages/CreateColocationPage";
import MarketplacePage from "./pages/MarketplacePage";
import ItemDetailPage from "./pages/ItemDetailPage";
import CreateItemPage from "./pages/CreateItemPage";
import OffersPage from "./pages/OffersPage";
import OfferDetailPage from "./pages/OfferDetailPage";
import CreateOfferPage from "./pages/CreateOfferPage";
import ProfilePage from "./pages/ProfilePage";
import MyApplicationsPage from "./pages/MyApplicationsPage";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/auth" element={<AuthPage />} />
        <Route element={<MainLayout />}>
          <Route path="/" element={<HomePage />} />
          <Route path="/colocation" element={<ColocationPage />} />
          <Route path="/colocation/:id" element={<ColocationDetailPage />} />
          <Route path="/colocation/create" element={<CreateColocationPage />} />
          <Route path="/marketplace" element={<MarketplacePage />} />
          <Route path="/marketplace/:id" element={<ItemDetailPage />} />
          <Route path="/marketplace/create" element={<CreateItemPage />} />
          <Route path="/offers" element={<OffersPage />} />
          <Route path="/offers/:id" element={<OfferDetailPage />} />
          <Route path="/offers/create" element={<CreateOfferPage />} />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/my-applications" element={<MyApplicationsPage />} />
          <Route path="/events" element={<HomePage />} />
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
