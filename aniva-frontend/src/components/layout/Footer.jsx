import "../../styles/layout.css";

function Footer() {
  return (
    <footer className="footer">
      <div className="footer-content">
        © {new Date().getFullYear()} ĀNIVA | Luxury Aroma Candles
      </div>
    </footer>
  );
}

export default Footer;