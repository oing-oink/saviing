import { useState, useEffect, useRef } from 'react';
import { useSavingsDisplayData } from '@/features/savings/query/useSavingsQuery';
import { useSavingsStore } from '@/features/savings/store/useSavingsStore';
import { useScroll } from '@/shared/hooks/useScroll';
import SavingsDetailCard from '@/features/savings/components/SavingsDetailCard';
import SavingsTransactionList from '@/features/savings/components/SavingsTransactionList';
import StickyBalance from '@/features/savings/components/StickyBalance';

const SavingsDetailPage = () => {
  const { currentAccountId } = useSavingsStore();
  const {
    data: savingsData,
    isLoading,
    error,
  } = useSavingsDisplayData(currentAccountId?.toString() || '');

  const { scrollY, scrollDirection } = useScroll();
  const [isSticky, setIsSticky] = useState(false);
  const balanceSectionRef = useRef<HTMLDivElement>(null);

  // 현재 잔액 섹션이 화면 상단에 닿을 때 sticky 활성화
  useEffect(() => {
    if (balanceSectionRef.current && savingsData) {
      const rect = balanceSectionRef.current.getBoundingClientRect();
      // progressSectionRef가 DetailTopBar(64px) 위치에 도달하면 sticky 활성화
      const shouldBeSticky = rect.top <= 64;

      setIsSticky(shouldBeSticky);
    }
  }, [scrollY, savingsData]);

  return (
    <>
      {/* Sticky Progress */}
      {savingsData && (
        <StickyBalance
          data={savingsData}
          isVisible={isSticky}
          scrollDirection={scrollDirection}
        />
      )}

      {/* 메인 컨텐츠 */}
      <div className="space-y-4">
        <SavingsDetailCard
          data={savingsData}
          isLoading={isLoading}
          error={error}
          progressSectionRef={balanceSectionRef}
          isSticky={isSticky}
        />

        {/* 적금 계좌 데이터가 로드된 후에만 거래 내역 표시 */}
        {currentAccountId && !isLoading && !error && (
          <SavingsTransactionList accountId={currentAccountId.toString()} />
        )}
      </div>
    </>
  );
};

export default SavingsDetailPage;
