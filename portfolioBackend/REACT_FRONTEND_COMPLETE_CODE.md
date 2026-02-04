# React Frontend - Complete Setup & Code

## Commands to Run (In Order)

### 1. Create React Project with Vite
```powershell
cd C:\Users\Administrator\Downloads\Portfolio\PortfolioManagement\PortfolioManagement
npm create vite@latest portfolioFrontend -- --template react
cd portfolioFrontend
npm install
```

### 2. Install Dependencies
```powershell
npm install axios lucide-react
```

### 3. Start Frontend
```powershell
npm run dev
# Opens at http://localhost:5173
```

---

## Backend Setup (If Not Already Done)

### Start MySQL
```powershell
net start MySQL80
```

### Start Backend (From Another Terminal)
```powershell
cd C:\Users\Administrator\Downloads\Portfolio\PortfolioManagement\PortfolioManagement\portfolioBackend
.\mvnw.cmd spring-boot:run
# Runs at http://localhost:8080
```

---

## React Project Structure

```
portfolioFrontend/
├── src/
│   ├── App.jsx                 (Main app with routing & theme)
│   ├── App.css                 (Global styles)
│   ├── components/
│   │   ├── Navbar.jsx          (Left vertical navbar)
│   │   ├── Dashboard.jsx       (Main dashboard)
│   │   ├── HoldingCard.jsx     (Individual holding card)
│   │   └── ThemeToggle.jsx     (Dark/Light theme toggle)
│   ├── services/
│   │   └── api.js              (API configuration)
│   ├── pages/
│   │   └── DashboardPage.jsx   (Full dashboard page)
│   └── index.css
├── package.json
└── vite.config.js
```

---

## Step 1: Create API Service (src/services/api.js)

```javascript
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
```

---

## Step 2: Create Theme Hook (src/hooks/useTheme.js)

```javascript
import { useState, useEffect } from 'react';

const useTheme = () => {
  const [isDark, setIsDark] = useState(() => {
    const saved = localStorage.getItem('theme');
    return saved ? saved === 'dark' : false;
  });

  useEffect(() => {
    localStorage.setItem('theme', isDark ? 'dark' : 'light');
    if (isDark) {
      document.documentElement.setAttribute('data-theme', 'dark');
    } else {
      document.documentElement.removeAttribute('data-theme');
    }
  }, [isDark]);

  const toggleTheme = () => setIsDark(!isDark);

  return { isDark, toggleTheme };
};

export default useTheme;
```

---

## Step 3: Create Navbar Component (src/components/Navbar.jsx)

```javascript
import React from 'react';
import { BarChart3, DollarSign, TrendingUp, Settings, LogOut, Home } from 'lucide-react';
import '../styles/Navbar.css';

const Navbar = ({ activeSection, onSectionChange }) => {
  const navItems = [
    { id: 'dashboard', label: 'Dashboard', icon: Home },
    { id: 'portfolio', label: 'Portfolio', icon: BarChart3 },
    { id: 'assets', label: 'Assets', icon: DollarSign },
    { id: 'performance', label: 'Performance', icon: TrendingUp },
  ];

  return (
    <nav className="navbar">
      <div className="navbar-header">
        <div className="navbar-logo">
          <BarChart3 size={24} />
          <span>Portfolio</span>
        </div>
      </div>

      <ul className="navbar-menu">
        {navItems.map(item => (
          <li key={item.id}>
            <button
              className={`nav-item ${activeSection === item.id ? 'active' : ''}`}
              onClick={() => onSectionChange(item.id)}
            >
              <item.icon size={20} />
              <span>{item.label}</span>
            </button>
          </li>
        ))}
      </ul>

      <div className="navbar-footer">
        <button className="nav-item">
          <Settings size={20} />
          <span>Settings</span>
        </button>
        <button className="nav-item">
          <LogOut size={20} />
          <span>Logout</span>
        </button>
      </div>
    </nav>
  );
};

export default Navbar;
```

