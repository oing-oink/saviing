import type { ReactNode } from 'react';
import StepHeader from '@/features/savings/components/StepHeader';

interface FunnelLayoutProps {
  children: ReactNode;
}

const FunnelLayout = ({ children }: FunnelLayoutProps) => {
  return (
    // 프로그레스 바 높이만큼 빼줘서 불필요한 스크롤 방지
    <div className="flex min-h-[calc(100vh-0.5rem)] flex-col bg-white">
      <StepHeader />
      {children}
    </div>
  );
};

export default FunnelLayout;