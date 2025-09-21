export interface SourceAccount {
  id: string;
  accountId?: number;
  bankName: string;
  productName: string;
  maskedNumber: string;
  balance: number;
  description?: string;
}

export interface SavingAccount {
  id: string;
  accountId?: number;
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

export interface TransferRequest {
  sourceAccountId: number;
  targetAccountId: number;
  amount: number;
  memo?: string;
  idempotencyKey: string;
}

export interface TransferResponse {
  transactionId: string;
  success: boolean;
  message?: string;
}
