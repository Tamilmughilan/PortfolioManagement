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
        <>
          {targets.length > 0 && (
            <GlowCard className="targets-chart-card" style={{ marginBottom: '1.5rem' }}>
              <h3>Target Allocation Distribution</h3>
              <div style={{ padding: '1rem', background: 'var(--bg-light)', borderRadius: '12px' }}>
                <svg viewBox="0 0 200 200" style={{ width: '200px', height: '200px', margin: '0 auto', display: 'block' }}>
                  {(() => {
                    const totalTarget = targets.reduce((sum, t) => sum + (t.targetPercentage || 0), 0);
                    if (totalTarget === 0) return null;
                    
                    let currentAngle = -90;
                    const colors = ['#DC2626', '#E11D48', '#F97316', '#10b981', '#f59e0b', '#06b6d4', '#8b5cf6', '#ec4899'];
                    
                    return targets
                      .filter(t => (t.targetPercentage || 0) > 0)
                      .map((target, index) => {
                        const percent = (target.targetPercentage || 0) / totalTarget;
                        const angle = percent * 360;
                        const startAngle = currentAngle;
                        const endAngle = currentAngle + angle;
                        
                        const x1 = 100 + 70 * Math.cos((startAngle * Math.PI) / 180);
                        const y1 = 100 + 70 * Math.sin((startAngle * Math.PI) / 180);
                        const x2 = 100 + 70 * Math.cos((endAngle * Math.PI) / 180);
                        const y2 = 100 + 70 * Math.sin((endAngle * Math.PI) / 180);
                        const largeArc = angle > 180 ? 1 : 0;
                        
                        currentAngle += angle;
                        
                        return (
                          <path
                            key={target.targetId}
                            d={`M 100 100 L ${x1} ${y1} A 70 70 0 ${largeArc} 1 ${x2} ${y2} Z`}
                            fill={colors[index % colors.length]}
                            opacity="0.8"
                          />
                        );
                      });
                  })()}
                  <circle cx="100" cy="100" r="50" fill="var(--bg-white)" />
                  <text x="100" y="95" textAnchor="middle" fontSize="16" fontWeight="600" fill="var(--text-dark)">
                    {targets.reduce((sum, t) => sum + (t.targetPercentage || 0), 0).toFixed(0)}%
                  </text>
                  <text x="100" y="110" textAnchor="middle" fontSize="11" fill="var(--text-light)">Total</text>
                </svg>
                <div style={{ marginTop: '1rem', display: 'flex', flexWrap: 'wrap', justifyContent: 'center', gap: '1rem' }}>
                  {targets.map((target, index) => {
                    const colors = ['#DC2626', '#E11D48', '#F97316', '#10b981', '#f59e0b', '#06b6d4', '#8b5cf6', '#ec4899'];
                    return (
                      <div key={target.targetId} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <div style={{ width: '12px', height: '12px', backgroundColor: colors[index % colors.length], borderRadius: '2px' }}></div>
                        <span style={{ fontSize: '12px', color: 'var(--text-dark)' }}>{target.assetType}: {target.targetPercentage}%</span>
                      </div>
                    );
                  })}
                </div>
              </div>
            </GlowCard>
          )}
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
        </>
      )}
    </div>
  );
};

export default TargetsPage;
