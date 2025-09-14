import { type ReactNode } from 'react';

const AppLayout = ({ children }: { children: ReactNode }) => {
  return (
    <div className="flex h-dvh touch-none items-start justify-center overflow-hidden bg-gray-200">
      <div className="safeArea mx-auto flex h-dvh w-full max-w-md flex-col overflow-hidden bg-white shadow-md">
        {children}
      </div>
    </div>
  );
};

export default AppLayout;
