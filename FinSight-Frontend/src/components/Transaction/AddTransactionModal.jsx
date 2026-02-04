import { useState } from 'react';
import { X, Search } from 'lucide-react';
import { stockApi } from '../../services/stockApi.js';
import { transactionApi } from '../../services/transactionApi.js';

export function AddTransactionModal({ portfolioId, onClose, onAdded }) {
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [selectedStock, setSelectedStock] = useState(null);
  const [transactionType, setTransactionType] = useState('BUY');
  const [shares, setShares] = useState('');
  const [price, setPrice] = useState('');
  const [transactionDate, setTransactionDate] = useState(
    new Date().toISOString().slice(0, 16)
  );
  const [loading, setLoading] = useState(false);

  const handleSearch = async () => {
    if (!searchQuery.trim()) {
      setSearchResults([]);
      return;
    }

    try {
      const allStocks = await stockApi.getAllStocks();
      const results = allStocks.filter(stock =>
        stock.stock_sym.toLowerCase().includes(searchQuery.toLowerCase()) ||
        stock.name.toLowerCase().includes(searchQuery.toLowerCase())
      );
      setSearchResults(results);
    } catch (error) {
      console.error('Error searching stocks:', error);
    }
  };

  const handleSelectStock = (stock) => {
    setSelectedStock(stock);
    setPrice(stock.current_price.toString());
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedStock) return;

    setLoading(true);
    try {
      const sharesNum = parseFloat(shares);
      const priceNum = parseFloat(price);

      await transactionApi.createTransaction({
        portfolio_id: portfolioId,
        stock_id: selectedStock.stock_id,
        transaction_type: transactionType,
        shares: sharesNum,
        price_per_share: priceNum,
        transaction_date: new Date(transactionDate).toISOString(),
      });

      onAdded();
    } catch (error) {
      console.error('Error adding transaction:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
      <div className="bg-white/95 backdrop-blur-sm rounded-2xl w-full max-w-lg mx-4 p-6 max-h-[90vh] overflow-y-auto shadow-2xl border border-white/20">
        <div className="flex items-center justify-between mb-6">
          <h3 className="text-xl font-bold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">Add Transaction</h3>
          <button
            onClick={onClose}
            className="p-2 bg-gray-100/80 rounded-xl hover:bg-gray-200/80 transition-all duration-200"
          >
            <X className="w-6 h-6 text-gray-600" />
          </button>
        </div>

        {!selectedStock ? (
          <div className="space-y-6">
            <div className="flex gap-4">
              <div className="flex-1 relative">
                <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
                  placeholder="Search by symbol or name..."
                  className="w-full pl-12 pr-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 bg-gray-50 focus:bg-white"
                />
              </div>
              <button
                onClick={handleSearch}
                className="px-6 py-3 bg-gradient-to-r from-blue-600 to-indigo-600 text-white rounded-xl hover:from-blue-700 hover:to-indigo-700 transition-all duration-200 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 font-semibold"
              >
                Search
              </button>
            </div>

            {searchResults.length > 0 && (
              <div className="space-y-4">
                <div className="text-lg font-semibold text-gray-700">Search Results:</div>
                {searchResults.map((stock) => (
                  <button
                    key={stock.id}
                    onClick={() => handleSelectStock(stock)}
                    className="w-full text-left p-6 bg-white/80 backdrop-blur-sm rounded-2xl border border-white/20 shadow-xl hover:shadow-2xl hover:transform hover:-translate-y-1 transition-all duration-300"
                  >
                    <div className="flex items-center justify-between">
                      <div>
                        <div className="text-xl font-bold text-gray-900">{stock.symbol}</div>
                        <div className="text-gray-600 mt-1">{stock.name}</div>
                      </div>
                      <div className="text-right">
                        <div className="text-2xl font-bold text-gray-900">
                          ${stock.current_price.toFixed(2)}
                        </div>
                      </div>
                    </div>
                  </button>
                ))}
              </div>
            )}
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="p-6 bg-blue-50/80 backdrop-blur-sm border border-blue-200/50 rounded-2xl">
              <div className="text-xl font-bold text-gray-900">{selectedStock.symbol}</div>
              <div className="text-gray-600 mt-1">{selectedStock.name}</div>
            </div>

            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-4">
                Transaction Type
              </label>
              <div className="flex gap-6">
                <label className="flex-1">
                  <input
                    type="radio"
                    name="transactionType"
                    value="BUY"
                    checked={transactionType === 'BUY'}
                    onChange={() => setTransactionType('BUY')}
                    className="sr-only"
                  />
                  <div className={`p-6 border-2 rounded-2xl text-center cursor-pointer transition-all duration-200 ${
                    transactionType === 'BUY'
                      ? 'border-green-500 bg-green-50/80 text-green-700 shadow-lg'
                      : 'border-gray-200 hover:border-gray-300 hover:bg-gray-50/50'
                  }`}>
                    <div className="text-lg font-bold">BUY</div>
                  </div>
                </label>
                <label className="flex-1">
                  <input
                    type="radio"
                    name="transactionType"
                    value="SELL"
                    checked={transactionType === 'SELL'}
                    onChange={() => setTransactionType('SELL')}
                    className="sr-only"
                  />
                  <div className={`p-6 border-2 rounded-2xl text-center cursor-pointer transition-all duration-200 ${
                    transactionType === 'SELL'
                      ? 'border-red-500 bg-red-50/80 text-red-700 shadow-lg'
                      : 'border-gray-200 hover:border-gray-300 hover:bg-gray-50/50'
                  }`}>
                    <div className="text-lg font-bold">SELL</div>
                  </div>
                </label>
              </div>
            </div>

            <div>
              <label htmlFor="shares" className="block text-sm font-semibold text-gray-700 mb-3">
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
                className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 bg-gray-50 focus:bg-white"
                placeholder="0.00"
              />
            </div>

            <div>
              <label htmlFor="price" className="block text-sm font-semibold text-gray-700 mb-3">
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
                className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 bg-gray-50 focus:bg-white"
                placeholder="0.00"
              />
            </div>

            <div>
              <label htmlFor="date" className="block text-sm font-semibold text-gray-700 mb-3">
                Transaction Date
              </label>
              <input
                id="date"
                type="datetime-local"
                value={transactionDate}
                onChange={(e) => setTransactionDate(e.target.value)}
                required
                className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 bg-gray-50 focus:bg-white"
              />
            </div>

            {shares && price && (
              <div className={`p-6 border rounded-2xl ${
                transactionType === 'BUY'
                  ? 'bg-red-50/80 border-red-200/50'
                  : 'bg-green-50/80 border-green-200/50'
              }`}>
                <div className="text-sm font-semibold text-gray-600 uppercase tracking-wide">Total Amount</div>
                <div className={`text-3xl font-bold mt-2 ${
                  transactionType === 'BUY' ? 'text-red-700' : 'text-green-700'
                }`}>
                  {transactionType === 'BUY' ? '-' : '+'}
                  ${(parseFloat(shares) * parseFloat(price)).toFixed(2)}
                </div>
              </div>
            )}

            <div className="flex gap-4 pt-6">
              <button
                type="button"
                onClick={() => setSelectedStock(null)}
                className="flex-1 px-6 py-3 border border-gray-300 text-gray-700 rounded-xl hover:bg-gray-50 transition-all duration-200 font-medium"
              >
                Back
              </button>
              <button
                type="submit"
                disabled={loading}
                className="flex-1 px-6 py-3 bg-gradient-to-r from-blue-600 to-indigo-600 text-white rounded-xl hover:from-blue-700 hover:to-indigo-700 transition-all duration-200 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none font-semibold"
              >
                {loading ? 'Adding...' : 'Add Transaction'}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}
