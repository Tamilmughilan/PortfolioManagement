import React, { useState, useEffect } from 'react';
import { TrendingUp, TrendingDown, DollarSign, Layers, Newspaper } from 'lucide-react';
import { getMarketNews, getPortfolioDashboard } from '../services/api';
import HoldingCard from './HoldingCard';
import Carousel from './Carousel';
import StatCard from './reactbits/StatCard';
import GlowCard from './reactbits/GlowCard';
import SkeletonCard from './reactbits/SkeletonCard';
import SectionHeader from './reactbits/SectionHeader';
import '../styles/Dashboard.css';
import { formatWithSymbol } from '../utils/currency';
import { DUMMY_PORTFOLIO } from '../utils/dummyData';

const Dashboard = ({ portfolioId }) => {
  const [portfolio, setPortfolio] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [news, setNews] = useState([]);
  const [newsLoading, setNewsLoading] = useState(true);

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        setLoading(true);
        const response = await getPortfolioDashboard(portfolioId);
        setPortfolio(response.data);
        setError(null);
      } catch (err) {
        // Fallback to dummy data for demo/presentation
        setPortfolio(DUMMY_PORTFOLIO);
        setError(null);
      } finally {
        setLoading(false);
      }
    };

    const fetchNews = async () => {
      try {
        setNewsLoading(true);
        const response = await getMarketNews();
        setNews(response.data || []);
      } catch (err) {
        setNews([]);
      } finally {
        setNewsLoading(false);
      }
    };

    if (portfolioId) {
      fetchDashboard();
      fetchNews();
    } else {
      // Show dummy data if no portfolio selected
      setPortfolio(DUMMY_PORTFOLIO);
      setLoading(false);
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

  if (!portfolio) {
    // Show dummy portfolio if nothing is available
    return <Dashboard portfolioId={null} />;
  }

  const holdings = portfolio.holdings || [];
  const totalGainLoss = holdings.reduce((sum, h) => sum + (h.gainLoss || 0), 0);
  const totalInvested = holdings.reduce((sum, h) => sum + (h.totalInvested || 0), 0);
  const totalGainLossPercent = totalInvested > 0 ? (totalGainLoss / totalInvested) * 100 : 0;
  const currency = portfolio.baseCurrency || '';

  return (
    <div className="dashboard">
      <SectionHeader
        title={portfolio.portfolioName}
        subtitle={`${currency} • Since ${new Date(portfolio.createdAt).toLocaleDateString()}`}
        icon={<DollarSign size={22} />}
      />

      <div className="stats-grid">
        <StatCard
          label="Total Value"
          value={formatWithSymbol(currency, portfolio.totalValue)}
          subtext="Current portfolio value"
          icon={<DollarSign size={20} />}
        />
        <StatCard
          label="Total Invested"
          value={formatWithSymbol(currency, totalInvested)}
          subtext="Cost basis"
          icon={<Layers size={20} />}
        />
        <StatCard
          label="Gain / Loss"
          value={formatWithSymbol(currency, Math.abs(totalGainLoss))}
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
              <HoldingCard key={holding.holdingId} holding={holding} currency={currency} />
            ))}
          </div>
        ) : (
          <Carousel itemsPerView={3} autoPlay autoPlayInterval={5500} className="rb-carousel">
            {holdings.map(holding => (
              <HoldingCard key={holding.holdingId} holding={holding} currency={currency} />
            ))}
          </Carousel>
        )}
      </div>

      <GlowCard className="allocation-section">
        <h2>Asset Allocation</h2>
        {holdings.length > 0 && (
          <div style={{ padding: '1rem', background: 'var(--bg-light)', borderRadius: '12px', marginBottom: '1rem' }}>
            <svg viewBox="0 0 200 200" style={{ width: '200px', height: '200px', margin: '0 auto', display: 'block' }}>
              {(() => {
                const totalAllocation = holdings.reduce((sum, h) => sum + (h.allocation || 0), 0);
                if (totalAllocation === 0) return null;
                
                let currentAngle = -90;
                const colors = ['#DC2626', '#E11D48', '#F97316', '#10b981', '#f59e0b', '#06b6d4', '#8b5cf6', '#ec4899'];
                
                return holdings
                  .filter(h => (h.allocation || 0) > 0)
                  .map((holding, index) => {
                    const percent = (holding.allocation || 0) / totalAllocation;
                    const angle = percent * 360;
                    const startAngle = currentAngle;
                    const endAngle = currentAngle + angle;
                    
                    const x1 = 100 + 70 * Math.cos((startAngle * Math.PI) / 180);
                    const y1 = 100 + 70 * Math.sin((startAngle * Math.PI) / 180);
                    const x2 = 100 + 70 * Math.cos((endAngle * Math.PI) / 180);
                    const y2 = 100 + 70 * Math.sin((endAngle * Math.PI) / 180);
                    const largeArc = angle > 180 ? 1 : 0;
                    
                    currentAngle += angle;
                    
                    return (
                      <path
                        key={holding.holdingId}
                        d={`M 100 100 L ${x1} ${y1} A 70 70 0 ${largeArc} 1 ${x2} ${y2} Z`}
                        fill={colors[index % colors.length]}
                        opacity="0.8"
                      />
                    );
                  });
              })()}
              <circle cx="100" cy="100" r="50" fill="var(--bg-white)" />
              <text x="100" y="95" textAnchor="middle" fontSize="16" fontWeight="600" fill="var(--text-dark)">
                {holdings.reduce((sum, h) => sum + (h.allocation || 0), 0).toFixed(0)}%
              </text>
              <text x="100" y="110" textAnchor="middle" fontSize="11" fill="var(--text-light)">Allocated</text>
            </svg>
          </div>
        )}
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

      <div className="news-section">
        <div className="news-header">
          <h2>
            <Newspaper size={20} /> Market Headlines
          </h2>
          <p>Latest market movers and macro headlines.</p>
        </div>

        {newsLoading ? (
          <div className="news-grid">
            {Array.from({ length: 3 }).map((_, index) => (
              <SkeletonCard key={index} lines={3} />
            ))}
          </div>
        ) : news.length === 0 ? (
          <div className="empty-holdings">No news available right now. Check your API key or retry later.</div>
        ) : news.length <= 3 ? (
          <div className="news-grid">
            {news.map(item => (
              <GlowCard key={item.url} className="news-card">
                {item.image && <img src={item.image} alt={item.headline} />}
                <div className="news-body">
                  <span className="news-source">{item.source}</span>
                  <h3>{item.headline}</h3>
                  <p>{item.summary}</p>
                  <a href={item.url} target="_blank" rel="noreferrer">Read more</a>
                </div>
              </GlowCard>
            ))}
          </div>
        ) : (
          <Carousel itemsPerView={3} autoPlay autoPlayInterval={6000} className="rb-carousel">
            {news.map(item => (
              <GlowCard key={item.url} className="news-card">
                {item.image && <img src={item.image} alt={item.headline} />}
                <div className="news-body">
                  <span className="news-source">{item.source}</span>
                  <h3>{item.headline}</h3>
                  <p>{item.summary}</p>
                  <a href={item.url} target="_blank" rel="noreferrer">Read more</a>
                </div>
              </GlowCard>
            ))}
          </Carousel>
        )}
      </div>
    </div>
  );
};

export default Dashboard;