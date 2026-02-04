import { TrendingUp, Home } from 'lucide-react';

export function Header({ onGoHome }) {
  return (
    <header className="bg-white/80 backdrop-blur-sm border-b border-gray-200/50 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center gap-3">
            <div className="bg-gradient-to-r from-blue-600 to-indigo-600 p-2 rounded-xl shadow-lg">
              <TrendingUp className="w-6 h-6 text-white" />
            </div>
            <h1 className="text-2xl font-bold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">
              FINSIGHT
            </h1>
          </div>

          {onGoHome && (
            <button
              onClick={onGoHome}
              className="flex items-center gap-2 px-6 py-2 text-sm font-medium text-gray-600 hover:text-gray-900 hover:bg-white/60 rounded-xl transition-all duration-200 shadow-sm hover:shadow-md"
            >
              <Home className="w-4 h-4" />
              Home
            </button>
          )}
        </div>
      </div>
    </header>
  );
}
