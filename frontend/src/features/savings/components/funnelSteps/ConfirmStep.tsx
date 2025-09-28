import { useEffect } from 'react';
import { useAccountCreationStore } from '@/features/savings/store/useAccountCreationStore';
import { useStepProgress } from '@/features/savings/hooks/useStepProgress';
import { useAccountCreation } from '@/features/savings/hooks/useAccountCreation';
import { useQuery } from '@tanstack/react-query';
import { useErrorBoundary } from 'react-error-boundary';
import { getAllAccounts } from '@/features/savings/api/savingsApi';
import { Button } from '@/shared/components/ui/button';
import { ACCOUNT_TYPES } from '@/features/savings/constants/accountTypes';
import { useCustomerStore } from '@/features/auth/store/useCustomerStore';

const ConfirmStep = () => {
  const { form } = useAccountCreationStore();
  const { goToPreviousStep } = useStepProgress();
  const {
    createAccount,
    isCreatingChecking,
    isCreatingSavings,
    createCheckingError,
    createSavingsError,
  } = useAccountCreation();
  const customerId = useCustomerStore(state => state.customerId);
  const { showBoundary } = useErrorBoundary();

  // 계좌 목록 가져와서 상품 정보 추출
  const { data: accounts, error: accountsError } = useQuery({
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

  // 현재 선택된 계좌 타입에 맞는 상품 정보 찾기
  const getSelectedAccount = () => {
    if (!accounts) {
      return null;
    }
    const targetProductId = form.productType === ACCOUNT_TYPES.CHECKING ? 1 : 2;
    return accounts.find(acc => acc.product?.productId === targetProductId) ?? null;
  };

  const selectedAccount = getSelectedAccount();
  const productInfo = selectedAccount?.product;

  // 적금인지 확인하는 타입 가드 (period가 있으면 적금으로 판단)
  const isSavingsAccount =
    form.productType === ACCOUNT_TYPES.SAVINGS ||
    ('period' in form && form.period);

  // 납입 주기에 따른 단위 라벨 (주/개월)
  const transferCycle =
    'transferCycle' in form ? form.transferCycle ?? 'WEEKLY' : 'WEEKLY';
  const periodUnitLabel = transferCycle === 'MONTHLY' ? '개월' : '주';

  // 예상 만기 금액 계산 (이자율 반영)
  const depositAmountValue =
    isSavingsAccount && 'depositAmount' in form ? form.depositAmount ?? 0 : 0;
  const periodValue =
    isSavingsAccount && 'period' in form ? form.period ?? 0 : 0;
  const normalizedPeriodValue = (() => {
    if (!periodValue) {
      return 0;
    }
    if (transferCycle === 'MONTHLY') {
      return Math.ceil(periodValue / 4);
    }
    return periodValue;
  })();
  const interestRateBps =
    (selectedAccount?.baseRate ?? 0) + (selectedAccount?.bonusRate ?? 0);
  const interestRateDecimal = interestRateBps / 10000;
  const hasMaturityData = depositAmountValue > 0 && normalizedPeriodValue > 0;
  const totalContribution = hasMaturityData
    ? depositAmountValue * normalizedPeriodValue
    : 0;
  const maturityAmount = hasMaturityData
    ? Math.round(totalContribution * (1 + interestRateDecimal))
    : 0;

  // 로딩 상태 확인
  const isLoading = isCreatingChecking || isCreatingSavings;

  // 에러 상태 확인
  const hasError = createCheckingError || createSavingsError;

  const handleEdit = () => {
    goToPreviousStep();
  };

  const handleSubmit = () => {
    createAccount();
  };

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col px-6 py-8 pb-24">
        <h1 className="mb-2 text-xl font-bold text-gray-900">
          설정한 조건을 확인해주세요
        </h1>
        <p className="mb-6 text-gray-600">마지막으로 한 번 더 확인해보세요</p>

        <div className="space-y-6 rounded-lg border bg-white p-6">
          {/* 계좌 정보 */}
          <div>
            <h3 className="mb-3 border-b pb-3 font-semibold text-gray-900">
              계좌 정보
            </h3>
            <div className="space-y-2">
              <div className="rounded-lg bg-primary/10 p-3">
                <div className="flex justify-between text-sm">
                  <span className="text-gray-700">계좌 유형</span>
                  <span className="font-medium text-primary">
                    {productInfo?.productName ?? form.productType ?? '-'}
                  </span>
                </div>
              </div>

              {isSavingsAccount && (
                <>
                  <div className="rounded-lg bg-primary/10 p-3">
                    <div className="flex justify-between text-sm">
                      <span className="text-gray-700">자동 납입액</span>
                      <span className="font-medium text-primary">
                        {'depositAmount' in form
                          ? (form.depositAmount?.toLocaleString() ?? 0) + '원'
                          : '-'}
                      </span>
                    </div>
                  </div>
                  <div className="rounded-lg bg-primary/10 p-3">
                    <div className="flex justify-between text-sm">
                      <span className="text-gray-700">이체일</span>
                      <span className="font-medium text-primary">
                        {'transferCycle' in form &&
                        form.transferCycle === 'WEEKLY' &&
                        'transferDate' in form &&
                        form.transferDate
                          ? `매주 ${['일', '월', '화', '수', '목', '금', '토'][Number(form.transferDate) % 7]}요일`
                          : 'transferDate' in form && form.transferDate
                            ? `매월 ${form.transferDate}일`
                            : '-'}
                      </span>
                    </div>
                  </div>
                  <div className="rounded-lg bg-primary/10 p-3">
                    <div className="flex justify-between text-sm">
                      <span className="text-gray-700">적금 기간</span>
                      <span className="font-medium text-primary">
                        {'period' in form && form.period
                          ? `${normalizedPeriodValue}${periodUnitLabel}`
                          : '-'}
                      </span>
                    </div>
                  </div>
                  <div className="rounded-lg bg-primary/10 p-3">
                    <div className="flex justify-between text-sm">
                      <span className="text-gray-700">예상 만기금액</span>
                      <span className="font-medium text-primary">
                        {hasMaturityData
                          ? `${maturityAmount.toLocaleString()}원`
                          : '-'}
                      </span>
                    </div>
                  </div>
                </>
              )}

              {!isSavingsAccount && (
                <div className="rounded-lg bg-primary/10 p-3">
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-700">설명</span>
                    <span className="font-medium text-primary">
                      {productInfo?.description ??
                        '자유로운 입출금이 가능합니다'}
                    </span>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* 에러 메시지 */}
        {hasError && (
          <div className="mt-4 rounded-lg bg-red-50 p-3">
            <p className="text-sm text-red-600">
              계좌 개설 중 오류가 발생했습니다. 다시 시도해주세요.
            </p>
          </div>
        )}
      </div>

      {/* 하단 고정 버튼 */}
      <div className="fixed right-0 bottom-0 left-0 z-10 bg-white p-4">
        <div className="flex space-x-3">
          <Button
            variant="outline"
            onClick={handleEdit}
            disabled={isLoading}
            className="h-12 flex-1 rounded-lg"
          >
            수정하기
          </Button>
          <Button
            onClick={handleSubmit}
            disabled={isLoading}
            className="flex h-12 flex-1 items-center justify-center space-x-2 rounded-lg bg-primary text-white hover:bg-primary/90 disabled:bg-gray-300 disabled:text-gray-500"
          >
            {isLoading ? (
              <>
                <div className="h-4 w-4 animate-spin rounded-full border-b-2 border-white"></div>
                <span>계좌 개설 중...</span>
              </>
            ) : (
              <span>계좌 개설하기</span>
            )}
          </Button>
        </div>
      </div>
    </>
  );
};

export default ConfirmStep;
