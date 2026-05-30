import { useEffect } from "react";
import { useSearchParams } from "react-router-dom";

export default function AuthCallbackPage() {
  const [params] = useSearchParams();

  useEffect(() => {
    const accessToken = params.get("accessToken");
    const refreshToken = params.get("refreshToken");

    if (accessToken && refreshToken) {
      localStorage.setItem("accessToken", accessToken);
      localStorage.setItem("refreshToken", refreshToken);
      // Full reload so AuthContext picks up the new tokens
      window.location.replace("/");
    } else {
      window.location.replace("/auth");
    }
  }, [params]);

  return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="w-8 h-8 border-4 border-primary border-t-transparent rounded-full animate-spin" />
    </div>
  );
}
