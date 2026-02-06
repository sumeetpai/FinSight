import { apiCall, handleApiError, handleApiSuccess } from '../utils/toast.js';

const API_BASE_URL = 'http://localhost:8080/api/v1';

class PortfolioService {
  // Fetch all portfolios with stock details
  async getAllPortfolios() {
    return apiCall(async () => {
      const response = await fetch(`${API_BASE_URL}/portfolio/`);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const portfoliosData = await response.json();

      // Transform each portfolio to include holdings with stock data
      const detailedPortfolios = await Promise.all(
        portfoliosData
          .filter(p => p.active !== false) // Only active portfolios
          .map(async (portfolioData) => {
            try {
              const stockEntries = Array.isArray(portfolioData.stock_entries)
                ? portfolioData.stock_entries
                : [];
              // Fetch stock details for each stock entry
              const totalShares = stockEntries.reduce((sum, e) => sum + (e.quantity || 0), 0);
              const averageCost = totalShares > 0
                ? (Number(portfolioData.cost_basis ?? 0) / totalShares)
                : 0;

              const holdingsWithStocks = await Promise.all(
                stockEntries.map(async (entry) => {
                  try {
                    const stockResponse = await fetch(`${API_BASE_URL}/stocks/${entry.stock_id}`);
                    if (!stockResponse.ok) {
                      console.warn(`Failed to fetch stock ${entry.stock_id}`);
                      return null;
                    }
                    const stockData = await stockResponse.json();

                    return {
                      id: entry.stock_id,
                      portfolio_id: portfolioData.portfolio_id,
                      stock_id: entry.stock_id,
                      shares: entry.quantity,
                      average_cost: averageCost,
                      stock: {
                        id: stockData.stock_id,
                        symbol: stockData.stock_sym,
                        name: stockData.name,
                        current_price: stockData.current_price,
                        previous_close: stockData.day_before_price,
                        market_cap: stockData.market_cap
                      }
                    };
                  } catch (err) {
                    console.error(`Error fetching stock ${entry.stock_id}:`, err);
                    return null;
                  }
                })
              );

              // Transform to expected format
              return {
                id: portfolioData.portfolio_id,
                name: portfolioData.name,
                description: portfolioData.description || `Portfolio ${portfolioData.portfolio_id}`,
                total_value: portfolioData.total_value,
                cost_basis: portfolioData.cost_basis,
                yield: portfolioData.yield,
                user_id: portfolioData.user_id,
                holdings: holdingsWithStocks.filter(holding => holding !== null)
              };
            } catch (err) {
              console.error(`Error processing portfolio ${portfolioData.portfolio_id}:`, err);
              return null;
            }
          })
      );

      return detailedPortfolios.filter(p => p !== null);
    }, {
      errorMessage: 'Failed to load portfolios'
    });
  }

  // Create a new portfolio
  async createPortfolio(name, description) {
    return apiCall(async () => {
      const response = await fetch(`${API_BASE_URL}/portfolio/`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          name: name,
          description: description || '',
          user_id: 4,
        }),
      });

      if (!response.ok) {
        const errorData = await response.text();
        throw new Error(`Failed to create portfolio: ${response.status} ${errorData}`);
      }

      return await response.json();
    }, {
      successMessage: 'Portfolio created successfully!',
      errorMessage: 'Failed to create portfolio',
      showLoadingToast: true,
      loadingMessage: 'Creating portfolio...'
    });
  }

  // Update a portfolio
  async updatePortfolio(portfolioId, name, description) {
    return apiCall(async () => {
      const response = await fetch(`${API_BASE_URL}/portfolio/${portfolioId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          name: name,
          description: description,
          user_id: 4,
        }),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      return await response.json();
    }, {
      successMessage: 'Portfolio updated successfully!',
      errorMessage: 'Failed to update portfolio',
      showLoadingToast: true,
      loadingMessage: 'Updating portfolio...'
    });
  }

  // Remove stock from a portfolio (records a transaction server-side)
  async removeStockFromPortfolio(portfolioId, stockId, qty, userId = 4) {
    return apiCall(async () => {
      const response = await fetch(`${API_BASE_URL}/portfolio/${portfolioId}/stocks/${stockId}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          user_id: userId,
          qty: qty,
        }),
      });

      if (!response.ok) {
        const errorData = await response.text();
        throw new Error(`Failed to remove stock: ${response.status} ${errorData}`);
      }

      return await response.json();
    }, {
      successMessage: 'Stock removed from portfolio!',
      errorMessage: 'Failed to remove stock from portfolio',
      showLoadingToast: true,
      loadingMessage: 'Removing stock...'
    });
  }

  // Delete a portfolio (soft delete by setting active to false)
  async deletePortfolio(portfolioId) {
    return apiCall(async () => {
      const response = await fetch(`${API_BASE_URL}/portfolio/${portfolioId}/status`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          active: false,
        }),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      return true; // Return success indicator
    }, {
      successMessage: 'Portfolio deleted successfully!',
      errorMessage: 'Failed to delete portfolio',
      showLoadingToast: true,
      loadingMessage: 'Deleting portfolio...'
    });
  }

  // Get a single portfolio with stock details
  async getPortfolio(portfolioId) {
    return apiCall(async () => {
      // Fetch portfolio data
      const portfolioResponse = await fetch(`${API_BASE_URL}/portfolio/`);
      if (!portfolioResponse.ok) {
        throw new Error(`HTTP error! status: ${portfolioResponse.status}`);
      }
      const portfolios = await portfolioResponse.json();

      // Find the specific portfolio
      const portfolioData = portfolios.find(p => p.portfolio_id === portfolioId && p.active !== false);
      if (!portfolioData) {
        throw new Error('Portfolio not found');
      }

      // Fetch stock details for each stock entry
      const stockEntries = Array.isArray(portfolioData.stock_entries)
        ? portfolioData.stock_entries
        : [];
      const totalShares = stockEntries.reduce((sum, e) => sum + (e.quantity || 0), 0);
      const averageCost = totalShares > 0
        ? (Number(portfolioData.cost_basis ?? 0) / totalShares)
        : 0;

      const holdingsWithStocks = await Promise.all(
        stockEntries.map(async (entry) => {
          try {
            const stockResponse = await fetch(`${API_BASE_URL}/stocks/${entry.stock_id}`);
            if (!stockResponse.ok) {
              console.warn(`Failed to fetch stock ${entry.stock_id}`);
              return null;
            }
            const stockData = await stockResponse.json();

            return {
              id: entry.stock_id,
              portfolio_id: portfolioId,
              stock_id: entry.stock_id,
              shares: entry.quantity,
              average_cost: averageCost,
              stock: {
                id: stockData.stock_id,
                symbol: stockData.stock_sym,
                name: stockData.name,
                current_price: stockData.current_price,
                previous_close: stockData.day_before_price,
                market_cap: stockData.market_cap
              }
            };
          } catch (err) {
            console.error(`Error fetching stock ${entry.stock_id}:`, err);
            return null;
          }
        })
      );

      // Transform to expected format
      return {
        id: portfolioData.portfolio_id,
        name: portfolioData.name,
        description: portfolioData.description || `Portfolio ${portfolioData.portfolio_id}`,
        total_value: portfolioData.total_value,
        cost_basis: portfolioData.cost_basis,
        yield: portfolioData.yield,
        user_id: portfolioData.user_id,
        holdings: holdingsWithStocks.filter(holding => holding !== null)
      };
    }, {
      errorMessage: 'Failed to load portfolio details'
    });
  }
}

export const portfolioApi = new PortfolioService();
