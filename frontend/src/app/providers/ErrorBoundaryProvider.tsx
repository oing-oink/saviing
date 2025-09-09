import type { PropsWithChildren } from 'react';
import { ErrorBoundary } from 'react-error-boundary';
import ErrorFallback from '@/shared/components/error/ErrorFallback';

/**
 * React 컴포넌트 렌더링 중 발생하는 에러를 잡고
 * Fallback UI를 제공하여 앱이 완전히 깨지는 것을 방지합니다.
 *
 * @param children - 앱 전체를 감싸는 React children
 * @returns ErrorBoundary로 감싼 children
 */
const ErrorBoundaryProvider = ({ children }: PropsWithChildren) => {
  return (
    <ErrorBoundary FallbackComponent={ErrorFallback}>{children}</ErrorBoundary>
  );
};

export default ErrorBoundaryProvider;
