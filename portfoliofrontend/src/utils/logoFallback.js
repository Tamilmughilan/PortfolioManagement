// Simple SVG logo generator as fallback
export const getSVGLogoFallback = (ticker) => {
  // Simple colored squares with first letter
  const colors = {
    A: '#3B82F6',
    T: '#8B5CF6',
    N: '#10B981',
    M: '#F59E0B',
    G: '#EC4899',
    B: '#FBBF24',
    E: '#6B7280',
    S: '#EF4444',
    V: '#1E40AF',
  };

  const letter = ticker?.[0]?.toUpperCase() || '?';
  const color = colors[letter] || '#6B7280';

  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 48 48">
    <rect fill="${color}" width="48" height="48"/>
    <text x="24" y="32" font-size="24" font-weight="700" fill="white" text-anchor="middle" font-family="Arial">${letter}</text>
  </svg>`;

  return `data:image/svg+xml;base64,${btoa(svg)}`;
};
