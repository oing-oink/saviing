import { useState } from 'react';
import { useAccountCreationStore } from '@/features/savings/store/useAccountCreationStore';
import { useStepProgress } from '@/features/savings/hooks/useStepProgress';
import { useAccountCreation } from '@/features/savings/hooks/useAccountCreation';
import { Button } from '@/shared/components/ui/button';
import { ACCOUNT_TYPES } from '@/features/savings/constants/accountTypes';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from '@/shared/components/ui/dialog';

const TermsStep = () => {
  const { setForm, form } = useAccountCreationStore();
  const { goToNextStep, goToPreviousStep } = useStepProgress();
  const { createAccount, isCreatingChecking, createCheckingError } =
    useAccountCreation();

  const [allChecked, setAllChecked] = useState(
    Boolean(form.terms.service && form.terms.privacy && form.terms.marketing),
  );
  const [requiredChecked, setRequiredChecked] = useState({
    service: form.terms.service,
    privacy: form.terms.privacy,
  });
  const [optionalChecked, setOptionalChecked] = useState(form.terms.marketing);
  const [openDialog, setOpenDialog] = useState<
    null | 'service' | 'privacy' | 'marketing'
  >(null);

  const isValid = requiredChecked.service && requiredChecked.privacy;
  const isCheckingAccount = form.productType === ACCOUNT_TYPES.CHECKING;
  const isLoading = isCreatingChecking;

  // 전체 동의 토글
  const handleAllToggle = () => {
    const newValue = !allChecked;
    setAllChecked(newValue);
    setRequiredChecked({ service: newValue, privacy: newValue });
    setOptionalChecked(newValue);
  };

  // 다음 단계로 이동 또는 계좌 생성
  const handleNext = () => {
    if (!isValid) {
      return;
    }

    // 폼 데이터 업데이트
    setForm({
      terms: {
        service: requiredChecked.service,
        privacy: requiredChecked.privacy,
        marketing: optionalChecked,
      },
    });

    if (isCheckingAccount) {
      // 입출금통장인 경우: API 호출
      createAccount();
    } else {
      // 적금인 경우: 다음 단계로 이동
      goToNextStep();
    }
  };

  const handleBack = () => {
    goToPreviousStep();
  };

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col px-6 py-8 pb-24">
        <h1 className="mb-2 text-xl font-bold text-gray-900">
          약관에 동의해주세요
        </h1>
        <p className="mb-6 text-gray-600">
          서비스 이용을 위해 필요한 약관이에요
        </p>

        {/* 전체 동의 */}
        <label className="mb-4 flex items-center gap-2">
          <input
            type="checkbox"
            checked={allChecked}
            onChange={handleAllToggle}
            className="h-4 w-4"
          />
          <span className="font-medium">전체 동의</span>
        </label>

        {/* 개별 약관 */}
        <div className="ml-4 flex flex-col gap-3">
          {/* 서비스 이용약관 */}
          <div className="flex items-center justify-between border-b py-3">
            <label className="flex items-center gap-2">
              <input
                type="checkbox"
                checked={requiredChecked.service}
                onChange={e =>
                  setRequiredChecked({
                    ...requiredChecked,
                    service: e.target.checked,
                  })
                }
                className="h-4 w-4"
              />
              <span>서비스 이용약관 (필수)</span>
            </label>
            <button
              onClick={() => setOpenDialog('service')}
              className="text-sm text-violet-500 hover:underline"
            >
              전문 보기
            </button>
          </div>

          {/* 개인정보 처리방침 */}
          <div className="flex items-center justify-between border-b py-3">
            <label className="flex items-center gap-2">
              <input
                type="checkbox"
                checked={requiredChecked.privacy}
                onChange={e =>
                  setRequiredChecked({
                    ...requiredChecked,
                    privacy: e.target.checked,
                  })
                }
                className="h-4 w-4"
              />
              <span>개인정보 처리방침 (필수)</span>
            </label>
            <button
              onClick={() => setOpenDialog('privacy')}
              className="text-sm text-violet-500 hover:underline"
            >
              전문 보기
            </button>
          </div>

          {/* 마케팅 동의 */}
          <div className="flex items-center justify-between border-b py-3">
            <label className="flex items-center gap-2">
              <input
                type="checkbox"
                checked={optionalChecked}
                onChange={e => setOptionalChecked(e.target.checked)}
                className="h-4 w-4"
              />
              <span>마케팅 정보 수신 (선택)</span>
            </label>
            <button
              onClick={() => setOpenDialog('marketing')}
              className="text-sm text-violet-500 hover:underline"
            >
              전문 보기
            </button>
          </div>
        </div>

        {/* 에러 메시지 */}
        {createCheckingError && (
          <div className="mt-4 rounded-lg bg-red-50 p-3">
            <p className="text-sm text-red-600">
              계좌 개설 중 오류가 발생했습니다. 다시 시도해주세요.
            </p>
          </div>
        )}
      </div>

      {/* 하단 고정 버튼 */}
      <div className="fixed right-0 bottom-0 left-0 z-10 bg-white p-4" style={{borderTop: 'none'}}>
        <div className="flex space-x-3">
          <Button
            variant="outline"
            onClick={handleBack}
            disabled={isLoading}
            className="h-12 flex-1 rounded-lg"
          >
            이전
          </Button>
          <Button
            onClick={handleNext}
            disabled={!isValid || isLoading}
            className="flex h-12 flex-1 items-center justify-center space-x-2 rounded-lg bg-primary text-white hover:bg-primary/90 disabled:bg-gray-300 disabled:text-gray-500"
          >
            {isLoading ? (
              <>
                <div className="h-4 w-4 animate-spin rounded-full border-b-2 border-white"></div>
                <span>계좌 개설 중...</span>
              </>
            ) : (
              <span>동의하고 계속</span>
            )}
          </Button>
        </div>
      </div>

      {/* 약관 전문 Dialog */}
      <Dialog
        open={Boolean(openDialog)}
        onOpenChange={() => setOpenDialog(null)}
      >
        <DialogContent className="max-h-[70vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>
              {openDialog === 'service'
                ? '서비스 이용약관'
                : openDialog === 'privacy'
                  ? '개인정보 처리방침'
                  : '마케팅 정보 수신 동의'}
            </DialogTitle>
            <DialogDescription>
              {openDialog === 'service' &&
                '여기에 서비스 이용약관 전문을 넣어주세요...'}
              {openDialog === 'privacy' &&
                '여기에 개인정보 처리방침 전문을 넣어주세요...'}
              {openDialog === 'marketing' &&
                '여기에 마케팅 정보 수신 전문을 넣어주세요...'}
            </DialogDescription>
          </DialogHeader>
        </DialogContent>
      </Dialog>
    </>
  );
};

export default TermsStep;
