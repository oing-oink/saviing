import { useParams } from 'react-router-dom';
import { useSavingsDisplayData } from '@/features/savings/query/useSavingsQuery';
import SavingsDetailCard from '@/features/savings/components/SavingsDetailCard';

const SavingsDetailPage = () => {
  const { accountId } = useParams<{ accountId: string }>();
  const {
    data: savingsData,
    isLoading,
    error,
  } = useSavingsDisplayData(accountId!);

  return (
    <div>
      <SavingsDetailCard
        data={savingsData}
        isLoading={isLoading}
        error={error}
      />
    </div>
  );
};

export default SavingsDetailPage;
