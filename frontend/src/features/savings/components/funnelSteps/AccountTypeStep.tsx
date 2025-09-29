import { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { useErrorBoundary } from 'react-error-boundary';
import { useAccountCreationStore } from '@/features/savings/store/useAccountCreationStore';
import { useStepProgress } from '@/features/savings/hooks/useStepProgress';
import { Button } from '@/shared/components/ui/button';
import { getAllAccounts } from '@/features/savings/api/savingsApi';
import { useCustomerStore } from '@/features/auth/store/useCustomerStore';
import { ACCOUNT_TYPES } from '@/features/savings/constants/accountTypes';
import {
  ACCOUNT_TYPE_OPTIONS,
  type AccountType,
} from '@/features/savings/constants/accountTypes';
import { PAGE_PATH } from '@/shared/constants/path';

const AccountTypeStep = () => {
  const { setForm, form } = useAccountCreationStore();
  const { goToNextStep, goToPreviousStep } = useStepProgress();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const customerId = useCustomerStore(state => state.customerId);
  const { showBoundary } = useErrorBoundary();
  const [selected, setSelected] = useState<AccountType | null>(
    form.productType,
  );
  const [showCheckingAccountAlert, setShowCheckingAccountAlert] =
    useState(false);

  // 내 계좌 목록 가져오기
  const {
    data: accounts,
    isLoading: isLoadingAccounts,
    error: accountsError,
  } = useQuery({
    queryKey: ['allAccounts', customerId],
    queryFn: () => {
      if (customerId == null) {
        throw new Error('로그인 정보가 없습니다.');
      }
      return getAllAccounts(customerId);
    },
    staleTime: 1000 * 60 * 5,
    enabled: customerId != null,
  });

  // API 에러 발생 시 ErrorBoundary로 전달
  useEffect(() => {
    if (accountsError) {
      showBoundary(accountsError);
    }
  }, [accountsError, showBoundary]);

  // 입출금 계좌 존재 여부 확인
  const hasCheckingAccount =
    accounts?.some(
      account =>
        account.product?.productId === 1 && account.status === 'ACTIVE',
    ) || false;

  // URL 파라미터에서 type 확인 (초기값 설정용)
  const urlAccountType = searchParams.get('type') as AccountType | null;

  useEffect(() => {
    // URL에서 특정 타입이 지정된 경우 자동으로 선택하고 폼에 저장
    if (urlAccountType && !selected) {
      setSelected(urlAccountType);
      setForm({ productType: urlAccountType });
    }
  }, [urlAccountType, selected, setForm]);

  const handleNext = () => {
    if (!selected) {
      return;
    }
    setForm({ productType: selected });
    goToNextStep();
  };

  const handleAccountTypeSelect = (accountType: AccountType) => {
    // 자유적금 선택 시 입출금 계좌 확인
    if (accountType === ACCOUNT_TYPES.SAVINGS) {
      if (isLoadingAccounts) {
        // 로딩 중일 때는 잠시 대기
        return;
      }

      if (!hasCheckingAccount) {
        setShowCheckingAccountAlert(true);
        return;
      }
    }

    setSelected(accountType);

    // productType 변경 시 즉시 URL 파라미터에 반영
    const params = new URLSearchParams(searchParams);
    params.set('type', accountType);
    navigate(`${window.location.pathname}?${params.toString()}`, {
      replace: true,
    });
  };

  const handleGoHome = () => {
    navigate(PAGE_PATH.HOME);
  };

  const handleBack = () => {
    goToPreviousStep();
  };

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col px-6 py-8 pb-24">
        <h1 className="mb-2 text-xl font-bold text-gray-900">
          어떤 계좌를 개설하고 싶으세요?
        </h1>
        <p className="mb-6 text-gray-600">원하는 계좌 타입을 선택해주세요</p>

        <div className="flex flex-col gap-4">
          {ACCOUNT_TYPE_OPTIONS.map(option => {
            const isSelected = selected === option.id;

            return (
              <div
                key={option.id}
                onClick={() => handleAccountTypeSelect(option.id)}
                className={`cursor-pointer rounded-lg border p-4 transition-all ${
                  isSelected
                    ? 'border-primary bg-primary/10'
                    : 'border-gray-200 hover:border-gray-300'
                }`}
              >
                <div className="flex items-start space-x-3">
                  <div className="flex-1">
                    <div className="mb-1 flex items-center gap-2">
                      {option.recommended && (
                        <span className="rounded bg-blue-100 px-2 py-0.5 text-xs text-blue-600">
                          추천
                        </span>
                      )}
                      <span className="font-semibold text-gray-900">
                        {option.title}
                      </span>
                    </div>
                    <p className="text-sm text-gray-600">
                      {option.description}
                    </p>
                  </div>
                  <div
                    className={`flex h-5 w-5 items-center justify-center rounded-full border-2 ${
                      isSelected
                        ? 'border-primary bg-primary'
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

      {/* 입출금 계좌 없음 알림 */}
      {showCheckingAccountAlert && (
        <div className="mx-6 mb-4 rounded-lg border border-orange-200 bg-orange-50 p-4">
          <div className="flex items-start gap-3">
            <div className="flex-shrink-0">
              <svg
                className="h-5 w-5 text-orange-500"
                fill="currentColor"
                viewBox="0 0 20 20"
              >
                <path
                  fillRule="evenodd"
                  d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z"
                  clipRule="evenodd"
                />
              </svg>
            </div>
            <div className="flex">
              <div className="flex-1">
                <div className="text-sm font-medium text-orange-800">
                  입출금 계좌가 필요해요
                </div>
                <div className="mt-1 text-sm text-orange-700">
                  입출금 계좌를 개설해주세요.
                </div>
              </div>
            </div>
            <div className="mt-2 flex justify-center">
              <Button
                onClick={handleGoHome}
                size="sm"
                variant="outline"
                className="border-orange-300 text-orange-700 hover:bg-orange-100"
              >
                홈으로 돌아가기
              </Button>
            </div>
          </div>
        </div>
      )}

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
            다음
          </Button>
        </div>
      </div>
    </>
  );
};

export default AccountTypeStep;
