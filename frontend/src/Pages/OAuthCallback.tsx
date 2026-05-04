
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

const OAuthCallback = () => {
  const navigate = useNavigate();

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const token = params.get("token");
    const error = params.get("error");

    if (token) {
      localStorage.setItem("jwt", token);
      navigate("/dashboard");
    } else if (error) {
      navigate("/?error=oauth_failed");
    } else {
      navigate("/?error=unknown");
    }
  }, [navigate]);

  return (
    <div style={{ textAlign: "center", marginTop: "4rem" }}>
      <p>Completing login...</p>
    </div>
  );
};

export default OAuthCallback;