import { http } from '@/shared/services/api/http';
import type {
  SavingsAccountData,
  TransactionData,
  GetTransactionsParams,
  SavingsAccountDetailResponse,
  UpdateAutoTransferRequest,
} from '@/features/savings/types/savingsTypes';
import type {
  TransferRequest,
  TransferResponse,
} from '@/features/savings/types/deposit';
import type {
  CreateCheckingAccountRequest,
  CreateSavingsAccountRequest,
  AccountCreationResponse,
  ExistingAccountsResponse,
} from '@/features/savings/types/accountCreation';
// import {
//   mockGetSavingsAccount,
//   mockGetSavingsTransactions,
// } from '@/features/savings/data/mockSavingsApi';

/**
 * 개발 환경에서 mock 데이터 사용 여부를 결정하는 플래그
 *
 * development 모드에서는 true, production에서는 false로 설정됩니다.
 */
//const USE_MOCK = import.meta.env.MODE === 'development';
//const USE_MOCK = false;

// 자동이체 설정 변경 테스트용 Mock 플래그
const USE_MOCK_UPDATE = false;

/**
 * 고객의 모든 계좌 목록을 조회하는 API 함수
 *
 * 적금 계좌와 입출금 계좌를 모두 포함한 계좌 목록을 반환합니다.
 * customerId는 인증 스토어에서 전달받은 값을 사용합니다.
 *
 * @param customerId - 조회할 고객 ID
 * @returns 고객의 모든 계좌 정보가 담긴 SavingsAccountData 배열
 * @throws API 호출 실패 시 네트워크 오류 또는 HTTP 오류 발생
 */
