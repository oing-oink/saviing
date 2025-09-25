export const PAGE_PATH = {
  HOME: '/',
  WALLET: '/wallet',
  PRODUCTS: '/products',
  PROFILE: '/profile',
  ONBOARDING: '/onboarding',
  AUTH_CALLBACK: '/auth/callback',
  SAVINGS: '/savings',
  DEPOSIT: '/deposit',
  DEPOSIT_WITH_ACCOUNT: '/deposit/:accountId',
  DEPOSIT_RESULT: '/deposit/result',
  ACCOUNT_CREATION: '/account-creation',
  SAVINGS_DETAIL: '/savings/detail',
  SAVINGS_DETAIL_WITH_ID: '/savings/detail/:accountId',
  SAVINGS_SETTINGS_WITH_ID: '/savings/detail/:accountId/settings',
  SAVINGS_TERMINATION_WITH_ID: '/savings/detail/:accountId/termination',
  ACCOUNT_DETAIL: '/account/detail',
  ACCOUNT_DETAIL_WITH_ID: '/account/detail/:accountId',
  GAME: '/game',
  SHOP: '/shop',
  GACHA: '/gacha',
  GACHA_INFO: '/gacha/info',
  GACHA_ROLLING: '/gacha/rolling',
  GACHA_RESULT: '/gacha/result',
  DECO: '/deco',
  GAME_ENTER: '/game/enter',
  COLORTEST: '/colortest',
  NOT_FOUND: '*',
} as const;

// 동적 경로 생성 헬퍼 함수
export const createSavingsDetailPath = (
  accountId: string | number,
  entryPoint?: string,
) => {
  const basePath = `/savings/detail/${accountId}`;
  return entryPoint
    ? `${basePath}?from=${encodeURIComponent(entryPoint)}`
    : basePath;
};

export const changeSavingsSettingsPath = (
  accountId: string | number,
  entryPoint?: string,
) => {
  const basePath = `/savings/detail/${accountId}/settings?step=CURRENT_INFO`;
  return entryPoint
    ? `${basePath}&from=${encodeURIComponent(entryPoint)}`
    : basePath;
};

export const createSavingsTerminationPath = (
  accountId: string | number,
  entryPoint?: string,
) => {
  const basePath = `/savings/detail/${accountId}/termination?step=WARNING`;
  return entryPoint
    ? `${basePath}&from=${encodeURIComponent(entryPoint)}`
    : basePath;
};

export const createAccountDetailPath = (accountId: string | number) =>
  `/account/detail/${accountId}`;

export const createDepositPath = (accountId: string | number) =>
  `/deposit/${accountId}`;

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

// 적금 설정 변경 funnel URL
export const SAVINGS_SETTINGS_STEPS_PATH = {
  CURRENT_INFO: 'CURRENT_INFO',
  SELECT_CHANGE: 'SELECT_CHANGE',
  NEW_SETTINGS: 'NEW_SETTINGS',
  IMPACT_REVIEW: 'IMPACT_REVIEW',
  CONFIRM: 'CONFIRM',
  COMPLETE: 'COMPLETE',
} as const;

// 적금 해지 funnel URL
export const SAVINGS_TERMINATION_STEPS_PATH = {
  WARNING: 'WARNING',
  AUTH: 'AUTH',
  REASON: 'REASON',
  CONFIRM: 'CONFIRM',
  COMPLETE: 'COMPLETE',
} as const;
