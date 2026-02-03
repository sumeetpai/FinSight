import { TrendingUp, TrendingDown, DollarSign } from 'lucide-react';

export function PortfolioCard({ portfolio, value, gain, gainPercent, onClick }) {
  const isPositive = gain >= 0;

  return (
    <div
      onClick={onClick}
      className="bg-white rounded-xl border border-gray-200 p-6 hover:shadow-lg transition-all cursor-pointer group"
    >
      <div className="flex items-start justify-between mb-4">
        <div>
          <h3 className="text-lg font-semibold text-gray-900 group-hover:text-blue-600 transition-colors">
            {portfolio.name}
          </h3>
          {portfolio.description && (
            <p className="text-sm text-gray-600 mt-1">{portfolio.description}</p>
          )}
        </div>
        <div className={`p-2 rounded-lg ${isPositive ? 'bg-green-100' : 'bg-red-100'}`}>
          {isPositive ? (
            <TrendingUp className="w-5 h-5 text-green-600" />
          ) : (
            <TrendingDown className="w-5 h-5 text-red-600" />
          )}
        </div>
      </div>

      <div className="space-y-3">
        <div>
          <div className="flex items-center gap-2 text-sm text-gray-600 mb-1">
            <DollarSign className="w-4 h-4" />
            Portfolio Value
          </div>
          <div className="text-2xl font-bold text-gray-900">
            ${value.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
          </div>
        </div>

        <div className="flex items-center justify-between pt-3 border-t border-gray-100">
          <div>
            <div className="text-sm text-gray-600">Total Gain/Loss</div>
            <div className={`font-semibold ${isPositive ? 'text-green-600' : 'text-red-600'}`}>
              {isPositive ? '+' : ''}${gain.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
            </div>
          </div>
          <div className={`text-right px-3 py-1 rounded-lg ${isPositive ? 'bg-green-50' : 'bg-red-50'}`}>
            <div className={`text-sm font-semibold ${isPositive ? 'text-green-700' : 'text-red-700'}`}>
              {isPositive ? '+' : ''}{gainPercent.toFixed(2)}%
            </div>
          </div>
        </div>

        <div className="text-sm text-gray-600">
          {portfolio.holdings?.length || 0} {portfolio.holdings?.length === 1 ? 'holding' : 'holdings'}
        </div>
      </div>
    </div>
  );
}
