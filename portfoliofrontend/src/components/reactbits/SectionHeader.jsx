import React from 'react';
import '../../styles/Reactbits.css';

const SectionHeader = ({ title, subtitle, icon }) => {
  return (
    <div className="rb-section-header">
      <div className="rb-section-title">
        {icon && <span className="rb-section-icon">{icon}</span>}
        <h1>{title}</h1>
      </div>
      {subtitle && <p>{subtitle}</p>}
    </div>
  );
};

export default SectionHeader;
