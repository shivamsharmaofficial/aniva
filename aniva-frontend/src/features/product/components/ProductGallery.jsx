import { useState } from "react";

const FALLBACK_IMAGE =
  "https://images.unsplash.com/photo-1603006905003-be475563bc59";

function ProductGallery({ images = [], productName }) {

  const [selected, setSelected] = useState(
    images?.[0]?.imageUrl || FALLBACK_IMAGE
  );

  const [lightbox, setLightbox] = useState(false);

  return (
    <div className="product-image-section">

      <div className="image-wrapper">
        <img
          src={selected}
          alt={productName}
          className="main-product-image"
          onClick={() => setLightbox(true)}
          onError={(e) => (e.target.src = FALLBACK_IMAGE)}
        />
      </div>

      <div className="thumbnail-container">
        {images.map((img) => (
          <img
            key={img.id}
            src={img.imageUrl}
            className="thumbnail"
            onClick={() => setSelected(img.imageUrl)}
          />
        ))}
      </div>

      {lightbox && (
        <div
          className="lightbox"
          onClick={() => setLightbox(false)}
        >
          <img src={selected} alt="" />
        </div>
      )}

    </div>
  );
}

export default ProductGallery;