import { Outlet } from "react-router-dom";
import TopAnnouncementBar from "./TopAnnouncementBar";
import HeaderBar from "./HeaderBar";
import Navbar from "./Navbar";
import Footer from "./Footer";
import "@/components/styles/mainLayout.css";
import "@/styles/global.css";
import "@/styles/theme.css";

function MainLayout() {
  return (
    <>
      <TopAnnouncementBar />
      <HeaderBar />
      <Navbar />
      <main className="min-h-[60vh]">
        <Outlet />
      </main>
      <Footer />
    </>
  );
}

export default MainLayout;
