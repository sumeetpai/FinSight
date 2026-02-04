import { useState } from 'react';
import { X } from 'lucide-react';
import { stockApi } from '../../services/stockApi.js';

export function CreateStockForm({ onClose, onCreated }) {
  const [symbol, setSymbol] = useState('');
  const [name, setName] = useState('');
  const [currentPrice, setCurrentPrice] = useState('');
  const [previousClose, setPreviousClose] = useState('');
  const [marketCap, setMarketCap] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await stockApi.createStock({
        symbol: symbol.toUpperCase(),
        name,
        current_price: parseFloat(currentPrice),
        previous_close: parseFloat(previousClose) || 0,
        market_cap: parseFloat(marketCap) || 0,
      });

      onCreated();
    } catch (err) {
      setError(err.message || 'Failed to create stock');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
      <div className="bg-white/95 backdrop-blur-sm rounded-2xl w-full max-w-sm mx-4 p-6 shadow-2xl border border-white/20 max-h-[90vh] overflow-y-auto">
        <div className="flex items-center justify-between mb-6">
          <h3 className="text-xl font-bold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">Create New Stock</h3>
          <button
            onClick={onClose}
            className="p-2 bg-gray-100/80 rounded-xl hover:bg-gray-200/80 transition-all duration-200"
          >
            <X className="w-5 h-5 text-gray-600" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          {error && (
            <div className="bg-red-50/80 backdrop-blur-sm border border-red-200/50 text-red-700 px-4 py-3 rounded-xl text-sm font-medium">
              {error}
            </div>
          )}

          <div>
            <label htmlFor="symbol" className="block text-sm font-semibold text-gray-700 mb-2">
              Stock Symbol
            </label>
            <input
              id="symbol"
              type="text"
              value={symbol}
              onChange={(e) => setSymbol(e.target.value.toUpperCase())}
              required
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 bg-gray-50 focus:bg-white"
              placeholder="e.g., AAPL"
            />
          </div>

          <div>
            <label htmlFor="name" className="block text-sm font-semibold text-gray-700 mb-2">
              Company Name
            </label>
            <input
              id="name"
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 bg-gray-50 focus:bg-white"
              placeholder="e.g., Apple Inc."
            />
          </div>

          <div>
            <label htmlFor="currentPrice" className="block text-sm font-semibold text-gray-700 mb-2">
              Current Price
            </label>
            <input
              id="currentPrice"
              type="number"
              step="0.01"
              value={currentPrice}
              onChange={(e) => setCurrentPrice(e.target.value)}
              required
              min="0.01"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 bg-gray-50 focus:bg-white"
              placeholder="0.00"
            />
          </div>

          <div>
            <label htmlFor="previousClose" className="block text-sm font-semibold text-gray-700 mb-2">
              Previous Close (Optional)
            </label>
            <input
              id="previousClose"
              type="number"
              step="0.01"
              value={previousClose}
              onChange={(e) => setPreviousClose(e.target.value)}
              min="0"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 bg-gray-50 focus:bg-white"
              placeholder="0.00"
            />
          </div>

          <div>
            <label htmlFor="marketCap" className="block text-sm font-semibold text-gray-700 mb-2">
              Market Cap (Optional)
            </label>
            <input
              id="marketCap"
              type="number"
              step="0.01"
              value={marketCap}
              onChange={(e) => setMarketCap(e.target.value)}
              min="0"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 bg-gray-50 focus:bg-white"
              placeholder="0.00"
            />
          </div>

          <div className="flex gap-3 pt-4">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-all duration-200 font-medium"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="flex-1 px-4 py-2 bg-gradient-to-r from-blue-600 to-indigo-600 text-white rounded-lg hover:from-blue-700 hover:to-indigo-700 transition-all duration-200 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none font-medium"
            >
              {loading ? 'Creating...' : 'Create Stock'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
