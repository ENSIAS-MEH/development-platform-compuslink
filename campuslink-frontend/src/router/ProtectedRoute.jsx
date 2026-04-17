import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/**
 * ProtectedRoute — redirects to /login if the user is not authenticated.
 * Optionally restricts access to specific roles.
 *
 * @param {string[]} roles - allowed roles (e.g. ['STUDENT', 'RECRUITER'])
 */
export default function ProtectedRoute({ children, roles }) {
  const { currentUser, loading } = useAuth();

  if (loading) return null; // or a global spinner

  if (!currentUser) return <Navigate to="/login" replace />;

  if (roles && !roles.includes(currentUser.role)) {
    return <Navigate to="/" replace />;
  }

  return children;
}
