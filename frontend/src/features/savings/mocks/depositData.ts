import type {
  QuickAmount,
  SavingAccount,
  SourceAccount,
} from '@/features/savings/types/deposit';

export const SOURCE_ACCOUNTS: SourceAccount[] = [
  {
    id: 'kb-main',
    bankName: '국민은행',
    productName: 'KB국민 한마음 통장',
    maskedNumber: '123-45-****-6789',
    balance: 835_000,
    description: '급여 · 생활비 메인 계좌',
  },
  {
    id: 'nh-spare',
    bankName: '농협은행',
    productName: 'NH주거래 우대 통장',
    maskedNumber: '302-12-****-4411',
    balance: 265_500,
    description: '고정비 분리 계좌',
  },
];

export const SAVING_ACCOUNT: SavingAccount = {
  id: 'saving-flex',
  name: '자유적금',
  bankName: '우리은행',
  maskedNumber: '1002-****-9920',
  balance: 680_000,
  targetAmount: 2_500_000,
  baseRate: 3.2,
  bonusRate: 0.6,
  nextAutoTransferDate: '2025-02-05',
};

export const QUICK_AMOUNTS: QuickAmount[] = [10_000, 50_000, 100_000, 300_000];
