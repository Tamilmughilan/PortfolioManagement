import React, { useState, useEffect } from 'react';
import {
  getAnalyticsSummary,
  getAllocationPercentages,
  getTargetDrift,
  getTargets
} from '../services/api';
import StatCard from '../components/reactbits/StatCard';
import GlowCard from '../components/reactbits/GlowCard';
import SectionHeader from '../components/reactbits/SectionHeader';
import '../styles/Analytics.css';

const AnalyticsPage = ({ portfolioId }) => {
  const [summary, setSummary] = useState(null);
  const [allocations, setAllocations] = useState({});
  const [drift, setDrift] = useState({});
  const [targets, setTargets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchAnalytics = async () => {
      if (!portfolioId) return;

      try {
        setLoading(true);
        setError(null);

        const [summaryRes, allocRes, driftRes, targetsRes] = await Promise.all([
          getAnalyticsSummary(portfolioId),
          getAllocationPercentages(portfolioId),
          getTargetDrift(portfolioId),
          getTargets(portfolioId)
        ]);

        setSummary(summaryRes.data);
        setAllocations(allocRes.data);
        setDrift(driftRes.data);
        setTargets(targetsRes.data);
      } catch (err) {
        setError('Failed to load analytics data');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchAnalytics();
  }, [portfolioId]);

  if (loading) {
    return (
      <div className="analytics-loading">
        <div className="spinner"></div>
        <p>Loading analytics...</p>
      </div>
    );
  }

  if (error) {
    return <div className="analytics-error">{error}</div>;
  }

  const allocationEntries = Object.entries(allocations);
  const driftEntries = Object.entries(drift);
  const colors = ['#DC2626', '#E11D48', '#F97316', '#10b981', '#f59e0b', '#06b6d4'];

  const getGainLossPercent = () => {
    if (!summary || summary.totalCost === 0) return 0;
    return ((summary.totalGainLoss / summary.totalCost) * 100).toFixed(2);
  };

  return (
    <div className="analytics-page">
      <SectionHeader
        title="Portfolio Analytics"
        subtitle="Deep insights into your investment performance"
      />

      {/* Summary Cards */}
      <div className="analytics-summary">
        <StatCard
          label="Market Value"
          value={`₹${summary?.totalMarketValue?.toLocaleString() || '0'}`}
          subtext="Current market value"
        />
        <StatCard
          label="Total Cost"
          value={`₹${summary?.totalCost?.toLocaleString() || '0'}`}
          subtext="Cost basis"
        />
        <StatCard
          label="Total Gain/Loss"
          value={`${summary?.totalGainLoss >= 0 ? '+' : ''}₹${summary?.totalGainLoss?.toLocaleString() || '0'}`}
          subtext={`${getGainLossPercent()}%`}
          trend={summary?.totalGainLoss >= 0 ? 'positive' : 'negative'}
        />
      </div>

      <div className="analytics-grid">
        {/* Allocation Chart */}
        <GlowCard className="analytics-card allocation-card">
          <h2>Asset Allocation</h2>
          {allocationEntries.length === 0 ? (
            <div className="empty-state">
              <PieChart size={48} />
              <p>No holdings to analyze</p>
            </div>
          ) : (
            <>
              <div className="donut-chart">
                <svg viewBox="0 0 100 100">
                  {allocationEntries.map((entry, index) => {
                    const [type, percent] = entry;
                    const prevPercent = allocationEntries
                      .slice(0, index)
                      .reduce((sum, [, p]) => sum + p, 0);
                    const circumference = 2 * Math.PI * 35;
                    const offset = (prevPercent / 100) * circumference;
                    const length = (percent / 100) * circumference;

                    return (
                      <circle
                        key={type}
                        cx="50"
                        cy="50"
                        r="35"
                        fill="none"
                        stroke={colors[index % colors.length]}
                        strokeWidth="12"
                        strokeDasharray={`${length} ${circumference - length}`}
                        strokeDashoffset={-offset}
                        transform="rotate(-90 50 50)"
                      />
                    );
                  })}
                  <text x="50" y="48" textAnchor="middle" className="donut-total">
                    100%
                  </text>
                  <text x="50" y="58" textAnchor="middle" className="donut-label">
                    Allocated
                  </text>
                </svg>
              </div>

              <div className="allocation-legend">
                {allocationEntries.map(([type, percent], index) => (
                  <div key={type} className="legend-item">
                    <span
                      className="legend-color"
                      style={{ backgroundColor: colors[index % colors.length] }}
                    />
                    <span className="legend-type">{type}</span>
                    <span className="legend-percent">{percent.toFixed(1)}%</span>
                  </div>
                ))}
              </div>
            </>
          )}
        </GlowCard>

        {/* Target Drift */}
        <GlowCard className="analytics-card drift-card">
          <h2>Target Drift Analysis</h2>
          {driftEntries.length === 0 ? (
            <div className="empty-state">
              <p>Set allocation targets to see drift analysis</p>
            </div>
          ) : (
            <div className="drift-list">
              {driftEntries.map(([type, driftValue]) => {
                const target = targets.find(t => t.assetType === type);
                const actualPercent = allocations[type] || 0;
                const isOnTarget = Math.abs(driftValue) < 2;
                const isOver = driftValue > 0;

                return (
                  <div key={type} className={`drift-item ${isOnTarget ? 'on-target' : isOver ? 'over' : 'under'}`}>
                    <div className="drift-header">
                      <div className="drift-type">
                        {isOnTarget ? (
                          <CheckCircle size={18} className="icon-success" />
                        ) : (
                          <AlertTriangle size={18} className="icon-warning" />
                        )}
                        <span>{type}</span>
                      </div>
                      <span className={`drift-value ${driftValue >= 0 ? 'positive' : 'negative'}`}>
                        {driftValue >= 0 ? '+' : ''}{driftValue.toFixed(2)}%
                      </span>
                    </div>

                    <div className="drift-comparison">
                      <div className="comparison-row">
                        <span>Target</span>
                        <span>{target?.targetPercentage?.toFixed(1) || '0'}%</span>
                      </div>
                      <div className="comparison-row">
                        <span>Actual</span>
                        <span>{actualPercent.toFixed(1)}%</span>
                      </div>
                    </div>

                    <div className="drift-bar">
                      <div
                        className="drift-bar-target"
                        style={{ left: `${Math.min(target?.targetPercentage || 0, 100)}%` }}
                      />
                      <div
                        className="drift-bar-actual"
                        style={{ width: `${Math.min(actualPercent, 100)}%` }}
                      />
                    </div>

                    <p className="drift-advice">
                      {isOnTarget
                        ? 'On target - well balanced!'
                        : isOver
                          ? `Consider reducing ${type} by ${Math.abs(driftValue).toFixed(1)}%`
                          : `Consider adding ${type} to reach target`
                      }
                    </p>
                  </div>
                );
              })}
            </div>
          )}
        </GlowCard>
      </div>

      {/* Targets Overview */}
      {targets.length > 0 && (
        <GlowCard className="analytics-card targets-card">
          <h2>Your Allocation Targets</h2>
          <div className="targets-grid">
            {targets.map(target => (
              <div key={target.targetId} className="target-item">
                <span className="target-type">{target.assetType}</span>
                <span className="target-percent">{target.targetPercentage}%</span>
              </div>
            ))}
          </div>
        </GlowCard>
      )}
    </div>
  );
};

export default AnalyticsPage;

