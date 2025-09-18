export const PAGE_PATH = {
  HOME: '/',
  WALLET: '/wallet',
  LOGIN: '/login',
  SAVINGS: '/savings',
  ACCOUNT_CREATION: '/account-creation',
  GAME: '/game',
  SHOP: '/shop',
  GACHA: '/gacha',
  GACHA_ROLLING: '/gacha/rolling',
  DECO: '/deco',
  COLORTEST: '/colortest',
  NOT_FOUND: '*',
} as const;

// 계좌 생성 URL
export const ACCOUNT_CREATION_STEPS_PATH = {
  START: '/account-creation/start',
  PRODUCT_TYPE: '/account-creation/type',
  USER_INFO: '/account-creation/user-info',
  AUTH: '/account-creation/auth',
  TERMS: '/account-creation/terms',
  SET_CONDITION: '/account-creation/condition',
  CONFIRM: '/account-creation/confirm',
  COMPLETE: '/account-creation/complete',
} as const;
