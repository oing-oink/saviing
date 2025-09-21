import type { ReactNode } from 'react';
import StepHeader from '@/features/savings/components/StepHeader';
import { PageScrollArea } from '@/shared/components/layout/PageScrollArea';
import { useScrollReset } from '@/shared/utils/scrollUtils';

interface FunnelLayoutProps {
  children: ReactNode;
}

const FunnelLayout = ({ children }: FunnelLayoutProps) => {
  // 스텝 변경 시 자동 스크롤 리셋
  useScrollReset();

  return (
    <div className="saving mx-auto flex h-dvh w-full max-w-md flex-col bg-white">
      <StepHeader />
      <PageScrollArea className="h-[calc(100dvh-56px)]">
        {children}
      </PageScrollArea>
    </div>
  );
};

export default FunnelLayout;
