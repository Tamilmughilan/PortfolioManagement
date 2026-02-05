// ...new file...
// Utility to map currency codes to symbols and format amounts
export const getCurrencySymbol = (code) => {
  if (!code) return '';
  const c = String(code).toUpperCase();
  switch (c) {
    case 'INR':
      return '₹';
    case 'USD':
      return '$';
    case 'EUR':
      return '€';
    case 'GBP':
      return '£';
    default:
      return c; // fallback to code itself
  }
};

export const formatWithSymbol = (code, amount) => {
  const symbol = getCurrencySymbol(code);
  return `${symbol} ${Number(amount || 0).toFixed(2)}`;
};
