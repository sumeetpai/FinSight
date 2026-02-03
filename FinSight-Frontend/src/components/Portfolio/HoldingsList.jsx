import { TrendingUp, TrendingDown, Trash2 } from 'lucide-react';
import { portfolioService } from '../../services/portfolioService';

export function HoldingsList({ portfolio, onUpdate }) {
  const handleDeleteHolding = async (holdingId) => {
    if (!confirm('Are you sure you want to remove this holding?')) return;

    try {
      await portfolioService.deleteHolding(holdingId);
      onUpdate();
    } catch (error) {
      console.error('Error deleting holding:', error);
    }
  };

  if (!portfolio.holdings || portfolio.holdings.length === 0) {
    return (
      <div className="text-center py-12">
        <p className="text-gray-600">No holdings yet. Add your first stock to get started.</p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {portfolio.holdings.map((holding) => {
        const currentPrice = holding.stock?.current_price || 0;
        const currentValue = holding.shares * currentPrice;
        const costBasis = holding.shares * holding.average_cost;
        const gain = currentValue - costBasis;
        const gainPercent = costBasis > 0 ? (gain / costBasis) * 100 : 0;
        const isPositive = gain >= 0;

        return (
          <div
            key={holding.id}
            className="flex items-center justify-between p-4 border border-gray-200 rounded-lg hover:border-gray-300 transition-colors"
          >
            <div className="flex-1">
              <div className="flex items-center gap-3 mb-2">
                <h4 className="text-lg font-semibold text-gray-900">
                  {holding.stock?.symbol}
                </h4>
                <span className="text-sm text-gray-600">{holding.stock?.name}</span>
              </div>
              <div className="flex items-center gap-6 text-sm">
                <div>
                  <span className="text-gray-600">Shares: </span>
                  <span className="font-medium text-gray-900">{holding.shares.toFixed(4)}</span>
                </div>
                <div>
                  <span className="text-gray-600">Avg Cost: </span>
                  <span className="font-medium text-gray-900">
                    ${holding.average_cost.toFixed(2)}
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

            <div className="flex items-center gap-6">
              <div className="text-right">
                <div className="text-lg font-bold text-gray-900">
                  ${currentValue.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                </div>
                <div className={`flex items-center gap-1 text-sm font-semibold ${isPositive ? 'text-green-600' : 'text-red-600'}`}>
                  {isPositive ? <TrendingUp className="w-4 h-4" /> : <TrendingDown className="w-4 h-4" />}
                  {isPositive ? '+' : ''}${gain.toFixed(2)} ({isPositive ? '+' : ''}{gainPercent.toFixed(2)}%)
                </div>
              </div>

              <button
                onClick={() => handleDeleteHolding(holding.id)}
                className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
              >
                <Trash2 className="w-5 h-5" />
              </button>
            </div>
          </div>
        );
      })}
    </div>
  );
}
