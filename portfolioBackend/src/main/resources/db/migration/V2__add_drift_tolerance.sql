-- Add drift_tolerance_percentage column to portfolios table
ALTER TABLE portfolios ADD COLUMN drift_tolerance_percentage DECIMAL(5, 2) DEFAULT 5.00;

-- Add comment to explain the field
ALTER TABLE portfolios MODIFY COLUMN drift_tolerance_percentage DECIMAL(5, 2) DEFAULT 5.00 COMMENT 'Tolerance percentage for drift alerts (e.g., 5 means alert if drift > 5%)';
