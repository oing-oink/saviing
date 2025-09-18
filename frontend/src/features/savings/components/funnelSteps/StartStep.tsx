import { useGetAccountStore } from '@/features/savings/store/useGetAccountStore';
import { Landmark } from 'lucide-react';
import { Button } from '@/shared/components/ui/button';
import FunnelLayout from '@/features/savings/components/FunnelLayout';

const StartStep = () => {
  const setStep = useGetAccountStore(state => state.setStep);

  const handleNext = () => {
    setStep('PRODUCT_TYPE');
  };

  return (
    <FunnelLayout>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col items-center justify-center px-6 text-center">
        <h1 className="mb-2 text-xl font-bold text-gray-900">
          적금 가입을 시작해볼까요?
        </h1>
        <p className="mb-6 text-gray-600">간편하게 목돈을 모아보세요</p>

        {/* 일러스트 */}
        <div className="mb-6 flex h-24 w-24 items-center justify-center rounded-full bg-blue-50 text-primary">
          <Landmark className="h-12 w-12" />
        </div>

        <p className="text-sm text-gray-600">
          매월 일정 금액을 저축하여 <br />
          목표 금액을 달성해보세요
        </p>
      </div>

      {/* 하단 고정 버튼 */}
      <div className="p-4">
        <Button
          onClick={handleNext}
          className="h-12 w-full rounded-lg bg-primary text-white hover:bg-blue-700"
        >
          시작하기
        </Button>
      </div>
    </FunnelLayout>
  );
};

export default StartStep;
