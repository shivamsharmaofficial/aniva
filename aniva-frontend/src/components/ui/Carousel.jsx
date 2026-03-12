import { useState } from "react";

function Carousel({ images = [] }) {
  const [index, setIndex] = useState(0);

  const next = () => {
    setIndex((prev) =>
      prev === images.length - 1 ? 0 : prev + 1
    );
  };

  const prev = () => {
    setIndex((prev) =>
      prev === 0 ? images.length - 1 : prev - 1
    );
  };

  return (
    <div className="carousel">
      <button onClick={prev}>‹</button>

      <img src={images[index]} alt="product" />

      <button onClick={next}>›</button>
    </div>
  );
}

export default Carousel;