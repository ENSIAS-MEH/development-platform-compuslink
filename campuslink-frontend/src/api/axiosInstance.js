import axios from 'axios';

/**
 * Shared Axios instance — all requests go through the API Gateway.
 * The JWT token is injected from localStorage on every request via
 * the request interceptor below.
 */
const api = axios.create({
  baseURL: import.meta.env.VITE_GATEWAY_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// ── Request interceptor: attach Bearer token ─────────────────────────────
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('campuslink_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// ── Response interceptor: handle 401 globally ────────────────────────────
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('campuslink_token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
