import { useState, useEffect, useCallback } from 'react';
import { Plus, ArrowUpCircle, ArrowDownCircle, Calendar } from 'lucide-react';
import { transactionApi } from '../../services/transactionApi.js';
import { stockApi } from '../../services/stockApi.js';
import { AddTransactionModal } from './AddTransactionModal.jsx';

export function TransactionList({ portfolioId, onUpdate, refreshKey }) {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showAddModal, setShowAddModal] = useState(false);
  // map of stockId -> stock object returned by stockApi.getStockById
  const [stockMap, setStockMap] = useState({});

  const loadTransactions = useCallback(async () => {
    try {
      setLoading(true);
      const data = await transactionApi.getTransactionsByPortfolio(portfolioId);
      setTransactions(data || []);

      // Collect unique stock IDs from transactions and fetch stock details
      const ids = Array.from(new Set((data || []).map(t => t.stock_id ?? t.stock?.id ?? t.stock?.stock_id).filter(id => id !== undefined && id !== null)));
      if (ids.length > 0) {
        // Only fetch stocks we don't already have cached
        const idsToFetch = ids.filter(id => !stockMap[id]);
        if (idsToFetch.length > 0) {
          const results = await Promise.all(idsToFetch.map(id => stockApi.getStockById(id).catch(err => { console.warn('Failed to load stock', id, err); return null; })));
          const newMap = { ...stockMap };
          idsToFetch.forEach((id, idx) => {
            if (results[idx]) newMap[id] = results[idx];
          });
          setStockMap(newMap);
        }
      }
    } catch (error) {
      console.error('Error loading transactions:', error);
    } finally {
      setLoading(false);
    }
  }, [portfolioId, stockMap]);

  useEffect(() => {
    loadTransactions();
  }, [loadTransactions]);

  // Reload when parent signals a refresh via refreshKey
  useEffect(() => {
    if (typeof refreshKey !== 'undefined') {
      loadTransactions();
    }
  }, [refreshKey]);

  const handleTransactionAdded = () => {
    setShowAddModal(false);
    loadTransactions();
    onUpdate();
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    if (Number.isNaN(date.getTime())) return 'N/A';
    return new Intl.DateTimeFormat('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    }).format(date);
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="text-lg font-bold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">Transaction History</h3>
      </div>

      {transactions.length === 0 ? (
        <div className="text-center py-8">
          <p className="text-gray-600">No transactions yet.</p>
        </div>
      ) : (
        <div className="space-y-3">
          {transactions.map((transaction) => {
            // Defensive normalization: backend may return different shapes (DTO vs UI shape)
            const rawType = transaction.transaction_type ?? transaction.type ?? '';
            const tType = ('' + rawType).toUpperCase();

            const sharesVal = Number(transaction.shares ?? transaction.qty ?? 0) || 0;
            const priceVal = Number(transaction.price_per_share ?? transaction.price ?? transaction.price_per_share ?? 0) || 0;
            const totalAmount = Number(transaction.total_amount ?? (sharesVal * priceVal)) || 0;
            const dateVal = transaction.transaction_date ?? transaction.timestamp_t ?? null;

            // derive stockId and try to resolve symbol/name from cached stockMap
            const stockId = transaction.stock_id ?? transaction.stock?.id ?? transaction.stock?.stock_id ?? null;
            const stockObj = stockId ? stockMap[stockId] : (transaction.stock || null);
            const stockSymbol = stockObj?.stock_sym ?? stockObj?.symbol ?? transaction.stock_sym ?? transaction.symbol ?? (stockId ? String(stockId) : '—');
            const stockName = stockObj?.name ?? transaction.stock?.name ?? '';

            return (
              <div
                key={transaction.id ?? transaction.t_id ?? `${stockId ?? 's'}-${transaction.timestamp_t ?? dateVal ?? Math.random()}`}
                className="bg-white/80 backdrop-blur-sm rounded-xl border border-white/20 p-4 shadow-lg hover:shadow-xl transition-all duration-300 hover:transform hover:-translate-y-1"
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-4">
                    <div className={`p-3 rounded-xl ${
                      tType === 'BUY' || tType === 'ADD' ? 'bg-green-100/80' : 'bg-red-100/80'
                    }`}>
                      {tType === 'BUY' || tType === 'ADD' ? (
                        <ArrowDownCircle className="w-5 h-5 text-green-600" />
                      ) : (
                        <ArrowUpCircle className="w-5 h-5 text-red-600" />
                      )}
                    </div>

                    <div>
                      <div className="flex items-center gap-2 mb-1">
                        <span className="font-bold text-gray-900">
                          {stockSymbol}
                        </span>
                        {stockName && <span className="text-sm text-gray-500">{stockName}</span>}
                        <span className={`text-sm font-medium ${
                          tType === 'BUY' || tType === 'ADD' ? 'text-green-600' : 'text-red-600'
                        }`}>
                          {tType || 'N/A'}
                        </span>
                      </div>
                      <div className="flex items-center gap-4 text-sm text-gray-600">
                        <span>{sharesVal.toFixed(4)} shares</span>
                        <span>@ ${priceVal.toFixed(2)}</span>
                        <div className="flex items-center gap-1">
                          <Calendar className="w-3 h-3" />
                          {formatDate(dateVal)}
                        </div>
                      </div>
                    </div>
                  </div>

                  <div className="text-right">
                    <div className={`text-xl font-bold ${
                      tType === 'BUY' || tType === 'ADD' ? 'text-red-600' : 'text-green-600'
                    }`}>
                      {tType === 'BUY' || tType === 'ADD' ? '-' : '+'}
                      ${totalAmount.toLocaleString('en-US', {
                        minimumFractionDigits: 2,
                        maximumFractionDigits: 2
                      })}
                    </div>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {showAddModal && (
        <AddTransactionModal
          portfolioId={portfolioId}
          onClose={() => setShowAddModal(false)}
          onAdded={handleTransactionAdded}
        />
      )}
    </div>
  );
}
