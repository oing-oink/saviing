import { useEffect, type ReactNode } from 'react';
import clsx from 'clsx';
import { useGlobalGameBackground } from '@/features/game/shared/components/GlobalGameBackground';

interface GameBackgroundLayoutProps {
  children: ReactNode;
  className?: string;
}

const GameBackgroundLayout = ({
  children,
  className,
}: GameBackgroundLayoutProps) => {
  const { showGameBackground, hideGameBackground } = useGlobalGameBackground();

  useEffect(() => {
    showGameBackground();
    return () => {
      hideGameBackground();
    };
  }, [showGameBackground, hideGameBackground]);

  return (
    <div className={clsx('flex h-full w-full flex-1 flex-col', className)}>
      {children}
    </div>
  );
};

export default GameBackgroundLayout;
