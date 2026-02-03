import { useState } from 'react';
import { X, Search } from 'lucide-react';
import { stockService } from '../../services/stockService.js';
import { transactionService } from '../../services/transactionService.js';

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

  const handleSubmit = async (e) => {
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
        transaction_type: transactionType,
        shares: sharesNum,
        price_per_share: priceNum,
        total_amount: totalAmount,
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
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div className="bg-white rounded-xl max-w-2xl w-full p-6 max-h-[90vh] overflow-y-auto">
        <div className="flex items-center justify-between mb-6">
          <h3 className="text-xl font-bold text-gray-900">Add Transaction</h3>
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
                      </div>
                    </div>
                  </button>
                ))}
              </div>
            )}
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="p-4 bg-blue-50 border border-blue-200 rounded-lg">
              <div className="font-semibold text-gray-900">{selectedStock.symbol}</div>
              <div className="text-sm text-gray-600">{selectedStock.name}</div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Transaction Type
              </label>
              <div className="flex gap-4">
                <label className="flex-1">
                  <input
                    type="radio"
                    name="transactionType"
                    value="BUY"
                    checked={transactionType === 'BUY'}
                    onChange={() => setTransactionType('BUY')}
                    className="sr-only"
                  />
                  <div className={`p-4 border-2 rounded-lg text-center cursor-pointer transition-colors ${
                    transactionType === 'BUY'
                      ? 'border-green-500 bg-green-50 text-green-700'
                      : 'border-gray-200 hover:border-gray-300'
                  }`}>
                    <div className="font-semibold">BUY</div>
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
                  <div className={`p-4 border-2 rounded-lg text-center cursor-pointer transition-colors ${
                    transactionType === 'SELL'
                      ? 'border-red-500 bg-red-50 text-red-700'
                      : 'border-gray-200 hover:border-gray-300'
                  }`}>
                    <div className="font-semibold">SELL</div>
                  </div>
                </label>
              </div>
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

            <div>
              <label htmlFor="date" className="block text-sm font-medium text-gray-700 mb-2">
                Transaction Date
              </label>
              <input
                id="date"
                type="datetime-local"
                value={transactionDate}
                onChange={(e) => setTransactionDate(e.target.value)}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            {shares && price && (
              <div className={`p-4 border rounded-lg ${
                transactionType === 'BUY'
                  ? 'bg-red-50 border-red-200'
                  : 'bg-green-50 border-green-200'
              }`}>
                <div className="text-sm text-gray-600">Total Amount</div>
                <div className={`text-2xl font-bold ${
                  transactionType === 'BUY' ? 'text-red-700' : 'text-green-700'
                }`}>
                  {transactionType === 'BUY' ? '-' : '+'}
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
                {loading ? 'Adding...' : 'Add Transaction'}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}
