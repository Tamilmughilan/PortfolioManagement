import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// ==================== USER APIs ====================
export const getAllUsers = () => api.get('/users');
export const getUserById = (userId) => api.get(`/users/${userId}`);
export const createUser = (userData) => api.post('/users', userData);

// ==================== AUTH APIs ====================
export const signup = (payload) => api.post('/auth/signup', payload);
export const login = (payload) => api.post('/auth/login', payload);

// ==================== PORTFOLIO APIs ====================
export const getAllPortfolios = () => api.get('/portfolios');
export const getUserPortfolios = (userId) => api.get(`/portfolios/user/${userId}`);
export const getPortfolioById = (portfolioId) => api.get(`/portfolios/${portfolioId}`);
export const getPortfolioDashboard = (portfolioId) => api.get(`/portfolios/${portfolioId}/dashboard`);
export const getPortfolioDriftStory = (portfolioId) => api.get(`/portfolios/${portfolioId}/drift-story`);
export const createPortfolio = (portfolioData) => api.post('/portfolios', portfolioData);
export const updatePortfolio = (portfolioId, portfolioData) => api.put(`/portfolios/${portfolioId}`, portfolioData);
export const deletePortfolio = (portfolioId) => api.delete(`/portfolios/${portfolioId}`);
export const getTotalValue = (portfolioId) => api.get(`/portfolios/${portfolioId}/total-value`);
export const getAssetTypes = (portfolioId) => api.get(`/portfolios/${portfolioId}/asset-types`);

// ==================== HOLDINGS APIs ====================
export const getHoldings = (portfolioId) => api.get(`/portfolios/${portfolioId}/holdings`);
export const getHoldingById = (portfolioId, holdingId) => api.get(`/portfolios/${portfolioId}/holdings/${holdingId}`);
export const addHolding = (portfolioId, holdingData) => api.post(`/portfolios/${portfolioId}/holdings`, holdingData);
export const updateHolding = (portfolioId, holdingId, holdingData) => api.put(`/portfolios/${portfolioId}/holdings/${holdingId}`, holdingData);
export const deleteHolding = (portfolioId, holdingId) => api.delete(`/portfolios/${portfolioId}/holdings/${holdingId}`);

// ==================== TARGETS APIs ====================
export const getTargets = (portfolioId) => api.get(`/portfolios/${portfolioId}/targets`);
export const addTarget = (portfolioId, targetData) => api.post(`/portfolios/${portfolioId}/targets`, targetData);
export const updateTarget = (portfolioId, targetId, targetData) => api.put(`/portfolios/${portfolioId}/targets/${targetId}`, targetData);
export const deleteTarget = (portfolioId, targetId) => api.delete(`/portfolios/${portfolioId}/targets/${targetId}`);

// ==================== SNAPSHOTS APIs ====================
export const getSnapshots = (portfolioId) => api.get(`/portfolios/${portfolioId}/snapshots`);
export const recordSnapshot = (portfolioId, snapshotData) => api.post(`/portfolios/${portfolioId}/snapshots`, snapshotData);
export const refreshSnapshots = (portfolioId, currency) => {
  const params = currency ? `?currency=${currency}` : '';
  return api.post(`/portfolios/${portfolioId}/snapshots/refresh${params}`);
};

// ==================== ANALYTICS APIs ====================
export const getAnalyticsSummary = (portfolioId) => api.get(`/analytics/portfolios/${portfolioId}/summary`);
export const getAllocationValues = (portfolioId) => api.get(`/analytics/portfolios/${portfolioId}/allocations`);
export const getAllocationPercentages = (portfolioId) => api.get(`/analytics/portfolios/${portfolioId}/allocation-percentages`);
export const getTargetDrift = (portfolioId) => api.get(`/analytics/portfolios/${portfolioId}/target-drift`);

export default api;
