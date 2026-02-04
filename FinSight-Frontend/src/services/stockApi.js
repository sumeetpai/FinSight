import { apiCall, handleApiError, handleApiSuccess } from '../utils/toast.js';

const API_BASE_URL = 'http://localhost:8080/api/v1';

class StockService {
  // Get all stocks
  async getAllStocks() {
    return apiCall(async () => {
      const response = await fetch(`${API_BASE_URL}/stocks/`);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const stocks = await response.json();
      return stocks;
    }, {
      errorMessage: 'Failed to load stocks'
    });
  }

  // Get a single stock by ID
  async getStockById(stockId) {
    return apiCall(async () => {
      const response = await fetch(`${API_BASE_URL}/stocks/${stockId}`);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const stock = await response.json();
      return stock;
    }, {
      errorMessage: 'Failed to load stock details'
    });
  }

  // Search stocks by symbol or name
  async searchStocks(query) {
    return apiCall(async () => {
      const response = await fetch(`${API_BASE_URL}/stocks/search?q=${encodeURIComponent(query)}`);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const stocks = await response.json();
      return stocks;
    }, {
      errorMessage: 'Failed to search stocks'
    });
  }

  // Create a new stock
  async createStock(stockData) {
    return apiCall(async () => {
      const response = await fetch(`${API_BASE_URL}/stocks/`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          stock_sym: stockData.symbol,
          name: stockData.name,
          current_price: stockData.current_price,
          day_before_price: stockData.previous_close,
          market_cap: stockData.market_cap,
        }),
      });

      if (!response.ok) {
        const errorData = await response.text();
        throw new Error(`Failed to create stock: ${response.status} ${errorData}`);
      }

      return await response.json();
    }, {
      successMessage: 'Stock created successfully!',
      errorMessage: 'Failed to create stock',
      showLoadingToast: true,
      loadingMessage: 'Creating stock...'
    });
  }
}

export const stockApi = new StockService();