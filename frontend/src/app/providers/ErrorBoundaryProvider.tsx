import type { PropsWithChildren } from 'react';
import { ErrorBoundary, type FallbackProps } from 'react-error-boundary';

/**
 * 에러 발생 시 사용자에게 보여줄 UI.
 *
 * @param error - 발생한 에러 객체
 * @param resetErrorBoundary - 에러 상태를 초기화하는 함수
 * @returns 에러 메시지와 재시도 버튼을 렌더링
 */
const ErrorFallback = ({ error, resetErrorBoundary }: FallbackProps) => {
  return (
    <div className="p-4 text-red-600">
      <h2>에러 발생</h2>
      <pre>{error.message}</pre>
      <button onClick={resetErrorBoundary}>다시 시도</button>
    </div>
  );
};

/**
 * React 컴포넌트 렌더링 중 발생하는 에러를 잡고
 * Fallback UI를 제공하여 앱이 완전히 깨지는 것을 방지합니다.
 *
 * @param children - 앱 전체를 감싸는 React children
 * @returns ErrorBoundary로 감싼 children
 */
export const ErrorBoundaryProvider = ({ children }: PropsWithChildren) => {
  return (
    <ErrorBoundary FallbackComponent={ErrorFallback}>{children}</ErrorBoundary>
  );
};
