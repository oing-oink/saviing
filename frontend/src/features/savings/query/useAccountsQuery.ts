import { useQuery } from '@tanstack/react-query';
import { getAllAccounts } from '@/features/savings/api/savingsApi';
import { savingsKeys } from '@/features/savings/query/savingsKeys';
import type { SavingsAccountData } from '@/features/savings/types/savingsTypes';

/**
 * 고객의 모든 계좌 목록을 조회하는 React Query 훅
 *
 * 적금 계좌와 입출금 계좌를 모두 포함한 계좌 목록을 반환합니다.
 *
 * @returns 계좌 목록 쿼리 결과
 */
export const useAllAccounts = () => {
  return useQuery({
    queryKey: savingsKeys.accountsList(),
    queryFn: getAllAccounts,
  });
};

/**
 * 적금계좌 연결 상태를 확인하는 커스텀 훅
 *
 * 고객의 계좌 목록에서 적금계좌(FREE_SAVINGS)의 존재 여부를 확인하고,
 * 적금계좌가 있으면 이자율을 표시하고 없으면 생성 버튼을 표시합니다.
 *
 * @returns 적금계좌 연결 상태 및 계좌 정보
 */
export const useAccountConnection = () => {
  const { data: accounts, isLoading, error } = useAllAccounts();

  const savingsAccount: SavingsAccountData | null =
    accounts?.find(account => account.product.productCode === 'FREE_SAVINGS') ||
    null;

  const hasSavingsAccount = savingsAccount !== null;

  return {
    isLoading,
    error,
    savingsAccount,
    hasSavingsAccount,
  };
};
