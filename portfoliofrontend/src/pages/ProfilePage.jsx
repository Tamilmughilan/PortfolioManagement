import React, { useState } from 'react';
import { Save } from 'lucide-react';
import { updateUser } from '../services/api';
import GlowCard from '../components/reactbits/GlowCard';
import SectionHeader from '../components/reactbits/SectionHeader';
import '../styles/Profile.css';

const ProfilePage = ({ user, onUserUpdated }) => {
  const [formData, setFormData] = useState({
    username: user?.username || '',
    email: user?.email || '',
    defaultCurrency: user?.defaultCurrency || 'INR'
  });
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    setError('');
    setSuccess('');
  };

  const handleSave = async (e) => {
    e.preventDefault();
    if (!user?.userId) return;
    try {
      setSaving(true);
      const response = await updateUser(user.userId, {
        username: formData.username,
        email: formData.email,
        defaultCurrency: formData.defaultCurrency
      });
      const updated = response.data;
      setSuccess('Profile updated successfully.');
      if (onUserUpdated) {
        onUserUpdated(updated);
      }
    } catch (err) {
      setError('Unable to update profile.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="profile-page">
      <SectionHeader
        title="Profile"
        subtitle="Update your account details."
        icon={<Save size={20} />}
      />

      <GlowCard className="profile-card">
        {error && <div className="profile-error">{error}</div>}
        {success && <div className="profile-success">{success}</div>}
        <form onSubmit={handleSave} className="profile-form">
          <div className="profile-field">
            <label>Username</label>
            <input name="username" value={formData.username} onChange={handleChange} />
          </div>
          <div className="profile-field">
            <label>Email</label>
            <input name="email" type="email" value={formData.email} onChange={handleChange} />
          </div>
          <div className="profile-field">
            <label>Default Currency</label>
            <input name="defaultCurrency" value={formData.defaultCurrency} onChange={handleChange} />
          </div>
          <button type="submit" className="profile-submit" disabled={saving}>
            {saving ? 'Saving...' : 'Save Changes'}
          </button>
        </form>
      </GlowCard>
    </div>
  );
};

export default ProfilePage;
