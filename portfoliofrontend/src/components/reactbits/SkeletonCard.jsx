import React from 'react';
import '../../styles/Reactbits.css';

const SkeletonCard = ({ lines = 3, className = '' }) => {
  return (
    <div className={`rb-card rb-skeleton ${className}`}>
      <div className="rb-skeleton-line rb-skeleton-title"></div>
      {Array.from({ length: lines }).map((_, index) => (
        <div key={index} className="rb-skeleton-line"></div>
      ))}
    </div>
  );
};

export default SkeletonCard;
