import { useState, useEffect } from 'react';
import { Plus, Folder, TrendingUp, TrendingDown, DollarSign } from 'lucide-react';
import { CreatePortfolioModal } from './CreatePortfolioModal.jsx';
import { PortfolioCard } from './PortfolioCard.jsx';

export function PortfolioList({ onSelectPortfolio }) {
  const [portfolios, setPortfolios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showCreateModal, setShowCreateModal] = useState(false);

  useEffect(() => {
    loadPortfolios();
  }, []);

  const loadPortfolios = async () => {
    try {
      setLoading(true);
      setError(null);

      // Fetch all portfolios
      const response = await fetch('http://localhost:8080/api/v1/portfolio/');
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const portfoliosData = await response.json();

      // Transform each portfolio to include holdings with stock data
      const detailedPortfolios = await Promise.all(
        portfoliosData.map(async (portfolioData) => {
          try {
            // Fetch stock details for each stock entry
            const holdingsWithStocks = await Promise.all(
              portfolioData.stock_entries.map(async (entry) => {
                try {
                  const stockResponse = await fetch(`http://localhost:8080/api/v1/stocks/${entry.stock_id}`);
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
                    average_cost: portfolioData.total_value / portfolioData.stock_entries.reduce((sum, e) => sum + e.quantity, 0), // Cost per share
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
              description: `Portfolio ${portfolioData.portfolio_id}`,
              total_value: portfolioData.total_value, // Cost basis
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

      setPortfolios(detailedPortfolios.filter(p => p !== null));
    } catch (err) {
      setError(err.message);
      console.error('Error loading portfolios:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreatePortfolio = async (name, description) => {
    const response = await fetch('http://localhost:8080/api/v1/portfolio/', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        name: name,
        description: description || '',
        user_id: 4, // Assuming user_id 4 for now - you may want to get this from auth context
      }),
    });

    if (!response.ok) {
      const errorData = await response.text();
      throw new Error(`Failed to create portfolio: ${response.status} ${errorData}`);
    }

    // Refresh the portfolio list to show the newly created portfolio
    await loadPortfolios();
    setShowCreateModal(false);
  };

  const calculatePortfolioValue = (portfolio) => {
    if (!portfolio.holdings) return 0;
    return portfolio.holdings.reduce((sum, holding) => {
      const currentPrice = holding.stock?.current_price || 0;
      return sum + (holding.shares * currentPrice);
    }, 0);
  };

  const calculatePortfolioCost = (portfolio) => {
    // Use the total_value from API as cost basis
    return portfolio.total_value || 0;
  };

  const calculatePortfolioGain = (portfolio) => {
    const value = calculatePortfolioValue(portfolio);
    const cost = calculatePortfolioCost(portfolio);
    return value - cost;
  };

  const calculatePortfolioGainPercent = (portfolio) => {
    const cost = calculatePortfolioCost(portfolio);
    if (cost === 0) return 0;
    const gain = calculatePortfolioGain(portfolio);
    return (gain / cost) * 100;
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center py-12">
        <div className="text-red-600 text-lg font-semibold mb-2">Error loading portfolios</div>
        <div className="text-gray-600 mb-4">{error}</div>
        <button
          onClick={loadPortfolios}
          className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          Try Again
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold text-gray-900">Your Portfolios</h2>
        <button
          onClick={() => setShowCreateModal(true)}
          className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          <Plus className="w-5 h-5" />
          Create Portfolio
        </button>
      </div>

      {portfolios.length === 0 ? (
        <div className="bg-white rounded-xl border-2 border-dashed border-gray-300 p-12 text-center">
          <Folder className="w-16 h-16 text-gray-400 mx-auto mb-4" />
          <h3 className="text-xl font-semibold text-gray-900 mb-2">No Portfolios Yet</h3>
          <p className="text-gray-600 mb-6">Create your first portfolio to start tracking your investments</p>
          <button
            onClick={() => setShowCreateModal(true)}
            className="inline-flex items-center gap-2 px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          >
            <Plus className="w-5 h-5" />
            Create Your First Portfolio
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {portfolios.map((portfolio) => (
            <PortfolioCard
              key={portfolio.id}
              portfolio={portfolio}
              value={calculatePortfolioValue(portfolio)}
              gain={calculatePortfolioGain(portfolio)}
              gainPercent={calculatePortfolioGainPercent(portfolio)}
              onClick={() => onSelectPortfolio(portfolio)}
            />
          ))}
        </div>
      )}

      {showCreateModal && (
        <CreatePortfolioModal
          onClose={() => setShowCreateModal(false)}
          onCreate={handleCreatePortfolio}
        />
      )}
    </div>
  );
}
