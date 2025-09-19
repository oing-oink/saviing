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
