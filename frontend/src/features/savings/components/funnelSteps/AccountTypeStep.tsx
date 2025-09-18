import { useState } from 'react';
import { useGetAccountStore } from '@/features/savings/store/useGetAccountStore';
import { useStepProgress } from '@/features/savings/hooks/useStepProgress';
import { Button } from '@/shared/components/ui/button';
import FunnelLayout from '@/features/savings/components/FunnelLayout';
import {
  ACCOUNT_TYPE_OPTIONS,
  type AccountType,
} from '@/features/savings/constants/accountTypes';

const AccountTypeStep = () => {
  const { setForm, form } = useGetAccountStore();
  const { goToNextStep } = useStepProgress();
  const [selected, setSelected] = useState<AccountType | null>(
    form.productType || null,
  );

  const handleNext = () => {
    if (!selected) {
      return;
    }
    setForm({ productType: selected });
    goToNextStep();
  };

  return (
    <FunnelLayout>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col px-6 py-8">
        <h1 className="mb-2 text-xl font-bold text-gray-900">
          어떤 적금을 가입하고 싶으세요?
        </h1>
        <p className="mb-6 text-gray-600">적금 유형을 선택해주세요</p>

        <div className="flex flex-col gap-4">
          {ACCOUNT_TYPE_OPTIONS.map(option => (
            <button
              key={option.id}
              onClick={() => setSelected(option.id)}
              className={`w-full rounded-xl border p-4 text-left transition ${
                selected === option.id
                  ? 'border-violet-500 bg-violet-50 shadow-sm'
                  : 'border-gray-200 hover:border-gray-300'
              }`}
            >
              <div className="flex items-center gap-2">
                {option.recommended && (
                  <span className="rounded bg-blue-100 px-2 py-0.5 text-xs text-blue-600">
                    추천
                  </span>
                )}
                <span className="font-semibold">{option.title}</span>
              </div>
              <p className="mt-1 text-sm text-gray-600">{option.description}</p>
            </button>
          ))}
        </div>
      </div>

      {/* 하단 버튼 */}
      <div className="bg-white p-4">
        <Button
          onClick={handleNext}
          disabled={!selected}
          className="h-12 w-full rounded-lg bg-primary text-white disabled:bg-gray-200 disabled:text-gray-400"
        >
          다음
        </Button>
      </div>
    </FunnelLayout>
  );
};

export default AccountTypeStep;
