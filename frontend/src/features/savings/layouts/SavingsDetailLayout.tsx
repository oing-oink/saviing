import DetailTopBar from '@/features/savings/components/DetailTopBar';
import { Outlet } from 'react-router-dom';
import { PageScrollArea } from '@/shared/components/layout/PageScrollArea';
import { useScrollReset } from '@/shared/utils/scrollUtils';

const SavingsDetailLayout = () => {
  // 라우터 이동 시 자동 스크롤 리셋
  useScrollReset();

  return (
    <div className="saving mx-auto flex h-dvh w-full max-w-md flex-col bg-secondary">
      <DetailTopBar />
      <PageScrollArea className="h-[calc(100dvh-56px)]">
        <Outlet />
      </PageScrollArea>
    </div>
  );
};

export default SavingsDetailLayout;
