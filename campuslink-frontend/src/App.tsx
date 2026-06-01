import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";
import MainLayout from "./components/MainLayout";
import AuthPage from "./pages/AuthPage";
import AuthCallbackPage from "./pages/AuthCallbackPage";
import HomePage from "./pages/HomePage";
import ColocationPage from "./pages/ColocationPage";
import ColocationDetailPage from "./pages/ColocationDetailPage";
import CreateColocationPage from "./pages/CreateColocationPage";
import MarketplacePage from "./pages/MarketplacePage";
import ItemDetailPage from "./pages/ItemDetailPage";
import CreateItemPage from "./pages/CreateItemPage";
import ProfilePage from "./pages/ProfilePage";
import EventsPage from "./pages/EventsPage";
import EventDetailPage from "./pages/EventDetailPage";
import CreateEventPage from "./pages/CreateEventPage";

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/auth" element={<AuthPage />} />
          <Route path="/auth/callback" element={<AuthCallbackPage />} />
          <Route element={<MainLayout />}>
            <Route path="/" element={<HomePage />} />
            <Route path="/colocation" element={<ColocationPage />} />
            <Route path="/colocation/:id" element={<ColocationDetailPage />} />
            <Route path="/colocation/create" element={<ProtectedRoute><CreateColocationPage /></ProtectedRoute>} />
            <Route path="/marketplace" element={<MarketplacePage />} />
            <Route path="/marketplace/:id" element={<ItemDetailPage />} />
            <Route path="/marketplace/create" element={<ProtectedRoute><CreateItemPage /></ProtectedRoute>} />
            <Route path="/events" element={<EventsPage />} />
            <Route path="/events/create" element={<ProtectedRoute><CreateEventPage /></ProtectedRoute>} />
            <Route path="/events/:id" element={<EventDetailPage />} />
            <Route path="/profile" element={<ProtectedRoute><ProfilePage /></ProtectedRoute>} />
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
