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
  savings: SavingsInfo;
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
  balance: number;
}
