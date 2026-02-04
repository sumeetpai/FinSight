import { apiCall, handleApiError, handleApiSuccess } from '../utils/toast.js';

const API_BASE_URL = 'http://localhost:8080/api/v1';

class TransactionService {
  // Get all transactions for a portfolio
  async getTransactionsByPortfolio(portfolioId) {
    return apiCall(async () => {
      const response = await fetch(`${API_BASE_URL}/transactions/portfolio/${portfolioId}`);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const transactions = await response.json();
      return transactions;
    }, {
      errorMessage: 'Failed to load transactions'
    });
  }

  // Create a new transaction
  async createTransaction(transactionData) {
    return apiCall(async () => {
      const response = await fetch(`${API_BASE_URL}/transactions/`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          portfolio_id: transactionData.portfolio_id,
          stock_id: transactionData.stock_id,
          transaction_type: transactionData.transaction_type,
          shares: transactionData.shares,
          price_per_share: transactionData.price_per_share,
          transaction_date: transactionData.transaction_date || new Date().toISOString(),
        }),
      });

      if (!response.ok) {
        const errorData = await response.text();
        throw new Error(`Failed to create transaction: ${response.status} ${errorData}`);
      }

      return await response.json();
    }, {
      successMessage: 'Transaction created successfully!',
      errorMessage: 'Failed to create transaction',
      showLoadingToast: true,
      loadingMessage: 'Creating transaction...'
    });
  }

  // Update a transaction
  async updateTransaction(transactionId, transactionData) {
    return apiCall(async () => {
      const response = await fetch(`${API_BASE_URL}/transactions/${transactionId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(transactionData),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      return await response.json();
    }, {
      successMessage: 'Transaction updated successfully!',
      errorMessage: 'Failed to update transaction',
      showLoadingToast: true,
      loadingMessage: 'Updating transaction...'
    });
  }

  // Delete a transaction
  async deleteTransaction(transactionId) {
    return apiCall(async () => {
      const response = await fetch(`${API_BASE_URL}/transactions/${transactionId}`, {
        method: 'DELETE',
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
    }, {
      successMessage: 'Transaction deleted successfully!',
      errorMessage: 'Failed to delete transaction',
      showLoadingToast: true,
      loadingMessage: 'Deleting transaction...'
    });
  }
}

export const transactionApi = new TransactionService();