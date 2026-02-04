import { useState } from 'react';
import { X, Search, Plus } from 'lucide-react';
import { stockService } from '../../services/stockService.js';
import { transactionService } from '../../services/transactionService.js';
import { CreateStockForm } from '../Stock/CreateStockForm.jsx';

export function AddStockModal({ portfolioId, onClose, onAdded }) {
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [selectedStock, setSelectedStock] = useState(null);
  const [shares, setShares] = useState('');
  const [price, setPrice] = useState('');
  const [loading, setLoading] = useState(false);
  const [showCreateStock, setShowCreateStock] = useState(false);

  const handleSearch = async () => {
    if (!searchQuery.trim()) {
      setSearchResults([]);
      return;
    }

    try {
      const results = await stockService.search(searchQuery);
      setSearchResults(results);
    } catch (error) {
      console.error('Error searching stocks:', error);
    }
  };

  const handleSelectStock = (stock) => {
    setSelectedStock(stock);
    setPrice(stock.current_price.toString());
  };

  const handleAddStock = async (e) => {
    e.preventDefault();
    if (!selectedStock) return;

    setLoading(true);
    try {
      const sharesNum = parseFloat(shares);
      const priceNum = parseFloat(price);
      const totalAmount = sharesNum * priceNum;

      await transactionService.create({
        portfolio_id: portfolioId,
        stock_id: selectedStock.id,
        transaction_type: 'BUY',
        shares: sharesNum,
        price_per_share: priceNum,
        total_amount: totalAmount,
      });

      onAdded();
    } catch (error) {
      console.error('Error adding stock:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleStockCreated = async () => {
    setShowCreateStock(false);
    if (searchQuery) {
      await handleSearch();
    }
  };

  if (showCreateStock) {
    return (
      <CreateStockForm
        onClose={() => setShowCreateStock(false)}
        onCreated={handleStockCreated}
      />
    );
  }

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div className="bg-white rounded-xl max-w-2xl w-full p-6 max-h-[90vh] overflow-y-auto">
        <div className="flex items-center justify-between mb-6">
          <h3 className="text-xl font-bold text-gray-900">Add Stock to Portfolio</h3>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <X className="w-6 h-6" />
          </button>
        </div>

        {!selectedStock ? (
          <div className="space-y-4">
            <div className="flex gap-2">
              <div className="flex-1 relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
                  placeholder="Search by symbol or name..."
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>
              <button
                onClick={handleSearch}
                className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
              >
                Search
              </button>
            </div>

            <button
              onClick={() => setShowCreateStock(true)}
              className="flex items-center gap-2 px-4 py-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors w-full justify-center border border-blue-200"
            >
              <Plus className="w-5 h-5" />
              Create New Stock
            </button>

            {searchResults.length > 0 && (
              <div className="space-y-2">
                <div className="text-sm font-medium text-gray-700">Search Results:</div>
                {searchResults.map((stock) => (
                  <button
                    key={stock.id}
                    onClick={() => handleSelectStock(stock)}
                    className="w-full text-left p-4 border border-gray-200 rounded-lg hover:border-blue-500 hover:bg-blue-50 transition-colors"
                  >
                    <div className="flex items-center justify-between">
                      <div>
                        <div className="font-semibold text-gray-900">{stock.symbol}</div>
                        <div className="text-sm text-gray-600">{stock.name}</div>
                      </div>
                      <div className="text-right">
                        <div className="font-semibold text-gray-900">
                          ${stock.current_price.toFixed(2)}
                        </div>
                        <div className="text-sm text-gray-600">
                          ${(stock.market_cap / 1000000000).toFixed(2)}B
                        </div>
                      </div>
                    </div>
                  </button>
                ))}
              </div>
            )}
          </div>
        ) : (
          <form onSubmit={handleAddStock} className="space-y-4">
            <div className="p-4 bg-blue-50 border border-blue-200 rounded-lg">
              <div className="font-semibold text-gray-900">{selectedStock.symbol}</div>
              <div className="text-sm text-gray-600">{selectedStock.name}</div>
            </div>

            <div>
              <label htmlFor="shares" className="block text-sm font-medium text-gray-700 mb-2">
                Number of Shares
              </label>
              <input
                id="shares"
                type="number"
                step="0.0001"
                value={shares}
                onChange={(e) => setShares(e.target.value)}
                required
                min="0.0001"
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="0.00"
              />
            </div>

            <div>
              <label htmlFor="price" className="block text-sm font-medium text-gray-700 mb-2">
                Price per Share
              </label>
              <input
                id="price"
                type="number"
                step="0.01"
                value={price}
                onChange={(e) => setPrice(e.target.value)}
                required
                min="0.01"
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="0.00"
              />
            </div>

            {shares && price && (
              <div className="p-4 bg-gray-50 border border-gray-200 rounded-lg">
                <div className="text-sm text-gray-600">Total Cost</div>
                <div className="text-2xl font-bold text-gray-900">
                  ${(parseFloat(shares) * parseFloat(price)).toFixed(2)}
                </div>
              </div>
            )}

            <div className="flex gap-3 pt-4">
              <button
                type="button"
                onClick={() => setSelectedStock(null)}
                className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
              >
                Back
              </button>
              <button
                type="submit"
                disabled={loading}
                className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {loading ? 'Adding...' : 'Add to Portfolio'}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}
