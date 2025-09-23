import { type ReactNode } from 'react';
import clsx from 'clsx';
import { useGlobalGameBackground } from '@/features/game/shared/components/GlobalGameBackground';
import GameBackground from '@/features/game/shared/components/GameBackground';

const AppLayout = ({ children }: { children: ReactNode }) => {
  const { isGameBackground } = useGlobalGameBackground();

  const outerClassName = clsx(
    'flex h-screen items-start justify-center',
    isGameBackground ? 'bg-transparent' : 'bg-gray-200',
  );

  const innerClassName = clsx(
    'safeArea relative mx-auto flex h-screen w-full max-w-md flex-col overflow-hidden',
    isGameBackground ? 'bg-transparent shadow-none' : 'bg-white shadow-md',
  );

  return (
    <div className={outerClassName}>
      <div className={innerClassName}>
        {isGameBackground && (
          <div className="absolute inset-0 -z-10">
            <GameBackground />
          </div>
        )}
        <div className="relative flex h-full flex-1 flex-col">{children}</div>
      </div>
    </div>
  );
};

export default AppLayout;
