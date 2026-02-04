import { useState } from 'react';
import { Header } from '../Layout/Header.jsx';
import { PortfolioList } from './PortfolioList.jsx';
import { PortfolioDetails } from '../Portfolio/PortfolioDetails.jsx';
import { PortfolioVisualization } from './PortfolioVisualization.jsx';

export function Dashboard({ onGoHome }) {
  const [selectedPortfolio, setSelectedPortfolio] = useState(null);
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  const handlePortfolioUpdate = () => {
    setRefreshTrigger(prev => prev + 1);
  };

  const handleBackToList = () => {
    setSelectedPortfolio(null);
    handlePortfolioUpdate(); // Refresh the list when coming back
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Header onGoHome={onGoHome} />
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
         {/* Portfolio Management Section*/}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-8">
          <div className="mb-6">
            <h1 className="text-3xl font-bold text-gray-900 mb-2">Portfolio Management</h1>
            <p className="text-gray-600">Manage your portfolios and track individual performance</p>
          </div>
          {selectedPortfolio ? (
            <PortfolioDetails
              portfolio={selectedPortfolio}
              onBack={handleBackToList}
              onPortfolioUpdate={handlePortfolioUpdate}
            />
          ) : (
            <PortfolioList 
              onSelectPortfolio={setSelectedPortfolio} 
              refreshTrigger={refreshTrigger}
            />
          )}
        </div>

        {/* Portfolio Analytics Section*/}
        <div className="mb-12 mt-8">
          <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-8">
            <div className="mb-6">
              <h1 className="text-3xl font-bold text-gray-900 mb-2">Portfolio Analytics</h1>
              <p className="text-gray-600">Comprehensive overview of all your portfolios</p>
            </div>
            <PortfolioVisualization refreshTrigger={refreshTrigger} />
          </div>
        </div>
      </main>
    </div>
  );
}
