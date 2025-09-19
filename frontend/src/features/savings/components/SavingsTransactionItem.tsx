import type { TransactionDisplayData } from '@/features/savings/types/savingsTypes';
import { Card, CardContent } from '@/shared/components/ui/card';

interface SavingsTransactionItemProps {
  transaction: TransactionDisplayData;
}

const SavingsTransactionItem = ({
  transaction,
}: SavingsTransactionItemProps) => {
  const { direction, amount, postedAt, description, balance } = transaction;

  // 입금/출금 구분
  // TODO: 이름 수정
  const isCredit = direction === 'DEBIT';

  // 날짜 포맷팅
  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');

    return `${month}.${day} ${hours}:${minutes}`;
  };

  return (
    <Card>
      <CardContent>
        <div className="flex items-center justify-between">
          <div className="flex-1">
            <p className="text-sm font-medium text-gray-900">{description}</p>
            <p className="mt-1 text-xs text-gray-500">{formatDate(postedAt)}</p>
          </div>

          <div className="text-right">
            <p
              className={`text-sm font-semibold ${
                isCredit ? 'text-red-600' : 'text-blue-600'
              }`}
            >
              {isCredit ? '-' : '+'}
              {(amount || 0).toLocaleString()}원
            </p>
            <p className="mt-1 text-xs text-gray-400">
              잔액: {(balance || 0).toLocaleString()}원
            </p>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default SavingsTransactionItem;
