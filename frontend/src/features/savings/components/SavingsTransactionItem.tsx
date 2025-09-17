import type { TransactionDisplayData } from '@/features/savings/types/savingsTypes';

interface SavingsTransactionItemProps {
  transaction: TransactionDisplayData;
}

const SavingsTransactionItem = ({
  transaction,
}: SavingsTransactionItemProps) => {
  const { direction, amount, postedAt, description } = transaction;

  // 입금/출금 구분
  const isDeposit = direction === 'DEBIT';

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
    <div className="flex items-center justify-between border-b border-gray-100 p-4">
      <div className="flex-1">
        <p className="text-sm font-medium text-gray-900">{description}</p>
        <p className="mt-1 text-xs text-gray-500">{formatDate(postedAt)}</p>
      </div>

      <div className="text-right">
        <p
          className={`text-sm font-semibold ${
            isDeposit ? 'text-blue-600' : 'text-red-600'
          }`}
        >
          {isDeposit ? '+' : '-'}
          {amount.toLocaleString()}원
        </p>
        <p className="mt-1 text-xs text-gray-400">
          {isDeposit ? '입금' : '출금'}
        </p>
      </div>
    </div>
  );
};

export default SavingsTransactionItem;
