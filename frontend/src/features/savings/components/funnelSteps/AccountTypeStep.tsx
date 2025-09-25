import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useAccountCreationStore } from '@/features/savings/store/useAccountCreationStore';
import { useStepProgress } from '@/features/savings/hooks/useStepProgress';
import { Button } from '@/shared/components/ui/button';
import {
  ACCOUNT_TYPE_OPTIONS,
  type AccountType,
} from '@/features/savings/constants/accountTypes';

const AccountTypeStep = () => {
  const { setForm, form } = useAccountCreationStore();
  const { goToNextStep } = useStepProgress();
  const [searchParams] = useSearchParams();
  const [selected, setSelected] = useState<AccountType | null>(
    form.productType,
  );

  // URL 파라미터에서 type 확인
  const urlAccountType = searchParams.get('type') as AccountType | null;
  const allowedAccountType = urlAccountType || null;

  useEffect(() => {
    // URL에서 특정 타입이 지정된 경우 자동으로 선택하고 폼에 저장
    if (allowedAccountType && !selected) {
      setSelected(allowedAccountType);
      setForm({ productType: allowedAccountType });
    }
  }, [allowedAccountType, selected, setForm]);

  const handleNext = () => {
    if (!selected) {
      return;
    }
    setForm({ productType: selected });
    goToNextStep();
  };

  const handleAccountTypeSelect = (accountType: AccountType) => {
    // URL에서 특정 타입이 지정된 경우 다른 타입 선택 불가
    if (allowedAccountType && accountType !== allowedAccountType) {
      return;
    }
    setSelected(accountType);
  };

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col px-6 py-8">
        <h1 className="mb-2 text-xl font-bold text-gray-900">
          어떤 적금을 가입하고 싶으세요?
        </h1>
        <p className="mb-6 text-gray-600">적금 유형을 선택해주세요</p>

        <div className="flex flex-col gap-4">
          {ACCOUNT_TYPE_OPTIONS.map(option => {
            const isDisabled =
              allowedAccountType && option.id !== allowedAccountType;
            const isSelected = selected === option.id;

            return (
              <button
                key={option.id}
                onClick={() => handleAccountTypeSelect(option.id)}
                disabled={Boolean(isDisabled)}
                className={`w-full rounded-xl border p-4 text-left transition ${
                  isSelected
                    ? 'border-violet-500 bg-violet-50 shadow-sm'
                    : isDisabled
                      ? 'cursor-not-allowed border-gray-100 bg-gray-50'
                      : 'border-gray-200 hover:border-gray-300'
                }`}
              >
                <div className="flex items-center gap-2">
                  {option.recommended && (
                    <span
                      className={`rounded px-2 py-0.5 text-xs ${
                        isDisabled
                          ? 'bg-gray-100 text-gray-400'
                          : 'bg-blue-100 text-blue-600'
                      }`}
                    >
                      추천
                    </span>
                  )}
                  <span
                    className={`font-semibold ${
                      isDisabled ? 'text-gray-400' : ''
                    }`}
                  >
                    {option.title}
                  </span>
                </div>
                <p
                  className={`mt-1 text-sm ${
                    isDisabled ? 'text-gray-400' : 'text-gray-600'
                  }`}
                >
                  {option.description}
                </p>
              </button>
            );
          })}
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
    </>
  );
};

export default AccountTypeStep;
