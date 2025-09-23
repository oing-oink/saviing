import { useNavigate } from 'react-router-dom';
import { Button } from '@/shared/components/ui/button';
import { PAGE_PATH } from '@/shared/constants/path';

const CompleteStep = () => {
  const navigate = useNavigate();

  const handleGoHome = () => {
    navigate(PAGE_PATH.HOME);
  };

  const handleGoWallet = () => {
    navigate(PAGE_PATH.WALLET);
  };

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col items-center px-6 py-8 pb-24">
        <div className="mb-8 text-center">
          {/* 성공 아이콘 */}
          <div className="mx-auto mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-green-100">
            <svg
              className="h-10 w-10 text-green-600"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M5 13l4 4L19 7"
              />
            </svg>
          </div>
          <h1 className="mb-2 text-xl font-bold text-gray-900">
            적금 해지 완료
          </h1>
          <p className="text-gray-600">
            적금 해지가 성공적으로 완료되었습니다.
          </p>
        </div>

        <div className="w-full space-y-4">
          <div className="rounded-lg border border-gray-200 p-4">
            <h3 className="mb-3 font-semibold text-gray-900">해지 완료 정보</h3>
            <div className="space-y-2 text-sm">
              <div className="flex justify-between">
                <span className="text-gray-600">해지일시</span>
                <span className="font-medium text-gray-900">
                  2024.09.23 14:35
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">지급 계좌</span>
                <span className="font-medium text-gray-900">
                  123-456-789012
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">지급 금액</span>
                <span className="font-bold text-primary">1,255,320원</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">처리 상태</span>
                <span className="font-medium text-green-600">입금 완료</span>
              </div>
            </div>
          </div>

          <div className="rounded-lg border border-blue-200 bg-blue-50 p-4">
            <h3 className="mb-2 font-semibold text-blue-800">📄 안내사항</h3>
            <ul className="space-y-1 text-sm text-blue-700">
              <li>• 해지 확인서는 이메일로 발송됩니다.</li>
              <li>• 세무 관련 문의는 고객센터로 연락하세요.</li>
              <li>• 새로운 적금 상품 가입을 원하시면 상품을 둘러보세요.</li>
            </ul>
          </div>
        </div>
      </div>

      {/* 하단 고정 버튼 */}
      <div className="fixed right-0 bottom-0 left-0 z-10 bg-white p-4">
        <div className="flex space-x-3">
          <Button
            variant="outline"
            onClick={handleGoWallet}
            className="h-12 flex-1 rounded-lg"
          >
            내 계좌
          </Button>
          <Button
            onClick={handleGoHome}
            className="h-12 flex-1 rounded-lg bg-primary text-white hover:bg-primary/90"
          >
            홈으로
          </Button>
        </div>
      </div>
    </>
  );
};

export default CompleteStep;
