import "@/pages/styles/about.css";

function About() {
  return (
    <section className="about-page">
      <div className="about-hero">
        <span className="about-hero__eyebrow">Our Story</span>
        <h1>About ANIVA</h1>
        <p>
          ANIVA creates fragrance-led home rituals with handcrafted candles
          designed for calm evenings, meaningful gifting, and elevated spaces.
        </p>
      </div>

      <div className="about-grid">
        <article className="about-card">
          <h2>Crafted with intention</h2>
          <p>
            Every candle is built around clean ingredients, balanced scent
            throw, and a premium finish that feels considered on the shelf
            and in use.
          </p>
        </article>

        <article className="about-card">
          <h2>Materials that matter</h2>
          <p>
            We focus on soy wax blends, elegant vessels, and fragrances that
            feel warm, layered, and easy to live with every day.
          </p>
        </article>

        <article className="about-card">
          <h2>Made for gifting</h2>
          <p>
            From signature candles to curated sets, the collection is shaped
            for both self-care rituals and thoughtful occasions.
          </p>
        </article>
      </div>
    </section>
  );
}

export default About;
