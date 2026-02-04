import React, { useState, useEffect } from 'react';
import { TrendingUp, TrendingDown, DollarSign } from 'lucide-react';
import { getPortfolioDashboard } from '../services/api';
import HoldingCard from './HoldingCard';
import '../styles/Dashboard.css';

const Dashboard = ({ portfolioId }) => {
  const [portfolio, setPortfolio] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        setLoading(true);
        const response = await getPortfolioDashboard(portfolioId);
        setPortfolio(response.data);
        setError(null);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    if (portfolioId) {
      fetchDashboard();
    }
  }, [portfolioId]);

  if (loading) {
    return <div className="loading">Loading portfolio...</div>;
  }

  if (error) {
    return <div className="error">Error: {error}</div>;
  }

  if (!portfolio) {
    return <div className="empty">Select a portfolio to view details</div>;
  }

  const totalGainLoss = portfolio.holdings.reduce((sum, h) => sum + (h.gainLoss || 0), 0);
  const totalGainLossPercent = portfolio.holdings.length > 0
    ? totalGainLoss / portfolio.holdings.reduce((sum, h) => sum + (h.totalInvested || 0), 0) * 100
    : 0;

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h1>{portfolio.portfolioName}</h1>
        <p className="portfolio-meta">{portfolio.baseCurrency} • Since {new Date(portfolio.createdAt).toLocaleDateString()}</p>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-label">Total Value</div>
          <div className="stat-value">{portfolio.baseCurrency} {parseFloat(portfolio.totalValue || 0).toFixed(2)}</div>
          <div className="stat-subtext">Current portfolio value</div>
        </div>

        <div className="stat-card">
          <div className="stat-label">Total Invested</div>
          <div className="stat-value">
            {portfolio.baseCurrency} {portfolio.holdings.reduce((sum, h) => sum + (h.totalInvested || 0), 0).toFixed(2)}
          </div>
          <div className="stat-subtext">Cost basis</div>
        </div>

        <div className={`stat-card gain-loss ${totalGainLoss >= 0 ? 'positive' : 'negative'}`}>
          <div className="stat-label">Gain / Loss</div>
          <div className="stat-value flex-center">
            {totalGainLoss >= 0 ? <TrendingUp size={20} /> : <TrendingDown size={20} />}
            {portfolio.baseCurrency} {Math.abs(totalGainLoss).toFixed(2)}
          </div>
          <div className="stat-subtext">{totalGainLossPercent.toFixed(2)}%</div>
        </div>

        <div className="stat-card">
          <div className="stat-label">Holdings Count</div>
          <div className="stat-value">{portfolio.holdings.length}</div>
          <div className="stat-subtext">Different investments</div>
        </div>
      </div>

      <div className="holdings-section">
        <h2>Your Holdings</h2>
        {portfolio.holdings.length === 0 ? (
          <div className="empty-holdings">No holdings yet. Add investments to get started.</div>
        ) : (
          <div className="holdings-grid">
            {portfolio.holdings.map(holding => (
              <HoldingCard key={holding.holdingId} holding={holding} currency={portfolio.baseCurrency} />
            ))}
          </div>
        )}
      </div>

      <div className="allocation-section">
        <h2>Asset Allocation</h2>
        <div className="allocation-chart">
          {portfolio.holdings.map(holding => (
            <div key={holding.holdingId} className="allocation-item">
              <div className="allocation-label">{holding.assetName}</div>
              <div className="allocation-bar">
                <div
                  className="allocation-fill"
                  style={{ width: `${holding.allocation || 0}%` }}
                />
              </div>
              <div className="allocation-percent">{(holding.allocation || 0).toFixed(1)}%</div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;