import { type ReactNode } from 'react';
import { useGlobalGameBackground } from '@/features/game/shared/components/GlobalGameBackground';

const AppLayout = ({ children }: { children: ReactNode }) => {
  const { isGameBackground } = useGlobalGameBackground();

  const outerClassName = `flex h-screen items-start justify-center ${
    isGameBackground ? 'bg-transparent' : 'bg-gray-200'
  }`;
  const innerClassName = `safeArea mx-auto flex h-screen w-full max-w-md flex-col ${
    isGameBackground ? 'bg-transparent shadow-none' : 'bg-white shadow-md'
  }`;

  return (
    <div className={outerClassName}>
      <div className={innerClassName}>
        {children}
      </div>
    </div>
  );
};

export default AppLayout;
