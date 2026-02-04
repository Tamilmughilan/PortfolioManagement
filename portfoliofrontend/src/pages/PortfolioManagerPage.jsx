import React, { useEffect, useState } from 'react';
import { Plus, Pencil, Trash2, FolderOpen } from 'lucide-react';
import {
  getUserPortfolios,
  createPortfolio,
  updatePortfolio,
  deletePortfolio
} from '../services/api';
import SectionHeader from '../components/reactbits/SectionHeader';
import GlowCard from '../components/reactbits/GlowCard';
import '../styles/PortfolioManager.css';

const PortfolioManagerPage = ({ user }) => {
  const [portfolios, setPortfolios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [formState, setFormState] = useState({ portfolioName: '', baseCurrency: 'INR' });
  const [editingId, setEditingId] = useState(null);

  const loadPortfolios = async () => {
    if (!user?.userId) return;
    try {
      setLoading(true);
      const response = await getUserPortfolios(user.userId);
      setPortfolios(response.data || []);
      setError('');
    } catch (err) {
      setError('Unable to load portfolios.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPortfolios();
  }, [user?.userId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormState(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formState.portfolioName.trim()) {
      setError('Portfolio name is required.');
      return;
    }
    try {
      if (editingId) {
        await updatePortfolio(editingId, {
          portfolioName: formState.portfolioName,
          baseCurrency: formState.baseCurrency
        });
      } else {
        await createPortfolio({
          portfolioName: formState.portfolioName,
          baseCurrency: formState.baseCurrency,
          userId: user.userId
        });
      }
      setFormState({ portfolioName: '', baseCurrency: 'INR' });
      setEditingId(null);
      loadPortfolios();
    } catch (err) {
      setError('Unable to save portfolio.');
    }
  };

  const startEdit = (portfolio) => {
    setEditingId(portfolio.portfolioId);
    setFormState({
      portfolioName: portfolio.portfolioName,
      baseCurrency: portfolio.baseCurrency || 'INR'
    });
  };

  const handleDelete = async (portfolioId) => {
    try {
      await deletePortfolio(portfolioId);
      loadPortfolios();
    } catch (err) {
      setError('Unable to delete portfolio.');
    }
  };

  return (
    <div className="portfolio-manager">
      <SectionHeader
        title="Portfolio Manager"
        subtitle="Create, edit, or remove portfolios with ease."
        icon={<FolderOpen size={22} />}
      />

      <GlowCard className="portfolio-form-card">
        <h2>{editingId ? 'Edit Portfolio' : 'Add Portfolio'}</h2>
        {error && <div className="portfolio-error">{error}</div>}
        <form onSubmit={handleSubmit} className="portfolio-form">
          <div className="portfolio-field">
            <label htmlFor="portfolioName">Portfolio Name</label>
            <input
              id="portfolioName"
              name="portfolioName"
              value={formState.portfolioName}
              onChange={handleChange}
              placeholder="e.g., Long-term Growth"
            />
          </div>
          <div className="portfolio-field">
            <label htmlFor="baseCurrency">Base Currency</label>
            <select
              id="baseCurrency"
              name="baseCurrency"
              value={formState.baseCurrency}
              onChange={handleChange}
            >
              <option value="INR">INR</option>
              <option value="USD">USD</option>
              <option value="EUR">EUR</option>
              <option value="GBP">GBP</option>
            </select>
          </div>
          <button type="submit" className="portfolio-submit">
            {editingId ? (
              <>
                <Pencil size={16} /> Update Portfolio
              </>
            ) : (
              <>
                <Plus size={16} /> Add Portfolio
              </>
            )}
          </button>
        </form>
      </GlowCard>

      <div className="portfolio-list">
        {loading ? (
          <GlowCard className="portfolio-empty">Loading portfolios...</GlowCard>
        ) : portfolios.length === 0 ? (
          <GlowCard className="portfolio-empty">No portfolios yet. Create one above.</GlowCard>
        ) : (
          portfolios.map(portfolio => (
            <GlowCard key={portfolio.portfolioId} className="portfolio-item">
              <div>
                <h3>{portfolio.portfolioName}</h3>
                <p>{portfolio.baseCurrency} • Created {new Date(portfolio.createdAt).toLocaleDateString()}</p>
              </div>
              <div className="portfolio-actions">
                <button type="button" onClick={() => startEdit(portfolio)}>
                  <Pencil size={16} /> Edit
                </button>
                <button type="button" onClick={() => handleDelete(portfolio.portfolioId)} className="danger">
                  <Trash2 size={16} /> Delete
                </button>
              </div>
            </GlowCard>
          ))
        )}
      </div>
    </div>
  );
};

export default PortfolioManagerPage;