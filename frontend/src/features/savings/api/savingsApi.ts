import { http } from '@/shared/services/api/http';
import type {
  SavingsAccountData,
  TransactionData,
  GetTransactionsParams,
} from '@/features/savings/types/savingsTypes';
import type {
  TransferRequest,
  TransferResponse,
} from '@/features/savings/types/deposit';
// import {
//   mockGetSavingsAccount,
//   mockGetSavingsTransactions,
// } from '@/features/savings/data/mockSavingsApi';

/**
 * 하드코딩된 고객 ID (추후 인증 시스템에서 가져올 예정)
 */
const CUSTOMER_ID = 1;

/**
 * 개발 환경에서 mock 데이터 사용 여부를 결정하는 플래그
 *
 * development 모드에서는 true, production에서는 false로 설정됩니다.
 */
//const USE_MOCK = import.meta.env.MODE === 'development';
//const USE_MOCK = false;

/**
 * 고객의 모든 계좌 목록을 조회하는 API 함수
 *
 * 적금 계좌와 입출금 계좌를 모두 포함한 계좌 목록을 반환합니다.
 * customerId는 현재 하드코딩되어 있습니다.
 *
 * @returns 고객의 모든 계좌 정보가 담긴 SavingsAccountData 배열
 * @throws API 호출 실패 시 네트워크 오류 또는 HTTP 오류 발생
 */
export const getAllAccounts = async (): Promise<SavingsAccountData[]> => {
  // Mock 우회하고 직접 실제 API 호출
  const response = await http.get<SavingsAccountData[]>(
    `/v1/accounts?customerId=${CUSTOMER_ID}`,
  );
  return response.body!;
};

/**
 * 특정 적금 계좌의 상세 정보를 조회하는 API 함수
 *
 * 개발 환경에서는 mock 데이터를, 프로덕션 환경에서는 실제 API를 호출합니다.
 * 계좌번호, 상품명, 이자율, 목표금액, 만기일 등 모든 적금 정보를 반환합니다.
 *
 * @param accountId - 조회할 적금 계좌의 고유 식별자
 * @returns 적금 계좌의 상세 정보가 담긴 SavingsAccountData 객체
 * @throws API 호출 실패 시 네트워크 오류 또는 HTTP 오류 발생
 */
export const getSavingsAccount = async (
  accountId: string,
): Promise<SavingsAccountData> => {
  // 실제 API 호출
  const response = await http.get<SavingsAccountData>(
    `/v1/accounts/id/${accountId}`,
  );
  return response.body!;
};

/**
 * 특정 적금 계좌의 거래 내역을 조회하는 API 함수 (무한 스크롤용)
 *
 * 개발 환경에서는 mock 데이터를, 프로덕션 환경에서는 실제 API를 호출합니다.
 * 최신 날짜순으로 정렬된 거래 내역을 페이지네이션으로 반환합니다.
 *
 * @param accountId - 조회할 적금 계좌의 고유 식별자
 * @param params - 페이지네이션 파라미터 (page, size)
 * @returns 거래 내역 배열이 담긴 TransactionData[] 객체
 * @throws API 호출 실패 시 네트워크 오류 또는 HTTP 오류 발생
 */
export const getSavingsTransactions = async (
  accountId: string,
  params: GetTransactionsParams,
): Promise<TransactionData[]> => {
  // 실제 API 호출
  const response = await http.get<TransactionData[]>(
    `/v1/transactions/accounts/${accountId}`,
    {
      params: {
        accountId,
        page: params.page,
        size: params.size,
      },
    },
  );
  return response.body!;
};

/**
 * 유니크한 멱등성 키를 생성하는 유틸리티 함수
 *
 * @returns UUID v4 형식의 멱등성 키
 */
const generateIdempotencyKey = (): string => {
  return crypto.randomUUID();
};

/**
 * 계좌 간 이체를 실행하는 API 함수
 *
 * 사용자가 입력한 PIN 확인 후 적금 계좌로 입금 처리를 수행합니다.
 * 멱등성 키를 자동으로 생성하여 중복 거래를 방지합니다.
 *
 * @param sourceAccountId - 출금할 계좌의 ID (숫자형)
 * @param targetAccountId - 입금할 적금 계좌의 ID (숫자형)
 * @param amount - 이체할 금액
 * @returns 이체 결과가 담긴 TransferResponse 객체
 * @throws API 호출 실패 시 네트워크 오류 또는 HTTP 오류 발생
 */
export const transferToSavings = async (
  sourceAccountId: number,
  targetAccountId: number,
  amount: number,
  memo?: string,
): Promise<TransferResponse> => {
  const transferData: TransferRequest = {
    sourceAccountId,
    targetAccountId,
    amount,
    memo,
    idempotencyKey: generateIdempotencyKey(),
  };

  const response = await http.post<TransferResponse>(
    '/v1/transactions/transfer',
    transferData,
  );

  return response.body!;
};
