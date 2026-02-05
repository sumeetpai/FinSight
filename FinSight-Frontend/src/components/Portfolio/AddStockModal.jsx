import { useState } from 'react';
import { X, Search, Plus } from 'lucide-react';
import { stockApi } from '../../services/stockApi.js';
import { transactionApi } from '../../services/transactionApi.js';
import { apiCall } from '../../utils/toast.js';
import { CreateStockForm } from '../Stock/CreateStockForm.jsx';

export function AddStockModal({ portfolioId, onClose, onAdded }) {
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [selectedStock, setSelectedStock] = useState(null);
  const [shares, setShares] = useState('');
  const [price, setPrice] = useState('');
  const [loading, setLoading] = useState(false);
  const [selectingStock, setSelectingStock] = useState(false);
  const [showCreateStock, setShowCreateStock] = useState(false);

  const handleSearch = async () => {
    if (!searchQuery.trim()) {
      setSearchResults([]);
      return;
    }

    try {
      const trimmed = searchQuery.trim();

      // If the user entered a single token (likely a symbol), query the price service first
      if (!/\s/.test(trimmed)) {
        try {
          const resp = await fetch(`http://localhost:8000/stock/price/${encodeURIComponent(trimmed)}`);
          if (resp.ok) {
            const data = await resp.json();
            // Map response into the shape the UI expects (price API returns { symbol, price, currency, timestamp })
            const parsedPrice = Number(data.price);
            const stockFromPrice = {
              id: data.symbol,
              stock_id: data.symbol,
              symbol: data.symbol,
              stock_sym: data.symbol,
              name: data.symbol, // no friendly name in price API, use symbol as fallback
              current_price: Number.isFinite(parsedPrice) ? parsedPrice : 0,
              market_cap: 0,
              currency: data.currency ?? 'USD',
              timestamp: data.timestamp ?? null,
            };

            setSearchResults([stockFromPrice]);
            return;
          }
        } catch (err) {
          // If the price service fails, fall back to the local stocks search below
          console.warn('Price service unavailable, falling back to local search:', err);
        }
      }

      // Default behavior: fetch all stocks and filter locally
      const allStocks = await stockApi.getAllStocks();
      const results = allStocks.filter(stock =>
        (stock.stock_sym || stock.symbol || '').toLowerCase().includes(searchQuery.toLowerCase()) ||
        (stock.name || '').toLowerCase().includes(searchQuery.toLowerCase())
      );
      setSearchResults(results);
    } catch (error) {
      console.error('Error searching stocks:', error);
    }
  };

  const handleSelectStock = async (stock) => {
  setSelectingStock(true);
  try {
    const sym = stock.symbol || stock.stock_sym || stock.id;

    // 1. Fetch live price ONLY from price service
    const priceResp = await fetch(
      `http://localhost:8000/stock/price/${encodeURIComponent(sym)}`
    );

    if (!priceResp.ok) {
      throw new Error('Failed to fetch stock price');
    }

    const priceData = await priceResp.json(); // { symbol, price, currency, timestamp }

    // 2. Fetch stock info (optional enrichment)
    let info = null;
    try {
      const infoResp = await fetch(
        `http://localhost:8000/stock/info/${encodeURIComponent(sym)}`
      );
      if (infoResp.ok) {
        info = await infoResp.json();
      }
    } catch {
      // non-fatal
    }

    // 3. Create or fetch stock in 8080 (NO PRICE)
    const payload = {
      stock_sym: info?.symbol ?? sym,
      name: info?.name ?? sym,
    };

    const resp = await fetch('http://localhost:8080/api/v1/stocks', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });

    let persistedStock = payload;
    if (resp.ok) {
      persistedStock = await resp.json();
    }

    // 4. Combine backend stock + live price (frontend-only)
    setSelectedStock({
      ...persistedStock,
      live_price: Number(priceData.price),
      currency: priceData.currency,
      price_timestamp: priceData.timestamp,
    });

    setPrice(Number(priceData.price).toString());
  } catch (err) {
    console.error('Error selecting stock:', err);
  } finally {
    setSelectingStock(false);
  }
};

  const handleAddStock = async (e) => {
    e.preventDefault();
    if (!selectedStock) return;

    setLoading(true);
    try {
      const sharesNum = parseFloat(shares);
      const priceNum = parseFloat(price);
      // Ensure we have a stock_id to send to the portfolio API
      const stockId = selectedStock.stock_id ?? selectedStock.id ?? selectedStock.stock_id;
      if (stockId == null) {
        throw new Error('Missing stock id for selected stock');
      }

      const userId =  4;

      // Add or update the portfolio's stock entry (shows toasts via apiCall)
      await apiCall(async () => {
        const resp = await fetch(`http://localhost:8080/api/v1/portfolio/${portfolioId}/stocks`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            stock_id: stockId,
            user_id: userId,
            qty: sharesNum,
          }),
        });

        if (!resp.ok) {
          const text = await resp.text();
          throw new Error(`Failed to add stock to portfolio: ${resp.status} ${text}`);
        }

        return await resp.json();
      }, {
        successMessage: 'Stock added to portfolio!',
        errorMessage: 'Failed to add stock to portfolio',
        showLoadingToast: true,
        loadingMessage: 'Adding stock to portfolio...'
      });

      // Fetch transactions for the portfolio so the newly-created transaction is retrieved via the GET endpoint
      let transactions = [];
      try {
        transactions = await transactionApi.getTransactionsByPortfolio(portfolioId);
      } catch (txErr) {
        console.warn('Failed to fetch transactions after adding stock:', txErr);
      }

      // Notify parent that a stock was added and provide fetched transactions (if any)
      onAdded(transactions);
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
    <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
      <div className="bg-white/95 backdrop-blur-sm rounded-2xl w-full max-w-lg mx-4 p-6 max-h-[90vh] overflow-y-auto shadow-2xl border border-white/20">
        <div className="flex items-center justify-between mb-6">
          <h3 className="text-xl font-bold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">Add Stock to Portfolio</h3>
          <button
            onClick={onClose}
            className="p-2 bg-gray-100/80 rounded-xl hover:bg-gray-200/80 transition-all duration-200"
          >
            <X className="w-6 h-6 text-gray-600" />
          </button>
        </div>

        {!selectedStock ? (
          <div className="space-y-4">
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
                    onClick={() => !selectingStock && handleSelectStock(stock)}
                    disabled={selectingStock}
                    className="w-full text-left p-6 bg-white/80 backdrop-blur-sm rounded-2xl border border-white/20 shadow-xl hover:shadow-2xl hover:transform hover:-translate-y-1 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    <div className="flex items-center justify-between">
                      <div>
                        <div className="text-xl font-bold text-gray-900">{stock.symbol}</div>
                        <div className="text-gray-600 mt-1 font-bold">{stock.name}</div>
                      </div>
                      <div className="text-right">
                        <div className="text-2xl font-bold text-gray-900">
                          ${Number(stock.current_price ?? 0).toFixed(2)}
                        </div>
                      </div>
                    </div>
                  </button>
                ))}
              </div>
            )}
          </div>
        ) : (
          <form onSubmit={handleAddStock} className="space-y-6">
            <div className="p-6 bg-blue-50/80 backdrop-blur-sm border border-blue-200/50 rounded-2xl">
              <div className="text-xl font-bold text-gray-900">{selectedStock.symbol}</div>
              <div className="text-gray-600 mt-1">{selectedStock.name}</div>
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
                dis
                min="0.01"
                className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 bg-gray-50 focus:bg-white"
                placeholder="0.00"
              />
            </div>

            {shares && price && (
              <div className="p-6 bg-gray-50/80 backdrop-blur-sm border border-gray-200/50 rounded-2xl">
                <div className="text-sm font-semibold text-gray-600 uppercase tracking-wide">Total Cost</div>
                <div className="text-3xl font-bold text-gray-900 mt-2">
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
                {loading ? 'Adding...' : 'Add to Portfolio'}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}
