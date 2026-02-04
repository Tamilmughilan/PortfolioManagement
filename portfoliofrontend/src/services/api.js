import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const getPortfolioDashboard = (portfolioId) =>
  api.get(`/portfolios/${portfolioId}/dashboard`);

export const getAllUsers = () =>
  api.get('/users');

export const getUserPortfolios = (userId) =>
  api.get(`/portfolios/user/${userId}`);

export const getAllPortfolios = () =>
  api.get('/portfolios');

export default api;