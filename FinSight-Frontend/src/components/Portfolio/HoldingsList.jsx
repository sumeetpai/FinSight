import { TrendingUp, TrendingDown } from 'lucide-react';

export function HoldingsList({ portfolio }) {
  if (!portfolio.holdings || portfolio.holdings.length === 0) {
    return (
      <div className="text-center py-8">
        <p className="text-gray-600">No holdings yet. Add your first stock to get started.</p>
      </div>
    );
  }

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
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );
}
