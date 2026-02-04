# FINSIGHT - Portfolio Management Application

A modern React-based portfolio management application for tracking stock investments and performance.

## Features

- **Beautiful Landing Page**: Professional home page with feature highlights and gradient design
- **Portfolio Management**: Create and manage multiple investment portfolios
- **Stock Tracking**: Monitor stock prices and performance
- **Transaction History**: Record buy/sell transactions with detailed history
- **Real-time Holdings**: View current portfolio holdings with profit/loss calculations
- **Responsive Design**: Modern UI built with Tailwind CSS and glassmorphism effects

## Tech Stack

- **Frontend**: React 19, Vite, Tailwind CSS
- **Icons**: Lucide React
- **Data**: Currently using mock data (ready for Spring Boot backend integration)
- **State Management**: React hooks
- **Build Tool**: Vite

## Getting Started

### Prerequisites

- Node.js (v18 or higher)
- npm or yarn

### Installation

1. Clone the repository
2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```

4. Open [http://localhost:5173](http://localhost:5173) in your browser

### Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint

## Project Structure

```
src/
├── components/
│   ├── Home/           # Landing page component
│   ├── Dashboard/      # Main dashboard components
│   ├── Layout/         # Layout components (Header)
│   ├── Portfolio/      # Portfolio-related components
│   ├── Stock/          # Stock management components
│   └── Transaction/    # Transaction components
├── lib/
│   └── mockData.js     # Mock data for development
├── services/           # API service layer
└── App.jsx            # Main application with routing
```

## Data Layer

Currently using mock data stored in `src/lib/mockData.js`. The application is designed to easily switch to a real API backend.

### Mock Data Includes:
- Sample stocks (AAPL, GOOGL, MSFT, TSLA, AMZN)
- Sample portfolios with holdings
- Sample transaction history

## Backend Integration

The application is designed to work with a Spring Boot backend API. When your backend is ready:

1. Update the API endpoints in the service files
2. Replace mock data calls with actual HTTP requests
3. Update the `.env` file with your API base URL

Example API structure expected:
```
GET    /api/portfolios
POST   /api/portfolios
GET    /api/portfolios/{id}
PUT    /api/portfolios/{id}
DELETE /api/portfolios/{id}

GET    /api/stocks
POST   /api/stocks
GET    /api/stocks/{symbol}

GET    /api/transactions/portfolio/{portfolioId}
POST   /api/transactions
DELETE /api/transactions/{id}
```

## Development Notes

- All data operations include artificial delays to simulate network requests
- The application works completely offline with mock data
- No authentication required - all features are accessible immediately
- Data persists only during the session (page refresh resets to initial mock data)

## Contributing

1. Follow the existing code structure
2. Use meaningful component and function names
3. Add proper error handling
4. Test all features with mock data before backend integration
