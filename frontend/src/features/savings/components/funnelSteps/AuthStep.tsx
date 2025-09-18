import { useState } from 'react';
import { useAccountCreationStore } from '@/features/savings/store/useAccountCreationStore';
import { Button } from '@/shared/components/ui/button';

const AuthStep = () => {
  const { setStep, setForm } = useAccountCreationStore();
  const [selected, setSelected] = useState(false);

  // 다음 단계로 이동
  const handleNext = () => {
    if (!selected) {
      return;
    }
    setForm({ authMethod: '휴대폰 본인인증' });
    setStep('TERMS'); //
  };

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col px-6 py-8">
        <h1 className="mb-2 text-xl font-bold text-gray-900">
          본인 인증을 진행해주세요
        </h1>
        <p className="mb-6 text-gray-600">안전한 서비스 이용을 위해 필요해요</p>

        {/* 인증 방법 카드 */}
        <button
          onClick={() => setSelected(true)}
          className={`w-full rounded-xl border p-4 text-left transition ${
            selected
              ? 'border-violet-500 bg-violet-50 shadow-sm'
              : 'border-gray-200 hover:border-gray-300'
          }`}
        >
          <p className="font-semibold">휴대폰 본인인증</p>
          <p className="mt-1 text-sm text-gray-600">
            휴대폰 SMS로 인증번호를 받아주세요
          </p>
        </button>
      </div>

      {/* 하단 버튼 */}
      <div className="bg-white p-4">
        <Button
          onClick={handleNext}
          disabled={!selected}
          className="h-12 w-full rounded-lg bg-primary text-white disabled:bg-gray-200 disabled:text-gray-400"
        >
          인증 완료 후 다음
        </Button>
      </div>
    </>
  );
};

export default AuthStep;
