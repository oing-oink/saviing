import { useEffect } from 'react';
import { useInView } from 'react-intersection-observer';
import { useSavingsTransactionsDisplay } from '@/features/savings/query/useSavingsQuery';
import SavingsTransactionItem from './SavingsTransactionItem';
import SavingsTransactionSkeleton from './SavingsTransactionSkeleton';
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from '@/shared/components/ui/card';

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

  // 올리브영 방식: inView가 true가 되면 다음 페이지 로드
  useEffect(() => {
    if (inView && hasNextPage) {
      fetchNextPage();
    }
  }, [inView, hasNextPage, fetchNextPage]);

  if (isError) {
    return (
      <Card className="mt-4">
        <CardContent className="p-6">
          <p className="text-center text-red-500">
            거래 내역을 불러오는 중 오류가 발생했습니다.
          </p>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className="mt-4">
      <CardHeader>
        <CardTitle className="text-lg">거래 내역</CardTitle>
      </CardHeader>
      <CardContent className="p-0">
        {/* 첫 로딩 시 스켈레톤 표시 */}
        {isLoading ? (
          <div className="px-4 pb-4">
            <SavingsTransactionSkeleton />
          </div>
        ) : (
          <>
            {/* 거래 내역이 없는 경우 */}
            {transactions.length === 0 ? (
              <div className="p-8 text-center">
                <p className="text-gray-500">거래 내역이 없습니다.</p>
              </div>
            ) : (
              <>
                {/* 거래 내역 목록 */}
                {transactions.map(transaction => (
                  <SavingsTransactionItem
                    key={transaction.transactionId}
                    transaction={transaction}
                  />
                ))}

                {/* 무한 스크롤 트리거 또는 로딩 스켈레톤 */}
                {isFetchingNextPage ? (
                  <div className="px-4 pb-4">
                    <SavingsTransactionSkeleton />
                  </div>
                ) : (
                  // 올리브영 방식: 다음 페이지가 있을 때만 ref 요소 표시
                  hasNextPage && <div ref={ref} className="h-4" />
                )}
              </>
            )}
          </>
        )}
      </CardContent>
    </Card>
  );
};

export default SavingsTransactionList;
