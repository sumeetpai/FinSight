import { mockPortfolios, mockPortfolioHoldings, mockStocks, delay, generateId } from '../lib/mockData.js';

export const portfolioService = {
  async getAll() {
    await delay();
    // Return portfolios with detailed holdings information
    return mockPortfolios.map(portfolio => {
      const holdings = mockPortfolioHoldings.filter(h => h.portfolio_id === portfolio.id);
      return {
        ...portfolio,
        holdings: holdings.map(holding => ({
          ...holding,
          stocks: mockStocks.find(s => s.id === holding.stock_id)
        }))
      };
    });
  },

  async getById(id) {
    await delay();
    const portfolio = mockPortfolios.find(p => p.id === id);
    if (!portfolio) return null;

    const holdings = mockPortfolioHoldings.filter(h => h.portfolio_id === id);
    return {
      ...portfolio,
      holdings: holdings.map(holding => ({
        ...holding,
        stocks: mockStocks.find(s => s.id === holding.stock_id)
      }))
    };
  },

  async create(portfolio) {
    await delay();
    const newPortfolio = {
      id: generateId(),
      ...portfolio,
      created_at: new Date().toISOString(),
      updated_at: new Date().toISOString()
    };
    mockPortfolios.push(newPortfolio);
    return newPortfolio;
  },

  async update(id, updates) {
    await delay();
    const portfolio = mockPortfolios.find(p => p.id === id);
    if (!portfolio) throw new Error('Portfolio not found');

    Object.assign(portfolio, {
      ...updates,
      updated_at: new Date().toISOString()
    });
    return portfolio;
  },

  async delete(id) {
    await delay();
    const index = mockPortfolios.findIndex(p => p.id === id);
    if (index === -1) throw new Error('Portfolio not found');

    mockPortfolios.splice(index, 1);
    // Also remove related holdings
    const holdingsToRemove = mockPortfolioHoldings.filter(h => h.portfolio_id === id);
    holdingsToRemove.forEach(holding => {
      const holdingIndex = mockPortfolioHoldings.findIndex(h => h.id === holding.id);
      if (holdingIndex !== -1) mockPortfolioHoldings.splice(holdingIndex, 1);
    });
  },

  async addHolding(holding) {
    await delay();
    const newHolding = {
      id: generateId(),
      ...holding,
      created_at: new Date().toISOString(),
      updated_at: new Date().toISOString()
    };
    mockPortfolioHoldings.push(newHolding);
    return newHolding;
  },

  async updateHolding(id, shares, averageCost) {
    await delay();
    const holding = mockPortfolioHoldings.find(h => h.id === id);
    if (!holding) throw new Error('Holding not found');

    Object.assign(holding, {
      shares,
      average_cost: averageCost,
      updated_at: new Date().toISOString()
    });
    return holding;
  },

  async deleteHolding(id) {
    await delay();
    const index = mockPortfolioHoldings.findIndex(h => h.id === id);
    if (index === -1) throw new Error('Holding not found');

    mockPortfolioHoldings.splice(index, 1);
  }
};