---

## Step 4: Create Dashboard Component (src/components/Dashboard.jsx)

```javascript
import React, { useState, useEffect } from 'react';
import { TrendingUp, TrendingDown, DollarSign } from 'lucide-react';
import { getPortfolioDashboard } from '../services/api';
import HoldingCard from './HoldingCard';
import '../styles/Dashboard.css';

const Dashboard = ({ portfolioId }) => {
  const [portfolio, setPortfolio] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        setLoading(true);
        const response = await getPortfolioDashboard(portfolioId);
        setPortfolio(response.data);
        setError(null);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    if (portfolioId) {
      fetchDashboard();
    }
  }, [portfolioId]);

  if (loading) {
    return <div className="loading">Loading portfolio...</div>;
  }

  if (error) {
    return <div className="error">Error: {error}</div>;
  }

  if (!portfolio) {
    return <div className="empty">Select a portfolio to view details</div>;
  }

  const totalGainLoss = portfolio.holdings.reduce((sum, h) => sum + (h.gainLoss || 0), 0);
  const totalGainLossPercent = portfolio.holdings.length > 0 
    ? totalGainLoss / portfolio.holdings.reduce((sum, h) => sum + (h.totalInvested || 0), 0) * 100 
    : 0;

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h1>{portfolio.portfolioName}</h1>
        <p className="portfolio-meta">{portfolio.baseCurrency} • Since {new Date(portfolio.createdAt).toLocaleDateString()}</p>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-label">Total Value</div>
          <div className="stat-value">{portfolio.baseCurrency} {parseFloat(portfolio.totalValue || 0).toFixed(2)}</div>
          <div className="stat-subtext">Current portfolio value</div>
        </div>

        <div className="stat-card">
          <div className="stat-label">Total Invested</div>
          <div className="stat-value">
            {portfolio.baseCurrency} {portfolio.holdings.reduce((sum, h) => sum + (h.totalInvested || 0), 0).toFixed(2)}
          </div>
          <div className="stat-subtext">Cost basis</div>
        </div>

        <div className={`stat-card gain-loss ${totalGainLoss >= 0 ? 'positive' : 'negative'}`}>
          <div className="stat-label">Gain / Loss</div>
          <div className="stat-value flex-center">
            {totalGainLoss >= 0 ? <TrendingUp size={20} /> : <TrendingDown size={20} />}
            {portfolio.baseCurrency} {Math.abs(totalGainLoss).toFixed(2)}
          </div>
          <div className="stat-subtext">{totalGainLossPercent.toFixed(2)}%</div>
        </div>

        <div className="stat-card">
          <div className="stat-label">Holdings Count</div>
          <div className="stat-value">{portfolio.holdings.length}</div>
          <div className="stat-subtext">Different investments</div>
        </div>
      </div>

      <div className="holdings-section">
        <h2>Your Holdings</h2>
        {portfolio.holdings.length === 0 ? (
          <div className="empty-holdings">No holdings yet. Add investments to get started.</div>
        ) : (
          <div className="holdings-grid">
            {portfolio.holdings.map(holding => (
              <HoldingCard key={holding.holdingId} holding={holding} currency={portfolio.baseCurrency} />
            ))}
          </div>
        )}
      </div>

      <div className="allocation-section">
        <h2>Asset Allocation</h2>
        <div className="allocation-chart">
          {portfolio.holdings.map(holding => (
            <div key={holding.holdingId} className="allocation-item">
              <div className="allocation-label">{holding.assetName}</div>
              <div className="allocation-bar">
                <div 
                  className="allocation-fill" 
                  style={{ width: `${holding.allocation || 0}%` }}
                />
              </div>
              <div className="allocation-percent">{(holding.allocation || 0).toFixed(1)}%</div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
```

---

## Step 5: Create Holding Card Component (src/components/HoldingCard.jsx)

