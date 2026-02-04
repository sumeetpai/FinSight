import { useState, useEffect, act } from 'react';
import { ArrowLeft, Plus, TrendingUp, TrendingDown, MoreVertical, Edit, Trash2 } from 'lucide-react';
import { HoldingsList } from './HoldingsList.jsx';
import { AddStockModal } from './AddStockModal.jsx';
import { TransactionList } from '../Transaction/TransactionList.jsx';

export function PortfolioDetails({ portfolio: initialPortfolio, onBack, onPortfolioUpdate }) {
  const [portfolio, setPortfolio] = useState(initialPortfolio);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [showAddStock, setShowAddStock] = useState(false);
  const [activeTab, setActiveTab] = useState('holdings');
  const [showMenu, setShowMenu] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [editName, setEditName] = useState('');
  const [editDescription, setEditDescription] = useState('');

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
        description: portfolioData.description || `Portfolio ${portfolioData.portfolio_id}`,
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

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (showMenu && !event.target.closest('.menu-container')) {
        setShowMenu(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [showMenu]);

  const handleEditPortfolio = () => {
    setEditName(portfolio.name);
    setEditDescription(portfolio.description || '');
    setShowMenu(false);
    setShowEditModal(true);
  };

  const handleUpdatePortfolio = async () => {
    try {
      setLoading(true);
      const response = await fetch(`http://localhost:8080/api/v1/portfolio/${portfolio.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          name: editName,
          description: editDescription,
          user_id: 4,
        }),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      // Refresh portfolio data
      await fetchPortfolio(portfolio.id);
      setShowEditModal(false);
      // Notify parent component to refresh portfolio list
      if (onPortfolioUpdate) {
        onPortfolioUpdate();
      }
    } catch (err) {
      setError(err.message);
      console.error('Error updating portfolio:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleDeletePortfolio = () => {
    setShowMenu(false);
    setShowDeleteConfirm(true);
  };

  const confirmDeletePortfolio = async () => {
    try {
      setLoading(true);
      const response = await fetch(`http://localhost:8080/api/v1/portfolio/${portfolio.id}/status`, {
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

      // Notify parent component to refresh portfolio list
      if (onPortfolioUpdate) {
        onPortfolioUpdate();
      }

      // Navigate back to dashboard
      onBack();
    } catch (err) {
      setError(err.message);
      console.error('Error deleting portfolio:', err);
      setShowDeleteConfirm(false);
    } finally {
      setLoading(false);
    }
  };

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
        <div className="relative menu-container">
          <button
            onClick={() => setShowMenu(!showMenu)}
            className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <MoreVertical className="w-6 h-6 text-gray-600" />
          </button>
          {showMenu && (
            <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-200 z-10">
              <button
                onClick={handleEditPortfolio}
                className="flex items-center gap-2 w-full px-4 py-3 text-left hover:bg-gray-50 transition-colors rounded-t-lg"
              >
                <Edit className="w-4 h-4 text-gray-600" />
                <span className="text-gray-700">Edit Portfolio</span>
              </button>
              <button
                onClick={handleDeletePortfolio}
                className="flex items-center gap-2 w-full px-4 py-3 text-left hover:bg-red-50 transition-colors rounded-b-lg text-red-600"
              >
                <Trash2 className="w-4 h-4" />
                <span>Delete Portfolio</span>
              </button>
            </div>
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

      {/* Edit Portfolio Modal */}
      {showEditModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-xl max-w-md w-full p-6">
            <div className="flex items-center justify-between mb-6">
              <h3 className="text-xl font-bold text-gray-900">Edit Portfolio</h3>
              <button
                onClick={() => setShowEditModal(false)}
                className="text-gray-400 hover:text-gray-600 transition-colors"
              >
                <ArrowLeft className="w-6 h-6" />
              </button>
            </div>

            <form onSubmit={(e) => { e.preventDefault(); handleUpdatePortfolio(); }} className="space-y-4">
              <div>
                <label htmlFor="edit-name" className="block text-sm font-medium text-gray-700 mb-2">
                  Portfolio Name
                </label>
                <input
                  id="edit-name"
                  type="text"
                  value={editName}
                  onChange={(e) => setEditName(e.target.value)}
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>

              <div>
                <label htmlFor="edit-description" className="block text-sm font-medium text-gray-700 mb-2">
                  Description (Optional)
                </label>
                <textarea
                  id="edit-description"
                  value={editDescription}
                  onChange={(e) => setEditDescription(e.target.value)}
                  rows={3}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
                />
              </div>

              <div className="flex gap-3 pt-4">
                <button
                  type="button"
                  onClick={() => setShowEditModal(false)}
                  className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={loading}
                  className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {loading ? 'Updating...' : 'Update Portfolio'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete Confirmation Modal */}
      {showDeleteConfirm && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-xl max-w-md w-full p-6">
            <div className="text-center">
              <div className="mx-auto flex items-center justify-center h-12 w-12 rounded-full bg-red-100 mb-4">
                <Trash2 className="h-6 w-6 text-red-600" />
              </div>
              <h3 className="text-lg font-semibold text-gray-900 mb-2">Delete Portfolio</h3>
              <p className="text-gray-600 mb-6">
                Are you sure you want to delete "{portfolio.name}"? This action cannot be undone and will permanently remove all associated data.
              </p>
              <div className="flex gap-3">
                <button
                  onClick={() => setShowDeleteConfirm(false)}
                  className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
                >
                  Cancel
                </button>
                <button
                  onClick={confirmDeletePortfolio}
                  disabled={loading}
                  className="flex-1 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {loading ? 'Deleting...' : 'Delete Portfolio'}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
