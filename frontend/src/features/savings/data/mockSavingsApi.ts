import type { ApiSuccessResponse } from '@/shared/types/api';
import type { SavingsAccountData } from '@/features/savings/types/savingsTypes';

/**
 * 개발 및 테스트용 모의 적금 계좌 데이터
 *
 * 실제 API 응답과 동일한 구조의 데이터를 제공합니다.
 */
export const mockSavingsAccountData: SavingsAccountData = {
  accountId: 1,
  accountNumber: "11012345678901234",
  customerId: 1001,
  product: {
    productId: 1,
    productName: "자유입출금통장",
    productCode: "FREE_CHECKING",
    productCategory: "DEMAND_DEPOSIT",
    description: "언제든지 자유롭게 입출금이 가능한 통장"
  },
  compoundingType: "SIMPLE",
  status: "ACTIVE",
  openedAt: "2024-01-15T09:30:00Z",
  closedAt: "2024-12-31T17:00:00Z",
  lastAccrualTs: "2024-01-15T23:59:59Z",
  lastRateChangeAt: "2024-01-15T09:30:00Z",
  createdAt: "2024-01-15T09:30:00Z",
  updatedAt: "2024-01-15T09:30:00Z",
  balance: 500000,
  interestAccrued: 1250.5,
  baseRate: 250,
  bonusRate: 50,
  savings: {
    maturityWithdrawalAccount: "11012345678901234",
    targetAmount: 1000000,
    termPeriod: 12,
    termPeriodUnit: "WEEKS",
    maturityDate: "2024-12-31"
  }
};

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
  accountId: string
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