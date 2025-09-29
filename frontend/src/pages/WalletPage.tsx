import CheckingAccountWalletCard from '@/features/savings/components/CheckingAccountWalletCard';
import SavingsAccountWalletCard from '@/features/savings/components/SavingAccountWalletCard';
import WalletButton from '@/features/savings/components/WalletButton';
import { useWalletButtons } from '@/shared/hooks/useWalletButtons';
import { useAccountsList } from '@/features/savings/query/useSavingsQuery';
import { useGlobalGameBackground } from '@/features/game/shared/components/GlobalGameBackground';
import { useEffect } from 'react';
import type { SavingsAccountData } from '@/features/savings/types/savingsTypes';

const WalletPage = () => {
  const { buttons } = useWalletButtons();
  const { data: accounts, isLoading, error } = useAccountsList();
  const { hideGameBackground } = useGlobalGameBackground();

  useEffect(() => {
    hideGameBackground();
  }, [hideGameBackground]);

  const savingsAccounts = (accounts ?? []).filter(
    account => account.product.productCategory === 'INSTALLMENT_SAVINGS',
  );
  const otherAccounts = (accounts ?? []).filter(
    account => account.product.productCategory !== 'INSTALLMENT_SAVINGS',
  );
  const hasAnyAccount = (accounts?.length ?? 0) > 0;

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

  const renderAccountCard = (account: SavingsAccountData) => {
    if (account.product.productCategory === 'INSTALLMENT_SAVINGS') {
      return (
        <SavingsAccountWalletCard
          key={`savings-${account.accountId}`}
          account={account}
        />
      );
    }

    return (
      <CheckingAccountWalletCard
        key={`account-${account.accountId}`}
        account={account}
      />
    );
  };

  return (
    <div className="px-5 py-4">
      <div className="flex flex-col items-center gap-4">
        {hasAnyAccount ? (
          <>
            {savingsAccounts.map(renderAccountCard)}
            {otherAccounts.map(renderAccountCard)}
          </>
        ) : (
          <div className="w-full max-w-md rounded-2xl bg-white p-6 pb-12 font-pretendard shadow">
            <h2 className="mb-4 font-medium text-gray-500">내 계좌</h2>
            <div className="text-center text-gray-500">
              <p>등록된 계좌가 없습니다.</p>
              <p className="mt-1 text-sm">새 계좌를 개설해보세요.</p>
            </div>
          </div>
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