```javascript
import React from 'react';
import { TrendingUp, TrendingDown } from 'lucide-react';
import '../styles/HoldingCard.css';

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
          <span className="value">{currency} {holding.purchasePrice.toFixed(2)}</span>
        </div>
        <div className="holding-stat">
          <span className="label">Current Price</span>
          <span className="value">{currency} {holding.currentPrice.toFixed(2)}</span>
        </div>
      </div>

      <div className="holding-footer">
        <div>
          <span className="label">Invested</span>
          <span className="value">{currency} {holding.totalInvested?.toFixed(2)}</span>
        </div>
        <div>
          <span className="label">Current Value</span>
          <span className="value">{currency} {holding.currentValue?.toFixed(2)}</span>
        </div>
      </div>

      <div className={`holding-gain-loss ${isPositive ? 'positive' : 'negative'}`}>
        <span className="label">Gain / Loss</span>
        <span className="value">{isPositive ? '+' : ''}{currency} {holding.gainLoss?.toFixed(2)}</span>
      </div>

      <div className="holding-date">
        <small>Purchased on {new Date(holding.purchaseDate).toLocaleDateString()}</small>
      </div>
    </div>
  );
};

export default HoldingCard;
```

---

## Step 6: Create Main App Component (src/App.jsx)

```javascript
import React, { useState, useEffect } from 'react';
import { Moon, Sun } from 'lucide-react';
import Navbar from './components/Navbar';
import Dashboard from './components/Dashboard';
import { getAllUsers, getUserPortfolios } from './services/api';
import useTheme from './hooks/useTheme';
import './App.css';

function App() {
  const [activeSection, setActiveSection] = useState('dashboard');
  const [selectedUser, setSelectedUser] = useState(1);
  const [selectedPortfolio, setSelectedPortfolio] = useState(1);
  const [users, setUsers] = useState([]);
  const [portfolios, setPortfolios] = useState([]);
  const [loading, setLoading] = useState(true);
  const { isDark, toggleTheme } = useTheme();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const usersResponse = await getAllUsers();
        setUsers(usersResponse.data);
        
        if (usersResponse.data.length > 0) {
          const firstUser = usersResponse.data[0];
          setSelectedUser(firstUser.userId);
          
          const portfoliosResponse = await getUserPortfolios(firstUser.userId);
          setPortfolios(portfoliosResponse.data);
          
          if (portfoliosResponse.data.length > 0) {
            setSelectedPortfolio(portfoliosResponse.data[0].portfolioId);
          }
        }
      } catch (err) {
        console.error('Error loading data:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const handleUserChange = async (userId) => {
    setSelectedUser(userId);
    try {
      const response = await getUserPortfolios(userId);
      setPortfolios(response.data);
      if (response.data.length > 0) {
        setSelectedPortfolio(response.data[0].portfolioId);
      }
    } catch (err) {
      console.error('Error loading portfolios:', err);
    }
  };

  if (loading) {
    return <div className="loading-screen">Loading...</div>;
  }

  return (
    <div className={`app ${isDark ? 'dark' : 'light'}`}>
      <Navbar activeSection={activeSection} onSectionChange={setActiveSection} />
      
      <main className="main-content">
        <div className="top-bar">
          <div className="user-portfolio-selector">
            <select value={selectedUser} onChange={(e) => handleUserChange(Number(e.target.value))}>
              {users.map(user => (
                <option key={user.userId} value={user.userId}>
                  {user.username}
                </option>
              ))}
            </select>
            
            <select value={selectedPortfolio} onChange={(e) => setSelectedPortfolio(Number(e.target.value))}>
              {portfolios.map(portfolio => (
                <option key={portfolio.portfolioId} value={portfolio.portfolioId}>
                  {portfolio.portfolioName}
                </option>
              ))}
            </select>
          </div>

          <button className="theme-toggle" onClick={toggleTheme}>
            {isDark ? <Sun size={20} /> : <Moon size={20} />}
          </button>
        </div>

        <div className="page-content">
          {activeSection === 'dashboard' && <Dashboard portfolioId={selectedPortfolio} />}
          {activeSection === 'portfolio' && <div className="placeholder">Portfolio Management Coming Soon</div>}
          {activeSection === 'assets' && <div className="placeholder">Assets Page Coming Soon</div>}
          {activeSection === 'performance' && <div className="placeholder">Performance Analytics Coming Soon</div>}
        </div>
      </main>
    </div>
  );
}

export default App;
```

