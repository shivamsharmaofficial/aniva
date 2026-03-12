import { useEffect, useRef, useState } from "react";
import { Link } from "react-router-dom";
import { ChevronLeft, ChevronRight } from "lucide-react";
import { OFFER_MESSAGES } from "@/constants/siteConstants";
import "@/components/styles/topAnnouncementBar.css";

const AUTOPLAY_DELAY = 4000;
const ANIMATION_DURATION = 450;

function TopAnnouncementBar() {
  const [activeIndex, setActiveIndex] = useState(0);
  const [previousIndex, setPreviousIndex] = useState(null);
  const [direction, setDirection] = useState(1);
  const [isAnimating, setIsAnimating] = useState(false);
  const [animationCycle, setAnimationCycle] = useState(0);
  const timeoutRef = useRef(null);

  const goToMessage = (nextIndex, nextDirection) => {
    if (
      nextIndex === activeIndex ||
      nextIndex < 0 ||
      nextIndex >= OFFER_MESSAGES.length
    ) {
      return;
    }

    if (timeoutRef.current) {
      window.clearTimeout(timeoutRef.current);
    }

    setPreviousIndex(activeIndex);
    setActiveIndex(nextIndex);
    setDirection(nextDirection);
    setIsAnimating(true);
    setAnimationCycle((prev) => prev + 1);

    timeoutRef.current = window.setTimeout(() => {
      setPreviousIndex(null);
      setIsAnimating(false);
    }, ANIMATION_DURATION);
  };

  const handleNext = () => {
    const nextIndex = (activeIndex + 1) % OFFER_MESSAGES.length;
    goToMessage(nextIndex, 1);
  };

  const handlePrevious = () => {
    const nextIndex =
      (activeIndex - 1 + OFFER_MESSAGES.length) % OFFER_MESSAGES.length;
    goToMessage(nextIndex, -1);
  };

  useEffect(() => {
    const intervalId = window.setInterval(() => {
      const nextIndex = (activeIndex + 1) % OFFER_MESSAGES.length;
      goToMessage(nextIndex, 1);
    }, AUTOPLAY_DELAY);

    return () => {
      window.clearInterval(intervalId);
      if (timeoutRef.current) {
        window.clearTimeout(timeoutRef.current);
      }
    };
  }, [activeIndex, isAnimating]);

  const currentMessage = OFFER_MESSAGES[activeIndex];
  const outgoingMessage =
    previousIndex !== null ? OFFER_MESSAGES[previousIndex] : null;

  return (
    <div className="announcement-bar">
      <div className="announcement-bar__inner">
        <button
          type="button"
          onClick={handlePrevious}
          className="announcement-bar__arrow announcement-bar__arrow--left"
          aria-label="Previous announcement"
          title={`Previous: ${
            OFFER_MESSAGES[
              (activeIndex - 1 + OFFER_MESSAGES.length) %
                OFFER_MESSAGES.length
            ].text
          }`}
        >
          <ChevronLeft className="size-4" />
        </button>

        <div className="announcement-bar__track">
          {outgoingMessage && (
            <Link
              key={`outgoing-${previousIndex}-${animationCycle}`}
              to={outgoingMessage.link}
              className={`announcement-bar__message--outgoing ${
                direction === 1
                  ? "announcement-exit-left"
                  : "announcement-exit-right"
              }`}
            >
              {outgoingMessage.text}
            </Link>
          )}

          <Link
            key={`active-${activeIndex}-${animationCycle}`}
            to={currentMessage.link}
            className={`announcement-bar__message ${
              isAnimating
                ? direction === 1
                  ? "announcement-enter-right"
                  : "announcement-enter-left"
                : ""
            }`}
          >
            {currentMessage.text}
          </Link>
        </div>

        <button
          type="button"
          onClick={handleNext}
          className="announcement-bar__arrow announcement-bar__arrow--right"
          aria-label="Next announcement"
          title={`Next: ${
            OFFER_MESSAGES[(activeIndex + 1) % OFFER_MESSAGES.length].text
          }`}
        >
          <ChevronRight className="size-4" />
        </button>
      </div>
    </div>
  );
}

export default TopAnnouncementBar;
