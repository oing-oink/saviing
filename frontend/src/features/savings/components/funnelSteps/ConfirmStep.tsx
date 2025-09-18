import { useAccountCreationStore } from '@/features/savings/store/useAccountCreationStore';
import { Button } from '@/shared/components/ui/button';

const ConfirmStep = () => {
  const { form, setStep } = useAccountCreationStore();

  // 예상 만기 금액 간단 계산 (임시)
  const maturityAmount =
    form.depositAmount && form.period ? form.depositAmount * form.period : 0;

  const handleEdit = () => {
    setStep('SET_CONDITION');
  };

  const handleSubmit = () => {
    setStep('COMPLETE');
  };

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col px-6 py-8">
        <h1 className="mb-2 text-xl font-bold text-gray-900">
          설정한 조건을 확인해주세요
        </h1>
        <p className="mb-6 text-gray-600">마지막으로 한 번 더 확인해보세요</p>

        {/* 요약 박스 */}
        <div className="rounded-lg border bg-gray-50 p-4">
          <div className="flex justify-between py-1">
            <span className="text-gray-600">적금 유형</span>
            <span className="font-medium">{form.productType ?? '-'}</span>
          </div>
          <div className="flex justify-between py-1">
            <span className="text-gray-600">월 납입액</span>
            <span className="font-medium">
              {form.depositAmount?.toLocaleString() ?? 0}원
            </span>
          </div>
          <div className="flex justify-between py-1">
            <span className="text-gray-600">이체일</span>
            <span className="font-medium">
              {form.transferDate ? `매월 ${form.transferDate}일` : '-'}
            </span>
          </div>
          <div className="flex justify-between py-1">
            <span className="text-gray-600">적금 기간</span>
            <span className="font-medium">
              {form.period ? `${form.period}개월` : '-'}
            </span>
          </div>
          <div className="flex justify-between py-1">
            <span className="text-gray-600">예상 만기금액</span>
            <span className="font-medium">
              {maturityAmount.toLocaleString()}원
            </span>
          </div>
        </div>

        {/* 수정 버튼 */}
        <div className="mt-6">
          <Button
            variant="outline"
            onClick={handleEdit}
            className="h-12 w-full rounded-lg"
          >
            수정하기
          </Button>
        </div>
      </div>

      {/* 하단 버튼 */}
      <div className="bg-white p-4">
        <Button
          onClick={handleSubmit}
          className="h-12 w-full rounded-lg bg-primary text-white disabled:bg-gray-200 disabled:text-gray-400"
        >
          계좌 개설하기
        </Button>
      </div>
    </>
  );
};

export default ConfirmStep;