---

## Step 7: Create CSS Files

### src/App.css

```css
:root {
  --primary: #667eea;
  --primary-dark: #5568d3;
  --secondary: #764ba2;
  --bg-light: #f8f9fa;
  --bg-white: #ffffff;
  --text-dark: #2d3748;
  --text-light: #718096;
  --border: #e2e8f0;
  --shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  --shadow-lg: 0 10px 30px rgba(0, 0, 0, 0.1);
  --success: #48bb78;
  --danger: #f56565;
}

[data-theme="dark"] {
  --bg-light: #1a202c;
  --bg-white: #2d3748;
  --text-dark: #f7fafc;
  --text-light: #cbd5e0;
  --border: #4a5568;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: var(--bg-light);
  color: var(--text-dark);
  transition: all 0.3s ease;
}

.app {
  display: flex;
  min-height: 100vh;
  background-color: var(--bg-light);
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 2rem;
  background-color: var(--bg-white);
  border-bottom: 1px solid var(--border);
  box-shadow: var(--shadow);
}

.user-portfolio-selector {
  display: flex;
  gap: 1rem;
}

.user-portfolio-selector select {
  padding: 0.5rem 1rem;
  border: 1px solid var(--border);
  border-radius: 6px;
  background-color: var(--bg-white);
  color: var(--text-dark);
  cursor: pointer;
  transition: all 0.2s ease;
}

.user-portfolio-selector select:hover {
  border-color: var(--primary);
}

.theme-toggle {
  background: none;
  border: 1px solid var(--border);
  padding: 0.5rem;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--text-dark);
}

.theme-toggle:hover {
  background-color: var(--bg-light);
}

.page-content {
  flex: 1;
  overflow-y: auto;
  padding: 2rem;
}

.loading-screen {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  font-size: 1.2rem;
  color: var(--text-light);
}

.placeholder {
  padding: 2rem;
  background-color: var(--bg-white);
  border-radius: 12px;
  text-align: center;
  color: var(--text-light);
}
```

### src/styles/Navbar.css

```css
.navbar {
  width: 250px;
  background-color: var(--bg-white);
  border-right: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  height: 100vh;
  box-shadow: var(--shadow);
  transition: all 0.3s ease;
}

.navbar-header {
  padding: 1.5rem;
  border-bottom: 1px solid var(--border);
}

.navbar-logo {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--primary);
}

.navbar-menu {
  flex: 1;
  list-style: none;
  padding: 1rem 0;
  overflow-y: auto;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  width: 100%;
  padding: 0.75rem 1.5rem;
  background: none;
  border: none;
  cursor: pointer;
  color: var(--text-light);
  transition: all 0.2s ease;
  font-size: 0.95rem;
  text-align: left;
}

.nav-item:hover {
  background-color: var(--bg-light);
  color: var(--primary);
}

.nav-item.active {
  background-color: var(--bg-light);
  color: var(--primary);
  border-left: 3px solid var(--primary);
  padding-left: calc(1.5rem - 3px);
}

.navbar-footer {
  padding: 1rem 0;
  border-top: 1px solid var(--border);
}

.navbar-footer .nav-item {
  color: var(--text-light);
}
```

### src/styles/Dashboard.css

