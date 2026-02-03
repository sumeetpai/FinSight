import { useState } from 'react';
import { Header } from '../Layout/Header.jsx';
import { PortfolioList } from './PortfolioList.jsx';
import { PortfolioDetails } from '../Portfolio/PortfolioDetails.jsx';

export function Dashboard() {
  const [selectedPortfolio, setSelectedPortfolio] = useState(null);

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {selectedPortfolio ? (
          <PortfolioDetails
            portfolio={selectedPortfolio}
            onBack={() => setSelectedPortfolio(null)}
          />
        ) : (
          <PortfolioList onSelectPortfolio={setSelectedPortfolio} />
        )}
      </main>
    </div>
  );
}
