/**
 * formatters.js — shared display helpers
 */

/** Format a number as Moroccan Dirham (or any locale/currency) */
export function formatPrice(amount) {
  return new Intl.NumberFormat('fr-MA', {
    style: 'currency',
    currency: 'MAD',
    maximumFractionDigits: 0,
  }).format(amount);
}

/** Format a date string to a readable locale string */
export function formatDate(dateString) {
  return new Date(dateString).toLocaleDateString('fr-MA', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  });
}

/** Truncate long text with ellipsis */
export function truncate(text, maxLength = 100) {
  if (!text) return '';
  return text.length > maxLength ? text.slice(0, maxLength) + '…' : text;
}
