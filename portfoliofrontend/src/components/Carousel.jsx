import React, { useState, useRef, useEffect } from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import '../styles/Carousel.css';

const Carousel = ({ children, itemsPerView = 3, autoPlay = false, autoPlayInterval = 5000, className = '' }) => {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [isAnimating, setIsAnimating] = useState(false);
  const containerRef = useRef(null);
  const childrenArray = React.Children.toArray(children);
  const totalItems = childrenArray.length;
  const maxIndex = Math.max(0, totalItems - itemsPerView);

  useEffect(() => {
    if (autoPlay && totalItems > itemsPerView) {
      const interval = setInterval(() => {
        handleNext();
      }, autoPlayInterval);
      return () => clearInterval(interval);
    }
  }, [autoPlay, autoPlayInterval, currentIndex, totalItems, itemsPerView]);

  const handlePrev = () => {
    if (isAnimating) return;
    setIsAnimating(true);
    setCurrentIndex(prev => Math.max(0, prev - 1));
    setTimeout(() => setIsAnimating(false), 400);
  };

  const handleNext = () => {
    if (isAnimating) return;
    setIsAnimating(true);
    setCurrentIndex(prev => Math.min(maxIndex, prev + 1));
    setTimeout(() => setIsAnimating(false), 400);
  };

  const goToSlide = (index) => {
    if (isAnimating) return;
    setIsAnimating(true);
    setCurrentIndex(Math.min(Math.max(0, index), maxIndex));
    setTimeout(() => setIsAnimating(false), 400);
  };

  if (totalItems === 0) {
    return null;
  }

  const showNavigation = totalItems > itemsPerView;

  return (
    <div className={`carousel-container ${className}`} data-items={itemsPerView}>
      <div className="carousel-wrapper">
        {showNavigation && (
          <button 
            className={`carousel-btn prev ${currentIndex === 0 ? 'disabled' : ''}`}
            onClick={handlePrev}
            disabled={currentIndex === 0}
            aria-label="Previous items"
          >
            <ChevronLeft size={24} />
          </button>
        )}

        <div className="carousel-viewport" ref={containerRef}>
          <div 
            className="carousel-track"
            style={{ 
              transform: `translateX(-${currentIndex * (100 / itemsPerView)}%)`,
              gridTemplateColumns: `repeat(${totalItems}, ${100 / itemsPerView}%)`
            }}
          >
            {childrenArray.map((child, index) => (
              <div 
                key={index} 
                className="carousel-item"
                style={{ animationDelay: `${index * 0.1}s` }}
              >
                {child}
              </div>
            ))}
          </div>
        </div>

        {showNavigation && (
          <button 
            className={`carousel-btn next ${currentIndex >= maxIndex ? 'disabled' : ''}`}
            onClick={handleNext}
            disabled={currentIndex >= maxIndex}
            aria-label="Next items"
          >
            <ChevronRight size={24} />
          </button>
        )}
      </div>

      {showNavigation && (
        <div className="carousel-indicators">
          {Array.from({ length: maxIndex + 1 }).map((_, index) => (
            <button
              key={index}
              className={`indicator ${index === currentIndex ? 'active' : ''}`}
              onClick={() => goToSlide(index)}
              aria-label={`Go to slide ${index + 1}`}
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default Carousel;
