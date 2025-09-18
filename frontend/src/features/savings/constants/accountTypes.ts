export const ACCOUNT_TYPES = {
  SAVINGS: '자유적금',
  CHECKING: '입출금통장',
} as const;

export type AccountType = (typeof ACCOUNT_TYPES)[keyof typeof ACCOUNT_TYPES];

export interface AccountTypeOption {
  id: AccountType;
  title: string;
  description: string;
  recommended?: boolean;
}

export const ACCOUNT_TYPE_OPTIONS: AccountTypeOption[] = [
  {
    id: ACCOUNT_TYPES.SAVINGS,
    title: '자유 적금',
    description: '언제든 자유롭게 입금할 수 있어요',
    recommended: true,
  },
  {
    id: ACCOUNT_TYPES.CHECKING,
    title: '입출금 통장',
    description: '자유로운 입출금 통장을 개설해요',
  },
];
