import { useOAuthCallback } from '@/features/auth/hooks/useOAuthCallback';

const AuthCallbackPage = () => {
  const { isLoading, error, isSuccess } = useOAuthCallback();

  if (isLoading) {
    return (
      <div className="flex min-h-screen flex-col items-center justify-center bg-gradient-to-b from-blue-50 to-white">
        <div className="flex flex-col items-center space-y-4">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-blue-500 border-t-transparent"></div>
          <p className="font-pretendard text-lg text-gray-700">로그인 처리중...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex min-h-screen flex-col items-center justify-center bg-gradient-to-b from-blue-50 to-white">
        <div className="flex flex-col items-center space-y-4">
          <div className="rounded-lg bg-red-50 p-4">
            <p className="font-pretendard text-red-600">❌ {error}</p>
          </div>
          <button
            onClick={() => window.location.href = '/login'}
            className="rounded-lg bg-blue-500 px-4 py-2 font-pretendard text-white hover:bg-blue-600"
          >
            로그인 페이지로 돌아가기
          </button>
        </div>
      </div>
    );
  }

  if (isSuccess) {
    return (
      <div className="flex min-h-screen flex-col items-center justify-center bg-gradient-to-b from-blue-50 to-white">
        <div className="flex flex-col items-center space-y-4">
          <div className="rounded-lg bg-green-50 p-4">
            <p className="font-pretendard text-green-600">✅ 로그인 성공! 홈으로 이동중...</p>
          </div>
        </div>
      </div>
    );
  }

  return null;
};

export default AuthCallbackPage;