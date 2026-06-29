import axios from "axios";

// In production the frontend is served from the same Ingress host as the API,
// so these are set to relative paths at build time (VITE_API_URL=/api,
// VITE_API_ORIGIN=""). They fall back to localhost:8080 for local `npm run dev`.
const API_BASE = import.meta.env.VITE_API_URL ?? "http://localhost:8080/api";
export const API_ORIGIN = import.meta.env.VITE_API_ORIGIN ?? "http://localhost:8080";

const api = axios.create({ baseURL: API_BASE });

// Attach access token to every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// On 401, try to refresh token
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      const refreshToken = localStorage.getItem("refreshToken");
      if (refreshToken) {
        try {
          const { data } = await axios.post(`${API_BASE}/auth/refresh-token`, { refreshToken });
          localStorage.setItem("accessToken", data.accessToken);
          localStorage.setItem("refreshToken", data.refreshToken);
          originalRequest.headers.Authorization = `Bearer ${data.accessToken}`;
          return api(originalRequest);
        } catch {
          localStorage.clear();
          window.location.href = "/auth";
        }
      } else {
        localStorage.clear();
        window.location.href = "/auth";
      }
    }
    return Promise.reject(error);
  }
);

export default api;
