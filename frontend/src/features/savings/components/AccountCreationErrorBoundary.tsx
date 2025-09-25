import type { PropsWithChildren } from 'react';
import { ErrorBoundary } from 'react-error-boundary';
import { useNavigate } from 'react-router-dom';
import { Button } from '@/shared/components/ui/button';
import { PAGE_PATH } from '@/shared/constants/path';

interface ErrorFallbackProps {
  error: Error;
  resetErrorBoundary: () => void;
}

/**
 * AccountCreationFunnel 전용 에러 폴백 컴포넌트
 * 에러 메시지를 그대로 표시하고 사용자 액션을 제공합니다.
 */
const AccountCreationErrorFallback = ({
  error,
  resetErrorBoundary,
}: ErrorFallbackProps) => {
  const navigate = useNavigate();

  const handleGoHome = () => {
    navigate(PAGE_PATH.WALLET);
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50 px-4">
      <div className="w-full max-w-md rounded-lg bg-white p-6 text-center shadow-lg">
        {/* 에러 아이콘 */}
        <div className="mb-4">
          <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-red-100">
            <svg
              className="h-8 w-8 text-red-600"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z"
              />
            </svg>
          </div>
        </div>

        {/* 에러 제목 */}
        <h2 className="mb-2 text-lg font-semibold text-gray-900">
          계좌 개설 중 오류가 발생했습니다
        </h2>

        {/* 에러 메시지 */}
        <p className="mb-6 text-sm leading-relaxed text-gray-600">
          {error.message || '예기치 못한 오류가 발생했습니다.'}
        </p>

        {/* 액션 버튼들 */}
        <div className="flex flex-col gap-3">
          <Button
            onClick={resetErrorBoundary}
            className="w-full bg-primary text-white hover:bg-primary/90"
          >
            다시 시도
          </Button>
          <Button onClick={handleGoHome} variant="outline" className="w-full">
            홈으로 돌아가기
          </Button>
        </div>
      </div>
    </div>
  );
};

/**
 * AccountCreationFunnel을 감싸는 ErrorBoundary 컴포넌트
 */
const AccountCreationErrorBoundary = ({ children }: PropsWithChildren) => {
  return (
    <ErrorBoundary FallbackComponent={AccountCreationErrorFallback}>
      {children}
    </ErrorBoundary>
  );
};

export default AccountCreationErrorBoundary;
