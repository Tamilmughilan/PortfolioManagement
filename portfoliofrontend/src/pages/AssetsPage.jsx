import React, { useEffect, useState } from 'react';
import { Edit3, Trash2, PlusCircle, Save, X } from 'lucide-react';
import { deleteHolding, getHoldings, updateHolding } from '../services/api';
import AddHoldingModal from '../components/AddHoldingModal';
import GlowCard from '../components/reactbits/GlowCard';
import SectionHeader from '../components/reactbits/SectionHeader';
import '../styles/Assets.css';

const AssetsPage = ({ portfolioId, onHoldingUpdated }) => {
  const [holdings, setHoldings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [editingId, setEditingId] = useState(null);
  const [editData, setEditData] = useState({});
  const [showAdd, setShowAdd] = useState(false);

  const loadHoldings = async () => {
    if (!portfolioId) return;
    try {
      setLoading(true);
      const response = await getHoldings(portfolioId);
      setHoldings(response.data || []);
      setError('');
    } catch (err) {
      setError('Unable to load holdings.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadHoldings();
  }, [portfolioId]);

  const startEdit = (holding) => {
    setEditingId(holding.holdingId);
    setEditData({
      assetName: holding.assetName,
      assetType: holding.assetType,
      quantity: holding.quantity,
      purchasePrice: holding.purchasePrice,
      currentPrice: holding.currentPrice,
      currency: holding.currency,
      purchaseDate: holding.purchaseDate,
      targetValue: holding.targetValue || ''
    });
  };

  const cancelEdit = () => {
    setEditingId(null);
    setEditData({});
  };

  const handleEditChange = (e) => {
    const { name, value } = e.target;
    setEditData(prev => ({ ...prev, [name]: value }));
  };

  const handleSave = async (holdingId) => {
    try {
      await updateHolding(portfolioId, holdingId, {
        ...editData,
        quantity: Number(editData.quantity),
        purchasePrice: Number(editData.purchasePrice),
        currentPrice: Number(editData.currentPrice),
        targetValue: editData.targetValue ? Number(editData.targetValue) : null
      });
      cancelEdit();
      loadHoldings();
      if (onHoldingUpdated) {
        onHoldingUpdated();
      }
    } catch (err) {
      setError('Unable to update holding.');
    }
  };

  const handleDelete = async (holdingId) => {
    try {
      await deleteHolding(portfolioId, holdingId);
      loadHoldings();
      if (onHoldingUpdated) {
        onHoldingUpdated();
      }
    } catch (err) {
      setError('Unable to delete holding.');
    }
  };

  return (
    <div className="assets-page">
      <SectionHeader
        title="Assets"
        subtitle="Manage all holdings in your selected portfolio."
        icon={<PlusCircle size={22} />}
      />

      <div className="assets-toolbar">
        <button className="assets-add" onClick={() => setShowAdd(true)}>
          <PlusCircle size={18} /> Add Holding
        </button>
      </div>

      {error && <div className="assets-error">{error}</div>}

      {loading ? (
        <GlowCard className="assets-empty">Loading holdings...</GlowCard>
      ) : holdings.length === 0 ? (
        <GlowCard className="assets-empty">No holdings yet. Add one to get started.</GlowCard>
      ) : (
        <>
          {holdings.length > 0 && (
            <GlowCard className="assets-chart-card" style={{ marginBottom: '1.5rem' }}>
              <h3>Holdings Value Distribution</h3>
              <div style={{ padding: '1rem', background: 'var(--bg-light)', borderRadius: '12px' }}>
                <svg viewBox="0 0 600 300" style={{ width: '100%', height: '300px' }}>
                  {(() => {
                    const holdingsWithValue = holdings.map(h => ({
                      ...h,
                      value: (h.currentPrice || 0) * (h.quantity || 0)
                    })).filter(h => h.value > 0);
                    
                    if (holdingsWithValue.length === 0) return null;
                    
                    const totalValue = holdingsWithValue.reduce((sum, h) => sum + h.value, 0);
                    const maxValue = Math.max(...holdingsWithValue.map(h => h.value));
                    const barWidth = 500;
                    const barHeight = 250;
                    const padding = 50;
                    const barSpacing = barHeight / holdingsWithValue.length;
                    const colors = ['#DC2626', '#E11D48', '#F97316', '#10b981', '#f59e0b', '#06b6d4', '#8b5cf6', '#ec4899'];
                    const labelRightPad = 10;
                    const labelMargin = 10;
                    const svgRight = padding + barWidth;
                    
                    return (
                      <>
                        {holdingsWithValue.map((holding, index) => {
                          const barLength = (holding.value / maxValue) * barWidth;
                          const yPos = padding + index * barSpacing;
                          const percent = (holding.value / totalValue) * 100;
                          // If the label would overflow the chart area, render it inside the bar (right-aligned)
                          const labelXOutside = padding + barLength + labelMargin;
                          const shouldLabelBeInside = labelXOutside > (svgRight - 120);
                          const labelX = shouldLabelBeInside
                                  ? Math.max(padding + 6, padding + barLength - 6)
                                  : Math.min(svgRight - labelRightPad, labelXOutside);
                          
                          return (
                            <g key={holding.holdingId}>
                              <text x="5" y={yPos + 12} fontSize="11" fill="var(--text-dark)" style={{ fontWeight: '500' }}>
                                {holding.assetName.substring(0, 20)}
                              </text>
                              <rect
                                x={padding}
                                y={yPos - 8}
                                width={barLength}
                                height={barSpacing - 4}
                                fill={colors[index % colors.length]}
                                opacity="0.8"
                                rx="4"
                              />
                              <text
                                x={labelX}
                                y={yPos + 4}
                                fontSize="10"
                                fill={shouldLabelBeInside ? "var(--text-dark)" : "var(--text-light)"}
                                textAnchor={shouldLabelBeInside ? "end" : "start"}
                              >
                                {percent.toFixed(1)}% ({holding.value.toLocaleString()})
                              </text>
                            </g>
                          );
                        })}
                        <line x1={padding} y1={padding - 10} x2={padding} y2={padding + barHeight} stroke="var(--border)" strokeWidth="1" />
                        <line x1={padding} y1={padding + barHeight} x2={padding + barWidth} y2={padding + barHeight} stroke="var(--border)" strokeWidth="1" />
                        <text x={padding + barWidth / 2} y={barHeight + padding + 25} textAnchor="middle" fontSize="11" fill="var(--text-light)">Value</text>
                      </>
                    );
                  })()}
                </svg>
              </div>
            </GlowCard>
          )}
          <div className="assets-grid">
          {holdings.map(holding => (
            <GlowCard key={holding.holdingId} className="assets-card">
              {editingId === holding.holdingId ? (
                <div className="assets-edit">
                  <div className="assets-row">
                    <label>Asset Name</label>
                    <input name="assetName" value={editData.assetName || ''} onChange={handleEditChange} />
                  </div>
                  <div className="assets-row">
                    <label>Asset Type</label>
                    <input name="assetType" value={editData.assetType || ''} onChange={handleEditChange} />
                  </div>
                  <div className="assets-row">
                    <label>Quantity</label>
                    <input name="quantity" type="number" value={editData.quantity || ''} onChange={handleEditChange} />
                  </div>
                  <div className="assets-row">
                    <label>Purchase Price</label>
                    <input name="purchasePrice" type="number" value={editData.purchasePrice || ''} onChange={handleEditChange} />
                  </div>
                  <div className="assets-row">
                    <label>Current Price</label>
                    <input name="currentPrice" type="number" value={editData.currentPrice || ''} onChange={handleEditChange} />
                  </div>
                  <div className="assets-row">
                    <label>Currency</label>
                    <input name="currency" value={editData.currency || ''} onChange={handleEditChange} />
                  </div>
                  <div className="assets-row">
                    <label>Purchase Date</label>
                    <input name="purchaseDate" type="date" value={editData.purchaseDate || ''} onChange={handleEditChange} />
                  </div>
                  <div className="assets-row">
                    <label>Target Value (Optional)</label>
                    <input name="targetValue" type="number" value={editData.targetValue || ''} onChange={handleEditChange} placeholder="Leave empty if no target" />
                  </div>
                  <div className="assets-actions">
                    <button onClick={() => handleSave(holding.holdingId)}>
                      <Save size={16} /> Save
                    </button>
                    <button className="ghost" onClick={cancelEdit}>
                      <X size={16} /> Cancel
                    </button>
                  </div>
                </div>
              ) : (
                <div className="assets-view">
                  <div>
                    <h3>{holding.assetName}</h3>
                    <p>{holding.assetType}</p>
                  </div>
                  <div className="assets-metrics">
                    <span>Qty: {holding.quantity}</span>
                    <span>Buy: {holding.purchasePrice}</span>
                    <span>Now: {holding.currentPrice}</span>
                    <span>Curr: {holding.currency}</span>
                  </div>
                  {holding.targetValue != null && (
                    <div className="assets-metrics">
                      <span>Target: {holding.targetValue}</span>
                      {holding.valueDrift != null && (
                        <span className={holding.valueDrift >= 0 ? 'positive' : 'negative'}>
                          Drift: {holding.valueDrift >= 0 ? '+' : ''}{Number(holding.valueDrift).toFixed(2)}
                          {holding.valueDriftPercentage != null && (
                            <span> ({holding.valueDriftPercentage >= 0 ? '+' : ''}{Number(holding.valueDriftPercentage).toFixed(2)}%)</span>
                          )}
                        </span>
                      )}
                    </div>
                  )}
                  <div className="assets-actions">
                    <button onClick={() => startEdit(holding)}>
                      <Edit3 size={16} /> Edit
                    </button>
                    <button className="danger" onClick={() => handleDelete(holding.holdingId)}>
                      <Trash2 size={16} /> Delete
                    </button>
                  </div>
                </div>
              )}
            </GlowCard>
          ))}
        </div>
        </>
      )}

      {showAdd && (
        <AddHoldingModal
          portfolioId={portfolioId}
          onClose={() => setShowAdd(false)}
          onSuccess={() => {
            setShowAdd(false);
            loadHoldings();
            if (onHoldingUpdated) {
              onHoldingUpdated();
            }
          }}
        />
      )}
    </div>
  );
};

export default AssetsPage;