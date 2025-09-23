import { useSavingsTermination } from '@/features/savings/hooks/useSavingsTermination';
import { Button } from '@/shared/components/ui/button';

const WarningStep = () => {
  const { goToNextStep } = useSavingsTermination();

  const handleNext = () => {
    goToNextStep();
  };

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col px-6 py-8 pb-24">
        <h1 className="mb-2 text-xl font-bold text-gray-900">적금 해지 안내</h1>
        <p className="mb-6 text-gray-600">
          적금을 해지하기 전 다음 사항을 확인해 주세요.
        </p>

        <div className="space-y-4">
          <div className="rounded-lg bg-red-50 border border-red-200 p-4">
            <h3 className="mb-2 font-semibold text-red-800">⚠️ 중요 안내</h3>
            <ul className="space-y-2 text-sm text-red-700">
              <li>• 만기 전 해지 시 약정 이율이 적용되지 않습니다.</li>
              <li>• 해지 시 별도의 수수료가 발생할 수 있습니다.</li>
              <li>• 해지 후에는 되돌릴 수 없습니다.</li>
            </ul>
          </div>

          <div className="rounded-lg bg-blue-50 border border-blue-200 p-4">
            <h3 className="mb-2 font-semibold text-blue-800">💡 대안 제안</h3>
            <ul className="space-y-2 text-sm text-blue-700">
              <li>• 적금 조건 변경을 먼저 고려해 보세요.</li>
              <li>• 일시적 자금 필요 시 대출 서비스를 이용해 보세요.</li>
            </ul>
          </div>
        </div>
      </div>

      {/* 하단 고정 버튼 */}
      <div className="fixed right-0 bottom-0 left-0 z-10 bg-white p-4 shadow-lg">
        <Button
          onClick={handleNext}
          className="h-12 w-full rounded-lg bg-primary text-white hover:bg-primary/90"
        >
          해지 진행하기
        </Button>
      </div>
    </>
  );
};

export default WarningStep;