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