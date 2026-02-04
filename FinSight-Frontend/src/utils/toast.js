import toast from 'react-hot-toast';

// Toast utility functions
export const showSuccess = (message) => {
  toast.success(message, {
    duration: 4000,
    position: 'top-right',
    style: {
      background: '#10b981',
      color: '#fff',
      fontWeight: '500',
    },
  });
};

export const showError = (message) => {
  toast.error(message, {
    duration: 5000,
    position: 'top-right',
    style: {
      background: '#ef4444',
      color: '#fff',
      fontWeight: '500',
    },
  });
};

export const showLoading = (message) => {
  return toast.loading(message, {
    position: 'top-right',
  });
};

export const dismissToast = (toastId) => {
  toast.dismiss(toastId);
};

// API utility functions
export const handleApiError = (error, defaultMessage = 'An error occurred') => {
  const message = error?.message || error?.error || defaultMessage;
  showError(message);
  console.error('API Error:', error);
};

export const handleApiSuccess = (message) => {
  showSuccess(message);
};

// Common API call wrapper
export const apiCall = async (apiFunction, options = {}) => {
  const {
    successMessage,
    errorMessage = 'Operation failed',
    showLoadingToast = false,
    loadingMessage = 'Loading...'
  } = options;

  let loadingToastId;

  try {
    if (showLoadingToast) {
      loadingToastId = showLoading(loadingMessage);
    }

    const result = await apiFunction();

    if (loadingToastId) {
      dismissToast(loadingToastId);
    }

    if (successMessage) {
      showSuccess(successMessage);
    }

    return result;
  } catch (error) {
    if (loadingToastId) {
      dismissToast(loadingToastId);
    }

    handleApiError(error, errorMessage);
    throw error;
  }
};