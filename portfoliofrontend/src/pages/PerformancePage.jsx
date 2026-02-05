import React, { useState, useEffect } from 'react';
import PerformanceCharts from '../components/PerformanceCharts';
import '../styles/Performance.css';

const PerformancePage = ({ portfolioId, selectedPortfolio }) => {
  const [title, setTitle] = useState('Portfolio Performance');

  useEffect(() => {
    if (selectedPortfolio) {
      setTitle(`${selectedPortfolio.portfolioName} - Performance`);
    }
  }, [selectedPortfolio]);

  return (
    <div className="performance-page">
      <div className="performance-header">
        <h1>{title}</h1>
        <p>Track your portfolio performance, asset allocation, and investment returns</p>
      </div>

      {portfolioId ? (
        <PerformanceCharts portfolioId={portfolioId} />
      ) : (
        <div className="no-portfolio-message">
          <p>Please select a portfolio to view performance charts</p>
        </div>
      )}
    </div>
  );
};

export default PerformancePage;
