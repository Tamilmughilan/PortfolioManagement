import React, { useState, useEffect } from 'react';
import { TrendingUp, TrendingDown, DollarSign, Layers } from 'lucide-react';
import { getPortfolioDashboard } from '../services/api';
import HoldingCard from './HoldingCard';
import Carousel from './Carousel';
import StatCard from './reactbits/StatCard';
import GlowCard from './reactbits/GlowCard';
import SkeletonCard from './reactbits/SkeletonCard';
import SectionHeader from './reactbits/SectionHeader';
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
    return (
      <div className="dashboard">
        <SectionHeader
          title="Portfolio Dashboard"
          subtitle="Fetching your latest positions and performance."
          icon={<DollarSign size={22} />}
        />
        <div className="stats-grid">
          {Array.from({ length: 4 }).map((_, index) => (
            <SkeletonCard key={index} lines={2} />
          ))}
        </div>
        <div className="holdings-section">
          <h2>Your Holdings</h2>
          <div className="holdings-grid">
            {Array.from({ length: 3 }).map((_, index) => (
              <SkeletonCard key={index} lines={4} />
            ))}
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return <div className="error">Error: {error}</div>;
  }

  if (!portfolio) {
    return <div className="empty">Select a portfolio to view details</div>;
  }

  const holdings = portfolio.holdings || [];
  const totalGainLoss = holdings.reduce((sum, h) => sum + (h.gainLoss || 0), 0);
  const totalInvested = holdings.reduce((sum, h) => sum + (h.totalInvested || 0), 0);
  const totalGainLossPercent = totalInvested > 0 ? (totalGainLoss / totalInvested) * 100 : 0;

  return (
    <div className="dashboard">
      <SectionHeader
        title={portfolio.portfolioName}
        subtitle={`${portfolio.baseCurrency} • Since ${new Date(portfolio.createdAt).toLocaleDateString()}`}
        icon={<DollarSign size={22} />}
      />

      <div className="stats-grid">
        <StatCard
          label="Total Value"
          value={`${portfolio.baseCurrency} ${parseFloat(portfolio.totalValue || 0).toFixed(2)}`}
          subtext="Current portfolio value"
          icon={<DollarSign size={20} />}
        />
        <StatCard
          label="Total Invested"
          value={`${portfolio.baseCurrency} ${totalInvested.toFixed(2)}`}
          subtext="Cost basis"
          icon={<Layers size={20} />}
        />
        <StatCard
          label="Gain / Loss"
          value={`${portfolio.baseCurrency} ${Math.abs(totalGainLoss).toFixed(2)}`}
          subtext={`${totalGainLossPercent.toFixed(2)}%`}
          icon={totalGainLoss >= 0 ? <TrendingUp size={20} /> : <TrendingDown size={20} />}
          trend={totalGainLoss >= 0 ? 'positive' : 'negative'}
        />
        <StatCard
          label="Holdings Count"
          value={`${holdings.length}`}
          subtext="Different investments"
          icon={<TrendingUp size={20} />}
        />
      </div>

      <div className="holdings-section">
        <h2>Your Holdings</h2>
        {holdings.length === 0 ? (
          <div className="empty-holdings">No holdings yet. Add investments to get started.</div>
        ) : holdings.length <= 3 ? (
          <div className="holdings-grid">
            {holdings.map(holding => (
              <HoldingCard key={holding.holdingId} holding={holding} currency={portfolio.baseCurrency} />
            ))}
          </div>
        ) : (
          <Carousel itemsPerView={3} autoPlay autoPlayInterval={5500} className="rb-carousel">
            {holdings.map(holding => (
              <HoldingCard key={holding.holdingId} holding={holding} currency={portfolio.baseCurrency} />
            ))}
          </Carousel>
        )}
      </div>

      <GlowCard className="allocation-section">
        <h2>Asset Allocation</h2>
        <div className="allocation-chart">
          {holdings.map(holding => (
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
      </GlowCard>
    </div>
  );
};

export default Dashboard;