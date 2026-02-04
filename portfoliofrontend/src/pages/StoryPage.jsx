import React, { useState, useEffect } from 'react';
import { History, TrendingUp, TrendingDown, Calendar, DollarSign, BarChart3 } from 'lucide-react';
import SectionHeader from '../components/reactbits/SectionHeader';
import GlowCard from '../components/reactbits/GlowCard';
import StatCard from '../components/reactbits/StatCard';
import '../styles/Story.css';

const StoryPage = ({ portfolioId }) => {
  const [snapshots, setSnapshots] = useState([]);
  const [loading, setLoading] = useState(true);

  // Dummy data for portfolio snapshots
  useEffect(() => {
    const loadSnapshots = async () => {
      setLoading(true);

      // Simulate API call delay
      setTimeout(() => {
        const dummySnapshots = [
          {
            id: 1,
            date: '2024-01-01',
            totalValue: 100000,
            change: 0,
            changePercent: 0,
            holdings: [
              { name: 'AAPL', value: 30000, change: 0 },
              { name: 'GOOGL', value: 25000, change: 0 },
              { name: 'MSFT', value: 20000, change: 0 },
              { name: 'TSLA', value: 25000, change: 0 }
            ]
          },
          {
            id: 2,
            date: '2024-02-01',
            totalValue: 110000,
            change: 10000,
            changePercent: 10,
            holdings: [
              { name: 'AAPL', value: 33000, change: 3000 },
              { name: 'GOOGL', value: 27500, change: 2500 },
              { name: 'MSFT', value: 22000, change: 2000 },
              { name: 'TSLA', value: 27500, change: 2500 }
            ]
          },
          {
            id: 3,
            date: '2024-03-01',
            totalValue: 125000,
            change: 15000,
            changePercent: 13.64,
            holdings: [
              { name: 'AAPL', value: 37500, change: 4500 },
              { name: 'GOOGL', value: 31250, change: 3750 },
              { name: 'MSFT', value: 25000, change: 3000 },
              { name: 'TSLA', value: 31250, change: 3750 }
            ]
          },
          {
            id: 4,
            date: '2024-04-01',
            totalValue: 118000,
            change: -7000,
            changePercent: -5.6,
            holdings: [
              { name: 'AAPL', value: 35400, change: -2100 },
              { name: 'GOOGL', value: 29500, change: -1750 },
              { name: 'MSFT', value: 23600, change: -1400 },
              { name: 'TSLA', value: 29500, change: -1750 }
            ]
          },
          {
            id: 5,
            date: '2024-05-01',
            totalValue: 135000,
            change: 17000,
            changePercent: 14.41,
            holdings: [
              { name: 'AAPL', value: 40500, change: 5100 },
              { name: 'GOOGL', value: 33750, change: 4250 },
              { name: 'MSFT', value: 27000, change: 3400 },
              { name: 'TSLA', value: 33750, change: 4250 }
            ]
          }
        ];

        setSnapshots(dummySnapshots);
        setLoading(false);
      }, 1000);
    };

    loadSnapshots();
  }, [portfolioId]);

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  if (loading) {
    return (
      <div className="story-page">
        <SectionHeader
          title="Portfolio Story"
          subtitle="Loading your portfolio journey..."
          icon={<History size={22} />}
        />
        <div className="story-loading">
          <div className="story-skeleton">
            <div className="skeleton-header"></div>
            <div className="skeleton-content"></div>
          </div>
        </div>
      </div>
    );
  }

  const latestSnapshot = snapshots[snapshots.length - 1];
  const firstSnapshot = snapshots[0];
  const totalGrowth = latestSnapshot ? latestSnapshot.totalValue - firstSnapshot.totalValue : 0;
  const totalGrowthPercent = firstSnapshot ? (totalGrowth / firstSnapshot.totalValue) * 100 : 0;

  return (
    <div className="story-page">
      <SectionHeader
        title="Portfolio Story"
        subtitle="Track your portfolio's journey through time"
        icon={<History size={22} />}
      />

      {/* Summary Stats */}
      <div className="story-stats">
        <StatCard
          title="Total Portfolio Value"
          value={formatCurrency(latestSnapshot?.totalValue || 0)}
          icon={<DollarSign size={20} />}
          trend="neutral"
        />
        <StatCard
          title="Total Growth"
          value={formatCurrency(totalGrowth)}
          subtitle={`${totalGrowthPercent >= 0 ? '+' : ''}${totalGrowthPercent.toFixed(2)}%`}
          icon={totalGrowth >= 0 ? <TrendingUp size={20} /> : <TrendingDown size={20} />}
          trend={totalGrowth >= 0 ? 'positive' : 'negative'}
        />
        <StatCard
          title="Time Period"
          value={`${snapshots.length} Snapshots`}
          subtitle={`${formatDate(firstSnapshot?.date)} - ${formatDate(latestSnapshot?.date)}`}
          icon={<Calendar size={20} />}
          trend="neutral"
        />
      </div>

      {/* Timeline */}
      <div className="story-timeline">
        <h3>Portfolio Timeline</h3>
        <div className="timeline-container">
          {snapshots.map((snapshot, index) => (
            <div key={snapshot.id} className="timeline-item">
              <div className="timeline-marker">
                <div className="timeline-dot"></div>
                {index < snapshots.length - 1 && <div className="timeline-line"></div>}
              </div>

              <GlowCard className="timeline-content">
                <div className="snapshot-header">
                  <div className="snapshot-date">
                    <Calendar size={16} />
                    <span>{formatDate(snapshot.date)}</span>
                  </div>
                  <div className="snapshot-value">
                    <BarChart3 size={16} />
                    <span>{formatCurrency(snapshot.totalValue)}</span>
                  </div>
                </div>

                <div className="snapshot-change">
                  <span className={`change-amount ${snapshot.change >= 0 ? 'positive' : 'negative'}`}>
                    {snapshot.change >= 0 ? <TrendingUp size={14} /> : <TrendingDown size={14} />}
                    {formatCurrency(Math.abs(snapshot.change))}
                    ({snapshot.changePercent >= 0 ? '+' : ''}{snapshot.changePercent.toFixed(2)}%)
                  </span>
                </div>

                <div className="snapshot-holdings">
                  <h4>Top Holdings</h4>
                  <div className="holdings-list">
                    {snapshot.holdings.slice(0, 3).map((holding, idx) => (
                      <div key={idx} className="holding-item">
                        <span className="holding-name">{holding.name}</span>
                        <span className="holding-value">{formatCurrency(holding.value)}</span>
                        <span className={`holding-change ${holding.change >= 0 ? 'positive' : 'negative'}`}>
                          {holding.change >= 0 ? '+' : ''}{formatCurrency(holding.change)}
                        </span>
                      </div>
                    ))}
                  </div>
                </div>
              </GlowCard>
            </div>
          ))}
        </div>
      </div>

      {/* Growth Chart Placeholder */}
      <GlowCard className="story-chart-placeholder">
        <div className="chart-placeholder-content">
          <BarChart3 size={48} />
          <h3>Growth Visualization</h3>
          <p>Interactive chart showing portfolio growth over time will be implemented here.</p>
          <div className="dummy-chart">
            <div className="chart-bar" style={{ height: '40%' }}></div>
            <div className="chart-bar" style={{ height: '60%' }}></div>
            <div className="chart-bar" style={{ height: '80%' }}></div>
            <div className="chart-bar" style={{ height: '55%' }}></div>
            <div className="chart-bar" style={{ height: '90%' }}></div>
          </div>
        </div>
      </GlowCard>
    </div>
  );
};

export default StoryPage;