import { type ReactNode } from 'react';

const AppLayout = ({ children }: { children: ReactNode }) => {
  return (
    <div className="flex h-screen items-start justify-center bg-gray-200">
      <div className="safeArea mx-auto flex h-screen w-full max-w-md flex-col bg-white shadow-md">
        {children}
      </div>
    </div>
  );
};

export default AppLayout;
