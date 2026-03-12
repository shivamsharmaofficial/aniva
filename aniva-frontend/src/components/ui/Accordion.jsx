import { useState } from "react";

function Accordion({ title, children }) {
  const [open, setOpen] = useState(false);

  return (
    <div className="accordion">
      <div
        className="accordion-header"
        onClick={() => setOpen(!open)}
      >
        {title}
      </div>

      {open && (
        <div className="accordion-body">
          {children}
        </div>
      )}
    </div>
  );
}

export default Accordion;