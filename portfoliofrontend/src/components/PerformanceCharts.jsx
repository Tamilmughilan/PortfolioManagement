import React, { useEffect, useState } from 'react';
import {
  LineChart, Line, AreaChart, Area,
  PieChart, Pie, Cell,
  BarChart, Bar,
  XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer
} from 'recharts';
import { getSnapshots, getPortfolioDashboard } from '../services/api';
import '../styles/Performance.css';

const PerformanceCharts = ({ portfolioId }) => {
  const [snapshotData, setSnapshotData] = useState([]);
  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [snapshots, dashboard] = await Promise.all([
          getSnapshots(portfolioId),
          getPortfolioDashboard(portfolioId)
        ]);

        // Process snapshots for timeline chart
        const processedSnapshots = snapshots.data
          .sort((a, b) => new Date(a.snapshotDate) - new Date(b.snapshotDate))
          .map(snap => ({
            date: new Date(snap.snapshotDate).toLocaleDateString('en-US', { month: 'short', day: 'numeric' }),
            value: parseFloat(snap.totalValue),
            fullDate: snap.snapshotDate
          }));

        setSnapshotData(processedSnapshots);
        setDashboardData(dashboard.data);
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to load performance data');
      } finally {
        setLoading(false);
      }
    };

    if (portfolioId) {
      fetchData();
    }
  }, [portfolioId]);

  if (loading) {
    return <div className="performance-loading">Loading performance data...</div>;
  }

  if (error) {
    return <div className="performance-error">{error}</div>;
  }

  // Prepare allocation data
  const allocationData = dashboardData?.holdings.map(holding => ({
    name: holding.assetName,
    value: parseFloat(holding.allocation) || 0
  })) || [];

  // Prepare holdings performance data
  const holdingsPerformance = dashboardData?.holdings.map(holding => {
    const invested = parseFloat(holding.quantity) * parseFloat(holding.purchasePrice);
    const current = parseFloat(holding.quantity) * parseFloat(holding.currentPrice);
    const gain = current - invested;
    const gainPercent = invested > 0 ? ((gain / invested) * 100) : 0;

    return {
      name: holding.assetName,
      invested,
      current,
      gain,
      gainPercent
    };
  }) || [];

  const COLORS = ['#1f77b4', '#ff7f0e', '#2ca02c', '#d62728', '#9467bd', '#8c564b', '#e377c2', '#7f7f7f'];

  return (
    <div className="performance-charts">
      {/* Portfolio Value Over Time */}
      <div className="chart-section">
        <h3>Portfolio Value Over Time</h3>
        {snapshotData.length > 0 ? (
          <ResponsiveContainer width="100%" height={300}>
            <AreaChart data={snapshotData} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
              <defs>
                <linearGradient id="colorValue" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#8884d8" stopOpacity={0.8} />
                  <stop offset="95%" stopColor="#8884d8" stopOpacity={0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="date" />
              <YAxis />
              <Tooltip
                formatter={(value) => `₹${value.toFixed(2)}`}
                labelFormatter={(label) => `Date: ${label}`}
              />
              <Area
                type="monotone"
                dataKey="value"
                stroke="#8884d8"
                fillOpacity={1}
                fill="url(#colorValue)"
              />
            </AreaChart>
          </ResponsiveContainer>
        ) : (
          <p className="no-data">No snapshot data available</p>
        )}
      </div>

      {/* Asset Allocation */}
      <div className="chart-section">
        <h3>Asset Allocation</h3>
        {allocationData.length > 0 ? (
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={allocationData}
                cx="50%"
                cy="50%"
                labelLine={false}
                label={({ name, value }) => `${name}: ${value.toFixed(2)}%`}
                outerRadius={80}
                fill="#8884d8"
                dataKey="value"
              >
                {allocationData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip formatter={(value) => `${value.toFixed(2)}%`} />
            </PieChart>
          </ResponsiveContainer>
        ) : (
          <p className="no-data">No holdings data available</p>
        )}
      </div>

      {/* Holdings Performance */}
      <div className="chart-section">
        <h3>Holdings Performance</h3>
        {holdingsPerformance.length > 0 ? (
          <ResponsiveContainer width="100%" height={300}>
            <BarChart
              data={holdingsPerformance}
              margin={{ top: 20, right: 30, left: 0, bottom: 60 }}
            >
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis
                dataKey="name"
                angle={-45}
                textAnchor="end"
                height={100}
              />
              <YAxis />
              <Tooltip
                formatter={(value) => `₹${value.toFixed(2)}`}
                contentStyle={{ backgroundColor: '#f5f5f5', border: '1px solid #ccc' }}
              />
              <Legend />
              <Bar dataKey="invested" fill="#8884d8" name="Invested" />
              <Bar dataKey="current" fill="#82ca9d" name="Current Value" />
            </BarChart>
          </ResponsiveContainer>
        ) : (
          <p className="no-data">No holdings performance data available</p>
        )}
      </div>

      {/* Performance Summary */}
      <div className="performance-summary">
        <h3>Performance Summary</h3>
        <div className="summary-grid">
          {holdingsPerformance.map((holding, idx) => (
            <div key={idx} className="summary-card">
              <h4>{holding.name}</h4>
              <div className="summary-item">
                <span>Invested:</span>
                <span className="value">₹{holding.invested.toFixed(2)}</span>
              </div>
              <div className="summary-item">
                <span>Current:</span>
                <span className="value">₹{holding.current.toFixed(2)}</span>
              </div>
              <div className={`summary-item ${holding.gain >= 0 ? 'positive' : 'negative'}`}>
                <span>Gain/Loss:</span>
                <span className="value">₹{holding.gain.toFixed(2)}</span>
              </div>
              <div className={`summary-item ${holding.gainPercent >= 0 ? 'positive' : 'negative'}`}>
                <span>Return:</span>
                <span className="value">{holding.gainPercent.toFixed(2)}%</span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default PerformanceCharts;
