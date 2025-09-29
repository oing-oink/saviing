import type { FallbackProps } from 'react-error-boundary';

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

export default ErrorFallback;
