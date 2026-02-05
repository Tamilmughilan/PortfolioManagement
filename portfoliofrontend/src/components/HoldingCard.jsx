import React from 'react';
import { TrendingUp, TrendingDown } from 'lucide-react';
import '../styles/HoldingCard.css';
import { formatWithSymbol } from '../utils/currency';

const HoldingCard = ({ holding, currency }) => {
  const isPositive = holding.gainLoss >= 0;

  return (
    <div className={`holding-card ${holding.assetType.toLowerCase()}`}>
      <div className="holding-header">
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
          <span className="value">{formatWithSymbol(currency, holding.purchasePrice)}</span>
        </div>
        <div className="holding-stat">
          <span className="label">Current Price</span>
          <span className="value">{formatWithSymbol(currency, holding.currentPrice)}</span>
        </div>
      </div>

      <div className="holding-footer">
        <div>
          <span className="label">Invested</span>
          <span className="value">{formatWithSymbol(currency, holding.totalInvested)}</span>
        </div>
        <div>
          <span className="label">Current Value</span>
          <span className="value">{formatWithSymbol(currency, holding.currentValue)}</span>
        </div>
      </div>

      <div className={`holding-gain-loss ${isPositive ? 'positive' : 'negative'}`}>
        <span className="label">Gain / Loss</span>
        <span className="value">{isPositive ? '+' : ''}{formatWithSymbol(currency, holding.gainLoss)}</span>
      </div>

      <div className="holding-date">
        <small>Purchased on {new Date(holding.purchaseDate).toLocaleDateString()}</small>
      </div>
    </div>
  );
};

export default HoldingCard;