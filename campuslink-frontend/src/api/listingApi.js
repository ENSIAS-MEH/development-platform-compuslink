import api from './axiosInstance';

// ── Marketplace ───────────────────────────────────────────────────────────

/** GET /api/listings/items?city=&category=&page= */
export const getItems = (params) => api.get('/api/listings/items', { params });

/** GET /api/listings/items/:id */
export const getItemById = (id) => api.get(`/api/listings/items/${id}`);

/** POST /api/listings/items — multipart/form-data (with images) */
export const createItem = (formData) =>
  api.post('/api/listings/items', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });

/** PATCH /api/listings/items/:id/status */
export const updateItemStatus = (id, status) =>
  api.patch(`/api/listings/items/${id}/status`, { status });

/** POST /api/listings/items/:id/interest */
export const expressItemInterest = (id) =>
  api.post(`/api/listings/items/${id}/interest`);

// ── Colocation ────────────────────────────────────────────────────────────

/** GET /api/listings/coloc?city=&page= */
export const getColocPosts = (params) =>
  api.get('/api/listings/coloc', { params });

/** GET /api/listings/coloc/:id */
export const getColocById = (id) => api.get(`/api/listings/coloc/${id}`);

/** POST /api/listings/coloc */
export const createColocPost = (data) => api.post('/api/listings/coloc', data);

/** PATCH /api/listings/coloc/:id/status */
export const updateColocStatus = (id, status) =>
  api.patch(`/api/listings/coloc/${id}/status`, { status });

/** POST /api/listings/coloc/:id/interest */
export const expressColocInterest = (id) =>
  api.post(`/api/listings/coloc/${id}/interest`);
