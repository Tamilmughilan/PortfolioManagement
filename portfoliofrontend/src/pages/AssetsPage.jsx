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
      purchaseDate: holding.purchaseDate
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
        currentPrice: Number(editData.currentPrice)
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
