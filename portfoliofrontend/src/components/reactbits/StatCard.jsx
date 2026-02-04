import React from 'react';
import GlowCard from './GlowCard';

const StatCard = ({ label, value, subtext, icon, accent = 'primary', trend }) => {
  return (
    <GlowCard className={`rb-stat-card ${trend ? `rb-trend-${trend}` : ''}`} accent={accent}>
      <div className="rb-stat-header">
        <div className="rb-stat-label">{label}</div>
        {icon && <div className="rb-stat-icon">{icon}</div>}
      </div>
      <div className="rb-stat-value">{value}</div>
      {subtext && <div className="rb-stat-subtext">{subtext}</div>}
    </GlowCard>
  );
};

export default StatCard;
