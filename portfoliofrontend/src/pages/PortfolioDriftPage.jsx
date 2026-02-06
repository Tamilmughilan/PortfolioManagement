import React, { useEffect, useState } from 'react';
import { BookOpen, TrendingUp, TrendingDown, Calendar, Target } from 'lucide-react';
import { getPortfolioDriftStory, refreshSnapshots, getPortfolioDashboard } from '../services/api';
import SectionHeader from '../components/reactbits/SectionHeader';
import StatCard from '../components/reactbits/StatCard';
import GlowCard from '../components/reactbits/GlowCard';
import SkeletonCard from '../components/reactbits/SkeletonCard';
import '../styles/Drift.css';
import { formatWithSymbol } from '../utils/currency';

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
        const [driftResponse, dashboardResponse] = await Promise.all([
          getPortfolioDriftStory(portfolioId),
          getPortfolioDashboard(portfolioId).catch(() => null)
        ]);
        setDrift(driftResponse.data);
        setDashboard(dashboardResponse?.data || null);
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
      const [driftResponse, dashboardResponse] = await Promise.all([
        getPortfolioDriftStory(portfolioId),
        getPortfolioDashboard(portfolioId).catch(() => null)
      ]);
      setDrift(driftResponse.data);
      setDashboard(dashboardResponse?.data || null);
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
          value={formatWithSymbol(currency, Number(drift.initialValue || 0))}
          subtext="Where your story started"
          icon={<TrendingUp size={18} />}
        />
        <StatCard
          label="Latest Value"
          value={formatWithSymbol(currency, Number(drift.latestValue || 0))}
          subtext="Where you are now"
          icon={<TrendingUp size={18} />}
        />
        <StatCard
          label="Total Drift"
          value={formatWithSymbol(currency, Number(Math.abs(driftValue)).toFixed(2))}
          subtext={`${isPositive ? '+' : '-'}${Number(Math.abs(driftPercent)).toFixed(2)}% from start`}
          icon={isPositive ? <TrendingUp size={18} /> : <TrendingDown size={18} />}
          trend={isPositive ? 'positive' : 'negative'}
        />
      </div>

      {drift.timeline && drift.timeline.length > 0 && (
        <GlowCard className="drift-story-card">
          <h3>Value Journey Visualization</h3>
          <div style={{ padding: '1rem', background: 'var(--bg-light)', borderRadius: '12px', marginBottom: '1rem' }}>
            <svg viewBox="0 0 800 200" style={{ width: '100%', height: '200px', overflow: 'visible' }}>
              {(() => {
                const timeline = drift.timeline || [];
                if (timeline.length === 0) return null;
                
                const values = timeline.map(e => Number(e.totalValue || 0));
                const minValue = Math.min(...values);
                const maxValue = Math.max(...values);
                const range = maxValue - minValue || 1;
                const padding = 20;
                const width = 800 - (padding * 2);
                const height = 200 - (padding * 2);
                
                const points = timeline.map((entry, index) => {
                  const x = padding + (index / Math.max(timeline.length - 1, 1)) * width;
                  const y = padding + height - ((Number(entry.totalValue || 0) - minValue) / range) * height;
                  return { x, y, value: Number(entry.totalValue || 0), date: entry.date };
                });
                
                const pathData = points.map((p, i) => `${i === 0 ? 'M' : 'L'} ${p.x} ${p.y}`).join(' ');
                
                return (
                  <>
                    <defs>
                      <linearGradient id="lineGradient" x1="0%" y1="0%" x2="100%" y2="0%">
                        <stop offset="0%" stopColor={isPositive ? '#10b981' : '#ef4444'} stopOpacity="0.8" />
                        <stop offset="100%" stopColor={isPositive ? '#10b981' : '#ef4444'} stopOpacity="0.4" />
                      </linearGradient>
                    </defs>
                    <line x1={padding} y1={padding + height} x2={width + padding} y2={padding + height} stroke="var(--border)" strokeWidth="1" />
                    <line x1={padding} y1={padding} x2={padding} y2={padding + height} stroke="var(--border)" strokeWidth="1" />
                    <path
                      d={pathData}
                      fill="none"
                      stroke={isPositive ? '#10b981' : '#ef4444'}
                      strokeWidth="3"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                    />
                    {points.map((point, index) => (
                      <g key={index}>
                        <circle
                          cx={point.x}
                          cy={point.y}
                          r="4"
                          fill={isPositive ? '#10b981' : '#ef4444'}
                          stroke="#fff"
                          strokeWidth="2"
                        />
                        {index === 0 && (
                          <text x={point.x} y={point.y - 8} textAnchor="middle" fontSize="10" fill="var(--text-light)">
                            Start
                          </text>
                        )}
                        {index === points.length - 1 && (
                          <text x={point.x} y={point.y - 8} textAnchor="middle" fontSize="10" fill="var(--text-light)">
                            Now
                          </text>
                        )}
                      </g>
                    ))}
                    <text x={padding + width / 2} y={height + padding + 30} textAnchor="middle" fontSize="11" fill="var(--text-light)">
                      Timeline
                    </text>
                    <text x={padding - 30} y={padding + height / 2} textAnchor="middle" fontSize="11" fill="var(--text-light)" transform={`rotate(-90 ${padding - 30} ${padding + height / 2})`}>
                      Value ({currency})
                    </text>
                  </>
                );
              })()}
            </svg>
          </div>
        </GlowCard>
      )}

      <GlowCard className="drift-story-card">
        <h3>Storyline</h3>
        <div className="drift-timeline">
          {(drift.timeline || []).map((entry, index) => {
            const entryPercent = entry.driftPercentFromStart ?? 0;
            const entryPositive = entryPercent >= 0;
            return (
              <div key={`${entry.date}-${index}`} className="drift-entry">
                <div className="drift-entry-marker"></div>
                <div className="drift-entry-content">
                  <div className="drift-entry-header">
                    <span className="drift-entry-title">Chapter {index + 1}</span>
                    <span className="drift-entry-date">{entry.date}</span>
                  </div>
                  <p className="drift-entry-story">{entry.story}</p>
                  <div className="drift-entry-metrics">
                    <span>{formatWithSymbol(currency, Number(entry.totalValue || 0))}</span>
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

      {dashboard && dashboard.holdings && dashboard.holdings.some(h => h.targetValue != null) && (
        <GlowCard className="drift-story-card">
          <h3>
            <Target size={18} style={{ marginRight: '0.5rem', verticalAlign: 'middle' }} />
            Holdings vs Target Values
          </h3>
          {(() => {
            const holdingsWithTargets = dashboard.holdings.filter(h => h.targetValue != null);
            if (holdingsWithTargets.length === 0) return null;
            
            return (
              <div style={{ padding: '1rem', background: 'var(--bg-light)', borderRadius: '12px', marginBottom: '1rem' }}>
                <svg viewBox="0 0 600 300" style={{ width: '100%', height: '300px' }}>
                  {(() => {
                    const maxValue = Math.max(...holdingsWithTargets.map(h => Math.max(Number(h.currentValue || 0), Number(h.targetValue || 0))));
                    const barWidth = 500;
                    const barHeight = 250;
                    const padding = 60;
                    const barSpacing = barHeight / holdingsWithTargets.length;
                    const svgRight = padding + barWidth;
                    const labelMargin = 10;
                    const labelRightPad = 10;
                    
                    return (
                      <>
                        {holdingsWithTargets.map((holding, index) => {
                          const currentVal = Number(holding.currentValue || 0);
                          const targetVal = Number(holding.targetValue || 0);
                          const currentBar = (currentVal / maxValue) * barWidth;
                          const targetBar = (targetVal / maxValue) * barWidth;
                          const yPos = padding + index * barSpacing;
                          const drift = holding.valueDriftPercentage || 0;
                          const barMax = Math.max(currentBar, targetBar);
                          const labelXOutside = padding + barMax + labelMargin;
                          const shouldLabelBeInside = labelXOutside > (svgRight - 80);
                          const labelX = shouldLabelBeInside
                                  ? Math.max(padding + 6, padding + barMax - 6)
                                  : Math.min(svgRight - labelRightPad, labelXOutside);
                          
                          return (
                            <g key={holding.holdingId || index}>
                              <text x="5" y={yPos + 12} fontSize="11" fill="var(--text-dark)" style={{ fontWeight: '500' }}>
                                {holding.assetName.substring(0, 15)}
                              </text>
                              <rect
                                x={padding}
                                y={yPos - 10}
                                width={targetBar}
                                height={barSpacing / 3 - 2}
                                fill="#10b981"
                                opacity="0.4"
                                rx="2"
                              />
                              <rect
                                x={padding}
                                y={yPos - 2}
                                width={currentBar}
                                height={barSpacing / 3 - 2}
                                fill={drift >= 0 ? '#ef4444' : '#3b82f6'}
                                opacity="0.8"
                                rx="2"
                              />
                              <text
                                x={labelX}
                                y={yPos + 4}
                                fontSize="10"
                                fill={shouldLabelBeInside ? "var(--text-dark)" : "var(--text-light)"}
                                textAnchor={shouldLabelBeInside ? "end" : "start"}
                              >
                                {drift >= 0 ? '+' : ''}{drift.toFixed(1)}%
                              </text>
                            </g>
                          );
                        })}
                        <line x1={padding} y1={padding - 15} x2={padding} y2={padding + barHeight} stroke="var(--border)" strokeWidth="1" />
                        <line x1={padding} y1={padding + barHeight} x2={padding + barWidth} y2={padding + barHeight} stroke="var(--border)" strokeWidth="1" />
                        <text x={padding + barWidth / 2} y={barHeight + padding + 25} textAnchor="middle" fontSize="11" fill="var(--text-light)">Value</text>
                        <text x="15" y={padding + barHeight / 2} textAnchor="middle" fontSize="11" fill="var(--text-light)" transform={`rotate(-90 15 ${padding + barHeight / 2})`}>Holdings</text>
                        <g>
                          <rect x={padding + barWidth + 20} y={padding - 10} width="12" height="8" fill="#10b981" opacity="0.4" />
                          <text x={padding + barWidth + 35} y={padding - 2} fontSize="10" fill="var(--text-light)">Target</text>
                          <rect x={padding + barWidth + 20} y={padding + 5} width="12" height="8" fill="#ef4444" opacity="0.8" />
                          <text x={padding + barWidth + 35} y={padding + 13} fontSize="10" fill="var(--text-light)">Current</text>
                        </g>
                      </>
                    );
                  })()}
                </svg>
              </div>
            );
          })()}
          <div className="drift-timeline">
            {dashboard.holdings
              .filter(h => h.targetValue != null)
              .map((holding, index) => {
                const isOnTarget = holding.valueDrift != null && Math.abs(holding.valueDriftPercentage || 0) < 5;
                const isOver = holding.valueDrift != null && holding.valueDrift > 0;
                return (
                  <div key={holding.holdingId || index} className="drift-entry">
                    <div className="drift-entry-marker"></div>
                    <div className="drift-entry-content">
                      <div className="drift-entry-header">
                        <span className="drift-entry-title">{holding.assetName}</span>
                        <span className={`drift-entry-date ${isOnTarget ? 'positive' : isOver ? 'positive' : 'negative'}`}>
                          {isOnTarget ? 'On Target' : isOver ? 'Above Target' : 'Below Target'}
                        </span>
                      </div>
                      <p className="drift-entry-story">
                        Current: {formatWithSymbol(currency, Number(holding.currentValue || 0))} • 
                        Target: {formatWithSymbol(currency, Number(holding.targetValue || 0))}
                      </p>
                      {holding.valueDrift != null && (
                        <div className="drift-entry-metrics">
                          <span>Drift: {formatWithSymbol(currency, Number(holding.valueDrift))}</span>
                          {holding.valueDriftPercentage != null && (
                            <span className={holding.valueDrift >= 0 ? 'positive' : 'negative'}>
                              {holding.valueDrift >= 0 ? '+' : ''}{Number(holding.valueDriftPercentage).toFixed(2)}%
                            </span>
                          )}
                        </div>
                      )}
                    </div>
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