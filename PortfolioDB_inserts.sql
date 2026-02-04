-- Sample insertions with bcrypt password_hash.
-- Password for all users below: Password@123

INSERT INTO users (username, email, password_hash, default_currency)
VALUES
('Sudharshan PS', 'sudharshanps@gmail.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoO8vQOQ5Hmf/8Y4GQJ0i8x3y1uG5sPqH2', 'USD'),
('Tamil ME', 'tamilme@gmail.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoO8vQOQ5Hmf/8Y4GQJ0i8x3y1uG5sPqH2', 'EUR');

INSERT INTO portfolios (user_id, portfolio_name, base_currency)
VALUES
(1, 'My Investment Portfolio', 'USD'),
(2, 'Jane Retirement Fund', 'EUR');

INSERT INTO holdings (portfolio_id, asset_name, asset_type, quantity, purchase_price, current_price, currency, purchase_date)
VALUES
(1, 'Apple Inc', 'STOCK', 10.0000, 150.00, 190.00, 'USD', '2024-01-10'),
(1, 'Vanguard S&P 500', 'ETF', 5.0000, 380.00, 420.00, 'USD', '2024-02-05'),
(2, 'Siemens AG', 'STOCK', 12.0000, 90.00, 110.00, 'EUR', '2024-03-14');

INSERT INTO portfolio_targets (portfolio_id, asset_type, target_percentage)
VALUES
(1, 'STOCK', 60.00),
(1, 'ETF', 40.00),
(2, 'STOCK', 70.00);

INSERT INTO portfolio_snapshots (portfolio_id, total_value, currency, snapshot_date)
VALUES
(1, 5000.00, 'USD', '2024-01-15'),
(1, 5600.00, 'USD', '2024-02-15'),
(1, 6100.00, 'USD', '2024-03-15'),
(2, 4200.00, 'EUR', '2024-02-01'),
(2, 4500.00, 'EUR', '2024-03-01');