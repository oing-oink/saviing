/**
 * 적금 관련 React Query 키 팩토리
 *
 * 쿼리 키의 일관성을 보장하고 캐시 관리를 용이하게 하기 위한 팩토리 함수들을 제공합니다.
 * 모든 적금 관련 쿼리는 이 팩토리를 통해 키를 생성해야 합니다.
 */
export const savingsKeys = {
  /**
   * 모든 적금 관련 쿼리의 기본 키
   */
  all: ['savings'] as const,

  /**
   * 적금 계좌 관련 쿼리의 기본 키
   */
  accounts: () => [...savingsKeys.all, 'accounts'] as const,

  /**
   * 고객의 모든 계좌 목록 조회 쿼리 키
   */
  accountsList: (customerId?: number) =>
    [...savingsKeys.accounts(), 'list', customerId] as const,

  /**
   * 특정 적금 계좌 상세 조회 쿼리 키
   * @param accountId - 적금 계좌 ID
   */
  detail: (accountId: string) =>
    [...savingsKeys.accounts(), 'detail', accountId] as const,

  /**
   * 특정 적금 계좌 상세 정보 조회 쿼리 키 (설정 변경용)
   * @param accountId - 적금 계좌 ID
   */
  savingsAccountDetail: (accountId: string) =>
    [...savingsKeys.accounts(), 'savings-detail', accountId] as const,

  /**
   * 거래 내역 관련 쿼리의 기본 키
   */
  transactions: () => [...savingsKeys.all, 'transactions'] as const,

  /**
   * 특정 적금 계좌의 거래 내역 조회 쿼리 키 (무한 스크롤용)
   * @param accountId - 적금 계좌 ID
   */
  transactionsList: (accountId: string) =>
    [...savingsKeys.transactions(), accountId] as const,
};
