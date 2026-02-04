import React from 'react';
import '../../styles/Reactbits.css';

const GlowCard = ({ children, className = '', accent = 'primary' }) => {
  return (
    <div className={`rb-card rb-accent-${accent} ${className}`}>
      {children}
    </div>
  );
};

export default GlowCard;
