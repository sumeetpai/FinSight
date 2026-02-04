import { useState } from 'react';
import { Toaster } from 'react-hot-toast';
import { Home } from './components/Home/Home.jsx';
import { Dashboard } from './components/Dashboard/Dashboard.jsx';

function App() {
  const [currentPage, setCurrentPage] = useState('home');

  const handleGetStarted = () => {
    setCurrentPage('dashboard');
  };

  const handleGoHome = () => {
    setCurrentPage('home');
  };

  return (
    <>
      <Toaster />
      {currentPage === 'home' && <Home onGetStarted={handleGetStarted} />}
      {currentPage === 'dashboard' && <Dashboard onGoHome={handleGoHome} />}
    </>
  );
}

export default App;
