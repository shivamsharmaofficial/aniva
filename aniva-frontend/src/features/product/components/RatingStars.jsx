function RatingStars({ rating = 0 }) {

  return (
    <div className="rating-stars">
      {[1,2,3,4,5].map((star)=>(
        <span key={star}>
          {star <= rating ? "★" : "☆"}
        </span>
      ))}
    </div>
  );

}

export default RatingStars;