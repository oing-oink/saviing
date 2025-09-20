import CheckingAccountWalletCard from '@/features/savings/components/CheckingAccountWalletCard';
import SavingsAccountWalletCard from '@/features/savings/components/SavingAccountWalletCard';
import WalletButton from '@/features/savings/components/WalletButton';
import { useWalletButtons } from '@/shared/hooks/useWalletButtons';
import { useAccountsList } from '@/features/savings/query/useSavingsQuery';

const WalletPage = () => {
  const { buttons } = useWalletButtons();
  const { data: accounts, isLoading, error } = useAccountsList();

  // 계좌 유형별로 분리
  const savingsAccount = accounts?.find(
    account => account.product.productCategory === 'INSTALLMENT_SAVINGS',
  );
  const demandAccount = accounts?.find(
    account => account.product.productCategory === 'DEMAND_DEPOSIT',
  );

  // 로딩 상태
  if (isLoading) {
    return (
      <div className="px-5 py-4">
        <div className="flex flex-col items-center gap-4">
          <div className="text-center">계좌 정보를 불러오는 중...</div>
        </div>
      </div>
    );
  }

  // 에러 상태
  if (error) {
    return (
      <div className="px-5 py-4">
        <div className="flex flex-col items-center gap-4">
          <div className="text-center text-red-500">
            계좌 정보를 불러오는 데 실패했습니다.
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="px-5 py-4">
      <div className="flex flex-col items-center gap-4">
        {savingsAccount && (
          <SavingsAccountWalletCard account={savingsAccount} />
        )}
        {demandAccount && (
          <CheckingAccountWalletCard account={demandAccount} />
        )}
        <div className="flex w-full gap-4">
          {buttons.map((btn, i) => (
            <WalletButton
              key={i}
              icon={btn.icon}
              label={btn.label}
              onClick={btn.onClick}
            />
          ))}
        </div>
      </div>
    </div>
  );
};

export default WalletPage;
