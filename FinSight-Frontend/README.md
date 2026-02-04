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
├── services/           # API service layer
└── App.jsx            # Main application with routing
```

## Data Layer

The application connects to a Spring Boot backend API for all data operations. The frontend includes service layers that handle API communication with proper error handling and user feedback.

### API Services:
- `portfolioApi.js` - Portfolio CRUD operations
- `stockApi.js` - Stock data and search
- `transactionApi.js` - Transaction management

## Backend Integration

The application connects to a Spring Boot backend API running on `http://localhost:8080`. The API services expect these endpoints:

### Portfolio Endpoints:
```
GET    /api/v1/portfolio/           # Get all portfolios
POST   /api/v1/portfolio/           # Create portfolio
GET    /api/v1/portfolio/{id}       # Get portfolio details (not implemented)
PUT    /api/v1/portfolio/{id}       # Update portfolio
PUT    /api/v1/portfolio/{id}/status # Soft delete (set active: false)
```

### Stock Endpoints:
```
GET    /api/v1/stocks/              # Get all stocks
GET    /api/v1/stocks/{id}          # Get stock by ID
POST   /api/v1/stocks/              # Create stock
```

### Transaction Endpoints:
```
GET    /api/v1/transactions/portfolio/{portfolioId}  # Get transactions for portfolio
POST   /api/v1/transactions/        # Create transaction
PUT    /api/v1/transactions/{id}    # Update transaction
DELETE /api/v1/transactions/{id}    # Delete transaction
```

## Development Notes

- The application requires a running Spring Boot backend on `http://localhost:8080`
- All API calls include proper error handling with toast notifications
- User feedback is provided through success/error/loading toast messages
- Data persists in the backend database

## Contributing

1. Follow the existing code structure
2. Use meaningful component and function names
3. Add proper error handling
4. Test all features with mock data before backend integration
