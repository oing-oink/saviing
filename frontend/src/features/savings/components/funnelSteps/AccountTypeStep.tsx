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
  const { goToNextStep, goToPreviousStep } = useStepProgress();
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

  const handleBack = () => {
    goToPreviousStep();
  };

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col px-6 py-8 pb-24">
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
              <div
                key={option.id}
                onClick={() => handleAccountTypeSelect(option.id)}
                className={`cursor-pointer rounded-lg border p-4 transition-all ${
                  isSelected
                    ? 'border-primary bg-primary/10'
                    : isDisabled
                      ? 'cursor-not-allowed border-gray-100 bg-gray-50'
                      : 'border-gray-200 hover:border-gray-300'
                }`}
              >
                <div className="flex items-start space-x-3">
                  <div className="flex-1">
                    <div className="flex items-center gap-2 mb-1">
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
                          isDisabled ? 'text-gray-400' : 'text-gray-900'
                        }`}
                      >
                        {option.title}
                      </span>
                    </div>
                    <p
                      className={`text-sm ${
                        isDisabled ? 'text-gray-400' : 'text-gray-600'
                      }`}
                    >
                      {option.description}
                    </p>
                  </div>
                  <div
                    className={`flex h-5 w-5 items-center justify-center rounded-full border-2 ${
                      isSelected
                        ? 'border-primary bg-primary'
                        : isDisabled
                          ? 'border-gray-300'
                          : 'border-gray-300'
                    }`}
                  >
                    {isSelected && (
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
            );
          })}
        </div>
      </div>

      {/* 하단 고정 버튼 */}
      <div className="fixed right-0 bottom-0 left-0 z-10 bg-white p-4" style={{borderTop: 'none'}}>
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
            다음
          </Button>
        </div>
      </div>
    </>
  );
};

export default AccountTypeStep;
