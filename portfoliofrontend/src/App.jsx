import React, { useState, useEffect } from 'react';
import Navbar from './components/Navbar';
import Dashboard from './components/Dashboard';
import AnalyticsPage from './pages/AnalyticsPage';
import AddHoldingModal from './components/AddHoldingModal';
import AuthPage from './pages/AuthPage';
import PortfolioDriftPage from './pages/PortfolioDriftPage';
import PortfolioManagerPage from './pages/PortfolioManagerPage';
import StoryPage from './pages/StoryPage';
import Chatbot from './components/Chatbot';
import { getUserPortfolios } from './services/api';
import useTheme from './hooks/useTheme';
import './App.css';

function App() {
  const [activeSection, setActiveSection] = useState('dashboard');
  const [currentUser, setCurrentUser] = useState(null);
  const [selectedPortfolio, setSelectedPortfolio] = useState(null);
  const [portfolios, setPortfolios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showAddHolding, setShowAddHolding] = useState(false);
  const [refreshKey, setRefreshKey] = useState(0);
  const { isDark, toggleTheme } = useTheme();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const storedUser = localStorage.getItem('authUser');
        if (storedUser) {
          const parsedUser = JSON.parse(storedUser);
          setCurrentUser(parsedUser);
          const portfoliosResponse = await getUserPortfolios(parsedUser.userId);
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

  const handleLogin = async (user) => {
    setCurrentUser(user);
    setLoading(true);
    try {
      const response = await getUserPortfolios(user.userId);
      setPortfolios(response.data);
      setSelectedPortfolio(response.data.length > 0 ? response.data[0].portfolioId : null);
    } catch (err) {
      console.error('Error loading portfolios:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('authUser');
    setCurrentUser(null);
    setSelectedPortfolio(null);
    setPortfolios([]);
  };

  const handleRefresh = () => {
    setRefreshKey(prev => prev + 1);
  };

  const handleHoldingAdded = () => {
    handleRefresh();
  };

  if (!currentUser && !loading) {
    return <AuthPage onAuthenticated={handleLogin} />;
  }

  if (loading) {
    return (
      <div className="loading-screen">
        <div className="loading-spinner"></div>
        <p>Loading your portfolio...</p>
      </div>
    );
  }

  const renderContent = () => {
    if (!selectedPortfolio && activeSection !== 'portfolio') {
      return (
        <div className="no-portfolio">
          <h2>No Portfolio Selected</h2>
          <p>Create or select a portfolio to get started</p>
        </div>
      );
    }

    switch (activeSection) {
      case 'dashboard':
        return <Dashboard key={refreshKey} portfolioId={selectedPortfolio} />;
      case 'performance':
        return <AnalyticsPage key={refreshKey} portfolioId={selectedPortfolio} />;
      case 'portfolio':
        return <PortfolioManagerPage user={currentUser} />;
      case 'assets':
        return (
          <div className="coming-soon">
            <h2>Assets Overview</h2>
            <p>Detailed assets breakdown coming soon...</p>
          </div>
        );
      case 'drift':
        return <PortfolioDriftPage key={refreshKey} portfolioId={selectedPortfolio} />;
      case 'story':
        return <StoryPage key={refreshKey} portfolioId={selectedPortfolio} />;
      default:
        return <Dashboard key={refreshKey} portfolioId={selectedPortfolio} />;
    }
  };

  return (
    <div className={`app ${isDark ? 'dark' : 'light'}`} data-theme={isDark ? 'dark' : 'light'}>
      <Navbar
        activeSection={activeSection}
        onSectionChange={setActiveSection}
        onLogout={handleLogout}
        user={currentUser}
      />
      
      <main className="main-content">
        <div className="top-bar">
          <div className="top-bar-left">
            <div className="user-portfolio-selector">
              <div className="selector-group">
                <label>Portfolio</label>
                <select 
                  value={selectedPortfolio || ''} 
                  onChange={(e) => setSelectedPortfolio(Number(e.target.value))}
                  disabled={portfolios.length === 0}
                >
                  {portfolios.length === 0 ? (
                    <option value="">No portfolios</option>
                  ) : (
                    portfolios.map(portfolio => (
                      <option key={portfolio.portfolioId} value={portfolio.portfolioId}>
                        {portfolio.portfolioName}
                      </option>
                    ))
                  )}
                </select>
              </div>
            </div>
          </div>

          <div className="top-bar-right">
            {selectedPortfolio && (
              <>
                <button className="action-btn add-btn" onClick={() => setShowAddHolding(true)}>
                  <span>Add Holding</span>
                </button>
                <button className="action-btn refresh-btn" onClick={handleRefresh} title="Refresh data">
                  <span>Refresh</span>
                </button>
              </>
            )}
            <button className="theme-toggle" onClick={toggleTheme} title="Toggle theme">
              {isDark ? 'Light' : 'Dark'}
            </button>
          </div>
        </div>

        <div className="page-content">
          {renderContent()}
        </div>
      </main>

      {showAddHolding && selectedPortfolio && (
        <AddHoldingModal
          portfolioId={selectedPortfolio}
          onClose={() => setShowAddHolding(false)}
          onSuccess={handleHoldingAdded}
        />
      )}

      <Chatbot />
    </div>
  );
}

export default App;
