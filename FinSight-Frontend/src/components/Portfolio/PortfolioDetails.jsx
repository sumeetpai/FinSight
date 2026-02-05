import { useState, useEffect, act } from 'react';
import { ArrowLeft, Plus, TrendingUp, TrendingDown, MoreVertical, Edit, Trash2 } from 'lucide-react';
import { HoldingsList } from './HoldingsList.jsx';
import { AddStockModal } from './AddStockModal.jsx';
import { TransactionList } from '../Transaction/TransactionList.jsx';
import { portfolioApi } from '../../services/portfolioApi.js';

export function PortfolioDetails({ portfolio: initialPortfolio, onBack, onPortfolioUpdate }) {
  const [portfolio, setPortfolio] = useState(initialPortfolio);
  const [loading, setLoading] = useState(false);
  const [showAddStock, setShowAddStock] = useState(false);
  const [activeTab, setActiveTab] = useState('holdings');
  const [showMenu, setShowMenu] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [editName, setEditName] = useState('');
  const [editDescription, setEditDescription] = useState('');
  const [transactionsRefreshKey, setTransactionsRefreshKey] = useState(0);
  const [priceOverrides, setPriceOverrides] = useState({});

  const applyPriceOverrides = (data, overrides) => {
    if (!data?.holdings?.length) return data;
    const holdings = data.holdings.map((holding) => {
      const override = overrides?.[holding.stock_id];
      if (!override) return holding;
      return {
        ...holding,
        stock: {
          ...holding.stock,
          live_price: override.live_price,
          price_timestamp: override.price_timestamp,
          currency: override.currency,
        },
      };
    });
    return { ...data, holdings };
  };

  const fetchPortfolio = async (portfolioId, overrides) => {
    setLoading(true);
    const data = await portfolioApi.getPortfolio(portfolioId);
    if (data) {
      setPortfolio(applyPriceOverrides(data, overrides ?? priceOverrides));
    }
    setLoading(false);
  };

  const refreshPortfolio = async (overrides) => {
    await fetchPortfolio(portfolio.id, overrides);
    // push updated portfolio up to parent so Dashboard can pass it to visualization
    if (onPortfolioUpdate) {
      onPortfolioUpdate(portfolio);
    }
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
    const success = await portfolioApi.updatePortfolio(portfolio.id, editName, editDescription);
    if (success) {
      // Refresh portfolio data
      await fetchPortfolio(portfolio.id);
      setShowEditModal(false);
      // Notify parent component to refresh portfolio list
      if (onPortfolioUpdate) {
        onPortfolioUpdate();
      }
    }
  };

  const handleDeletePortfolio = () => {
    setShowMenu(false);
    setShowDeleteConfirm(true);
  };

  const confirmDeletePortfolio = async () => {
    setLoading(true);
    try {
      const success = await portfolioApi.deletePortfolio(portfolio.id);
      if (success) {
        // Navigate back to dashboard (which will trigger refresh)
        onBack();
      }
    } finally {
      setLoading(false);
      setShowDeleteConfirm(false);
    }
  };

  const calculatePortfolioValue = () => {
    if (!portfolio.holdings) return 0;
    return portfolio.holdings.reduce((sum, holding) => {
      const currentPrice = (holding.stock?.live_price ?? holding.stock?.current_price) || 0;
      return sum + (holding.shares * currentPrice);
    }, 0);
    
  };

  const calculatePortfolioCost = () => {
    // Use the cost_basis from API as cost basis

    return portfolio.cost_basis || 0;
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
  const safeGainPercent = Number(gainPercent ?? 0) || 0;

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-4">
        <button
          onClick={onBack}
          className="p-2 bg-white/80 backdrop-blur-sm rounded-xl hover:bg-white/90 transition-all duration-200 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5"
        >
          <ArrowLeft className="w-5 h-5 text-gray-600" />
        </button>
        <div className="flex-1">
          <h2 className="text-2xl font-bold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">{portfolio.name}</h2>
          {portfolio.description && (
            <p className="text-gray-600 mt-1">{portfolio.description}</p>
          )}
        </div>
          <div className="relative menu-container">
            <button
              onClick={() => setShowMenu(!showMenu)}
              className="p-2 bg-white/80 backdrop-blur-sm rounded-xl hover:bg-white/90 transition-all duration-200 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5"
            >
              <MoreVertical className="w-5 h-5 text-gray-600" />
            </button>
            {showMenu && (
              <div className="absolute right-0 mt-2 w-48 bg-white/95 backdrop-blur-sm rounded-xl shadow-2xl border border-white/20 z-10">
                <button
                  onClick={handleEditPortfolio}
                  className="flex items-center gap-3 w-full px-4 py-3 text-left hover:bg-gray-50/80 transition-all duration-200 rounded-t-xl"
                >
                  <Edit className="w-4 h-4 text-gray-600" />
                  <span className="text-gray-700 font-medium">Edit Portfolio</span>
                </button>
                <button
                  onClick={handleDeletePortfolio}
                  className="flex items-center gap-3 w-full px-4 py-3 text-left hover:bg-red-50/80 transition-all duration-200 rounded-b-xl text-red-600"
                >
                  <Trash2 className="w-4 h-4" />
                  <span className="font-medium">Delete Portfolio</span>
                </button>
              </div>
            )}
          </div>
          <button
            onClick={() => setShowAddStock(true)}
            className="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-blue-600 to-indigo-600 text-white rounded-xl hover:from-blue-700 hover:to-indigo-700 transition-all duration-200 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 font-medium"
          >
            <Plus className="w-4 h-4" />
            Add Stock
          </button>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="bg-white/80 backdrop-blur-sm rounded-xl border border-white/20 p-4 shadow-lg hover:shadow-xl transition-all duration-300">
            <div className="text-xs font-semibold text-gray-600 mb-1 uppercase tracking-wide">Portfolio Value</div>
            <div className="text-2xl font-bold text-gray-900">
              ${value.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
            </div>
          </div>

          <div className="bg-white/80 backdrop-blur-sm rounded-xl border border-white/20 p-4 shadow-lg hover:shadow-xl transition-all duration-300">
            <div className="text-xs font-semibold text-gray-600 mb-1 uppercase tracking-wide">Total Cost</div>
            <div className="text-2xl font-bold text-gray-900">
              ${cost.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
            </div>
          </div>

          <div className={`bg-white/80 backdrop-blur-sm rounded-xl border border-white/20 p-4 shadow-lg hover:shadow-xl transition-all duration-300 ${isPositive ? 'border-green-200/50' : 'border-red-200/50'}`}>
            <div className="flex items-center gap-2 mb-1">
              {isPositive ? (
                <TrendingUp className="w-4 h-4 text-green-600" />
              ) : (
                <TrendingDown className="w-4 h-4 text-red-600" />
              )}
              <div className={`text-xs font-semibold uppercase tracking-wide ${isPositive ? 'text-green-700' : 'text-red-700'}`}>
                Total Gain/Loss
              </div>
            </div>
            <div className={`text-2xl font-bold ${isPositive ? 'text-green-700' : 'text-red-700'}`}>
              {isPositive ? '+' : ''}${gain.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
            </div>
            <div className={`text-sm font-semibold mt-1 ${isPositive ? 'text-green-600' : 'text-red-600'}`}>
              {isPositive ? '+' : ''}{safeGainPercent.toFixed(2)}%
            </div>
          </div>
        </div>

        <div className="bg-white/80 backdrop-blur-sm rounded-xl border border-white/20 shadow-lg overflow-hidden">
          <div className="border-b border-white/30">
            <div className="flex">
              <button
                onClick={() => setActiveTab('holdings')}
                className={`flex-1 px-6 py-3 text-sm font-semibold border-b-2 transition-all duration-200 ${
                  activeTab === 'holdings'
                    ? 'border-blue-600 text-blue-600 bg-blue-50/50'
                    : 'border-transparent text-gray-600 hover:text-gray-900 hover:bg-gray-50/50'
                }`}
              >
                Holdings ({portfolio.holdings?.length || 0})
              </button>
              <button
                onClick={() => setActiveTab('transactions')}
                className={`flex-1 px-6 py-3 text-sm font-semibold border-b-2 transition-all duration-200 ${
                  activeTab === 'transactions'
                    ? 'border-blue-600 text-blue-600 bg-blue-50/50'
                    : 'border-transparent text-gray-600 hover:text-gray-900 hover:bg-gray-50/50'
                }`}
              >
                Transaction History
              </button>
            </div>
          </div>

          <div className="p-4">
            {activeTab === 'holdings' ? (
              <HoldingsList
                portfolio={portfolio}
                onUpdate={refreshPortfolio}
              />
            ) : (
              <TransactionList
                portfolioId={portfolio.id}
                onUpdate={refreshPortfolio}
                refreshKey={transactionsRefreshKey}
              />
            )}
          </div>
        </div>

      {showAddStock && (
        <AddStockModal
          portfolioId={portfolio.id}
          onClose={() => setShowAddStock(false)}
          onAdded={(payload) => {
            setShowAddStock(false);
            const selectedStock = payload?.selectedStock;
            let nextOverrides = priceOverrides;
            if (selectedStock?.stock_id || selectedStock?.id) {
              const stockId = selectedStock.stock_id ?? selectedStock.id;
              nextOverrides = {
                ...priceOverrides,
                [stockId]: {
                  live_price: selectedStock.live_price ?? selectedStock.current_price,
                  price_timestamp: selectedStock.price_timestamp,
                  currency: selectedStock.currency,
                },
              };
              setPriceOverrides(nextOverrides);
            }
            refreshPortfolio(nextOverrides);
            // Trigger transaction list refresh; increment key so TransactionList useEffect sees change
            setTransactionsRefreshKey(k => k + 1);
          }}
        />
      )}

      {/* Edit Portfolio Modal */}
      {showEditModal && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="bg-white/95 backdrop-blur-sm rounded-2xl max-w-md w-full p-8 shadow-2xl border border-white/20">
            <div className="flex items-center justify-between mb-8">
              <h3 className="text-2xl font-bold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">Edit Portfolio</h3>
              <button
                onClick={() => setShowEditModal(false)}
                className="p-2 bg-gray-100/80 rounded-xl hover:bg-gray-200/80 transition-all duration-200"
              >
                <ArrowLeft className="w-6 h-6 text-gray-600" />
              </button>
            </div>

            <form onSubmit={(e) => { e.preventDefault(); handleUpdatePortfolio(); }} className="space-y-6">
              <div>
                <label htmlFor="edit-name" className="block text-sm font-semibold text-gray-700 mb-3">
                  Portfolio Name
                </label>
                <input
                  id="edit-name"
                  type="text"
                  value={editName}
                  onChange={(e) => setEditName(e.target.value)}
                  required
                  className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 bg-gray-50 focus:bg-white"
                />
              </div>

              <div>
                <label htmlFor="edit-description" className="block text-sm font-semibold text-gray-700 mb-3">
                  Description (Optional)
                </label>
                <textarea
                  id="edit-description"
                  value={editDescription}
                  onChange={(e) => setEditDescription(e.target.value)}
                  rows={3}
                  className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 bg-gray-50 focus:bg-white resize-none"
                  placeholder="Describe your investment strategy..."
                />
              </div>

              <div className="flex gap-4 pt-6">
                <button
                  type="button"
                  onClick={() => setShowEditModal(false)}
                  className="flex-1 px-6 py-3 border border-gray-300 text-gray-700 rounded-xl hover:bg-gray-50 transition-all duration-200 font-medium"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={loading}
                  className="flex-1 px-6 py-3 bg-gradient-to-r from-blue-600 to-indigo-600 text-white rounded-xl hover:from-blue-700 hover:to-indigo-700 transition-all duration-200 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none font-semibold"
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
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="bg-white/95 backdrop-blur-sm rounded-2xl max-w-md w-full p-8 shadow-2xl border border-white/20">
            <div className="text-center">
              <div className="mx-auto flex items-center justify-center h-16 w-16 rounded-2xl bg-red-100/80 mb-6">
                <Trash2 className="h-8 w-8 text-red-600" />
              </div>
              <h3 className="text-2xl font-bold text-gray-900 mb-4">Delete Portfolio</h3>
              <p className="text-gray-600 mb-8 text-lg leading-relaxed">
                Are you sure you want to delete "{portfolio.name}"? This action cannot be undone and will permanently remove all associated data.
              </p>
              <div className="flex gap-4">
                <button
                  onClick={() => setShowDeleteConfirm(false)}
                  className="flex-1 px-6 py-3 border border-gray-300 text-gray-700 rounded-xl hover:bg-gray-50 transition-all duration-200 font-medium"
                >
                  Cancel
                </button>
                <button
                  onClick={confirmDeletePortfolio}
                  disabled={loading}
                  className="flex-1 px-6 py-3 bg-gradient-to-r from-red-600 to-red-700 text-white rounded-xl hover:from-red-700 hover:to-red-800 transition-all duration-200 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none font-semibold"
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
