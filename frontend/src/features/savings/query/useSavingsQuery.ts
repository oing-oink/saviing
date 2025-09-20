import { useQuery, useInfiniteQuery } from '@tanstack/react-query';
import {
  getSavingsAccount,
  getSavingsTransactions,
  getAllAccounts,
} from '@/features/savings/api/savingsApi';
import { savingsKeys } from '@/features/savings/query/savingsKeys';
import type {
  SavingsDisplayData,
  TransactionDisplayData,
} from '@/features/savings/types/savingsTypes';
import { useMemo } from 'react';

/**
 * 고객의 모든 계좌 목록을 조회하는 React Query 훅
 *
 * @returns 계좌 목록 쿼리 결과
 */
export const useAccountsList = () => {
  return useQuery({
    queryKey: savingsKeys.accountsList(),
    queryFn: () => getAllAccounts(),
  });
};

/**
 * 적금 계좌 상세 정보를 조회하는 React Query 훅
 *
 * @param accountId - 조회할 적금 계좌의 ID
 * @returns 적금 계좌 상세 정보 쿼리 결과
 */
export const useSavingsAccount = (accountId: string, options?: { enabled?: boolean }) => {
  return useQuery({
    queryKey: savingsKeys.detail(accountId),
    queryFn: () => getSavingsAccount(accountId),
    enabled: options?.enabled,
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
        createdAt: query.data.createdAt,
        balance: query.data.balance,
      }
    : undefined;

  return {
    ...query,
    data: displayData,
  };
};

/**
 * 적금 계좌의 거래 내역을 무한 스크롤로 조회하는 React Query 훅
 *
 * 올리브영 방식을 참고하여 useInfiniteQuery를 활용한 무한 스크롤을 구현합니다.
 * 페이지당 20개의 거래 내역을 가져오며, 최신 날짜순으로 정렬됩니다.
 *
 * @param accountId - 조회할 적금 계좌의 ID
 * @returns 무한 스크롤 거래 내역 쿼리 결과
 */
export const useSavingsTransactions = (accountId: string) => {
  const TRANSACTIONS_PER_PAGE = 20;

  return useInfiniteQuery({
    queryKey: savingsKeys.transactionsList(accountId),
    queryFn: ({ pageParam = 0 }) =>
      getSavingsTransactions(accountId, {
        page: pageParam,
        size: TRANSACTIONS_PER_PAGE,
      }),
    initialPageParam: 0,
    getNextPageParam: (lastPage, allPages) => {
      // 현재 로드된 총 아이템 수 계산
      const totalLoadedItems = allPages.flat().length;

      // Mock 데이터 제한(250개) 또는 페이지 크기보다 작으면 종료
      if (
        !lastPage ||
        lastPage.length < TRANSACTIONS_PER_PAGE ||
        totalLoadedItems >= 250
      ) {
        return undefined;
      }

      return allPages.length;
    },
    staleTime: 1000 * 60, // 1분
    gcTime: 1000 * 60 * 5, // 5분
    refetchOnMount: false,
    refetchOnReconnect: false,
    refetchOnWindowFocus: false,
  });
};

/**
 * 거래 내역 데이터를 UI용으로 가공하여 반환하는 훅
 *
 * 서버에서 받은 거래 내역 데이터에서 UI에 필요한 필드만 추출하고 변환합니다.
 * direction, amount, postedAt, description, balance 필드만 사용합니다.
 *
 * @param accountId - 조회할 적금 계좌의 ID
 * @returns 가공된 거래 내역 데이터와 무한 스크롤 상태
 */
export const useSavingsTransactionsDisplay = (accountId: string) => {
  const query = useSavingsTransactions(accountId);

  const transactions = useMemo(() => {
    if (!query.data) {
      return [];
    }

    // 모든 페이지의 거래 내역을 하나의 배열로 합치기
    const allTransactions = query.data.pages.flat();

    // UI용 데이터로 변환
    return allTransactions.map(
      (transaction): TransactionDisplayData => ({
        transactionId: transaction.transactionId,
        direction: transaction.direction,
        amount: transaction.amount,
        postedAt: transaction.postedAt,
        description: transaction.description,
        balance: transaction.balance || 0,
      }),
    );
  }, [query.data]);

  return {
    ...query,
    data: transactions,
  };
};
