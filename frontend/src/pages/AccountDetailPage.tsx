import { useState, useEffect, useRef } from 'react';
import { useParams } from 'react-router-dom';
import { useSavingsAccount } from '@/features/savings/query/useSavingsQuery';
import { useScroll } from '@/shared/hooks/useScroll';
import AccountDetailCard from '@/features/savings/components/AccountDetailCard';
import SavingsTransactionList from '@/features/savings/components/SavingsTransactionList';

const AccountDetailPage = () => {
  const { accountId } = useParams<{ accountId: string }>();
  const {
    data: accountData,
    isLoading,
    error,
  } = useSavingsAccount(accountId || '');

  const { scrollY } = useScroll();
  const [isSticky, setIsSticky] = useState(false);
  const balanceSectionRef = useRef<HTMLDivElement>(null);

  // 현재 잔액 섹션이 화면 상단에 닿을 때 sticky 활성화
  useEffect(() => {
    if (balanceSectionRef.current && accountData) {
      const rect = balanceSectionRef.current.getBoundingClientRect();
      // progressSectionRef가 DetailTopBar(64px) 위치에 도달하면 sticky 활성화
      const shouldBeSticky = rect.top <= 64;

      setIsSticky(shouldBeSticky);
    }
  }, [scrollY, accountData]);

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="space-y-4">
        <AccountDetailCard
          data={accountData}
          isLoading={isLoading}
          error={error}
          isSticky={isSticky}
        />

        {/* 계좌 데이터가 로드된 후에만 거래 내역 표시 */}
        {accountId && !isLoading && !error && (
          <SavingsTransactionList accountId={accountId} />
        )}
      </div>
    </>
  );
};

export default AccountDetailPage;
