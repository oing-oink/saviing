import { useState } from 'react';
import { useAccountCreationStore } from '@/features/savings/store/useAccountCreationStore';
import { useStepProgress } from '@/features/savings/hooks/useStepProgress';
import { Button } from '@/shared/components/ui/button';

const AuthStep = () => {
  const { setForm } = useAccountCreationStore();
  const { goToNextStep, goToPreviousStep } = useStepProgress();
  const [selected, setSelected] = useState(false);

  // 다음 단계로 이동
  const handleNext = () => {
    if (!selected) {
      return;
    }
    setForm({ authMethod: '휴대폰 본인인증' });
    goToNextStep();
  };

  const handleBack = () => {
    goToPreviousStep();
  };

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col px-6 py-8 pb-24">
        <h1 className="mb-2 text-xl font-bold text-gray-900">
          본인 인증을 진행해주세요
        </h1>
        <p className="mb-6 text-gray-600">안전한 서비스 이용을 위해 필요해요</p>

        {/* 인증 방법 카드 */}
        <div
          onClick={() => setSelected(true)}
          className={`cursor-pointer rounded-lg border p-4 transition-all ${
            selected
              ? 'border-primary bg-primary/10'
              : 'border-gray-200 hover:border-gray-300'
          }`}
        >
          <div className="flex items-start space-x-3">
            <div className="flex-1">
              <h3 className="font-semibold text-gray-900">휴대폰 본인인증</h3>
              <p className="mt-1 text-sm text-gray-600">
                휴대폰 SMS로 인증번호를 받아주세요
              </p>
            </div>
            <div
              className={`flex h-5 w-5 items-center justify-center rounded-full border-2 ${
                selected ? 'border-primary bg-primary' : 'border-gray-300'
              }`}
            >
              {selected && (
                <svg
                  className="h-3 w-3 text-white"
                  fill="currentColor"
                  viewBox="0 0 20 20"
                >
                  <path
                    fillRule="evenodd"
                    d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                    clipRule="evenodd"
                  />
                </svg>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* 하단 고정 버튼 */}
      <div
        className="fixed right-0 bottom-0 left-0 z-10 bg-white p-4"
        style={{ borderTop: 'none' }}
      >
        <div className="flex space-x-3">
          <Button
            variant="outline"
            onClick={handleBack}
            className="h-12 flex-1 rounded-lg"
          >
            이전
          </Button>
          <Button
            onClick={handleNext}
            disabled={!selected}
            className="h-12 flex-1 rounded-lg bg-primary text-white hover:bg-primary/90 disabled:bg-gray-300 disabled:text-gray-500"
          >
            인증 완료 후 다음
          </Button>
        </div>
      </div>
    </>
  );
};

export default AuthStep;
