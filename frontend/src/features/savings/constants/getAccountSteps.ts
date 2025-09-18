// 적금용 스텝 플로우
export const SAVINGS_STEPS = [
  'START',
  'PRODUCT_TYPE',
  'USER_INFO',
  'AUTH',
  'TERMS',
  'SET_CONDITION',
  'CONFIRM',
  'COMPLETE',
] as const;

// 입출금 통장용 스텝 플로우 (SET_CONDITION, CONFIRM 제외)
export const CHECKING_STEPS = [
  'START',
  'PRODUCT_TYPE',
  'USER_INFO',
  'AUTH',
  'TERMS',
  'COMPLETE',
] as const;

export type SavingsStep = (typeof SAVINGS_STEPS)[number];
export type CheckingStep = (typeof CHECKING_STEPS)[number];
export type GetAccountStep = SavingsStep | CheckingStep;
