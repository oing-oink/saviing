/**
 * 계좌 생성 요청 관련 타입 정의
 */

/**
 * 기간 단위 타입
 */
export type PeriodUnit = 'WEEKS' | 'MONTHS';

/**
 * 기간 정보
 */
export interface TermPeriod {
  /** 기간 값 */
  value: number;
  /** 기간 단위 */
  unit: PeriodUnit;
}

/**
 * 자동이체 주기 타입
 */
export type AutoTransferCycle = 'WEEKLY' | 'MONTHLY';

/**
 * 자동이체 설정
 */
export interface AutoTransfer {
  /** 자동이체 활성화 여부 */
  enabled: boolean;
  /** 자동이체 주기 */
  cycle: AutoTransferCycle;
  /** 이체일 */
  transferDay: number;
  /** 이체 금액 */
  amount: number;
  /** 출금 계좌 ID */
  withdrawAccountId: number;
}

/**
 * 입출금 통장 생성 요청
 */
export interface CreateCheckingAccountRequest {
  /** 고객 ID */
  customerId: number;
  /** 상품 ID (입출금통장: 1) */
  productId: number;
}

/**
 * 자유적금 통장 생성 요청
 */
export interface CreateSavingsAccountRequest {
  /** 고객 ID */
  customerId: number;
  /** 상품 ID (자유적금: 2) */
  productId: number;
  /** 목표 금액 */
  targetAmount: number;
  /** 기간 정보 */
  termPeriod: TermPeriod;
  /** 만기 시 출금할 계좌번호 */
  maturityWithdrawalAccount: string;
  /** 자동이체 설정 (자유적금에서만 사용) */
  autoTransfer: AutoTransfer;
}

/**
 * 계좌 생성 요청 타입 (Union)
 */
export type AccountCreationRequest =
  | CreateCheckingAccountRequest
  | CreateSavingsAccountRequest;

/**
 * 계좌 생성 응답
 */
export interface AccountCreationResponse {
  /** 생성된 계좌 ID */
  accountId: string;
  /** 계좌번호 */
  accountNumber: string;
  /** 상품명 */
  productName: string;
  /** 생성 일시 */
  createdAt: string;
}

/**
 * 기존 계좌 확인 응답
 */
export interface ExistingAccountsResponse {
  /** 입출금 계좌 존재 여부 */
  hasCheckingAccount: boolean;
  /** 기존 입출금 계좌 정보 (있는 경우) */
  checkingAccount?: {
    accountId: string;
    accountNumber: string;
    productName: string;
  };
}
