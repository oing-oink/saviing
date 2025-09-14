import { QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { queryClient } from '@/app/query/client';
import type { PropsWithChildren } from 'react';

/**
 * 앱 전체에서 데이터 캐싱 및 상태 관리를 일관되게 처리합니다.
 *
 * @param children - 앱 전체를 감싸는 React children
 * @returns QueryClientProvider로 감싼 children
 */
const QueryProvider = ({ children }: PropsWithChildren) => {
  return (
    <QueryClientProvider client={queryClient}>
      {children}
      {import.meta.env.DEV && <ReactQueryDevtools />}
    </QueryClientProvider>
  );
};

export default QueryProvider;
