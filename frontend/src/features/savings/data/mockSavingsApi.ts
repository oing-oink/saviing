import type { ApiSuccessResponse } from '@/shared/types/api';
import type {
  SavingsAccountData,
  TransactionData,
  GetTransactionsParams,
} from '@/features/savings/types/savingsTypes';

/**
 * 개발 및 테스트용 모의 적금 계좌 데이터
 *
 * 실제 API 응답과 동일한 구조의 데이터를 제공합니다.
 */
export const mockSavingsAccountData: SavingsAccountData = {
  accountId: 1,
  accountNumber: '11012345678901234',
  customerId: 1001,
  product: {
    productId: 1,
    productName: '자유입출금통장',
    productCode: 'FREE_CHECKING',
    productCategory: 'DEMAND_DEPOSIT',
    description: '언제든지 자유롭게 입출금이 가능한 통장',
  },
  compoundingType: 'SIMPLE',
  status: 'ACTIVE',
  openedAt: '2024-01-15T09:30:00Z',
  closedAt: '2024-12-31T17:00:00Z',
  lastAccrualTs: '2024-01-15T23:59:59Z',
  lastRateChangeAt: '2024-01-15T09:30:00Z',
  createdAt: '2024-01-15T09:30:00Z',
  updatedAt: '2024-01-15T09:30:00Z',
  balance: 500000,
  interestAccrued: 1250.5,
  baseRate: 250,
  bonusRate: 50,
  savings: {
    maturityWithdrawalAccount: '11012345678901234',
    targetAmount: 1000000,
    termPeriod: 12,
    termPeriodUnit: 'WEEKS',
    maturityDate: '2024-12-31',
  },
};

/**
 * 개발 및 테스트용 모의 거래 내역 데이터
 */
export const mockTransactionsData: TransactionData[] = [
  {
    transactionId: 1,
    accountId: 1,
    transactionType: 'TRANSFER_IN',
    direction: 'DEBIT',
    amount: 100000,
    valueDate: '2024-01-15',
    postedAt: '2024-01-15T14:30:00Z',
    status: 'POSTED',
    relatedTransactionId: 2,
    description: '월급 입금',
    createdAt: '2024-01-15T14:30:00Z',
    updatedAt: '2024-01-15T14:30:00Z',
  },
  {
    transactionId: 2,
    accountId: 1,
    transactionType: 'TRANSFER_OUT',
    direction: 'CREDIT',
    amount: 50000,
    valueDate: '2024-01-14',
    postedAt: '2024-01-14T10:15:00Z',
    status: 'POSTED',
    relatedTransactionId: 1,
    description: '생활비 출금',
    createdAt: '2024-01-14T10:15:00Z',
    updatedAt: '2024-01-14T10:15:00Z',
  },
  {
    transactionId: 3,
    accountId: 1,
    transactionType: 'TRANSFER_IN',
    direction: 'DEBIT',
    amount: 200000,
    valueDate: '2024-01-13',
    postedAt: '2024-01-13T16:45:00Z',
    status: 'POSTED',
    relatedTransactionId: 4,
    description: '적금 입금',
    createdAt: '2024-01-13T16:45:00Z',
    updatedAt: '2024-01-13T16:45:00Z',
  },
  {
    transactionId: 4,
    accountId: 1,
    transactionType: 'TRANSFER_OUT',
    direction: 'CREDIT',
    amount: 30000,
    valueDate: '2024-01-12',
    postedAt: '2024-01-12T12:20:00Z',
    status: 'POSTED',
    relatedTransactionId: 3,
    description: '카페 결제',
    createdAt: '2024-01-12T12:20:00Z',
    updatedAt: '2024-01-12T12:20:00Z',
  },
  {
    transactionId: 5,
    accountId: 1,
    transactionType: 'TRANSFER_IN',
    direction: 'DEBIT',
    amount: 150000,
    valueDate: '2024-01-11',
    postedAt: '2024-01-11T09:00:00Z',
    status: 'POSTED',
    relatedTransactionId: 6,
    description: '용돈 입금',
    createdAt: '2024-01-11T09:00:00Z',
    updatedAt: '2024-01-11T09:00:00Z',
  },
];

/**
 * 적금 계좌 정보 조회 API를 시뮬레이션하는 모의 함수
 *
 * 실제 네트워크 지연을 시뮬레이션하여 실제 API와 유사한 동작을 제공합니다.
 * 개발 환경에서 백엔드 API 없이도 프론트엔드 개발을 진행할 수 있게 해줍니다.
 *
 * @param accountId - 조회할 적금 계좌의 ID
 * @returns Promise로 래핑된 API 성공 응답 객체
 * @throws 네트워크 오류 등은 시뮬레이션하지 않음
 */
export const mockGetSavingsAccount = async (
  accountId: string,
): Promise<ApiSuccessResponse<SavingsAccountData>> => {
  // 실제 네트워크 지연 시뮬레이션
  await new Promise(resolve => setTimeout(resolve, 500));

  // accountId에 따라 다른 데이터를 반환할 수 있도록 설정
  const mockData = {
    ...mockSavingsAccountData,
    accountId: parseInt(accountId),
  };

  return {
    success: true,
    status: 0,
    body: mockData,
  };
};

/**
 * 거래 내역 조회 API를 시뮬레이션하는 모의 함수 (무한 스크롤용)
 *
 * 실제 네트워크 지연과 페이지네이션을 시뮬레이션하여 실제 API와 유사한 동작을 제공합니다.
 * 올리브영 방식을 참고하여 페이지별로 데이터를 분할하여 반환합니다.
 *
 * @param accountId - 조회할 적금 계좌의 ID
 * @param params - 페이지네이션 파라미터 (page, size)
 * @returns Promise로 래핑된 API 성공 응답 객체
 * @throws 네트워크 오류 등은 시뮬레이션하지 않음
 */
export const mockGetSavingsTransactions = async (
  _accountId: string,
  params: GetTransactionsParams,
): Promise<ApiSuccessResponse<TransactionData[]>> => {
  // 실제 네트워크 지연 시뮬레이션
  await new Promise(resolve => setTimeout(resolve, 500));

  const { page, size } = params;
  const startIndex = (page - 1) * size;
  const endIndex = startIndex + size;

  // 더 많은 테스트 데이터를 위해 기본 데이터를 반복하여 확장
  const expandedTransactions: TransactionData[] = [];
  for (let i = 0; i < 50; i++) {
    // 총 250개의 거래 내역 생성
    mockTransactionsData.forEach((transaction, index) => {
      expandedTransactions.push({
        ...transaction,
        transactionId: i * mockTransactionsData.length + index + 1,
        amount: transaction.amount + i * 1000, // 금액 변화를 위해 조금씩 다르게
        description: `${transaction.description} #${i + 1}`,
        postedAt: new Date(
          Date.now() - i * 24 * 60 * 60 * 1000 - index * 60 * 60 * 1000,
        ).toISOString(),
      });
    });
  }

  // 최신 날짜순 정렬 (postedAt 기준 내림차순)
  expandedTransactions.sort(
    (a, b) => new Date(b.postedAt).getTime() - new Date(a.postedAt).getTime(),
  );

  // 페이지에 해당하는 데이터 슬라이싱
  const paginatedTransactions = expandedTransactions.slice(
    startIndex,
    endIndex,
  );

  return {
    success: true,
    status: 0,
    body: paginatedTransactions,
  };
};
