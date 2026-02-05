create database portfolio_db;
use portfolio_db;
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255),
    default_currency VARCHAR(3) DEFAULT 'INR',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Portfolios table
CREATE TABLE portfolios (
    portfolio_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    portfolio_name VARCHAR(100) NOT NULL,
    base_currency VARCHAR(3) DEFAULT 'INR',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Portfolio targets (for drift calculation)
CREATE TABLE portfolio_targets (
    target_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    portfolio_id BIGINT NOT NULL,
    asset_type VARCHAR(50) NOT NULL,
    target_percentage DECIMAL(5,2) NOT NULL,
    FOREIGN KEY (portfolio_id) REFERENCES portfolios(portfolio_id)
);

-- Portfolio goals (goal-based forecasting)
CREATE TABLE portfolio_goals (
    goal_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    portfolio_id BIGINT NOT NULL,
    goal_name VARCHAR(120) NOT NULL,
    target_amount DECIMAL(18,2) NOT NULL,
    target_date DATE NOT NULL,
    expected_annual_return DECIMAL(5,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (portfolio_id) REFERENCES portfolios(portfolio_id)
);

-- Holdings table
CREATE TABLE holdings (
    holding_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    portfolio_id BIGINT NOT NULL,
    asset_name VARCHAR(100) NOT NULL,
    asset_type VARCHAR(50) NOT NULL,
    quantity DECIMAL(15,4) NOT NULL,
    purchase_price DECIMAL(15,2) NOT NULL,
    current_price DECIMAL(15,2) NOT NULL,
    target_value DECIMAL(15,2),
    currency VARCHAR(3) DEFAULT 'INR',
    purchase_date DATE NOT NULL,
    FOREIGN KEY (portfolio_id) REFERENCES portfolios(portfolio_id)
);

-- Portfolio snapshots (for story feature)
CREATE TABLE portfolio_snapshots (
    snapshot_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    portfolio_id BIGINT NOT NULL,
    total_value DECIMAL(15,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'INR',
    snapshot_date DATE NOT NULL,
    FOREIGN KEY (portfolio_id) REFERENCES portfolios(portfolio_id)
);

-- Sample data
INSERT INTO users (username, email, default_currency) VALUES 
('John Doe', 'john@example.com', 'USD'),
('Jane Smith', 'jane@example.com', 'EUR');

INSERT INTO portfolios (user_id, portfolio_name, base_currency) VALUES 
(1, 'My Investment Portfolio', 'USD'),
(2, 'Jane Retirement Fund', 'EUR');