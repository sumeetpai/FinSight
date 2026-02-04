import { mockTransactions, mockPortfolioHoldings, mockStocks, delay, generateId } from '../lib/mockData.js';
import { portfolioService } from './portfolioService.js';

export const transactionService = {
  async getByPortfolio(portfolioId) {
    await delay();
    return mockTransactions
      .filter(t => t.portfolio_id === portfolioId)
      .map(transaction => ({
        ...transaction,
        stocks: mockStocks.find(s => s.id === transaction.stock_id)
      }))
      .sort((a, b) => new Date(b.transaction_date) - new Date(a.transaction_date));
  },

  async create(transaction) {
    await delay();
    const newTransaction = {
      id: generateId(),
      ...transaction,
      total_amount: transaction.shares * transaction.price_per_share,
      created_at: new Date().toISOString(),
      transaction_date: transaction.transaction_date || new Date().toISOString()
    };
    mockTransactions.push(newTransaction);

    await this.updatePortfolioHolding(
      transaction.portfolio_id,
      transaction.stock_id,
      transaction.transaction_type,
      transaction.shares,
      transaction.price_per_share
    );

    return newTransaction;
  },

  async updatePortfolioHolding(
    portfolioId,
    stockId,
    transactionType,
    shares,
    pricePerShare
  ) {
    await delay();
    const existing = mockPortfolioHoldings.find(
      h => h.portfolio_id === portfolioId && h.stock_id === stockId
    );

    if (transactionType === 'BUY') {
      if (existing) {
        const totalShares = existing.shares + shares;
        const totalCost = (existing.shares * existing.average_cost) + (shares * pricePerShare);
        const newAverageCost = totalCost / totalShares;

        await portfolioService.updateHolding(existing.id, totalShares, newAverageCost);
      } else {
        await portfolioService.addHolding({
          portfolio_id: portfolioId,
          stock_id: stockId,
          shares,
          average_cost: pricePerShare
        });
      }
    } else if (transactionType === 'SELL' && existing) {
      const newShares = existing.shares - shares;
      if (newShares <= 0) {
        await portfolioService.deleteHolding(existing.id);
      } else {
        await portfolioService.updateHolding(existing.id, newShares, existing.average_cost);
      }
    }
  },

  async delete(id) {
    await delay();
    const index = mockTransactions.findIndex(t => t.id === id);
    if (index === -1) throw new Error('Transaction not found');

    mockTransactions.splice(index, 1);
  }
};
