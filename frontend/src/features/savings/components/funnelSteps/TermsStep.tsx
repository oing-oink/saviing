import { useState } from 'react';
import { useGetAccountStore } from '@/features/savings/store/useGetAccountStore';
import { useStepProgress } from '@/features/savings/hooks/useStepProgress';
import { Button } from '@/shared/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from '@/shared/components/ui/dialog';

const TermsStep = () => {
  const { setForm, form } = useGetAccountStore();
  const { goToNextStep } = useStepProgress();

  const [allChecked, setAllChecked] = useState(
    Boolean(
      form.terms?.service && form.terms?.privacy && form.terms?.marketing,
    ),
  );
  const [requiredChecked, setRequiredChecked] = useState({
    service: form.terms?.service || false,
    privacy: form.terms?.privacy || false,
  });
  const [optionalChecked, setOptionalChecked] = useState(
    form.terms?.marketing || false,
  );
  const [openDialog, setOpenDialog] = useState<
    null | 'service' | 'privacy' | 'marketing'
  >(null);

  const isValid = requiredChecked.service && requiredChecked.privacy;

  // 전체 동의 토글
  const handleAllToggle = () => {
    const newValue = !allChecked;
    setAllChecked(newValue);
    setRequiredChecked({ service: newValue, privacy: newValue });
    setOptionalChecked(newValue);
  };

  // 다음 단계로 이동
  const handleNext = () => {
    if (!isValid) {
      return;
    }
    setForm({
      terms: {
        service: requiredChecked.service,
        privacy: requiredChecked.privacy,
        marketing: optionalChecked,
      },
    });
    goToNextStep(); // 계좌 타입에 따라 자동으로 다음 단계 결정
  };

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col px-6 py-8">
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
      </div>

      {/* 하단 버튼 */}
      <div className="bg-white p-4">
        <Button
          onClick={handleNext}
          disabled={!isValid}
          className="h-12 w-full rounded-lg bg-primary text-white disabled:bg-gray-200 disabled:text-gray-400"
        >
          동의하고 계속
        </Button>
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
