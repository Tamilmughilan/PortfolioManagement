import React, { useEffect, useState } from 'react';
import { PlusCircle, Save, Trash2, X } from 'lucide-react';
import {
  deleteSnapshot,
  getSnapshots,
  recordSnapshot,
  updateSnapshot
} from '../services/api';
import GlowCard from '../components/reactbits/GlowCard';
import SectionHeader from '../components/reactbits/SectionHeader';
import '../styles/Snapshots.css';

const SnapshotsPage = ({ portfolioId }) => {
  const [snapshots, setSnapshots] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [editingId, setEditingId] = useState(null);
  const [editData, setEditData] = useState({});
  const [formData, setFormData] = useState({
    totalValue: '',
    currency: 'INR',
    snapshotDate: ''
  });

  const loadSnapshots = async () => {
    if (!portfolioId) return;
    try {
      setLoading(true);
      const response = await getSnapshots(portfolioId);
      setSnapshots(response.data || []);
      setError('');
    } catch (err) {
      setError('Unable to load snapshots.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadSnapshots();
  }, [portfolioId]);

  const handleFormChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      await recordSnapshot(portfolioId, {
        totalValue: formData.totalValue ? Number(formData.totalValue) : null,
        currency: formData.currency,
        snapshotDate: formData.snapshotDate || null
      });
      setFormData({ totalValue: '', currency: 'INR', snapshotDate: '' });
      loadSnapshots();
    } catch (err) {
      setError('Unable to record snapshot.');
    }
  };

  const startEdit = (snapshot) => {
    setEditingId(snapshot.snapshotId);
    setEditData({
      totalValue: snapshot.totalValue,
      currency: snapshot.currency,
      snapshotDate: snapshot.snapshotDate
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

  const handleSave = async (snapshotId) => {
    try {
      await updateSnapshot(portfolioId, snapshotId, {
        totalValue: Number(editData.totalValue),
        currency: editData.currency,
        snapshotDate: editData.snapshotDate
      });
      cancelEdit();
      loadSnapshots();
    } catch (err) {
      setError('Unable to update snapshot.');
    }
  };

  const handleDelete = async (snapshotId) => {
    try {
      await deleteSnapshot(portfolioId, snapshotId);
      loadSnapshots();
    } catch (err) {
      setError('Unable to delete snapshot.');
    }
  };

  return (
    <div className="snapshots-page">
      <SectionHeader
        title="Snapshots"
        subtitle="Track portfolio value snapshots over time."
        icon={<PlusCircle size={22} />}
      />

      <GlowCard className="snapshots-form-card">
        <h2>Record Snapshot</h2>
        {error && <div className="snapshots-error">{error}</div>}
        <form onSubmit={handleCreate} className="snapshots-form">
          <div className="snapshots-field">
            <label>Total Value</label>
            <input
              name="totalValue"
              type="number"
              value={formData.totalValue}
              onChange={handleFormChange}
            />
          </div>
          <div className="snapshots-field">
            <label>Currency</label>
            <input
              name="currency"
              value={formData.currency}
              onChange={handleFormChange}
            />
          </div>
          <div className="snapshots-field">
            <label>Snapshot Date</label>
            <input
              name="snapshotDate"
              type="date"
              value={formData.snapshotDate}
              onChange={handleFormChange}
            />
          </div>
          <button type="submit" className="snapshots-submit">
            <PlusCircle size={16} /> Add Snapshot
          </button>
        </form>
      </GlowCard>

      {loading ? (
        <GlowCard className="snapshots-empty">Loading snapshots...</GlowCard>
      ) : snapshots.length === 0 ? (
        <GlowCard className="snapshots-empty">No snapshots yet.</GlowCard>
      ) : (
        <div className="snapshots-grid">
          {snapshots.map(snapshot => (
            <GlowCard key={snapshot.snapshotId} className="snapshots-card">
              {editingId === snapshot.snapshotId ? (
                <div className="snapshots-edit">
                  <div className="snapshots-field">
                    <label>Total Value</label>
                    <input
                      name="totalValue"
                      type="number"
                      value={editData.totalValue || ''}
                      onChange={handleEditChange}
                    />
                  </div>
                  <div className="snapshots-field">
                    <label>Currency</label>
                    <input
                      name="currency"
                      value={editData.currency || ''}
                      onChange={handleEditChange}
                    />
                  </div>
                  <div className="snapshots-field">
                    <label>Snapshot Date</label>
                    <input
                      name="snapshotDate"
                      type="date"
                      value={editData.snapshotDate || ''}
                      onChange={handleEditChange}
                    />
                  </div>
                  <div className="snapshots-actions">
                    <button onClick={() => handleSave(snapshot.snapshotId)}>
                      <Save size={16} /> Save
                    </button>
                    <button className="ghost" onClick={cancelEdit}>
                      <X size={16} /> Cancel
                    </button>
                  </div>
                </div>
              ) : (
                <div className="snapshots-view">
                  <h3>{snapshot.currency} {snapshot.totalValue}</h3>
                  <p>{snapshot.snapshotDate}</p>
                  <div className="snapshots-actions">
                    <button onClick={() => startEdit(snapshot)}>
                      <Save size={16} /> Edit
                    </button>
                    <button className="danger" onClick={() => handleDelete(snapshot.snapshotId)}>
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

export default SnapshotsPage;
