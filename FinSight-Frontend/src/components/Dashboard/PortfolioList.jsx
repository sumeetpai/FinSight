import { useState, useEffect } from 'react';
import { Plus, Folder, TrendingUp, TrendingDown, DollarSign } from 'lucide-react';
import { CreatePortfolioModal } from './CreatePortfolioModal.jsx';
import { PortfolioCard } from './PortfolioCard.jsx';
import { portfolioApi } from '../../services/portfolioApi.js';

const API_BASE_URL = 'http://localhost:8080/api/v1';

export function PortfolioList({ onSelectPortfolio, refreshTrigger }) {
  const [portfolios, setPortfolios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [portfolioRisks, setPortfolioRisks] = useState({});

  useEffect(() => {
    loadPortfolios();
  }, [refreshTrigger]); // Reload when refreshTrigger changes

  const loadPortfolios = async () => {
    setLoading(true);
    const data = await portfolioApi.getAllPortfolios();
    if (data) {
      setPortfolios(data.filter(p => p.active !== false));
    }
    setLoading(false);
  };

  const handleCreatePortfolio = async (name, description) => {
    const success = await portfolioApi.createPortfolio(name, description);
    if (success) {
      await loadPortfolios();
      setShowCreateModal(false);
    }
  };

  useEffect(() => {
    let alive = true;

    const fetchRiskScores = async () => {
      if (!portfolios.length) {
        if (alive) setPortfolioRisks({});
        return;
      }

      const riskEntries = await Promise.all(
        portfolios.map(async (portfolio) => {
          const holdings = portfolio.holdings || [];
          if (!holdings.length) {
            return [portfolio.id, null];
          }

          const risks = await Promise.all(
            holdings.map(async (holding) => {
              const symbol = holding.stock?.symbol;
              if (!symbol) return null;
              try {
                const resp = await fetch(`${API_BASE_URL}/market/risk/${encodeURIComponent(symbol)}`);
                if (!resp.ok) return null;
                const data = await resp.json();
                const score = Number(data?.risk_score);
                return Number.isFinite(score) ? score : null;
              } catch {
                return null;
              }
            })
          );

          const valid = risks.filter((v) => Number.isFinite(v));
          if (!valid.length) return [portfolio.id, null];
          const avg = valid.reduce((sum, v) => sum + v, 0) / valid.length;
          return [portfolio.id, avg];
        })
      );

      if (!alive) return;
      setPortfolioRisks(Object.fromEntries(riskEntries));
    };

    fetchRiskScores();

    return () => {
      alive = false;
    };
  }, [portfolios]);

  const calculatePortfolioValue = (portfolio) => {
    if (!portfolio.holdings) return 0;
    return portfolio.holdings.reduce((sum, holding) => {
      const currentPrice = holding.stock?.live_price ?? holding.stock?.current_price ?? 0;
      return sum + (holding.shares * currentPrice);
    }, 0);
  };

  const calculatePortfolioCost = (portfolio) => {
    // Use the cost_basis from API as cost basis
    return portfolio.cost_basis || 0;
  };

  const calculatePortfolioGain = (portfolio) => {
    const value = calculatePortfolioValue(portfolio);
    const cost = calculatePortfolioCost(portfolio);
    return value - cost;
  };

  const calculatePortfolioGainPercent = (portfolio) => {
    const cost = calculatePortfolioCost(portfolio);
    if (cost === 0) return 0;
    const gain = calculatePortfolioGain(portfolio);
    return (gain / cost) * 100;
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-8">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-3xl font-bold text-gray-900 mb-2">Your Portfolios</h2>
          <p className="text-gray-600">Track and manage all your investment portfolios</p>
        </div>
        <button
          onClick={() => setShowCreateModal(true)}
          className="bg-gradient-to-r from-blue-600 to-indigo-600 text-white px-6 py-3 rounded-xl font-semibold hover:from-blue-700 hover:to-indigo-700 transition-all duration-200 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 flex items-center gap-2"
        >
          <Plus className="w-5 h-5" />
          Create Portfolio
        </button>
      </div>

      {portfolios.length === 0 ? (
        <div className="bg-white/60 backdrop-blur-sm rounded-2xl border-2 border-dashed border-gray-300/50 p-16 text-center shadow-lg">
          <div className="bg-gray-100 w-20 h-20 rounded-2xl flex items-center justify-center mx-auto mb-6">
            <Folder className="w-10 h-10 text-gray-400" />
          </div>
          <h3 className="text-2xl font-semibold text-gray-900 mb-3">No Portfolios Yet</h3>
          <p className="text-gray-600 mb-8 text-lg">Create your first portfolio to start tracking your investments and building wealth</p>
          <button
            onClick={() => setShowCreateModal(true)}
            className="bg-gradient-to-r from-blue-600 to-indigo-600 text-white px-8 py-4 rounded-xl font-semibold text-lg hover:from-blue-700 hover:to-indigo-700 transition-all duration-200 shadow-xl hover:shadow-2xl transform hover:-translate-y-1 flex items-center gap-2"
          >
            <Plus className="w-6 h-6" />
            Create Your First Portfolio
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {portfolios.map((portfolio) => (
            <PortfolioCard
              key={portfolio.id}
              portfolio={portfolio}
              value={calculatePortfolioValue(portfolio)}
              gain={calculatePortfolioGain(portfolio)}
              gainPercent={calculatePortfolioGainPercent(portfolio)}
              riskScore={portfolioRisks[portfolio.id]}
              onClick={() => onSelectPortfolio(portfolio)}
            />
          ))}
        </div>
      )}

      {showCreateModal && (
        <CreatePortfolioModal
          onClose={() => setShowCreateModal(false)}
          onCreate={handleCreatePortfolio}
        />
      )}
    </div>
  );
}
