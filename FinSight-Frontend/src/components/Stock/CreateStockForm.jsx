import { useState } from 'react';
import { X } from 'lucide-react';
import { stockService } from '../../services/stockService.js';

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
      await stockService.create({
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
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div className="bg-white rounded-xl max-w-md w-full p-6">
        <div className="flex items-center justify-between mb-6">
          <h3 className="text-xl font-bold text-gray-900">Create New Stock</h3>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <X className="w-6 h-6" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm">
              {error}
            </div>
          )}

          <div>
            <label htmlFor="symbol" className="block text-sm font-medium text-gray-700 mb-2">
              Stock Symbol
            </label>
            <input
              id="symbol"
              type="text"
              value={symbol}
              onChange={(e) => setSymbol(e.target.value.toUpperCase())}
              required
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="e.g., AAPL"
            />
          </div>

          <div>
            <label htmlFor="name" className="block text-sm font-medium text-gray-700 mb-2">
              Company Name
            </label>
            <input
              id="name"
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="e.g., Apple Inc."
            />
          </div>

          <div>
            <label htmlFor="currentPrice" className="block text-sm font-medium text-gray-700 mb-2">
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
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="0.00"
            />
          </div>

          <div>
            <label htmlFor="previousClose" className="block text-sm font-medium text-gray-700 mb-2">
              Previous Close (Optional)
            </label>
            <input
              id="previousClose"
              type="number"
              step="0.01"
              value={previousClose}
              onChange={(e) => setPreviousClose(e.target.value)}
              min="0"
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="0.00"
            />
          </div>

          <div>
            <label htmlFor="marketCap" className="block text-sm font-medium text-gray-700 mb-2">
              Market Cap (Optional)
            </label>
            <input
              id="marketCap"
              type="number"
              step="0.01"
              value={marketCap}
              onChange={(e) => setMarketCap(e.target.value)}
              min="0"
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="0.00"
            />
          </div>

          <div className="flex gap-3 pt-4">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? 'Creating...' : 'Create Stock'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
