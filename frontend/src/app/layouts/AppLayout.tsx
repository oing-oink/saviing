import { type ReactNode } from 'react';

export const AppLayout = ({ children }: { children: ReactNode }) => {
  return (
    <div className="flex min-h-screen items-start justify-center bg-gray-200">
      <div className="safeArea mx-auto flex min-h-screen w-full max-w-md flex-col bg-white shadow-md">
        {children}
      </div>
    </div>
  );
};
