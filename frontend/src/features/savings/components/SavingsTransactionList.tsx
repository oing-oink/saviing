import { useEffect } from 'react';
import { useInView } from 'react-intersection-observer';
import { useSavingsTransactionsDisplay } from '@/features/savings/query/useSavingsQuery';
import SavingsTransactionItem from './SavingsTransactionItem';
import SavingsTransactionSkeleton from './SavingsTransactionSkeleton';

interface SavingsTransactionListProps {
  accountId: string;
}

const SavingsTransactionList = ({ accountId }: SavingsTransactionListProps) => {
  const {
    data: transactions,
    isLoading,
    isError,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  } = useSavingsTransactionsDisplay(accountId);

  const { ref, inView } = useInView();

  // inView가 true가 되면 다음 페이지 로드
  useEffect(() => {
    if (inView && hasNextPage) {
      fetchNextPage();
    }
  }, [inView, hasNextPage, fetchNextPage]);

  if (isError) {
    return (
      <div className="mt-4">
        <h2 className="mb-4 text-lg font-semibold">거래 내역</h2>
        <div className="rounded-lg bg-red-50 p-6 text-center">
          <p className="text-red-500">
            거래 내역을 불러오는 중 오류가 발생했습니다.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="m-8 mt-10">
      <h2 className="mb-4 text-lg font-semibold">거래 내역</h2>

      {/* 첫 로딩 시 스켈레톤 표시 */}
      {isLoading ? (
        <SavingsTransactionSkeleton />
      ) : (
        <>
          {/* 거래 내역이 없는 경우 */}
          {transactions.length === 0 ? (
            <div className="rounded-lg bg-gray-50 p-8 text-center">
              <p className="text-gray-500">거래 내역이 없습니다.</p>
            </div>
          ) : (
            <div className="space-y-3">
              {/* 거래 내역 목록 */}
              {transactions.map(transaction => (
                <SavingsTransactionItem
                  key={transaction.transactionId}
                  transaction={transaction}
                />
              ))}

              {/* 무한 스크롤 트리거 또는 로딩 스켈레톤 */}
              {isFetchingNextPage ? (
                <SavingsTransactionSkeleton />
              ) : (
                // 다음 페이지가 있을 때만 ref 요소 표시
                hasNextPage && <div ref={ref} className="h-4" />
              )}
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default SavingsTransactionList;
