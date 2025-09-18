export interface SourceAccount {
  id: string;
  bankName: string;
  productName: string;
  maskedNumber: string;
  balance: number;
  description?: string;
}

export interface SavingAccount {
  id: string;
  name: string;
  bankName: string;
  maskedNumber: string;
  balance: number;
  targetAmount: number;
  baseRate: number;
  bonusRate: number;
  nextAutoTransferDate: string;
}

export type QuickAmount = number;
