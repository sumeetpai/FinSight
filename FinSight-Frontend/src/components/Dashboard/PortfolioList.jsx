import { useState, useEffect } from 'react';
import { Plus, Folder, TrendingUp, TrendingDown, DollarSign } from 'lucide-react';
import { portfolioService } from '../../services/portfolioService.js';
import { CreatePortfolioModal } from './CreatePortfolioModal.jsx';
import { PortfolioCard } from './PortfolioCard.jsx';

export function PortfolioList({ onSelectPortfolio }) {
  const [portfolios, setPortfolios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCreateModal, setShowCreateModal] = useState(false);

  useEffect(() => {
    loadPortfolios();
  }, []);

  const loadPortfolios = async () => {
    try {
      setLoading(true);
      const data = await portfolioService.getAll();
      const detailedPortfolios = await Promise.all(
        data.map(p => portfolioService.getById(p.id))
      );
      setPortfolios(detailedPortfolios.filter(p => p !== null));
    } catch (error) {
      console.error('Error loading portfolios:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreatePortfolio = async (name, description) => {
    try {
      await portfolioService.create({
        name,
        description
      });
      await loadPortfolios();
      setShowCreateModal(false);
    } catch (error) {
      console.error('Error creating portfolio:', error);
    }
  };

  const calculatePortfolioValue = (portfolio) => {
    if (!portfolio.holdings) return 0;
    return portfolio.holdings.reduce((sum, holding) => {
      const currentPrice = holding.stock?.current_price || 0;
      return sum + (holding.shares * currentPrice);
    }, 0);
  };

  const calculatePortfolioCost = (portfolio) => {
    if (!portfolio.holdings) return 0;
    return portfolio.holdings.reduce((sum, holding) => {
      return sum + (holding.shares * holding.average_cost);
    }, 0);
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
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold text-gray-900">Your Portfolios</h2>
        <button
          onClick={() => setShowCreateModal(true)}
          className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          <Plus className="w-5 h-5" />
          Create Portfolio
        </button>
      </div>

      {portfolios.length === 0 ? (
        <div className="bg-white rounded-xl border-2 border-dashed border-gray-300 p-12 text-center">
          <Folder className="w-16 h-16 text-gray-400 mx-auto mb-4" />
          <h3 className="text-xl font-semibold text-gray-900 mb-2">No Portfolios Yet</h3>
          <p className="text-gray-600 mb-6">Create your first portfolio to start tracking your investments</p>
          <button
            onClick={() => setShowCreateModal(true)}
            className="inline-flex items-center gap-2 px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          >
            <Plus className="w-5 h-5" />
            Create Your First Portfolio
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {portfolios.map((portfolio) => (
            <PortfolioCard
              key={portfolio.id}
              portfolio={portfolio}
              value={calculatePortfolioValue(portfolio)}
              gain={calculatePortfolioGain(portfolio)}
              gainPercent={calculatePortfolioGainPercent(portfolio)}
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
