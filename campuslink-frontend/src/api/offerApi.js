import api from './axiosInstance';

// ── Offers ────────────────────────────────────────────────────────────────

/** GET /api/offers?type=&city=&page= */
export const getOffers = (params) => api.get('/api/offers', { params });

/** GET /api/offers/:id */
export const getOfferById = (id) => api.get(`/api/offers/${id}`);

/** POST /api/offers */
export const createOffer = (data) => api.post('/api/offers', data);

/** PATCH /api/offers/:id/status */
export const updateOfferStatus = (id, status) =>
  api.patch(`/api/offers/${id}/status`, { status });

// ── Applications ─────────────────────────────────────────────────────────

/** POST /api/offers/:id/apply */
export const applyToOffer = (id, data) =>
  api.post(`/api/offers/${id}/apply`, data);

/** GET /api/offers/:id/applications  (recruiter only) */
export const getApplications = (offerId) =>
  api.get(`/api/offers/${offerId}/applications`);
