import React, { useState } from 'react';
import { X, Plus } from 'lucide-react';
import { addHolding } from '../services/api';
import '../styles/Modal.css';

const AddHoldingModal = ({ portfolioId, onClose, onSuccess }) => {
  const [formData, setFormData] = useState({
    assetName: '',
    assetType: 'STOCK',
    quantity: '',
    purchasePrice: '',
    currentPrice: '',
    currency: 'INR',
    purchaseDate: new Date().toISOString().split('T')[0]
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const assetTypes = ['STOCK', 'BOND', 'MUTUAL_FUND', 'ETF', 'CRYPTO', 'CASH', 'REAL_ESTATE', 'GOLD', 'OTHER'];
  const currencies = ['INR', 'USD', 'EUR', 'GBP'];

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.assetName.trim()) {
      setError('Asset name is required');
      return;
    }
    if (!formData.quantity || parseFloat(formData.quantity) <= 0) {
      setError('Quantity must be greater than 0');
      return;
    }
    if (!formData.purchasePrice || parseFloat(formData.purchasePrice) <= 0) {
      setError('Purchase price must be greater than 0');
      return;
    }
    if (!formData.currentPrice || parseFloat(formData.currentPrice) <= 0) {
      setError('Current price must be greater than 0');
      return;
    }

    try {
      setLoading(true);
      await addHolding(portfolioId, {
        ...formData,
        quantity: parseFloat(formData.quantity),
        purchasePrice: parseFloat(formData.purchasePrice),
        currentPrice: parseFloat(formData.currentPrice)
      });
      onSuccess();
      onClose();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to add holding');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h2>
            <Plus size={24} />
            Add New Holding
          </h2>
          <button className="close-btn" onClick={onClose}>
            <X size={24} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="modal-form">
          {error && <div className="form-error">{error}</div>}

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="assetName">Asset Name *</label>
              <input
                type="text"
                id="assetName"
                name="assetName"
                value={formData.assetName}
                onChange={handleChange}
                placeholder="e.g., Apple Inc, Reliance"
                autoFocus
              />
            </div>

            <div className="form-group">
              <label htmlFor="assetType">Asset Type</label>
              <select
                id="assetType"
                name="assetType"
                value={formData.assetType}
                onChange={handleChange}
              >
                {assetTypes.map(type => (
                  <option key={type} value={type}>{type.replace('_', ' ')}</option>
                ))}
              </select>
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="quantity">Quantity *</label>
              <input
                type="number"
                id="quantity"
                name="quantity"
                value={formData.quantity}
                onChange={handleChange}
                placeholder="0.00"
                step="0.0001"
                min="0"
              />
            </div>

            <div className="form-group">
              <label htmlFor="currency">Currency</label>
              <select
                id="currency"
                name="currency"
                value={formData.currency}
                onChange={handleChange}
              >
                {currencies.map(curr => (
                  <option key={curr} value={curr}>{curr}</option>
                ))}
              </select>
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="purchasePrice">Purchase Price *</label>
              <input
                type="number"
                id="purchasePrice"
                name="purchasePrice"
                value={formData.purchasePrice}
                onChange={handleChange}
                placeholder="0.00"
                step="0.01"
                min="0"
              />
            </div>

            <div className="form-group">
              <label htmlFor="currentPrice">Current Price *</label>
              <input
                type="number"
                id="currentPrice"
                name="currentPrice"
                value={formData.currentPrice}
                onChange={handleChange}
                placeholder="0.00"
                step="0.01"
                min="0"
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="purchaseDate">Purchase Date</label>
            <input
              type="date"
              id="purchaseDate"
              name="purchaseDate"
              value={formData.purchaseDate}
              onChange={handleChange}
            />
          </div>

          {formData.quantity && formData.purchasePrice && formData.currentPrice && (
            <div className="form-preview">
              <div className="preview-item">
                <span>Total Invested</span>
                <span className="preview-value">
                  {formData.currency} {(parseFloat(formData.quantity) * parseFloat(formData.purchasePrice)).toFixed(2)}
                </span>
              </div>
              <div className="preview-item">
                <span>Current Value</span>
                <span className="preview-value">
                  {formData.currency} {(parseFloat(formData.quantity) * parseFloat(formData.currentPrice)).toFixed(2)}
                </span>
              </div>
              <div className="preview-item">
                <span>Gain/Loss</span>
                <span className={`preview-value ${parseFloat(formData.currentPrice) >= parseFloat(formData.purchasePrice) ? 'positive' : 'negative'}`}>
                  {formData.currency} {(parseFloat(formData.quantity) * (parseFloat(formData.currentPrice) - parseFloat(formData.purchasePrice))).toFixed(2)}
                </span>
              </div>
            </div>
          )}

          <div className="form-actions">
            <button type="button" className="btn-secondary" onClick={onClose}>
              Cancel
            </button>
            <button type="submit" className="btn-primary" disabled={loading}>
              {loading ? 'Adding...' : 'Add Holding'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AddHoldingModal;

