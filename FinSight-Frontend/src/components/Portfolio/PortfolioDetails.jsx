import { useState, useEffect } from 'react';
import { ArrowLeft, Plus, TrendingUp, TrendingDown } from 'lucide-react';
import { portfolioService } from '../../services/portfolioService.js';
import { HoldingsList } from './HoldingsList.jsx';
import { AddStockModal } from './AddStockModal.jsx';
import { TransactionList } from '../Transaction/TransactionList.jsx';

export function PortfolioDetails({ portfolio: initialPortfolio, onBack }) {
  const [portfolio, setPortfolio] = useState(initialPortfolio);
  const [showAddStock, setShowAddStock] = useState(false);
  const [activeTab, setActiveTab] = useState('holdings');

  const refreshPortfolio = async () => {
    const updated = await portfolioService.getById(portfolio.id);
    if (updated) {
      setPortfolio(updated);
    }
  };

  const calculatePortfolioValue = () => {
    if (!portfolio.holdings) return 0;
    return portfolio.holdings.reduce((sum, holding) => {
      const currentPrice = holding.stock?.current_price || 0;
      return sum + (holding.shares * currentPrice);
    }, 0);
  };

  const calculatePortfolioCost = () => {
    if (!portfolio.holdings) return 0;
    return portfolio.holdings.reduce((sum, holding) => {
      return sum + (holding.shares * holding.average_cost);
    }, 0);
  };

  const calculatePortfolioGain = () => {
    const value = calculatePortfolioValue();
    const cost = calculatePortfolioCost();
    return value - cost;
  };

  const calculatePortfolioGainPercent = () => {
    const cost = calculatePortfolioCost();
    if (cost === 0) return 0;
    const gain = calculatePortfolioGain();
    return (gain / cost) * 100;
  };

  const value = calculatePortfolioValue();
  const cost = calculatePortfolioCost();
  const gain = calculatePortfolioGain();
  const gainPercent = calculatePortfolioGainPercent();
  const isPositive = gain >= 0;

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-4">
        <button
          onClick={onBack}
          className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
        >
          <ArrowLeft className="w-6 h-6 text-gray-600" />
        </button>
        <div className="flex-1">
          <h2 className="text-2xl font-bold text-gray-900">{portfolio.name}</h2>
          {portfolio.description && (
            <p className="text-gray-600 mt-1">{portfolio.description}</p>
          )}
        </div>
        <button
          onClick={() => setShowAddStock(true)}
          className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          <Plus className="w-5 h-5" />
          Add Stock
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white rounded-xl border border-gray-200 p-6">
          <div className="text-sm text-gray-600 mb-2">Portfolio Value</div>
          <div className="text-3xl font-bold text-gray-900">
            ${value.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
          </div>
        </div>

        <div className="bg-white rounded-xl border border-gray-200 p-6">
          <div className="text-sm text-gray-600 mb-2">Total Cost</div>
          <div className="text-3xl font-bold text-gray-900">
            ${cost.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
          </div>
        </div>

        <div className={`rounded-xl border p-6 ${isPositive ? 'bg-green-50 border-green-200' : 'bg-red-50 border-red-200'}`}>
          <div className="flex items-center gap-2 mb-2">
            {isPositive ? (
              <TrendingUp className="w-5 h-5 text-green-600" />
            ) : (
              <TrendingDown className="w-5 h-5 text-red-600" />
            )}
            <div className={`text-sm ${isPositive ? 'text-green-700' : 'text-red-700'}`}>
              Total Gain/Loss
            </div>
          </div>
          <div className={`text-3xl font-bold ${isPositive ? 'text-green-700' : 'text-red-700'}`}>
            {isPositive ? '+' : ''}${gain.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
          </div>
          <div className={`text-sm font-semibold mt-2 ${isPositive ? 'text-green-600' : 'text-red-600'}`}>
            {isPositive ? '+' : ''}{gainPercent.toFixed(2)}%
          </div>
        </div>
      </div>

      <div className="bg-white rounded-xl border border-gray-200">
        <div className="border-b border-gray-200">
          <div className="flex">
            <button
              onClick={() => setActiveTab('holdings')}
              className={`flex-1 px-6 py-4 text-sm font-medium border-b-2 transition-colors ${
                activeTab === 'holdings'
                  ? 'border-blue-600 text-blue-600'
                  : 'border-transparent text-gray-600 hover:text-gray-900'
              }`}
            >
              Holdings ({portfolio.holdings?.length || 0})
            </button>
            <button
              onClick={() => setActiveTab('transactions')}
              className={`flex-1 px-6 py-4 text-sm font-medium border-b-2 transition-colors ${
                activeTab === 'transactions'
                  ? 'border-blue-600 text-blue-600'
                  : 'border-transparent text-gray-600 hover:text-gray-900'
              }`}
            >
              Transaction History
            </button>
          </div>
        </div>

        <div className="p-6">
          {activeTab === 'holdings' ? (
            <HoldingsList
              portfolio={portfolio}
              onUpdate={refreshPortfolio}
            />
          ) : (
            <TransactionList
              portfolioId={portfolio.id}
              onUpdate={refreshPortfolio}
            />
          )}
        </div>
      </div>

      {showAddStock && (
        <AddStockModal
          portfolioId={portfolio.id}
          onClose={() => setShowAddStock(false)}
          onAdded={() => {
            setShowAddStock(false);
            refreshPortfolio();
          }}
        />
      )}
    </div>
  );
}
