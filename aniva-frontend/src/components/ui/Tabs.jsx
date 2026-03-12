import { useState } from "react";

function Tabs({ tabs }) {
  const [active, setActive] = useState(0);

  return (
    <div className="tabs">

      <div className="tabs-header">
        {tabs.map((tab, i) => (
          <button
            key={i}
            className={active === i ? "active" : ""}
            onClick={() => setActive(i)}
          >
            {tab.label}
          </button>
        ))}
      </div>

      <div className="tabs-body">
        {tabs[active].content}
      </div>

    </div>
  );
}

export default Tabs;