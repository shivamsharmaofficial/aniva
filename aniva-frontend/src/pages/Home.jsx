import ProductSection from "../features/product/components/ProductSection";
import "@/pages/styles/home.css";

function Home() {
  return (
    <>
      {/* ================= HERO ================= */}
      <section className="hero">
        <div className="hero-overlay"></div>

        <div className="hero-content">
          <span className="hero-subtitle">
            Luxury Handcrafted Aroma
          </span>

          <h1>
            Light the Moment. <br />
            Live the Calm.
          </h1>

          <div className="gold-line"></div>

          <p>
            Premium soy wax candles crafted to elevate your sacred
            space with warmth, elegance, and serenity.
          </p>

          <div className="hero-buttons">
            <button className="primary-btn">
              Shop Collection
            </button>

            <button className="secondary-btn">
              Explore Scents
            </button>
          </div>
        </div>
      </section>

      {/* ================= BENTO CATEGORIES ================= */}
      <section className="bento container">
        <h2 className="section-title">
          Find Your Perfect Mood
        </h2>

        <div className="bento-grid">
          <div className="bento-large">
            <img src="/images/category1.png" alt="Relax & Unwind" />
            <div className="bento-text">Relax & Unwind</div>
          </div>

          <div className="bento-small">
            <img src="/images/category2.png" alt="Sleep & Serenity" />
            <div className="bento-text">Sleep & Serenity</div>
          </div>

          <div className="bento-small">
            <img src="/images/category3.png" alt="Home Ambience" />
            <div className="bento-text">Home Ambience</div>
          </div>

          <div className="bento-wide">
            <img src="/images/category4.png" alt="Perfect for Gifting" />
            <div className="bento-text">Perfect for Gifting</div>
          </div>
        </div>
      </section>

      {/* ================= PRODUCTS (DYNAMIC MODULE) ================= */}
      <ProductSection />

      {/* ================= WHY SECTION ================= */}
      <section className="why container">
        <h2 className="section-title">
          The ĀNIVA Difference
        </h2>

        <div className="why-grid">
          <div>
            <h3>🌿 100% Natural Soy Wax</h3>
            <p>Clean burn, toxin-free & eco-conscious.</p>
          </div>

          <div>
            <h3>🖐 Handcrafted</h3>
            <p>Made in small batches with care.</p>
          </div>

          <div>
            <h3>🔥 Long Lasting</h3>
            <p>Up to 40+ hours of fragrance.</p>
          </div>

          <div>
            <h3>🚚 Fast Delivery</h3>
            <p>Safe shipping across India.</p>
          </div>
        </div>
      </section>

      {/* ================= NEWSLETTER ================= */}
      <section className="newsletter">
        <div className="newsletter-container">
          <h2>
            Get 10% Off Your First Order
          </h2>

          <p>
            Join our community for exclusive offers & new launches.
          </p>

          <div className="newsletter-form">
            <input
              type="email"
              placeholder="Enter your email"
            />
            <button>Subscribe</button>
          </div>
        </div>
      </section>
    </>
  );
}

export default Home;