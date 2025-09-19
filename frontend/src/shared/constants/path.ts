export const PAGE_PATH = {
  HOME: '/',
  WALLET: '/wallet',
  LOGIN: '/login',
  ONBOARDING: '/onboarding',
  AUTH_CALLBACK: '/auth/callback',
  SAVINGS: '/savings',
  DEPOSIT: '/deposit',
  DEPOSIT_RESULT: '/deposit/result',
  ACCOUNT_CREATION: '/account-creation',
  SAVINGS_DETAIL: '/savings/detail',
  SAVINGS_DETAIL_WITH_ID: '/savings/detail/:accountId',
  ACCOUNT_DETAIL: '/account/detail',
  ACCOUNT_DETAIL_WITH_ID: '/account/detail/:accountId',
  GAME: '/game',
  SHOP: '/shop',
  GACHA: '/gacha',
  GACHA_ROLLING: '/gacha/rolling',
  DECO: '/deco',
  COLORTEST: '/colortest',
  NOT_FOUND: '*',
} as const;

// 동적 경로 생성 헬퍼 함수
export const createSavingsDetailPath = (accountId: string | number) =>
  `/savings/detail/${accountId}`;

export const createAccountDetailPath = (accountId: string | number) =>
  `/account/detail/${accountId}`;

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
