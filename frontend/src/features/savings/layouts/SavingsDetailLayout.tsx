import DetailTopBar from '@/features/savings/components/DetailTopBar';
import { Outlet } from 'react-router-dom';
import { PageScrollArea } from '@/shared/components/layout/PageScrollArea';
import { useScrollReset } from '@/shared/utils/scrollUtils';

const SavingsDetailLayout = () => {
  // 라우터 이동 시 자동 스크롤 리셋
  useScrollReset();

  return (
<<<<<<< HEAD
    <div className="saving mx-auto min-h-dvh w-full max-w-md bg-secondary">
      <DetailTopBar title={title} backMode="entry" />
      <div>
=======
    <div className="saving mx-auto flex h-dvh w-full max-w-md flex-col bg-secondary">
      <DetailTopBar />
      <PageScrollArea className="h-[calc(100dvh-56px)]">
>>>>>>> 7dee7b84714a778a7f45647f0eedf63ec6ce8d48
        <Outlet />
      </PageScrollArea>
    </div>
  );
};

export default SavingsDetailLayout;
