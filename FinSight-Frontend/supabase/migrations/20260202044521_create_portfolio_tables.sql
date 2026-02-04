/*
  # Stock Portfolio Management System

  ## Overview
  Creates a comprehensive database schema for managing stock portfolios.
  This is a demo system without user authentication - all data is publicly accessible.

  ## New Tables

  ### 1. `stocks`
  Stores information about available stocks in the market
  - `id` (uuid, primary key) - Unique identifier for each stock
  - `symbol` (text, unique) - Stock ticker symbol (e.g., AAPL, GOOGL)
  - `name` (text) - Full company name
  - `current_price` (numeric) - Current market price
  - `previous_close` (numeric) - Previous day's closing price
  - `market_cap` (numeric) - Market capitalization
  - `created_at` (timestamptz) - Record creation timestamp
  - `updated_at` (timestamptz) - Last update timestamp

  ### 2. `portfolios`
  Stores portfolio information
  - `id` (uuid, primary key) - Unique identifier for each portfolio
  - `user_id` (uuid, nullable) - Optional user identifier (for future auth)
  - `name` (text) - Portfolio name
  - `description` (text) - Portfolio description
  - `created_at` (timestamptz) - Portfolio creation date
  - `updated_at` (timestamptz) - Last modification date

  ### 3. `portfolio_holdings`
  Tracks stock holdings within portfolios
  - `id` (uuid, primary key) - Unique identifier
  - `portfolio_id` (uuid, foreign key) - References portfolios
  - `stock_id` (uuid, foreign key) - References stocks
  - `shares` (numeric) - Number of shares owned
  - `average_cost` (numeric) - Average cost basis per share
  - `created_at` (timestamptz) - Record creation timestamp
  - `updated_at` (timestamptz) - Last update timestamp

  ### 4. `transactions`
  Records all buy/sell transactions
  - `id` (uuid, primary key) - Unique identifier
  - `portfolio_id` (uuid, foreign key) - References portfolios
  - `stock_id` (uuid, foreign key) - References stocks
  - `transaction_type` (text) - 'BUY' or 'SELL'
  - `shares` (numeric) - Number of shares traded
  - `price_per_share` (numeric) - Transaction price per share
  - `total_amount` (numeric) - Total transaction amount
  - `transaction_date` (timestamptz) - When the transaction occurred
  - `created_at` (timestamptz) - Record creation timestamp

  ## Security
  - Row Level Security (RLS) enabled on all tables
  - All tables are publicly accessible for demo purposes
  - No authentication required

  ## Indexes
  - Created on foreign keys for better query performance
  - Unique constraints on stock symbols
  - Composite index on portfolio_holdings for faster lookups
*/

-- Create stocks table
CREATE TABLE IF NOT EXISTS stocks (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  symbol text UNIQUE NOT NULL,
  name text NOT NULL,
  current_price numeric(12, 2) DEFAULT 0,
  previous_close numeric(12, 2) DEFAULT 0,
  market_cap numeric(20, 2) DEFAULT 0,
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);

-- Create portfolios table
CREATE TABLE IF NOT EXISTS portfolios (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id uuid,
  name text NOT NULL,
  description text DEFAULT '',
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);

-- Create portfolio_holdings table
CREATE TABLE IF NOT EXISTS portfolio_holdings (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  portfolio_id uuid REFERENCES portfolios(id) ON DELETE CASCADE NOT NULL,
  stock_id uuid REFERENCES stocks(id) ON DELETE CASCADE NOT NULL,
  shares numeric(12, 4) DEFAULT 0,
  average_cost numeric(12, 2) DEFAULT 0,
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now(),
  UNIQUE(portfolio_id, stock_id)
);

