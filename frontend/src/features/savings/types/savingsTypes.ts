/**
 * 적금 상품 정보 타입
 */
export interface ProductInfo {
  productId: number;
  productName: string;
  productCode: string;
  productCategory: string;
  description: string;
}

/**
 * 적금 정보 타입
 */
export interface SavingsInfo {
  maturityWithdrawalAccount: string;
  targetAmount: number;
  termPeriod: number;
  termPeriodUnit: string;
  maturityDate: string;
}

/**
 * 적금 계좌 상세 정보 타입
 */
export interface SavingsAccountData {
  accountId: number;
  accountNumber: string;
  customerId: number;
  product: ProductInfo;
  compoundingType: string;
  status: string;
  openedAt: string;
  closedAt: string;
  lastAccrualTs: string;
  lastRateChangeAt: string;
  createdAt: string;
  updatedAt: string;
  balance: number;
  interestAccrued: number;
  baseRate: number;
  bonusRate: number;
  savings?: SavingsInfo;
}

/**
 * 적금 상세 페이지에서 사용할 필수 정보만 추출한 타입
 */
export interface SavingsDisplayData {
  accountNumber: string;
  productName: string;
  interestRate: number; // (baseRate + bonusRate) / 100.0
  targetAmount: number;
  maturityDate: string;
  createdAt: string;
  balance: number;
}

/**
 * 거래 내역 데이터 타입 (서버 응답 원본 구조)
 */
export interface TransactionData {
  transactionId: number;
  accountId: number;
  transactionType: 'TRANSFER_OUT' | 'TRANSFER_IN'; // TRANSFER_IN = 입금, TRANSFER_OUT = 출금
  direction: 'CREDIT' | 'DEBIT'; // CREDIT = 입금, DEBIT = 출금
  amount: number;
  valueDate: string; // 금융 거래가 성사되고 자산의 가치가 결정되는 날짜
  postedAt: string;
  status: 'POSTED'; // POSTED = 완료된 거래
  relatedTransactionId: number;
  description: string;
  createdAt: string;
  updatedAt: string;
  balanceAfter: number;
}

/**
 * UI용 거래 내역 표시 데이터 타입
 */
export interface TransactionDisplayData {
  transactionId: number;
  direction: 'CREDIT' | 'DEBIT';
  amount: number;
  postedAt: string;
  description: string;
  balanceAfter: number;
}

/**
 * 거래 내역 API 요청 파라미터 타입
 */
export interface GetTransactionsParams {
  page: number;
  size: number;
}

/**
 * 자동이체 정보 타입
 */
export interface AutoTransferInfo {
  enabled: boolean;
  cycle: string;
  transferDay: number;
  amount: number;
  nextRunDate: string;
  lastExecutedAt: string;
  withdrawAccountId: number;
}

/**
 * 적금 상세 정보 타입
 */
export interface SavingsDetailInfo {
  maturityWithdrawalAccount: string;
  targetAmount: number;
  termPeriod: number;
  termPeriodUnit: string;
  maturityDate: string;
  autoTransfer: AutoTransferInfo;
}

/**
 * 적금 계좌 상세 정보 API 응답 타입
 */
export interface SavingsAccountDetailResponse {
  accountId: number;
  accountNumber: string;
  customerId: number;
  product: ProductInfo;
  compoundingType: string;
  status: string;
  openedAt: string;
  closedAt: string;
  lastAccrualTs: string;
  lastRateChangeAt: string;
  createdAt: string;
  updatedAt: string;
  balance: number;
  interestAccrued: number;
  baseRate: number;
  bonusRate: number;
  savings: SavingsDetailInfo;
}

/**
 * 자동이체 설정 변경 요청 타입
 */
export interface UpdateAutoTransferRequest {
  enabled?: boolean;
  amount?: number;
  cycle?: 'WEEKLY' | 'MONTHLY';
  transferDay?: number;
  withdrawAccountId?: number;
}
