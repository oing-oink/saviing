import { useNavigate } from 'react-router-dom';
import { useCustomerStore } from '@/features/auth/store/useCustomerStore';
import { PAGE_PATH } from '@/shared/constants/path';

const NotFoundPage = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useCustomerStore();

  const handleGoHome = () => {
    if (isAuthenticated) {
      navigate(PAGE_PATH.HOME);
    } else {
      navigate(PAGE_PATH.ONBOARDING);
    }
  };

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-gradient-to-b from-blue-50 to-white px-4">
      <div className="text-center">
        {/* 404 아이콘/이미지 */}
        <div className="mb-8">
          <div className="mx-auto mb-4 flex h-32 w-32 items-center justify-center rounded-full bg-gray-100">
            <span className="text-4xl font-bold text-gray-400">404</span>
          </div>
        </div>

        {/* 메시지 */}
        <h1 className="mb-4 text-2xl font-bold text-gray-800">
          페이지를 찾을 수 없습니다
        </h1>
        <p className="mb-8 text-gray-600">
          요청하신 페이지가 존재하지 않거나 이동되었을 수 있습니다.
        </p>

        {/* 홈으로 돌아가기 버튼 */}
        <button
          onClick={handleGoHome}
          className="rounded-xl bg-indigo-600 px-6 py-3 font-medium text-white transition-colors hover:bg-indigo-700"
        >
          {isAuthenticated ? '홈으로 돌아가기' : '시작 페이지로 이동'}
        </button>
      </div>
    </div>
  );
};

export default NotFoundPage;