```css
.dashboard {
  max-width: 1400px;
  width: 100%;
}

.dashboard-header {
  margin-bottom: 2rem;
}

.dashboard-header h1 {
  font-size: 2rem;
  margin-bottom: 0.5rem;
  color: var(--text-dark);
}

.portfolio-meta {
  color: var(--text-light);
  font-size: 0.95rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 3rem;
}

.stat-card {
  background-color: var(--bg-white);
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: var(--shadow);
  border: 1px solid var(--border);
  transition: all 0.3s ease;
}

.stat-card:hover {
  box-shadow: var(--shadow-lg);
  transform: translateY(-2px);
}

.stat-card.positive {
  border-left: 4px solid var(--success);
}

.stat-card.negative {
  border-left: 4px solid var(--danger);
}

.stat-label {
  font-size: 0.85rem;
  color: var(--text-light);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 0.5rem;
}

.stat-value {
  font-size: 1.75rem;
  font-weight: 600;
  color: var(--text-dark);
  margin-bottom: 0.5rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.stat-value.flex-center {
  justify-content: center;
}

.stat-value.positive {
  color: var(--success);
}

.stat-value.negative {
  color: var(--danger);
}

.stat-subtext {
  font-size: 0.85rem;
  color: var(--text-light);
}

.holdings-section {
  margin-bottom: 3rem;
}

.holdings-section h2 {
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
  color: var(--text-dark);
}

.holdings-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 1.5rem;
}

.empty-holdings {
  padding: 2rem;
  text-align: center;
  color: var(--text-light);
  background-color: var(--bg-white);
  border-radius: 12px;
  border: 1px dashed var(--border);
}

.allocation-section {
  background-color: var(--bg-white);
  padding: 2rem;
  border-radius: 12px;
  box-shadow: var(--shadow);
}

.allocation-section h2 {
  font-size: 1.3rem;
  margin-bottom: 1.5rem;
  color: var(--text-dark);
}

.allocation-chart {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.allocation-item {
  display: grid;
  grid-template-columns: 150px 1fr 80px;
  gap: 1rem;
  align-items: center;
}

.allocation-label {
  font-weight: 500;
  color: var(--text-dark);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.allocation-bar {
  background-color: var(--bg-light);
  height: 24px;
  border-radius: 12px;
  overflow: hidden;
}

.allocation-fill {
  background: linear-gradient(90deg, var(--primary), var(--secondary));
  height: 100%;
  transition: width 0.3s ease;
}

.allocation-percent {
  text-align: right;
  font-weight: 600;
  color: var(--primary);
  font-size: 0.95rem;
}

.error,
.empty,
.loading {
  padding: 2rem;
  text-align: center;
  background-color: var(--bg-white);
  border-radius: 12px;
  color: var(--text-light);
}

.error {
  color: var(--danger);
  border: 1px solid var(--danger);
}
```

### src/styles/HoldingCard.css

