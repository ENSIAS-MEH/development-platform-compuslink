import api from './axiosInstance';

// ── Auth endpoints ────────────────────────────────────────────────────────

/** POST /api/auth/register */
export const register = (data) => api.post('/api/auth/register', data);

/** POST /api/auth/login → returns { token, user } */
export const login = (data) => api.post('/api/auth/login', data);

/** GET /api/auth/users/me */
export const getMyProfile = () => api.get('/api/auth/users/me');

/** PUT /api/auth/users/me */
export const updateProfile = (data) => api.put('/api/auth/users/me', data);

/** POST /api/auth/users/cv — multipart/form-data */
export const uploadCv = (formData) =>
  api.post('/api/auth/users/cv', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
