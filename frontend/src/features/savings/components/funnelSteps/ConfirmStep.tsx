import { useAccountCreationStore } from '@/features/savings/store/useAccountCreationStore';
import { useStepProgress } from '@/features/savings/hooks/useStepProgress';
import { Button } from '@/shared/components/ui/button';
import { ACCOUNT_TYPES } from '@/features/savings/constants/accountTypes';

const ConfirmStep = () => {
  const { form } = useAccountCreationStore();
  const { goToPreviousStep, goToNextStep } = useStepProgress();

  // 적금인지 확인하는 타입 가드
  const isSavingsAccount = form.productType === ACCOUNT_TYPES.SAVINGS;

  // 예상 만기 금액 간단 계산 (적금만)
  const maturityAmount =
    isSavingsAccount && 'depositAmount' in form && 'period' in form
      ? (form.depositAmount || 0) * (form.period || 0)
      : 0;

  const handleEdit = () => {
    goToPreviousStep();
  };

  const handleSubmit = () => {
    goToNextStep();
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
            <span className="text-gray-600">계좌 유형</span>
            <span className="font-medium">{form.productType ?? '-'}</span>
          </div>

          {isSavingsAccount && (
            <>
              <div className="flex justify-between py-1">
                <span className="text-gray-600">월 납입액</span>
                <span className="font-medium">
                  {'depositAmount' in form
                    ? (form.depositAmount?.toLocaleString() ?? 0) + '원'
                    : '-'}
                </span>
              </div>
              <div className="flex justify-between py-1">
                <span className="text-gray-600">이체일</span>
                <span className="font-medium">
                  {'transferDate' in form && form.transferDate
                    ? `매월 ${form.transferDate}일`
                    : '-'}
                </span>
              </div>
              <div className="flex justify-between py-1">
                <span className="text-gray-600">적금 기간</span>
                <span className="font-medium">
                  {'period' in form && form.period ? `${form.period}개월` : '-'}
                </span>
              </div>
              <div className="flex justify-between py-1">
                <span className="text-gray-600">예상 만기금액</span>
                <span className="font-medium">
                  {maturityAmount.toLocaleString()}원
                </span>
              </div>
            </>
          )}

          {!isSavingsAccount && (
            <div className="flex justify-between py-1">
              <span className="text-gray-600">설명</span>
              <span className="font-medium">자유로운 입출금이 가능합니다</span>
            </div>
          )}
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
