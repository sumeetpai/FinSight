// Mock data for the portfolio management application
// This will be replaced with actual API calls when the Spring Boot backend is ready

export const mockStocks = [
  {
    id: '1',
    symbol: 'AAPL',
    name: 'Apple Inc.',
    current_price: 175.50,
    previous_close: 172.30,
    market_cap: 2800000000000,
    created_at: '2024-01-01T00:00:00Z',
    updated_at: '2024-01-01T00:00:00Z'
  },
  {
    id: '2',
    symbol: 'GOOGL',
    name: 'Alphabet Inc.',
    current_price: 142.80,
    previous_close: 140.20,
    market_cap: 1800000000000,
    created_at: '2024-01-01T00:00:00Z',
    updated_at: '2024-01-01T00:00:00Z'
  },
  {
    id: '3',
    symbol: 'MSFT',
    name: 'Microsoft Corporation',
    current_price: 378.90,
    previous_close: 375.20,
    market_cap: 2800000000000,
    created_at: '2024-01-01T00:00:00Z',
    updated_at: '2024-01-01T00:00:00Z'
  },
  {
    id: '4',
    symbol: 'TSLA',
    name: 'Tesla Inc.',
    current_price: 248.50,
    previous_close: 245.80,
    market_cap: 790000000000,
    created_at: '2024-01-01T00:00:00Z',
    updated_at: '2024-01-01T00:00:00Z'
  },
  {
    id: '5',
    symbol: 'AMZN',
    name: 'Amazon.com Inc.',
    current_price: 155.20,
    previous_close: 153.40,
    market_cap: 1600000000000,
    created_at: '2024-01-01T00:00:00Z',
    updated_at: '2024-01-01T00:00:00Z'
  }
];

export const mockPortfolios = [
  {
    id: '1',
    name: 'Tech Growth Portfolio',
    description: 'High-growth technology stocks',
    created_at: '2024-01-01T00:00:00Z',
    updated_at: '2024-01-01T00:00:00Z'
  },
  {
    id: '2',
    name: 'Blue Chip Stocks',
    description: 'Stable, established companies',
    created_at: '2024-01-02T00:00:00Z',
    updated_at: '2024-01-02T00:00:00Z'
  }
];

export const mockPortfolioHoldings = [
  {
    id: '1',
    portfolio_id: '1',
    stock_id: '1',
    shares: 50,
    average_cost: 160.00,
    created_at: '2024-01-01T00:00:00Z',
    updated_at: '2024-01-01T00:00:00Z',
    stocks: {
      symbol: 'AAPL',
      name: 'Apple Inc.',
      current_price: 175.50
    }
  },
  {
    id: '2',
    portfolio_id: '1',
    stock_id: '2',
    shares: 30,
    average_cost: 135.00,
    created_at: '2024-01-01T00:00:00Z',
    updated_at: '2024-01-01T00:00:00Z',
    stocks: {
      symbol: 'GOOGL',
      name: 'Alphabet Inc.',
      current_price: 142.80
    }
  },
  {
    id: '3',
    portfolio_id: '2',
    stock_id: '3',
    shares: 25,
    average_cost: 350.00,
    created_at: '2024-01-02T00:00:00Z',
    updated_at: '2024-01-02T00:00:00Z',
    stocks: {
      symbol: 'MSFT',
      name: 'Microsoft Corporation',
      current_price: 378.90
    }
  }
];

export const mockTransactions = [
  {
    id: '1',
    portfolio_id: '1',
    stock_id: '1',
    transaction_type: 'BUY',
    shares: 50,
    price_per_share: 160.00,
    total_amount: 8000.00,
    transaction_date: '2024-01-01T00:00:00Z',
    created_at: '2024-01-01T00:00:00Z',
    stocks: {
      symbol: 'AAPL',
      name: 'Apple Inc.'
    }
  },
  {
    id: '2',
    portfolio_id: '1',
    stock_id: '2',
    transaction_type: 'BUY',
    shares: 30,
    price_per_share: 135.00,
    total_amount: 4050.00,
    transaction_date: '2024-01-01T00:00:00Z',
    created_at: '2024-01-01T00:00:00Z',
    stocks: {
      symbol: 'GOOGL',
      name: 'Alphabet Inc.'
    }
  },
  {
    id: '3',
    portfolio_id: '2',
    stock_id: '3',
    transaction_type: 'BUY',
    shares: 25,
    price_per_share: 350.00,
    total_amount: 8750.00,
    transaction_date: '2024-01-02T00:00:00Z',
    created_at: '2024-01-02T00:00:00Z',
    stocks: {
      symbol: 'MSFT',
      name: 'Microsoft Corporation'
    }
  }
];

// Utility functions to simulate API delays
export const delay = (ms = 500) => new Promise(resolve => setTimeout(resolve, ms));

export const generateId = () => Math.random().toString(36).substr(2, 9);