export const API_BASE_URLS = {
  BANK: import.meta.env.VITE_API_BANK_BASE_URL,
  GAME: import.meta.env.VITE_API_GAME_BASE_URL,
} as const;

export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: '/v1/auth/login',
  },
} as const;
