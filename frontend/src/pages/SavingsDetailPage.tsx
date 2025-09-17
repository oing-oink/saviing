import { useParams } from 'react-router-dom';
import { useSavingsDisplayData } from '@/features/savings/query/useSavingsQuery';
import SavingsDetailCard from '@/features/savings/components/SavingsDetailCard';
import SavingsTransactionList from '@/features/savings/components/SavingsTransactionList';

const SavingsDetailPage = () => {
  const { accountId } = useParams<{ accountId: string }>();
  const {
    data: savingsData,
    isLoading,
    error,
  } = useSavingsDisplayData(accountId!);

  return (
    <div className="space-y-4">
      <SavingsDetailCard
        data={savingsData}
        isLoading={isLoading}
        error={error}
      />

      {/* 적금 계좌 데이터가 로드된 후에만 거래 내역 표시 */}
      {accountId && !isLoading && !error && (
        <SavingsTransactionList accountId={accountId} />
      )}
    </div>
  );
};

export default SavingsDetailPage;