-- Create transactions table
CREATE TABLE IF NOT EXISTS transactions (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  portfolio_id uuid REFERENCES portfolios(id) ON DELETE CASCADE NOT NULL,
  stock_id uuid REFERENCES stocks(id) ON DELETE CASCADE NOT NULL,
  transaction_type text NOT NULL CHECK (transaction_type IN ('BUY', 'SELL')),
  shares numeric(12, 4) NOT NULL,
  price_per_share numeric(12, 2) NOT NULL,
  total_amount numeric(12, 2) NOT NULL,
  transaction_date timestamptz DEFAULT now(),
  created_at timestamptz DEFAULT now()
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_portfolios_user_id ON portfolios(user_id);
CREATE INDEX IF NOT EXISTS idx_portfolio_holdings_portfolio_id ON portfolio_holdings(portfolio_id);
CREATE INDEX IF NOT EXISTS idx_portfolio_holdings_stock_id ON portfolio_holdings(stock_id);
CREATE INDEX IF NOT EXISTS idx_transactions_portfolio_id ON transactions(portfolio_id);
CREATE INDEX IF NOT EXISTS idx_transactions_stock_id ON transactions(stock_id);

-- Enable Row Level Security
ALTER TABLE stocks ENABLE ROW LEVEL SECURITY;
ALTER TABLE portfolios ENABLE ROW LEVEL SECURITY;
ALTER TABLE portfolio_holdings ENABLE ROW LEVEL SECURITY;
ALTER TABLE transactions ENABLE ROW LEVEL SECURITY;

-- Stocks policies (publicly accessible)
CREATE POLICY "Stocks are viewable by everyone"
  ON stocks FOR SELECT
  TO public
  USING (true);

CREATE POLICY "Anyone can insert stocks"
  ON stocks FOR INSERT
  TO public
  WITH CHECK (true);

CREATE POLICY "Anyone can update stocks"
  ON stocks FOR UPDATE
  TO public
  USING (true)
  WITH CHECK (true);

CREATE POLICY "Anyone can delete stocks"
  ON stocks FOR DELETE
  TO public
  USING (true);

-- Portfolios policies (publicly accessible)
CREATE POLICY "Anyone can view portfolios"
  ON portfolios FOR SELECT
  TO public
  USING (true);

CREATE POLICY "Anyone can create portfolios"
  ON portfolios FOR INSERT
  TO public
  WITH CHECK (true);

CREATE POLICY "Anyone can update portfolios"
  ON portfolios FOR UPDATE
  TO public
  USING (true)
  WITH CHECK (true);

CREATE POLICY "Anyone can delete portfolios"
  ON portfolios FOR DELETE
  TO public
  USING (true);

CREATE POLICY "Anyone can update portfolios"
  ON portfolios FOR UPDATE
  TO public
  USING (true)
  WITH CHECK (true);

CREATE POLICY "Anyone can delete portfolios"
  ON portfolios FOR DELETE
  TO public
  USING (true);

-- Portfolio holdings policies
CREATE POLICY "Anyone can view holdings"
  ON portfolio_holdings FOR SELECT
  TO public
  USING (true);

CREATE POLICY "Anyone can create holdings"
  ON portfolio_holdings FOR INSERT
  TO public
  WITH CHECK (true);

CREATE POLICY "Anyone can update holdings"
  ON portfolio_holdings FOR UPDATE
  TO public
  USING (true)
  WITH CHECK (true);

CREATE POLICY "Anyone can delete holdings"
  ON portfolio_holdings FOR DELETE
  TO public
  USING (true);

-- Transactions policies
CREATE POLICY "Anyone can view transactions"
  ON transactions FOR SELECT
  TO public
  USING (true);

CREATE POLICY "Anyone can create transactions"
  ON transactions FOR INSERT
  TO public
  WITH CHECK (true);

CREATE POLICY "Anyone can update transactions"
  ON transactions FOR UPDATE
  TO public
  USING (true)
  WITH CHECK (true);

CREATE POLICY "Anyone can delete transactions"
  ON transactions FOR DELETE
  TO public
  USING (true);