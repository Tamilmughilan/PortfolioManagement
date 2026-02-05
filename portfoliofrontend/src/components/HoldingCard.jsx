import React, { useState } from 'react';
import { TrendingUp, TrendingDown } from 'lucide-react';
import '../styles/HoldingCard.css';
import { formatWithSymbol } from '../utils/currency';

const HoldingCard = ({ holding, currency }) => {
  const isPositive = holding.gainLoss >= 0;
  const [logoError, setLogoError] = useState(false);
  
  // Use holding's currency if available, otherwise fall back to portfolio currency
  const displayCurrency = holding.currency || currency || 'USD';

  const getBackgroundColor = () => {
    const colors = {
      stock: '#3B82F6',
      crypto: '#8B5CF6', 
      etf: '#10B981',
      mutual_fund: '#F59E0B',
      bond: '#EC4899',
      gold: '#FBBF24',
      cash: '#6B7280',
      real_estate: '#EF4444',
    };
    return colors[holding.assetType] || '#6B7280';
  };

  return (
    <div className={`holding-card ${holding.assetType.toLowerCase()}`}>
      <div className="holding-header">
        <div className="holding-logo" style={{ backgroundColor: getBackgroundColor() + '20' }}>
          {holding.logo && !logoError ? (
            <img 
              src={holding.logo} 
              alt={holding.assetName}
              onError={() => setLogoError(true)}
            />
          ) : (
            <div className="logo-fallback">{holding.ticker?.substring(0, 2).toUpperCase() || holding.assetName.charAt(0).toUpperCase()}</div>
          )}
        </div>
        <div className="holding-name">
          <h3>{holding.assetName}</h3>
          <span className="asset-type">{holding.assetType}</span>
        </div>
        <div className={`gain-loss-badge ${isPositive ? 'positive' : 'negative'}`}>
          {isPositive ? <TrendingUp size={16} /> : <TrendingDown size={16} />}
          <span>{isPositive ? '+' : ''}{holding.gainLossPercentage?.toFixed(2)}%</span>
        </div>
      </div>

      <div className="holding-body">
        <div className="holding-stat">
          <span className="label">Quantity</span>
          <span className="value">{holding.quantity.toFixed(4)}</span>
        </div>
        <div className="holding-stat">
          <span className="label">Entry Price</span>
          <span className="value">{formatWithSymbol(displayCurrency, holding.purchasePrice)}</span>
        </div>
        <div className="holding-stat">
          <span className="label">Current Price</span>
          <span className="value">{formatWithSymbol(displayCurrency, holding.currentPrice)}</span>
        </div>
      </div>

      <div className="holding-footer">
        <div>
          <span className="label">Invested</span>
          <span className="value">{formatWithSymbol(displayCurrency, holding.totalInvested)}</span>
        </div>
        <div>
          <span className="label">Current Value</span>
          <span className="value">{formatWithSymbol(displayCurrency, holding.currentValue)}</span>
        </div>
      </div>

      <div className={`holding-gain-loss ${isPositive ? 'positive' : 'negative'}`}>
        <span className="label">Gain / Loss</span>
        <span className="value">{isPositive ? '+' : ''}{formatWithSymbol(displayCurrency, holding.gainLoss)}</span>
      </div>

      <div className="holding-date">
        <small>Purchased on {new Date(holding.purchaseDate).toLocaleDateString()}</small>
      </div>
    </div>
  );
};

export default HoldingCard;