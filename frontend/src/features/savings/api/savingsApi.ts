import { http } from '@/shared/services/api/http';
import type {
  SavingsAccountData,
  TransactionData,
  GetTransactionsParams,
} from '@/features/savings/types/savingsTypes';
import {
  mockGetSavingsAccount,
  mockGetSavingsTransactions,
} from '@/features/savings/data/mockSavingsApi';

/**
 * 개발 환경에서 mock 데이터 사용 여부를 결정하는 플래그
 *
 * development 모드에서는 true, production에서는 false로 설정됩니다.
 */
//const USE_MOCK = import.meta.env.MODE === 'development';
const USE_MOCK = false;

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
  if (USE_MOCK) {
    // Mock 데이터 사용
    const mockResponse = await mockGetSavingsAccount(accountId);
    return mockResponse.body!;
  }

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
  if (USE_MOCK) {
    // Mock 데이터 사용
    const mockResponse = await mockGetSavingsTransactions(accountId, params);
    return mockResponse.body!;
  }

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
