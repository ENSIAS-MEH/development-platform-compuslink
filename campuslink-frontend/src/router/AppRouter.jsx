import { BrowserRouter, Routes, Route } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';

// ── Pages ─────────────────────────────────────────────────────────────────
import LoginPage from '../pages/auth/LoginPage';
import RegisterPage from '../pages/auth/RegisterPage';
import ProfilePage from '../pages/profile/ProfilePage';
import MarketplacePage from '../pages/marketplace/MarketplacePage';
import ItemDetailPage from '../pages/marketplace/ItemDetailPage';
import CreateItemPage from '../pages/marketplace/CreateItemPage';
import ColocPage from '../pages/colocation/ColocPage';
import ColocDetailPage from '../pages/colocation/ColocDetailPage';
import CreateColocPage from '../pages/colocation/CreateColocPage';
import OffersPage from '../pages/offers/OffersPage';
import OfferDetailPage from '../pages/offers/OfferDetailPage';
import CreateOfferPage from '../pages/offers/CreateOfferPage';

export default function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        {/* ── Public ──────────────────────────────────────────── */}
        <Route path="/login"      element={<LoginPage />} />
        <Route path="/register"   element={<RegisterPage />} />

        <Route path="/marketplace"      element={<MarketplacePage />} />
        <Route path="/marketplace/:id"  element={<ItemDetailPage />} />
        <Route path="/colocation"       element={<ColocPage />} />
        <Route path="/colocation/:id"   element={<ColocDetailPage />} />
        <Route path="/offers"           element={<OffersPage />} />
        <Route path="/offers/:id"       element={<OfferDetailPage />} />

        {/* ── Protected: any logged-in user ───────────────────── */}
        <Route path="/profile" element={
          <ProtectedRoute><ProfilePage /></ProtectedRoute>
        } />

        {/* ── Protected: student only ─────────────────────────── */}
        <Route path="/marketplace/new" element={
          <ProtectedRoute roles={['STUDENT']}>
            <CreateItemPage />
          </ProtectedRoute>
        } />
        <Route path="/colocation/new" element={
          <ProtectedRoute roles={['STUDENT']}>
            <CreateColocPage />
          </ProtectedRoute>
        } />

        {/* ── Protected: student or recruiter ─────────────────── */}
        <Route path="/offers/new" element={
          <ProtectedRoute roles={['STUDENT', 'RECRUITER']}>
            <CreateOfferPage />
          </ProtectedRoute>
        } />

        {/* ── Default redirect ─────────────────────────────────── */}
        <Route path="*" element={<MarketplacePage />} />
      </Routes>
    </BrowserRouter>
  );
}
