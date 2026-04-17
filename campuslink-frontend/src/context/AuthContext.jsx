import { createContext, useContext, useEffect, useState } from 'react';
import { getMyProfile } from '../api/authApi';

/**
 * AuthContext — provides currentUser, token, login, and logout
 * to the entire application tree.
 */
const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('campuslink_token'));
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Fetch profile when token is present
  useEffect(() => {
    if (!token) {
      setLoading(false);
      return;
    }
    getMyProfile()
      .then((res) => setCurrentUser(res.data))
      .catch(() => logout())
      .finally(() => setLoading(false));
  }, [token]);

  const login = (newToken, user) => {
    localStorage.setItem('campuslink_token', newToken);
    setToken(newToken);
    setCurrentUser(user);
  };

  const logout = () => {
    localStorage.removeItem('campuslink_token');
    setToken(null);
    setCurrentUser(null);
  };

  return (
    <AuthContext.Provider value={{ token, currentUser, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
