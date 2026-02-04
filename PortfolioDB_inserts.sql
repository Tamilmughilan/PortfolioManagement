INSERT INTO portfolios (user_id, portfolio_name, base_currency)
VALUES
(1, 'Sab Hai Mutual Funds', 'USD'),
(2, 'Long Term Builder', 'EUR');

INSERT INTO holdings (portfolio_id, asset_name, asset_type, quantity, purchase_price, current_price, currency, purchase_date)
VALUES
(1, 'Global Tech Basket', 'STOCK', 10.0000, 150.00, 190.00, 'USD', '2024-01-10'),
(1, 'Bluechip Index Fund', 'ETF', 5.0000, 380.00, 420.00, 'USD', '2024-02-05'),
(2, 'Euro Growth Equity', 'STOCK', 12.0000, 90.00, 110.00, 'EUR', '2024-03-14');

INSERT INTO portfolio_targets (portfolio_id, asset_type, target_percentage)
VALUES
(1, 'STOCK', 60.00),
(1, 'ETF', 40.00),
(2, 'STOCK', 70.00);

INSERT INTO portfolio_goals (portfolio_id, goal_name, target_amount, target_date, expected_annual_return)
VALUES
(1, 'Starter Goal', 15000.00, '2026-12-31', 8.00),
(2, 'Long Horizon Goal', 20000.00, '2027-06-30', 7.50);

-- Additional test data for existing user_id = 1
INSERT INTO portfolios (user_id, portfolio_name, base_currency)
VALUES
(1, 'Sab Hai Growth', 'INR'),
(1, 'Stable Income Vault', 'INR'),
(1, 'High Volatility Lab', 'INR');

-- Holdings for user_id 1 portfolios (assumes portfolio_id 3,4,5 are created above)
INSERT INTO holdings (portfolio_id, asset_name, asset_type, quantity, purchase_price, current_price, currency, purchase_date)
VALUES
(3, 'Large Cap Core', 'STOCK', 20.0000, 2200.00, 2450.00, 'INR', '2024-01-12'),
(3, 'Mid Cap Momentum', 'STOCK', 30.0000, 650.00, 780.00, 'INR', '2024-02-05'),
(3, 'Nifty 50 Tracker', 'ETF', 15.0000, 185.00, 210.00, 'INR', '2024-02-20'),
(4, 'Government Bond 2033', 'BOND', 10.0000, 980.00, 1010.00, 'INR', '2023-12-10'),
(4, 'Debt Advantage Fund', 'MUTUAL_FUND', 50.0000, 120.00, 128.00, 'INR', '2024-01-25'),
(5, 'Alpha Crypto Index', 'CRYPTO', 0.0500, 2500000.00, 3200000.00, 'INR', '2024-03-02'),
(5, 'Beta Crypto Index', 'CRYPTO', 0.5000, 150000.00, 210000.00, 'INR', '2024-03-12');

-- Targets for user_id 1 portfolios
INSERT INTO portfolio_targets (portfolio_id, asset_type, target_percentage)
VALUES
(3, 'STOCK', 70.00),
(3, 'ETF', 30.00),
(4, 'BOND', 60.00),
(4, 'MUTUAL_FUND', 40.00),
(5, 'CRYPTO', 100.00);

-- Snapshots for user_id 1 portfolios
INSERT INTO portfolio_snapshots (portfolio_id, total_value, currency, snapshot_date)
VALUES
(3, 120000.00, 'INR', '2024-01-31'),
(3, 138500.00, 'INR', '2024-02-29'),
(3, 152300.00, 'INR', '2024-03-31'),
(4, 82000.00, 'INR', '2024-01-31'),
(4, 84500.00, 'INR', '2024-02-29'),
(4, 86000.00, 'INR', '2024-03-31'),
(5, 200000.00, 'INR', '2024-03-15'),
(5, 255000.00, 'INR', '2024-04-15');

-- Goals for user_id 1 portfolios
INSERT INTO portfolio_goals (portfolio_id, goal_name, target_amount, target_date, expected_annual_return)
VALUES
(3, 'Education Goal', 500000.00, '2029-06-30', 10.00),
(4, 'Safety Net', 200000.00, '2026-12-31', 6.00),
(5, 'High Risk Target', 300000.00, '2026-06-30', 18.00);

-- Additional portfolios for user_id 1 and user_id 2 (assumes portfolio_id 6-9)
INSERT INTO portfolios (user_id, portfolio_name, base_currency)
VALUES
(1, 'Bluechip Core', 'INR'),
(1, 'Global Tech Growth', 'INR'),
(2, 'Value Hunters', 'EUR'),
(2, 'Balanced Shield', 'EUR');

