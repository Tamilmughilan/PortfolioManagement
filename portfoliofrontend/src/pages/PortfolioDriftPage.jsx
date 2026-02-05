import React, { useEffect, useState } from 'react';
import { BookOpen, TrendingUp, TrendingDown, Calendar } from 'lucide-react';
import { getPortfolioDriftStory, getPortfolioDashboard, refreshSnapshots } from '../services/api';
import SectionHeader from '../components/reactbits/SectionHeader';
import StatCard from '../components/reactbits/StatCard';
import GlowCard from '../components/reactbits/GlowCard';
import SkeletonCard from '../components/reactbits/SkeletonCard';
import '../styles/Drift.css';

const PortfolioDriftPage = ({ portfolioId }) => {
  const [drift, setDrift] = useState(null);
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [refreshing, setRefreshing] = useState(false);

  useEffect(() => {
    const loadDrift = async () => {
      if (!portfolioId) return;
      try {
        setLoading(true);
        const [driftRes, dashboardRes] = await Promise.all([
          getPortfolioDriftStory(portfolioId),
          getPortfolioDashboard(portfolioId)
        ]);
        setDrift(driftRes.data);
        setDashboard(dashboardRes.data);
        setError(null);
      } catch (err) {
        setError('Unable to load drift story.');
      } finally {
        setLoading(false);
      }
    };

    loadDrift();
  }, [portfolioId]);

  const handleRefreshStory = async () => {
    if (!portfolioId) return;
    try {
      setRefreshing(true);
      await refreshSnapshots(portfolioId);
      const [driftRes, dashboardRes] = await Promise.all([
        getPortfolioDriftStory(portfolioId),
        getPortfolioDashboard(portfolioId)
      ]);
      setDrift(driftRes.data);
      setDashboard(dashboardRes.data);
      setError(null);
    } catch (err) {
      setError('Unable to refresh drift story.');
    } finally {
      setRefreshing(false);
    }
  };

  if (loading) {
    return (
      <div className="drift-page">
        <SectionHeader
          title="Portfolio Drift"
          subtitle="We are assembling your story..."
          icon={<BookOpen size={22} />}
        />
        <div className="drift-stats">
          {Array.from({ length: 3 }).map((_, index) => (
            <SkeletonCard key={index} lines={2} />
          ))}
        </div>
        <GlowCard className="drift-story-card">
          <SkeletonCard lines={4} />
        </GlowCard>
      </div>
    );
  }

  if (error) {
    return <div className="drift-error">{error}</div>;
  }

  if (!drift) {
    return <div className="drift-empty">No drift story available.</div>;
  }

  const driftPercent = drift.driftPercent ?? 0;
  const driftValue = drift.driftValue ?? 0;
  const isPositive = driftPercent >= 0;
  const currency = drift.baseCurrency || '';

  const buildPath = (points, width, height) => {
    if (!points || points.length === 0) return '';
    const values = points.map(p => Number(p.totalValue || 0));
    const min = Math.min(...values);
    const max = Math.max(...values);
    const range = max - min || 1;
    return points
      .map((point, index) => {
        const x = (index / Math.max(points.length - 1, 1)) * width;
        const y = height - ((Number(point.totalValue || 0) - min) / range) * height;
        return `${index === 0 ? 'M' : 'L'} ${x} ${y}`;
      })
      .join(' ');
  };

  return (
    <div className="drift-page">
      <div className="drift-header-row">
        <SectionHeader
          title="Portfolio Drift"
          subtitle="A clear story of how your portfolio evolved from day one."
          icon={<BookOpen size={22} />}
        />
        <button
          className="drift-refresh-btn"
          onClick={handleRefreshStory}
          disabled={refreshing}
          type="button"
        >
          {refreshing ? 'Refreshing...' : 'Refresh Drift Story'}
        </button>
      </div>

      <div className="drift-hero">
        <GlowCard className="drift-hero-card">
          <div className="drift-hero-header">
            <div>
              <h2>{drift.portfolioName}</h2>
              <p>{drift.narrative}</p>
            </div>
            <div className={`drift-hero-badge ${isPositive ? 'positive' : 'negative'}`}>
              {isPositive ? <TrendingUp size={18} /> : <TrendingDown size={18} />}
              <span>{isPositive ? '+' : ''}{Number(driftPercent).toFixed(2)}%</span>
            </div>
          </div>
          <div className="drift-hero-dates">
            <div>
              <Calendar size={16} />
              <span>Start: {drift.initialDate}</span>
            </div>
            <div>
              <Calendar size={16} />
              <span>Latest: {drift.latestDate}</span>
            </div>
          </div>
        </GlowCard>
      </div>

      <div className="drift-stats">
        <StatCard
          label="Initial Value"
          value={`${currency} ${Number(drift.initialValue || 0).toFixed(2)}`}
          subtext="Where your story started"
          icon={<TrendingUp size={18} />}
        />
        <StatCard
          label="Latest Value"
          value={`${currency} ${Number(drift.latestValue || 0).toFixed(2)}`}
          subtext="Where you are now"
          icon={<TrendingUp size={18} />}
        />
        <StatCard
          label="Total Drift"
          value={`${currency} ${Number(Math.abs(driftValue)).toFixed(2)}`}
          subtext={`${isPositive ? '+' : '-'}${Number(Math.abs(driftPercent)).toFixed(2)}% from start`}
          icon={isPositive ? <TrendingUp size={18} /> : <TrendingDown size={18} />}
          trend={isPositive ? 'positive' : 'negative'}
        />
      </div>

      {drift.timeline && drift.timeline.length > 1 && (
        <GlowCard className="drift-chart-card">
          <h3>Value Journey</h3>
          <p className="drift-chart-subtitle">
            How your total portfolio value moved from the first snapshot to the latest.
          </p>
          <div className="drift-chart">
            <svg viewBox="0 0 320 140" preserveAspectRatio="none">
              <path
                d={buildPath(drift.timeline, 300, 110)}
                className="drift-chart-line"
              />
            </svg>
            <div className="drift-chart-footer">
              <span>{drift.initialDate}</span>
              <span>{drift.latestDate}</span>
            </div>
          </div>
        </GlowCard>
      )}

      <GlowCard className="drift-story-card">
        <h3>Storyline</h3>
        <div className="drift-timeline">
          {(drift.timeline || []).map((entry, index) => {
            const totalValue = Number(entry.totalValue || 0);
            let entryPercent = 0;
            const initial = Number(drift.initialValue || 0);
            if (initial !== 0) {
              entryPercent = ((totalValue - initial) / initial) * 100;
            }
            const entryPositive = entryPercent >= 0;
            return (
              <div key={`${entry.date || entry.snapshotDate}-${index}`} className="drift-entry">
                <div className="drift-entry-marker"></div>
                <div className="drift-entry-content">
                  <div className="drift-entry-header">
                    <span className="drift-entry-title">Chapter {index + 1}</span>
                    <span className="drift-entry-date">{entry.date || entry.snapshotDate}</span>
                  </div>
                  <p className="drift-entry-story">{entry.story}</p>
                  <div className="drift-entry-metrics">
                    <span>{currency} {totalValue.toFixed(2)}</span>
                    <span className={entryPositive ? 'positive' : 'negative'}>
                      {entryPositive ? '+' : '-'}{Number(Math.abs(entryPercent)).toFixed(2)}%
                    </span>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </GlowCard>

      {dashboard && dashboard.holdings && dashboard.holdings.length > 0 && (
        <GlowCard className="drift-holdings-card">
          <h3>Holdings vs Targets</h3>
          <p className="drift-holdings-subtitle">
            See which holdings are closest to or furthest from the value you planned for.
          </p>
          <div className="drift-holdings-table">
            <div className="drift-holdings-header">
              <span>Asset</span>
              <span>Current</span>
              <span>Target</span>
              <span>Drift</span>
            </div>
            {dashboard.holdings.map((h) => {
              if (h.targetValue == null) return null;
              const driftAmount = Number(h.valueDrift || 0);
              const driftPct = Number(h.valueDriftPercentage || 0);
              const positive = driftAmount >= 0;
              return (
                <div key={h.holdingId} className="drift-holdings-row">
                  <span className="drift-holdings-name">{h.assetName}</span>
                  <span>{currency} {Number(h.currentValue || 0).toFixed(2)}</span>
                  <span>{currency} {Number(h.targetValue || 0).toFixed(2)}</span>
                  <span className={positive ? 'positive' : 'negative'}>
                    {positive ? '+' : '-'}
                    {Math.abs(driftPct).toFixed(2)}%
                  </span>
                </div>
              );
            })}
          </div>
        </GlowCard>
      )}
    </div>
  );
};

export default PortfolioDriftPage;
