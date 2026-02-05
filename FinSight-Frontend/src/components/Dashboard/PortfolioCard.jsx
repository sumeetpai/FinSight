import { TrendingUp, TrendingDown, DollarSign } from 'lucide-react';

export function PortfolioCard({ portfolio, value, gain, gainPercent, onClick }) {
  const isPositive = gain >= 0;
  const safeGainPercent = Number(gainPercent ?? 0) || 0;

  return (
    <div
      onClick={onClick}
      className="bg-white/80 backdrop-blur-sm rounded-2xl border border-white/20 p-6 hover:shadow-xl transition-all duration-300 cursor-pointer group hover:-translate-y-1"
    >
      <div className="flex items-start justify-between mb-6">
        <div>
          <h3 className="text-xl font-semibold text-gray-900 group-hover:text-blue-600 transition-colors">
            {portfolio.name}
          </h3>
          {portfolio.description && (
            <p className="text-sm text-gray-600 mt-1">{portfolio.description}</p>
          )}
        </div>
        <div className={`p-3 rounded-xl ${isPositive ? 'bg-green-100' : 'bg-red-100'} group-hover:scale-110 transition-transform duration-200`}>
          {isPositive ? (
            <TrendingUp className="w-6 h-6 text-green-600" />
          ) : (
            <TrendingDown className="w-6 h-6 text-red-600" />
          )}
        </div>
      </div>

      <div className="space-y-4">
        <div>
          <div className="flex items-center gap-2 text-sm text-gray-600 mb-2">
            <DollarSign className="w-4 h-4" />
            Portfolio Value
          </div>
          <div className="text-3xl font-bold text-gray-900">
            ${value.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
          </div>
        </div>

        <div className="flex items-center justify-between pt-4 border-t border-gray-100/50">
          <div>
            <div className="text-sm text-gray-600 mb-1">Total Gain/Loss</div>
            <div className={`text-lg font-semibold ${isPositive ? 'text-green-600' : 'text-red-600'}`}>
              {isPositive ? '+' : ''}${gain.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
            </div>
          </div>
          <div className={`text-right px-4 py-2 rounded-xl ${isPositive ? 'bg-green-50' : 'bg-red-50'} border ${isPositive ? 'border-green-200' : 'border-red-200'}`}>
            <div className={`text-sm font-bold ${isPositive ? 'text-green-700' : 'text-red-700'}`}>
              {isPositive ? '+' : ''}{safeGainPercent.toFixed(2)}%
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
