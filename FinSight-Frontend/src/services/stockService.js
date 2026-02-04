import { mockStocks, delay, generateId } from '../lib/mockData.js';

export const stockService = {
  async getAll() {
    await delay();
    return [...mockStocks].sort((a, b) => a.symbol.localeCompare(b.symbol));
  },

  async getBySymbol(symbol) {
    await delay();
    return mockStocks.find(stock => stock.symbol === symbol) || null;
  },

  async create(stock) {
    await delay();
    const newStock = {
      id: generateId(),
      ...stock,
      created_at: new Date().toISOString(),
      updated_at: new Date().toISOString()
    };
    mockStocks.push(newStock);
    return newStock;
  },

  async update(id, updates) {
    await delay();
    const stock = mockStocks.find(s => s.id === id);
    if (!stock) throw new Error('Stock not found');

    Object.assign(stock, {
      ...updates,
      updated_at: new Date().toISOString()
    });
    return stock;
  },

  async delete(id) {
    await delay();
    const index = mockStocks.findIndex(s => s.id === id);
    if (index === -1) throw new Error('Stock not found');

    mockStocks.splice(index, 1);
  },

  async search(query) {
    await delay();
    const lowerQuery = query.toLowerCase();
    return mockStocks
      .filter(stock =>
        stock.symbol.toLowerCase().includes(lowerQuery) ||
        stock.name.toLowerCase().includes(lowerQuery)
      )
      .sort((a, b) => a.symbol.localeCompare(b.symbol))
      .slice(0, 10);
  }
};
