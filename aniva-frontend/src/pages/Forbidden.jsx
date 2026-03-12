import { Link } from "react-router-dom";
import "@/pages/styles/forbidden.css";

const Forbidden = () => {
  return (
    <section className="forbidden-page">
      <div className="forbidden-card">
        <span className="forbidden-code">403</span>
        <h1>Access denied</h1>
        <p>
          You do not have permission to view this page with the current
          account.
        </p>

        <div className="forbidden-actions">
          <Link to="/" className="forbidden-primary">
            Return Home
          </Link>
          <Link to="/profile" className="forbidden-secondary">
            Go to Profile
          </Link>
        </div>
      </div>
    </section>
  );
};

export default Forbidden;
