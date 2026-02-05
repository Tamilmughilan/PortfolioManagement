import React, { useState } from 'react';
import { login, signup } from '../services/api';
import '../styles/Auth.css';

const AuthPage = ({ onAuthenticated }) => {
  const [mode, setMode] = useState('login');
  const [formData, setFormData] = useState({
    identifier: '',
    username: '',
    email: '',
    password: '',
    defaultCurrency: 'INR'
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (mode === 'login') {
      if (!formData.identifier.trim() || !formData.password.trim()) {
        setError('Please enter your email/username and password.');
        return;
      }
    } else {
      if (!formData.username.trim() || !formData.email.trim() || !formData.password.trim()) {
        setError('Please complete all required fields.');
        return;
      }
    }

    try {
      setLoading(true);
      const response = mode === 'login'
        ? await login({ identifier: formData.identifier, password: formData.password })
        : await signup({
            username: formData.username,
            email: formData.email,
            password: formData.password,
            defaultCurrency: formData.defaultCurrency
          });

      const user = response.data;
      localStorage.setItem('authUser', JSON.stringify(user));
      onAuthenticated(user);
    } catch (err) {
      if (err.response?.status === 401) {
        setError('Invalid credentials. Please try again.');
      } else if (err.response?.status === 409) {
        setError('Username or email already exists.');
      } else {
        setError('Authentication failed. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="auth-header">
          <div className="auth-icon">
          </div>
          <h1>{mode === 'login' ? 'Welcome Back' : 'Create Your Account'}</h1>
          <p>{mode === 'login' ? 'Sign in to access your portfolios.' : 'Start tracking your investments today.'}</p>
        </div>

        <div className="auth-toggle">
          <button
            className={mode === 'login' ? 'active' : ''}
            onClick={() => setMode('login')}
            type="button"
          >
            Login
          </button>
          <button
            className={mode === 'signup' ? 'active' : ''}
            onClick={() => setMode('signup')}
            type="button"
          >
            Sign Up
          </button>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          {error && <div className="auth-error">{error}</div>}

          {mode === 'login' ? (
            <div className="auth-field">
              <label htmlFor="identifier">Email or Username</label>
              <div className="auth-input">
                <input
                  id="identifier"
                  name="identifier"
                  value={formData.identifier}
                  onChange={handleChange}
                  placeholder="you@example.com or username"
                />
              </div>
            </div>
          ) : (
            <>
              <div className="auth-field">
                <label htmlFor="username">Username</label>
                <div className="auth-input">
                  <input
                    id="username"
                    name="username"
                    value={formData.username}
                    onChange={handleChange}
                    placeholder="Your name"
                  />
                </div>
              </div>

              <div className="auth-field">
                <label htmlFor="email">Email</label>
                <div className="auth-input">
                  <input
                    id="email"
                    name="email"
                    type="email"
                    value={formData.email}
                    onChange={handleChange}
                    placeholder="you@example.com"
                  />
                </div>
              </div>
            </>
          )}

          <div className="auth-field">
            <label htmlFor="password">Password</label>
            <div className="auth-input">
              <input
                id="password"
                name="password"
                type="password"
                value={formData.password}
                onChange={handleChange}
                placeholder="Enter a secure password"
              />
            </div>
          </div>

          {mode === 'signup' && (
            <div className="auth-field">
              <label htmlFor="defaultCurrency">Base Currency</label>
              <select
                id="defaultCurrency"
                name="defaultCurrency"
                value={formData.defaultCurrency}
                onChange={handleChange}
              >
                <option value="INR">INR</option>
                <option value="USD">USD</option>
                <option value="EUR">EUR</option>
                <option value="GBP">GBP</option>
              </select>
            </div>
          )}

          <button type="submit" className="auth-submit" disabled={loading}>
            {loading ? 'Please wait...' : mode === 'login' ? 'Login' : 'Create Account'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default AuthPage;
