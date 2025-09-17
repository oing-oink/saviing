import { useQuery } from '@tanstack/react-query';
import { getSavingsAccount } from '@/features/savings/api/savingsApi';
import { savingsKeys } from '@/features/savings/query/savingsKeys';
import type { SavingsDisplayData } from '@/features/savings/types/savingsTypes';

/**
 * 적금 계좌 상세 정보를 조회하는 React Query 훅
 *
 * @param accountId - 조회할 적금 계좌의 ID
 * @returns 적금 계좌 상세 정보 쿼리 결과
 */
export const useSavingsAccount = (accountId: string) => {
  return useQuery({
    queryKey: savingsKeys.detail(accountId),
    queryFn: () => getSavingsAccount(accountId),
  });
};

/**
 * 적금 상세 페이지에서 사용할 데이터를 가공하여 반환하는 훅
 *
 * @param accountId - 조회할 적금 계좌의 ID
 * @returns 상세 페이지에 필요한 가공된 적금 정보
 */
export const useSavingsDisplayData = (accountId: string) => {
  const query = useSavingsAccount(accountId);

  const displayData: SavingsDisplayData | undefined = query.data
    ? {
        accountNumber: query.data.accountNumber,
        productName: query.data.product.productName,
        interestRate: (query.data.baseRate + query.data.bonusRate) / 100.0,
        targetAmount: query.data.savings.targetAmount,
        maturityDate: query.data.savings.maturityDate,
        balance: query.data.balance,
      }
    : undefined;

  return {
    ...query,
    data: displayData,
  };
};
