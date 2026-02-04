import React, { useEffect, useState } from 'react';
import { PlusCircle, Save, Trash2, X } from 'lucide-react';
import { addTarget, deleteTarget, getTargets, updateTarget } from '../services/api';
import GlowCard from '../components/reactbits/GlowCard';
import SectionHeader from '../components/reactbits/SectionHeader';
import '../styles/Targets.css';

const TargetsPage = ({ portfolioId }) => {
  const [targets, setTargets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [editingId, setEditingId] = useState(null);
  const [editData, setEditData] = useState({});
  const [formData, setFormData] = useState({ assetType: '', targetPercentage: '' });

  const loadTargets = async () => {
    if (!portfolioId) return;
    try {
      setLoading(true);
      const response = await getTargets(portfolioId);
      setTargets(response.data || []);
      setError('');
    } catch (err) {
      setError('Unable to load targets.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadTargets();
  }, [portfolioId]);

  const handleFormChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    if (!formData.assetType || !formData.targetPercentage) {
      setError('Asset type and target percentage are required.');
      return;
    }
    try {
      await addTarget(portfolioId, {
        assetType: formData.assetType,
        targetPercentage: Number(formData.targetPercentage)
      });
      setFormData({ assetType: '', targetPercentage: '' });
      loadTargets();
    } catch (err) {
      setError('Unable to add target.');
    }
  };

  const startEdit = (target) => {
    setEditingId(target.targetId);
    setEditData({
      assetType: target.assetType,
      targetPercentage: target.targetPercentage
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

  const handleSave = async (targetId) => {
    try {
      await updateTarget(portfolioId, targetId, {
        assetType: editData.assetType,
        targetPercentage: Number(editData.targetPercentage)
      });
      cancelEdit();
      loadTargets();
    } catch (err) {
      setError('Unable to update target.');
    }
  };

  const handleDelete = async (targetId) => {
    try {
      await deleteTarget(portfolioId, targetId);
      loadTargets();
    } catch (err) {
      setError('Unable to delete target.');
    }
  };

  return (
    <div className="targets-page">
      <SectionHeader
        title="Targets"
        subtitle="Maintain target allocations for each asset type."
        icon={<PlusCircle size={22} />}
      />

      <GlowCard className="targets-form-card">
        <h2>Add Target</h2>
        {error && <div className="targets-error">{error}</div>}
        <form onSubmit={handleCreate} className="targets-form">
          <div className="targets-field">
            <label>Asset Type</label>
            <input name="assetType" value={formData.assetType} onChange={handleFormChange} placeholder="e.g., STOCK" />
          </div>
          <div className="targets-field">
            <label>Target %</label>
            <input
              name="targetPercentage"
              type="number"
              value={formData.targetPercentage}
              onChange={handleFormChange}
            />
          </div>
          <button type="submit" className="targets-submit">
            <PlusCircle size={16} /> Add Target
          </button>
        </form>
      </GlowCard>

      {loading ? (
        <GlowCard className="targets-empty">Loading targets...</GlowCard>
      ) : targets.length === 0 ? (
        <GlowCard className="targets-empty">No targets yet.</GlowCard>
      ) : (
        <div className="targets-grid">
          {targets.map(target => (
            <GlowCard key={target.targetId} className="targets-card">
              {editingId === target.targetId ? (
                <div className="targets-edit">
                  <div className="targets-field">
                    <label>Asset Type</label>
                    <input name="assetType" value={editData.assetType || ''} onChange={handleEditChange} />
                  </div>
                  <div className="targets-field">
                    <label>Target %</label>
                    <input
                      name="targetPercentage"
                      type="number"
                      value={editData.targetPercentage || ''}
                      onChange={handleEditChange}
                    />
                  </div>
                  <div className="targets-actions">
                    <button onClick={() => handleSave(target.targetId)}>
                      <Save size={16} /> Save
                    </button>
                    <button className="ghost" onClick={cancelEdit}>
                      <X size={16} /> Cancel
                    </button>
                  </div>
                </div>
              ) : (
                <div className="targets-view">
                  <h3>{target.assetType}</h3>
                  <p>{target.targetPercentage}% target</p>
                  <div className="targets-actions">
                    <button onClick={() => startEdit(target)}>
                      <Save size={16} /> Edit
                    </button>
                    <button className="danger" onClick={() => handleDelete(target.targetId)}>
                      <Trash2 size={16} /> Delete
                    </button>
                  </div>
                </div>
              )}
            </GlowCard>
          ))}
        </div>
      )}
    </div>
  );
};

export default TargetsPage;
