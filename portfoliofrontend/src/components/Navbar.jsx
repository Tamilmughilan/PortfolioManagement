import React from 'react';
import '../styles/Navbar.css';

const Navbar = ({ activeSection, onSectionChange, onLogout, user }) => {
  const navItems = [
    { id: 'dashboard', label: 'Dashboard' },
    { id: 'portfolio', label: 'Portfolio' },
    { id: 'assets', label: 'Assets' },
    { id: 'performance', label: 'Performance' },
    { id: 'drift', label: 'Drift Story' },
    { id: 'story', label: 'Portfolio Story' },
  ];

  return (
    <nav className="navbar">
      <div className="navbar-header">
        <div className="navbar-logo">
          <span>Portfolio</span>
        </div>
      </div>

      {user && (
        <div className="navbar-user">
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
              <span>{item.label}</span>
            </button>
          </li>
        ))}
      </ul>

      <div className="navbar-footer">
        <button className="nav-item">
          <span>Settings</span>
        </button>
        <button className="nav-item" onClick={onLogout}>
          <span>Logout</span>
        </button>
      </div>
    </nav>
  );
};

export default Navbar;