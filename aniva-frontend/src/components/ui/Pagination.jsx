import "@/components/styles/Pagination.css";

function Pagination({ page, totalPages, onPageChange }) {

  if (totalPages <= 1) return null;

  const pages = [];

  const start = Math.max(0, page - 2);
  const end = Math.min(totalPages - 1, page + 2);

  for (let i = start; i <= end; i++) {
    pages.push(i);
  }

  return (

    <div className="pagination">

      {/* PREVIOUS */}

      <button
        disabled={page === 0}
        onClick={() => onPageChange(page - 1)}
      >
        Previous
      </button>

      {/* FIRST PAGE */}

      {start > 0 && (
        <>
          <button onClick={() => onPageChange(0)}>1</button>
          {start > 1 && <span className="dots">...</span>}
        </>
      )}

      {/* PAGE WINDOW */}

      {pages.map((p) => (

        <button
          key={p}
          className={p === page ? "active" : ""}
          onClick={() => onPageChange(p)}
        >
          {p + 1}
        </button>

      ))}

      {/* LAST PAGE */}

      {end < totalPages - 1 && (
        <>
          {end < totalPages - 2 && <span className="dots">...</span>}
          <button onClick={() => onPageChange(totalPages - 1)}>
            {totalPages}
          </button>
        </>
      )}

      {/* NEXT */}

      <button
        disabled={page === totalPages - 1}
        onClick={() => onPageChange(page + 1)}
      >
        Next
      </button>

    </div>

  );

}

export default Pagination;