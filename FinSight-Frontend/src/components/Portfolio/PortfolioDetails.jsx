import { useState, useEffect } from 'react';
import { ArrowLeft, Plus, TrendingUp, TrendingDown } from 'lucide-react';
import { HoldingsList } from './HoldingsList.jsx';
import { AddStockModal } from './AddStockModal.jsx';
import { TransactionList } from '../Transaction/TransactionList.jsx';

export function PortfolioDetails({ portfolio: initialPortfolio, onBack }) {
  const [portfolio, setPortfolio] = useState(initialPortfolio);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [showAddStock, setShowAddStock] = useState(false);
  const [activeTab, setActiveTab] = useState('holdings');

  const fetchPortfolio = async (portfolioId) => {
    setLoading(true);
    setError(null);
    try {
      // Fetch portfolio data
      const portfolioResponse = await fetch(`http://localhost:8080/api/v1/portfolio/`);
      if (!portfolioResponse.ok) {
        throw new Error(`HTTP error! status: ${portfolioResponse.status}`);
      }
      const portfolios = await portfolioResponse.json();

      // Find the specific portfolio
      const portfolioData = portfolios.find(p => p.portfolio_id === portfolioId);
      if (!portfolioData) {
        throw new Error('Portfolio not found');
      }

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
              portfolio_id: portfolioId,
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

      // Filter out null entries and transform to expected format
      const transformedPortfolio = {
        id: portfolioData.portfolio_id,
        name: portfolioData.name,
        description: `Portfolio ${portfolioData.portfolio_id}`,
        total_value: portfolioData.total_value, // This is cost basis
        cost_basis: portfolioData.cost_basis,
        yield: portfolioData.yield,
        user_id: portfolioData.user_id,
        holdings: holdingsWithStocks.filter(holding => holding !== null)
      };

      setPortfolio(transformedPortfolio);
    } catch (err) {
      setError(err.message);
      console.error('Error fetching portfolio:', err);
    } finally {
      setLoading(false);
    }
  };

  const refreshPortfolio = async () => {
    await fetchPortfolio(portfolio.id);
  };

  useEffect(() => {
    // Fetch fresh data when component mounts
    fetchPortfolio(initialPortfolio.id);
  }, [initialPortfolio.id]);

  const calculatePortfolioValue = () => {
    if (!portfolio.holdings) return 0;
    return portfolio.holdings.reduce((sum, holding) => {
      const currentPrice = holding.stock?.current_price || 0;
      return sum + (holding.shares * currentPrice);
    }, 0);
    
  };

  const calculatePortfolioCost = () => {
    // Use the total_value from API as cost basis
    return portfolio.total_value || 0;
  };

  const calculatePortfolioGain = () => {
    const value = calculatePortfolioValue();
    const cost = calculatePortfolioCost();
    return value - cost;
  };

  const calculatePortfolioGainPercent = () => {
    const cost = calculatePortfolioCost();
    if (cost === 0) return 0;
    const gain = calculatePortfolioGain();
    return (gain / cost) * 100;
  };

  const value = calculatePortfolioValue();
  const cost = calculatePortfolioCost();
  const gain = calculatePortfolioGain();
  const gainPercent = calculatePortfolioGainPercent();
  const isPositive = gain >= 0;

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
        <div className="text-red-600 text-lg font-semibold mb-2">Error loading portfolio</div>
        <div className="text-gray-600 mb-4">{error}</div>
        <button
          onClick={() => fetchPortfolio(portfolio.id)}
          className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          Try Again
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-4">
        <button
          onClick={onBack}
          className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
        >
          <ArrowLeft className="w-6 h-6 text-gray-600" />
        </button>
        <div className="flex-1">
          <h2 className="text-2xl font-bold text-gray-900">{portfolio.name}</h2>
          {portfolio.description && (
            <p className="text-gray-600 mt-1">{portfolio.description}</p>
          )}
        </div>
        <button
          onClick={() => setShowAddStock(true)}
          className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          <Plus className="w-5 h-5" />
          Add Stock
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white rounded-xl border border-gray-200 p-6">
          <div className="text-sm text-gray-600 mb-2">Portfolio Value</div>
          <div className="text-3xl font-bold text-gray-900">
            ${value.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
          </div>
        </div>

        <div className="bg-white rounded-xl border border-gray-200 p-6">
          <div className="text-sm text-gray-600 mb-2">Total Cost</div>
          <div className="text-3xl font-bold text-gray-900">
            ${cost.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
          </div>
        </div>

        <div className={`rounded-xl border p-6 ${isPositive ? 'bg-green-50 border-green-200' : 'bg-red-50 border-red-200'}`}>
          <div className="flex items-center gap-2 mb-2">
            {isPositive ? (
              <TrendingUp className="w-5 h-5 text-green-600" />
            ) : (
              <TrendingDown className="w-5 h-5 text-red-600" />
            )}
            <div className={`text-sm ${isPositive ? 'text-green-700' : 'text-red-700'}`}>
              Total Gain/Loss
            </div>
          </div>
          <div className={`text-3xl font-bold ${isPositive ? 'text-green-700' : 'text-red-700'}`}>
            {isPositive ? '+' : ''}${gain.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
          </div>
          <div className={`text-sm font-semibold mt-2 ${isPositive ? 'text-green-600' : 'text-red-600'}`}>
            {isPositive ? '+' : ''}{gainPercent.toFixed(2)}%
          </div>
        </div>
      </div>

      <div className="bg-white rounded-xl border border-gray-200">
        <div className="border-b border-gray-200">
          <div className="flex">
            <button
              onClick={() => setActiveTab('holdings')}
              className={`flex-1 px-6 py-4 text-sm font-medium border-b-2 transition-colors ${
                activeTab === 'holdings'
                  ? 'border-blue-600 text-blue-600'
                  : 'border-transparent text-gray-600 hover:text-gray-900'
              }`}
            >
              Holdings ({portfolio.holdings?.length || 0})
            </button>
            <button
              onClick={() => setActiveTab('transactions')}
              className={`flex-1 px-6 py-4 text-sm font-medium border-b-2 transition-colors ${
                activeTab === 'transactions'
                  ? 'border-blue-600 text-blue-600'
                  : 'border-transparent text-gray-600 hover:text-gray-900'
              }`}
            >
              Transaction History
            </button>
          </div>
        </div>

        <div className="p-6">
          {activeTab === 'holdings' ? (
            <HoldingsList
              portfolio={portfolio}
              onUpdate={refreshPortfolio}
            />
          ) : (
            <TransactionList
              portfolioId={portfolio.id}
              onUpdate={refreshPortfolio}
            />
          )}
        </div>
      </div>

      {showAddStock && (
        <AddStockModal
          portfolioId={portfolio.id}
          onClose={() => setShowAddStock(false)}
          onAdded={() => {
            setShowAddStock(false);
            refreshPortfolio();
          }}
        />
      )}
    </div>
  );
}
