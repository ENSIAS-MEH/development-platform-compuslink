import { createContext, useContext, useState, useEffect, ReactNode } from "react";
import api from "../services/api";

interface User {
  userId: string;
  email: string;
  fullName?: string;
  role: string;
}

interface AuthContextType {
  user: User | null;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (data: RegisterData) => Promise<void>;
  logout: () => void;
}

interface RegisterData {
  email: string;
  password: string;
  fullName: string;
  university?: string;
  city?: string;
  phoneNumber?: string;
}

const AuthContext = createContext<AuthContextType>(null!);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    if (token) {
      api.get("/me")
        .then(({ data }) => {
          console.log("/me response:", data);
          setUser({ userId: data.userId, email: data.email, fullName: data.fullName, role: data.role });
        })
        .catch((err) => { console.error("/me failed:", err.response?.status, err.response?.data); localStorage.clear(); setUser(null); })
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  }, []);

  const login = async (email: string, password: string) => {
    const { data } = await api.post("/auth/login", { email, password });
    localStorage.setItem("accessToken", data.accessToken);
    localStorage.setItem("refreshToken", data.refreshToken);
    setUser({ userId: data.userId, email: data.email, role: data.role });
  };

  const register = async (regData: RegisterData) => {
    const { data } = await api.post("/auth/register", regData);
    localStorage.setItem("accessToken", data.accessToken);
    localStorage.setItem("refreshToken", data.refreshToken);
    setUser({ userId: data.userId, email: data.email, role: data.role });
  };

  const logout = () => {
    const refreshToken = localStorage.getItem("refreshToken");
    if (refreshToken) {
      api.post("/auth/logout", { refreshToken }).catch(() => {});
    }
    localStorage.clear();
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
