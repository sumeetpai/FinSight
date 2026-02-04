import { useState, useEffect } from 'react';
import { Plus, ArrowUpCircle, ArrowDownCircle, Calendar } from 'lucide-react';
import { transactionService } from '../../services/transactionService.js';
import { AddTransactionModal } from './AddTransactionModal.jsx';

export function TransactionList({ portfolioId, onUpdate }) {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showAddModal, setShowAddModal] = useState(false);

  useEffect(() => {
    loadTransactions();
  }, [portfolioId]);

  const loadTransactions = async () => {
    try {
      setLoading(true);
      const data = await transactionService.getByPortfolio(portfolioId);
      setTransactions(data);
    } catch (error) {
      console.error('Error loading transactions:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleTransactionAdded = () => {
    setShowAddModal(false);
    loadTransactions();
    onUpdate();
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
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
        <h3 className="text-lg font-semibold text-gray-900">Transaction History</h3>
        {/* <button
          onClick={() => setShowAddModal(true)}
          className="flex items-center gap-2 px-3 py-2 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          <Plus className="w-4 h-4" />
          Add Transaction
        </button> */}
      </div>

      {transactions.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-600">No transactions yet.</p>
        </div>
      ) : (
        <div className="space-y-3">
          {transactions.map((transaction) => (
            <div
              key={transaction.id}
              className="flex items-center justify-between p-4 border border-gray-200 rounded-lg"
            >
              <div className="flex items-center gap-4">
                <div className={`p-2 rounded-lg ${
                  transaction.transaction_type === 'BUY'
                    ? 'bg-green-100'
                    : 'bg-red-100'
                }`}>
                  {transaction.transaction_type === 'BUY' ? (
                    <ArrowDownCircle className="w-5 h-5 text-green-600" />
                  ) : (
                    <ArrowUpCircle className="w-5 h-5 text-red-600" />
                  )}
                </div>

                <div>
                  <div className="flex items-center gap-2">
                    <span className="font-semibold text-gray-900">
                      {transaction.stock?.symbol}
                    </span>
                    <span className={`text-sm font-medium ${
                      transaction.transaction_type === 'BUY'
                        ? 'text-green-600'
                        : 'text-red-600'
                    }`}>
                      {transaction.transaction_type}
                    </span>
                  </div>
                  <div className="flex items-center gap-4 text-sm text-gray-600 mt-1">
                    <span>{transaction.shares.toFixed(4)} shares</span>
                    <span>@ ${transaction.price_per_share.toFixed(2)}</span>
                    <div className="flex items-center gap-1">
                      <Calendar className="w-3 h-3" />
                      {formatDate(transaction.transaction_date)}
                    </div>
                  </div>
                </div>
              </div>

              <div className="text-right">
                <div className={`text-lg font-semibold ${
                  transaction.transaction_type === 'BUY'
                    ? 'text-red-600'
                    : 'text-green-600'
                }`}>
                  {transaction.transaction_type === 'BUY' ? '-' : '+'}
                  ${transaction.total_amount.toLocaleString('en-US', {
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2
                  })}
                </div>
              </div>
            </div>
          ))}
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
