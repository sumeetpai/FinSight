import { useState } from 'react';
import { TrendingUp, TrendingDown, Trash2, Eye } from 'lucide-react';
import { portfolioApi } from '../../services/portfolioApi.js';
import { StockInsightModal } from './StockInsightModal.jsx';

export function HoldingsList({ portfolio, onHoldingsChange, onUpdate }) {
  const [removingId, setRemovingId] = useState(null);
  const [removeQty, setRemoveQty] = useState('');
  const [removing, setRemoving] = useState(false);
  const [insightStock, setInsightStock] = useState(null);

  if (!portfolio.holdings || portfolio.holdings.length === 0) {
    return (
      <div className="text-center py-8">
        <p className="text-gray-600">No holdings yet. Add your first stock to get started.</p>
      </div>
    );
  }

  const handleRemove = async (holding) => {
    if (!portfolio?.id) return;
    const qtyNum = parseInt(removeQty, 10);
    if (!Number.isFinite(qtyNum) || qtyNum <= 0) return;

    setRemoving(true);
    try {
      await portfolioApi.removeStockFromPortfolio(
        portfolio.id,
        holding.stock_id ?? holding.id ?? holding.stock?.id,
        qtyNum
      );
      setRemovingId(null);
      setRemoveQty('');
      if (onHoldingsChange) onHoldingsChange();
      if (onUpdate) onUpdate();
    } catch (err) {
      console.error('Failed to remove stock from portfolio:', err);
    } finally {
      setRemoving(false);
    }
  };

  return (
    <div className="space-y-3">
      {portfolio.holdings.map((holding) => {
        const currentPrice = Number(holding.stock?.live_price ?? holding.stock?.current_price ?? 0) || 0;
        const shares = Number(holding.shares ?? 0) || 0;
        const averageCost = Number(holding.average_cost ?? 0) || 0;
        const currentValue = shares * currentPrice;
        const costBasis = shares * averageCost;
        const gain = currentValue - costBasis;
        const gainPercent = costBasis > 0 ? (gain / costBasis) * 100 : 0;
        const isPositive = gain >= 0;

        return (
          <div
            key={holding.id}
            className="bg-white/80 backdrop-blur-sm rounded-xl border border-white/20 p-4 shadow-lg hover:shadow-xl transition-all duration-300 hover:transform hover:-translate-y-1"
          >
            <div className="flex items-center justify-between">
              <div className="flex-1">
                <div className="flex items-center gap-3 mb-2">
                  <h4 className="text-lg font-bold text-gray-900">
                    {holding.stock?.symbol}
                  </h4>
                  <span className="text-gray-600">{holding.stock?.name}</span>
                </div>
                <div className="grid grid-cols-3 gap-4 text-sm">
                  <div>
                    <span className="text-gray-600">Shares: </span>
                    <span className="font-medium text-gray-900">{shares.toFixed(4)}</span>
                  </div>
                  <div>
                    <span className="text-gray-600">Avg Cost: </span>
                    <span className="font-medium text-gray-900">
                      ${averageCost.toFixed(2)}
                    </span>
                  </div>
                  <div>
                    <span className="text-gray-600">Current: </span>
                    <span className="font-medium text-gray-900">
                      ${currentPrice.toFixed(2)}
                    </span>
                  </div>
                </div>
              </div>

              <div className="text-right ml-6">
                <div className="text-xl font-bold text-gray-900 mb-1">
                  ${currentValue.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                </div>
                <div className={`flex items-center justify-center gap-1 px-2 py-1 rounded-lg text-sm font-semibold ${isPositive ? 'bg-green-100/80 text-green-700' : 'bg-red-100/80 text-red-700'}`}>
                  {isPositive ? <TrendingUp className="w-3 h-3" /> : <TrendingDown className="w-3 h-3" />}
                  {isPositive ? '+' : ''}${gain.toFixed(2)} ({isPositive ? '+' : ''}{gainPercent.toFixed(2)}%)
                </div>
                <div className="mt-3 flex justify-end">
                  <button
                    type="button"
                    onClick={() => setInsightStock(holding)}
                    className="inline-flex items-center gap-2 px-3 py-2 text-sm font-semibold text-slate-700 bg-slate-100 border border-slate-200 rounded-lg hover:bg-slate-200 transition-colors mr-2"
                  >
                    <Eye className="w-4 h-4" />
                    View
                  </button>
                  <button
                    type="button"
                    onClick={() => {
                      setRemovingId(holding.id);
                      setRemoveQty('');
                    }}
                    className="inline-flex items-center gap-2 px-3 py-2 text-sm font-semibold text-red-700 bg-red-50 border border-red-200 rounded-lg hover:bg-red-100 transition-colors"
                  >
                    <Trash2 className="w-4 h-4" />
                    Remove
                  </button>
                </div>
              </div>
            </div>
            {removingId === holding.id && (
              <div className="mt-4 p-4 rounded-xl bg-red-50/80 border border-red-200/60">
                <div className="text-sm font-semibold text-red-700 mb-2">
                  Remove shares from {holding.stock?.symbol}
                </div>
                <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
                  <input
                    type="number"
                    step="1"
                    min="1"
                    value={removeQty}
                    onChange={(e) => setRemoveQty(e.target.value)}
                    className="w-full sm:w-40 px-3 py-2 border border-red-200 rounded-lg bg-white focus:ring-2 focus:ring-red-300 focus:border-transparent"
                    placeholder="Shares"
                  />
                  <div className="flex gap-2">
                    <button
                      type="button"
                      onClick={() => handleRemove(holding)}
                      disabled={removing}
                      className="px-4 py-2 bg-red-600 text-white rounded-lg font-semibold hover:bg-red-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      {removing ? 'Removing...' : 'Confirm'}
                    </button>
                    <button
                      type="button"
                      onClick={() => {
                        setRemovingId(null);
                        setRemoveQty('');
                      }}
                      className="px-4 py-2 border border-red-200 text-red-700 rounded-lg font-semibold hover:bg-white transition-colors"
                    >
                      Cancel
                    </button>
                  </div>
                </div>
                <div className="mt-2 text-xs text-red-700/80">
                  You currently hold {shares.toFixed(4)} shares.
                </div>
              </div>
            )}
          </div>
        );
      })}
      {insightStock && (
        <StockInsightModal
          symbol={insightStock.stock?.symbol}
          name={insightStock.stock?.name}
          onClose={() => setInsightStock(null)}
        />
      )}
    </div>
  );
}
