import React from 'react';
import { BarChart3, DollarSign, TrendingUp, TrendingDown, Settings, LogOut, Home, UserCircle2, History } from 'lucide-react';
import '../styles/Navbar.css';

const Navbar = ({ activeSection, onSectionChange, onLogout, user }) => {
  const navItems = [
    { id: 'dashboard', label: 'Dashboard', icon: Home },
    { id: 'portfolio', label: 'Portfolio', icon: BarChart3 },
    { id: 'assets', label: 'Assets', icon: DollarSign },
    { id: 'performance', label: 'Performance', icon: TrendingUp },
    { id: 'drift', label: 'Drift Story', icon: TrendingDown },
    { id: 'story', label: 'Portfolio Story', icon: History },
  ];

  return (
    <nav className="navbar">
      <div className="navbar-header">
        <div className="navbar-logo">
          <BarChart3 size={24} />
          <span>Portfolio</span>
        </div>
      </div>

      {user && (
        <div className="navbar-user">
          <div className="navbar-user-icon">
            <UserCircle2 size={22} />
          </div>
          <div className="navbar-user-info">
            <span className="navbar-user-name">{user.username}</span>
            <span className="navbar-user-email">{user.email}</span>
          </div>
        </div>
      )}

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
        <button className="nav-item" onClick={onLogout}>
          <LogOut size={20} />
          <span>Logout</span>
        </button>
      </div>
    </nav>
  );
};

export default Navbar;