import type { ReactNode } from 'react';
import StepHeader from '@/features/savings/components/StepHeader';

interface FunnelLayoutProps {
  children: ReactNode;
}

const FunnelLayout = ({ children }: FunnelLayoutProps) => {
  return (
    // 프로그레스 바 높이만큼 빼줘서 불필요한 스크롤 방지
    <div className="flex min-h-[calc(100vh-0.5rem)] flex-col bg-white">
      {/* 완전 고정된 헤더 */}
      <div className="fixed top-2 left-0 right-0 z-10 bg-white">
        <StepHeader />
      </div>
      {/* 스크롤 가능한 컨텐츠 영역 - 헤더와 하단 버튼 높이만큼 패딩 추가 */}
      <div className="flex-1 overflow-y-auto pt-16 pb-20">
        {children}
      </div>
    </div>
  );
};

export default FunnelLayout;