-- Holdings for portfolio_id 6-9
INSERT INTO holdings (portfolio_id, asset_name, asset_type, quantity, purchase_price, current_price, currency, purchase_date)
VALUES
(6, 'Infosys', 'STOCK', 40.0000, 1300.00, 1480.00, 'INR', '2024-01-15'),
(6, 'HDFC Bank', 'STOCK', 25.0000, 1500.00, 1650.00, 'INR', '2024-02-12'),
(6, 'ICICI Bank', 'STOCK', 30.0000, 900.00, 1040.00, 'INR', '2024-03-10'),
(7, 'NVIDIA', 'STOCK', 8.0000, 420.00, 720.00, 'INR', '2024-01-20'),
(7, 'Microsoft', 'STOCK', 6.0000, 310.00, 420.00, 'INR', '2024-02-18'),
(7, 'Alphabet', 'STOCK', 10.0000, 125.00, 165.00, 'INR', '2024-03-08'),
(8, 'SAP SE', 'STOCK', 15.0000, 110.00, 140.00, 'EUR', '2024-01-22'),
(8, 'LVMH', 'STOCK', 4.0000, 650.00, 710.00, 'EUR', '2024-02-14'),
(9, 'Siemens', 'STOCK', 8.0000, 120.00, 135.00, 'EUR', '2024-01-12'),
(9, 'Euro Bond Fund', 'BOND', 20.0000, 98.00, 101.50, 'EUR', '2024-02-02');

-- Targets for portfolio_id 6-9
INSERT INTO portfolio_targets (portfolio_id, asset_type, target_percentage)
VALUES
(6, 'STOCK', 100.00),
(7, 'STOCK', 100.00),
(8, 'STOCK', 100.00),
(9, 'STOCK', 60.00),
(9, 'BOND', 40.00);

-- Deeper snapshots for portfolio_id 1-2
INSERT INTO portfolio_snapshots (portfolio_id, total_value, currency, snapshot_date)
VALUES
(1, 4800.00, 'USD', '2023-12-15'),
(1, 5300.00, 'USD', '2024-04-15'),
(1, 5900.00, 'USD', '2024-05-15'),
(1, 6400.00, 'USD', '2024-06-15'),
(2, 4000.00, 'EUR', '2024-01-01'),
(2, 4650.00, 'EUR', '2024-04-01'),
(2, 4800.00, 'EUR', '2024-05-01'),
(2, 5050.00, 'EUR', '2024-06-01');

-- Deeper snapshots for portfolio_id 3-5
INSERT INTO portfolio_snapshots (portfolio_id, total_value, currency, snapshot_date)
VALUES
(3, 110000.00, 'INR', '2023-12-31'),
(3, 165000.00, 'INR', '2024-04-30'),
(3, 176500.00, 'INR', '2024-05-31'),
(3, 182000.00, 'INR', '2024-06-30'),
(4, 79000.00, 'INR', '2023-12-31'),
(4, 87500.00, 'INR', '2024-04-30'),
(4, 89000.00, 'INR', '2024-05-31'),
(4, 90500.00, 'INR', '2024-06-30'),
(5, 185000.00, 'INR', '2024-02-15'),
(5, 270000.00, 'INR', '2024-05-15'),
(5, 295000.00, 'INR', '2024-06-15');

-- Snapshots for portfolio_id 6-9
INSERT INTO portfolio_snapshots (portfolio_id, total_value, currency, snapshot_date)
VALUES
(6, 95000.00, 'INR', '2024-01-31'),
(6, 102000.00, 'INR', '2024-02-29'),
(6, 114500.00, 'INR', '2024-03-31'),
(6, 121000.00, 'INR', '2024-04-30'),
(7, 210000.00, 'INR', '2024-02-29'),
(7, 248000.00, 'INR', '2024-03-31'),
(7, 265000.00, 'INR', '2024-04-30'),
(7, 289000.00, 'INR', '2024-05-31'),
(8, 52000.00, 'EUR', '2024-02-29'),
(8, 55800.00, 'EUR', '2024-03-31'),
(8, 59000.00, 'EUR', '2024-04-30'),
(9, 46000.00, 'EUR', '2024-02-29'),
(9, 48250.00, 'EUR', '2024-03-31'),
(9, 50500.00, 'EUR', '2024-04-30');

INSERT INTO portfolio_snapshots (portfolio_id, total_value, currency, snapshot_date)
VALUES
(1, 5000.00, 'USD', '2024-01-15'),
(1, 5600.00, 'USD', '2024-02-15'),
(1, 6100.00, 'USD', '2024-03-15'),
(2, 4200.00, 'EUR', '2024-02-01'),
(2, 4500.00, 'EUR', '2024-03-01');