```css
.holding-card {
  background-color: var(--bg-white);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 1.5rem;
  transition: all 0.3s ease;
  box-shadow: var(--shadow);
  display: flex;
  flex-direction: column;
}

.holding-card:hover {
  box-shadow: var(--shadow-lg);
  transform: translateY(-4px);
}

.holding-card.equity {
  border-left: 4px solid #667eea;
}

.holding-card.bond {
  border-left: 4px solid #48bb78;
}

.holding-card.mutualfund {
  border-left: 4px solid #ed8936;
}

.holding-card.sip {
  border-left: 4px solid #9f7aea;
}

.holding-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
  gap: 1rem;
}

.holding-name h3 {
  font-size: 1.1rem;
  color: var(--text-dark);
  margin-bottom: 0.25rem;
}

.asset-type {
  font-size: 0.75rem;
  color: var(--text-light);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.gain-loss-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.4rem 0.8rem;
  border-radius: 6px;
  font-size: 0.85rem;
  font-weight: 600;
  white-space: nowrap;
}

.gain-loss-badge.positive {
  background-color: rgba(72, 187, 120, 0.1);
  color: var(--success);
}

.gain-loss-badge.negative {
  background-color: rgba(245, 101, 101, 0.1);
  color: var(--danger);
}

.holding-body {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
  padding: 1rem 0;
  border-top: 1px solid var(--border);
  border-bottom: 1px solid var(--border);
  margin-bottom: 1rem;
}

.holding-stat {
  display: flex;
  flex-direction: column;
}

.holding-stat .label {
  font-size: 0.75rem;
  color: var(--text-light);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 0.25rem;
}

.holding-stat .value {
  font-size: 0.95rem;
  font-weight: 600;
  color: var(--text-dark);
}

.holding-footer {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
  margin-bottom: 1rem;
}

.holding-footer > div {
  display: flex;
  flex-direction: column;
}

.holding-footer .label {
  font-size: 0.75rem;
  color: var(--text-light);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 0.25rem;
}

.holding-footer .value {
  font-size: 0.95rem;
  font-weight: 600;
  color: var(--text-dark);
}

.holding-gain-loss {
  padding: 1rem;
  border-radius: 8px;
  margin-bottom: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.holding-gain-loss.positive {
  background-color: rgba(72, 187, 120, 0.1);
  color: var(--success);
}

.holding-gain-loss.negative {
  background-color: rgba(245, 101, 101, 0.1);
  color: var(--danger);
}

.holding-gain-loss .label {
  font-size: 0.85rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  opacity: 0.8;
}

.holding-gain-loss .value {
  font-weight: 600;
  font-size: 1.1rem;
}

.holding-date {
  text-align: center;
  color: var(--text-light);
  font-size: 0.8rem;
}
```

---

## Step 8: Add Dummy Data to Database

First create a user and portfolio:

```powershell
# Use the testing UI at C:\Users\Administrator\Downloads\Portfolio\PortfolioManagement\PortfolioManagement\portfolioBackend\index.html

# OR use API:
# Create User
curl -X POST http://localhost:8080/api/users `
  -H "Content-Type: application/json" `
  -d '{"username":"John Doe","email":"john@example.com","defaultCurrency":"USD"}'

# Create Portfolio (use userId from above)
curl -X POST http://localhost:8080/api/portfolios `
  -H "Content-Type: application/json" `
  -d '{"userId":1,"portfolioName":"My Investment Portfolio","baseCurrency":"USD"}'

# Add Holdings (use portfolioId from above)
curl -X POST http://localhost:8080/api/portfolios/1/holdings `
  -H "Content-Type: application/json" `
  -d '{
    "assetName":"Apple Inc.",
    "assetType":"Equity",
    "quantity":10,
    "purchasePrice":150,
    "currentPrice":175,
    "currency":"USD",
    "purchaseDate":"2023-01-15"
  }'

# Add more holdings similarly for Tesla, Nvidia, Mutual Funds, etc.
```

Or run the test HTML app to add data more easily.

---

## Run Everything Together

### Terminal 1 - MySQL
```powershell
net start MySQL80
```

### Terminal 2 - Backend
```powershell
cd C:\Users\Administrator\Downloads\Portfolio\PortfolioManagement\PortfolioManagement\portfolioBackend
.\mvnw.cmd spring-boot:run
```

### Terminal 3 - Frontend
```powershell
cd C:\Users\Administrator\Downloads\Portfolio\PortfolioManagement\PortfolioManagement\portfolioFrontend
npm run dev
```

Then open browser:
```
http://localhost:5173
```

---

## Features Implemented

✅ Vertical left navbar with navigation  
✅ Light/Dark theme toggle  
✅ Dashboard displaying portfolio overview  
✅ Individual holding cards with gain/loss  
✅ Asset allocation visualization  
✅ Professional, clean UI design  
✅ Responsive grid layouts  
✅ Smooth transitions and animations  
✅ User and portfolio selection  
✅ Real-time data from backend  

---

## Next Steps (Can Be Added Later)

- Portfolio management page
- Asset search and add functionality
- Performance analytics page
- Settings page
- More detailed charts and graphs
