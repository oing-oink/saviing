// API 도메인 정의
const API_DOMAINS = {
  BANK: {
    DEV: 'https://api.bank-dev.saviing.life',
    PROD: 'https://api.bank.saviing.life',
  },
  GAME: {
    DEV: 'https://api.game-dev.saviing.life',
    PROD: 'https://api.game.saviing.life',
  },
} as const;

// 환경변수로 어떤 도메인과 환경을 사용할지 결정
// VITE_API_DOMAIN=BANK, VITE_API_ENV=DEV 형태로 설정
const selectedDomain = (import.meta.env.VITE_API_DOMAIN ||
  'BANK') as keyof typeof API_DOMAINS;
const selectedEnv = (import.meta.env.VITE_API_ENV || 'DEV') as 'DEV' | 'PROD';

// 현재 선택된 API URL
export const CURRENT_API_BASE_URL = API_DOMAINS[selectedDomain][selectedEnv];

export const API_BASE_URLS = {
  BANK: API_DOMAINS.BANK,
  GAME: API_DOMAINS.GAME,
} as const;

export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: '/v1/auth/login',
  },
} as const;
