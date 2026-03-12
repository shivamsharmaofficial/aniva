import "../../styles/layout.css";

function Sidebar() {
  return (
    <aside className="sidebar">
      <h3>Categories</h3>
      <ul className="sidebar-list">
        <li>Luxury Candles</li>
        <li>Festive Collection</li>
        <li>Floral Aroma</li>
        <li>Wood & Spice</li>
      </ul>
    </aside>
  );
}

export default Sidebar;