export const getAllAccounts = async (
  customerId: number,
): Promise<SavingsAccountData[]> => {
  // Mock 우회하고 직접 실제 API 호출
  const response = await http.get<SavingsAccountData[]>(
    `/v1/accounts?customerId=${customerId}`,
  );

  const accounts = response.body!;
  return accounts;
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
 * 특정 적금 계좌의 상세 정보를 조회하는 API 함수 (설정 변경용)
 *
 * 적금 정보(savings)와 자동이체 정보를 포함한 완전한 계좌 정보를 조회합니다.
 * 설정 변경 시 현재 적금 정보를 표시하는 용도로 사용됩니다.
 *
 * @param accountId - 조회할 적금 계좌의 고유 식별자
 * @returns 적금 상세 정보가 담긴 SavingsAccountDetailResponse 객체
 * @throws API 호출 실패 시 네트워크 오류 또는 HTTP 오류 발생
 */
export const getSavingsAccountDetail = async (
  accountId: string,
): Promise<SavingsAccountDetailResponse> => {
  // Mock 데이터로 변경된 설정 확인
  if (USE_MOCK_UPDATE) {
    const mockResponse: SavingsAccountDetailResponse = {
      accountId: Number(accountId),
      accountNumber: '11012345678901234',
      customerId: 1001,
      product: {
        productId: 1,
        productName: '자유적금',
        productCode: 'FREE_SAVINGS',
        productCategory: 'SAVINGS',
        description: '자유롭게 적금하는 상품',
      },
      compoundingType: 'COMPOUND',
      status: 'ACTIVE',
      openedAt: '2024-01-15T09:30:00Z',
      closedAt: '2024-12-31T17:00:00Z',
      lastAccrualTs: '2024-01-15T23:59:59Z',
      lastRateChangeAt: '2024-01-15T09:30:00Z',
      createdAt: '2024-01-15T09:30:00Z',
      updatedAt: new Date().toISOString(),
      balance: 500000,
      interestAccrued: 1250.5,
      baseRate: 250,
      bonusRate: 50,
      savings: {
        maturityWithdrawalAccount: '11012345678901234',
        targetAmount: 1000000,
        termPeriod: 12,
        termPeriodUnit: 'MONTHS',
        maturityDate: '2024-12-31',
        autoTransfer: {
          enabled: true,
          cycle: 'MONTHLY',
          transferDay: 1, // 변경된 값
          amount: 500000, // 변경된 값
          nextRunDate: '2024-02-14',
          lastExecutedAt: '2024-01-07T09:00:00Z',
          withdrawAccountId: 11, // 변경된 값
        },
      },
    };
    return mockResponse;
  }

  const response = await http.get<SavingsAccountDetailResponse>(
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
 * 적금 계좌의 자동이체 설정을 변경하는 API 함수
 *
 * 적금 계좌의 자동이체 설정(납입금액, 납입일, 연결계좌)을 변경합니다.
 * 변경하고자 하는 항목만 요청 본문에 포함하여 전송합니다.
 *
 * @param accountId - 변경할 적금 계좌의 고유 식별자
 * @param updateData - 변경할 자동이체 설정 데이터
 * @returns 변경된 적금 계좌 상세 정보가 담긴 SavingsAccountDetailResponse 객체
 * @throws API 호출 실패 시 네트워크 오류 또는 HTTP 오류 발생
 */

export const updateSavingsAutoTransfer = async (
  accountId: string,
  updateData: UpdateAutoTransferRequest,
): Promise<SavingsAccountDetailResponse> => {
  // Mock 데이터로 성공 응답 시뮬레이션
  if (USE_MOCK_UPDATE) {
    // 2초 지연으로 실제 API 호출 시뮬레이션
    await new Promise(resolve => setTimeout(resolve, 2000));

    const mockResponse: SavingsAccountDetailResponse = {
      accountId: Number(accountId),
      accountNumber: '11012345678901234',
      customerId: 1001,
      product: {
        productId: 1,
        productName: '자유적금',
        productCode: 'FREE_SAVINGS',
        productCategory: 'SAVINGS',
        description: '자유롭게 적금하는 상품',
      },
      compoundingType: 'COMPOUND',
      status: 'ACTIVE',
      openedAt: '2024-01-15T09:30:00Z',
      closedAt: '2024-12-31T17:00:00Z',
      lastAccrualTs: '2024-01-15T23:59:59Z',
      lastRateChangeAt: '2024-01-15T09:30:00Z',
      createdAt: '2024-01-15T09:30:00Z',
      updatedAt: new Date().toISOString(),
      balance: 500000,
      interestAccrued: 1250.5,
      baseRate: 250,
      bonusRate: 50,
      savings: {
        maturityWithdrawalAccount: '11012345678901234',
        targetAmount: 1000000,
        termPeriod: 12,
        termPeriodUnit: 'MONTHS',
        maturityDate: '2024-12-31',
        autoTransfer: {
          enabled: updateData.enabled !== undefined ? updateData.enabled : true,
          cycle: updateData.cycle || 'MONTHLY',
          transferDay: updateData.transferDay || 3,
          amount: updateData.amount || 100000,
          nextRunDate: '2024-02-14',
          lastExecutedAt: '2024-01-07T09:00:00Z',
          withdrawAccountId: updateData.withdrawAccountId || 2001,
        },
      },
    };

    return mockResponse;
  }

  // 실제 API 호출
  const response = await http.patch<SavingsAccountDetailResponse>(
    `/v1/accounts/id/${accountId}/savings/auto-transfer`,
    updateData,
    {
      headers: {
        'Content-Type': 'application/json',
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

/**
 * 고객의 기존 계좌 현황을 확인하는 API 함수
 *
 * 입출금 계좌 존재 여부를 확인하여 적금 개설 가능 여부를 판단합니다.
 *
 * @param customerId - 조회할 고객 ID
 * @returns 기존 계좌 현황 정보가 담긴 ExistingAccountsResponse 객체
 * @throws API 호출 실패 시 네트워크 오류 또는 HTTP 오류 발생
 */
export const checkExistingAccounts = async (
  customerId: number,
): Promise<ExistingAccountsResponse> => {
  const response = await http.get<ExistingAccountsResponse>(
    `/v1/accounts?customerId=${customerId}`,
  );
  return response.body!;
};

/**
 * 입출금 통장을 개설하는 API 함수
 *
 * @param request - 입출금 통장 생성 요청 데이터
 * @returns 생성된 계좌 정보가 담긴 AccountCreationResponse 객체
 * @throws API 호출 실패 시 네트워크 오류 또는 HTTP 오류 발생
 */
export const createCheckingAccount = async (
  request: CreateCheckingAccountRequest,
): Promise<AccountCreationResponse> => {
  const response = await http.post<AccountCreationResponse>(
    '/v1/accounts',
    request,
  );
  return response.body!;
};

/**
 * 자유적금 통장을 개설하는 API 함수
 *
 * 입출금 계좌가 존재하는 경우에만 호출되어야 합니다.
 *
 * @param request - 자유적금 통장 생성 요청 데이터
 * @returns 생성된 계좌 정보가 담긴 AccountCreationResponse 객체
 * @throws API 호출 실패 시 네트워크 오류 또는 HTTP 오류 발생
 */
export const createSavingsAccount = async (
  request: CreateSavingsAccountRequest,
): Promise<AccountCreationResponse> => {
  const response = await http.post<AccountCreationResponse>(
    '/v1/accounts',
    request,
  );
  return response.body!;
};

/**
 * 적금 계좌를 해지하는 API 함수
 *
 * 적금 계좌의 상태를 변경하여 해지 처리를 수행합니다.
 * 해지 후 계좌 상태가 변경되고 잔액이 지정된 계좌로 이체됩니다.
 *
 * @param accountId - 해지할 적금 계좌의 고유 식별자
 * @returns 해지 처리 결과가 담긴 SavingsAccountDetailResponse 객체
 * @throws API 호출 실패 시 네트워크 오류 또는 HTTP 오류 발생
 */
export const terminateSavingsAccount = async (
  accountId: string,
): Promise<SavingsAccountDetailResponse> => {
  const response = await http.patch<SavingsAccountDetailResponse>(
    `/v1/accounts/id/${accountId}/status`,
    {
      status: 'CLOSED',
    },
    {
      headers: {
        'Content-Type': 'application/json',
      },
    },
  );

  return response.body!;
};
