/**
 * 은행 상품 정보 타입
 */
export interface Product {
  productId: number;
  productName: string;
  productCode: string;
  productCategory: 'DEMAND_DEPOSIT' | 'INSTALLMENT_SAVINGS';
  description: string;
  minInterestRateBps: number;
  maxInterestRateBps: number;
}

/**
 * 금리 범위 정보
 */
export interface InterestRate {
  minBps: number;
  maxBps: number;
}

/**
 * 기간 제약 조건
 */
export interface TermConstraints {
  minValue: number;
  maxValue: number;
  unit: 'WEEKS' | 'MONTHS' | 'YEARS';
  stepValue: number;
}

/**
 * 적금 상품 설정
 */
export interface SavingsConfig {
  defaultPaymentCycle: 'MONTHLY' | 'WEEKLY' | 'DAILY';
  minPaymentAmount: number;
  maxPaymentAmount: number;
  termConstraints: TermConstraints;
}

/**
 * 입출금 상품 설정
 */
export interface DemandDepositConfig {
  minimumBalance: number;
}

/**
 * 상품 상세 정보 타입
 */
export interface ProductDetail {
  productId: number;
  productName: string;
  productCode: string;
  productCategory: 'DEMAND_DEPOSIT' | 'INSTALLMENT_SAVINGS';
  description: string;
  compoundingType: 'DAILY' | 'MONTHLY' | 'YEARLY';
  interestRate: InterestRate;
  savingsConfig?: SavingsConfig;
  demandDepositConfig?: DemandDepositConfig;
}

/**
 * 금리 BPS를 퍼센트로 변환하는 유틸리티 함수
 */
export const bpsToPercent = (bps: number): string => {
  return (bps / 100).toFixed(1);
};

/**
 * 상품 카테고리를 한국어로 변환하는 함수
 */
export const getCategoryLabel = (
  category: Product['productCategory'],
): string => {
  switch (category) {
    case 'DEMAND_DEPOSIT':
      return '입출금통장';
    case 'INSTALLMENT_SAVINGS':
      return '적금';
    default:
      return '기타';
  }
};

/**
 * 상품 카테고리별 색상 클래스를 반환하는 함수
 */
export const getCategoryColorClass = (
  category: Product['productCategory'],
): string => {
  switch (category) {
    case 'DEMAND_DEPOSIT':
      return 'bg-blue-100 text-blue-800';
    case 'INSTALLMENT_SAVINGS':
      return 'bg-green-100 text-green-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
};

/**
 * 금리 범위를 문자열로 포맷하는 함수
 */
export const formatInterestRateRange = (
  minBps: number,
  maxBps: number,
): string => {
  if (minBps === maxBps) {
    return `${bpsToPercent(minBps)}%`;
  }
  return `${bpsToPercent(minBps)}% ~ ${bpsToPercent(maxBps)}%`;
};

/**
 * InterestRate 객체를 문자열로 포맷하는 함수
 */
export const formatInterestRate = (interestRate: InterestRate): string => {
  return formatInterestRateRange(interestRate.minBps, interestRate.maxBps);
};

/**
 * 복리 계산 주기를 한국어로 변환하는 함수
 */
export const getCompoundingTypeLabel = (
  compoundingType: ProductDetail['compoundingType'],
): string => {
  switch (compoundingType) {
    case 'DAILY':
      return '일복리';
    case 'MONTHLY':
      return '월복리';
    case 'YEARLY':
      return '연복리';
    default:
      return '기타';
  }
};

/**
 * 납입 주기를 한국어로 변환하는 함수
 */
export const getPaymentCycleLabel = (
  cycle: SavingsConfig['defaultPaymentCycle'],
): string => {
  switch (cycle) {
    case 'DAILY':
      return '매일';
    case 'WEEKLY':
      return '매주';
    case 'MONTHLY':
      return '매월';
    default:
      return '기타';
  }
};

/**
 * 기간 단위를 한국어로 변환하는 함수
 */
export const getTermUnitLabel = (unit: TermConstraints['unit']): string => {
  switch (unit) {
    case 'WEEKS':
      return '주';
    case 'MONTHS':
      return '개월';
    case 'YEARS':
      return '년';
    default:
      return '';
  }
};

/**
 * 금액을 한국어 표기로 포맷하는 함수
 */
export const formatAmount = (amount: number): string => {
  return amount.toLocaleString('ko-KR') + '원';
};
