import { useState } from 'react';
import { Header } from '../Layout/Header.jsx';
import { PortfolioList } from './PortfolioList.jsx';
import { PortfolioDetails } from '../Portfolio/PortfolioDetails.jsx';
import { PortfolioVisualization } from './PortfolioVisualization.jsx';

export function Dashboard({ onGoHome }) {
  const [selectedPortfolio, setSelectedPortfolio] = useState(null);
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  // Accept an optional updatedPortfolio so child can push fresh state up
  const handlePortfolioUpdate = (updatedPortfolio) => {
    if (updatedPortfolio) {
      setSelectedPortfolio(updatedPortfolio);
    }
    setRefreshTrigger(prev => prev + 1);
  };

  const handleBackToList = () => {
    setSelectedPortfolio(null);
    handlePortfolioUpdate(); // Refresh the list when coming back
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-100">
      <Header onGoHome={onGoHome} />
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
         {/* Portfolio Management Section*/}
        <div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-xl border border-white/20 p-8 mb-8">
          <div className="mb-8 text-center">
            <h1 className="text-4xl font-bold text-gray-900 mb-3">
              Portfolio
              <span className="bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent"> Management</span>
            </h1>
            <p className="text-xl text-gray-600">Manage your portfolios and track individual performance with real-time insights</p>
          </div>
          {selectedPortfolio ? (
            <PortfolioDetails
              portfolio={selectedPortfolio}
              onBack={handleBackToList}
              onPortfolioUpdate={handlePortfolioUpdate}
            />
          ) : (
            <PortfolioList 
              key={refreshTrigger}
              onSelectPortfolio={setSelectedPortfolio} 
              refreshTrigger={refreshTrigger}
            />
          )}
        </div>

        {/* Portfolio Analytics Section*/}
        <div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-xl border border-white/20 p-8">
          <div className="mb-8">
            <h1 className="text-4xl font-bold text-gray-900 mb-3">
              Portfolio
              <span className="bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent"> Analytics</span>
            </h1>
            <p className="text-xl text-gray-600">Comprehensive overview of all your portfolios with real-time insights</p>
          </div>
          <PortfolioVisualization refreshTrigger={refreshTrigger} portfolio={selectedPortfolio} />
        </div>
      </main>
    </div>
  );
}
