import { Check } from 'lucide-react';
import { Button } from '@/shared/components/ui/button';
import { useNavigate } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';

const CompleteStep = () => {
  const navigate = useNavigate();

  const handleGoHome = () => {
    navigate(PAGE_PATH.HOME);
  };

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col items-center justify-center px-6 py-12 text-center">
        {/* 체크 아이콘 */}
        <div className="mb-6 flex h-24 w-24 items-center justify-center rounded-full bg-green-100">
          <Check className="h-12 w-12 text-green-600" strokeWidth={3} />
        </div>

        {/* 메시지 */}
        <p className="mb-1 text-lg font-semibold text-gray-900">
          적금 계좌가 개설되었습니다
        </p>
        <p className="text-sm text-gray-500">
          통장은 앱에서 언제든 확인할 수 있어요
        </p>
      </div>

      {/* 하단 버튼 */}
      <div className="bg-white p-4">
        <Button
          onClick={handleGoHome}
          className="h-12 w-full rounded-lg bg-primary text-white disabled:bg-gray-200 disabled:text-gray-400"
        >
          메인으로 가기
        </Button>
      </div>
    </>
  );
};

export default CompleteStep;
