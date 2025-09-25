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
  const getProductInfo = () => {
    if (!accounts) {
      return null;
    }
    const targetProductId = form.productType === ACCOUNT_TYPES.CHECKING ? 1 : 2;
    const account = accounts.find(
      acc => acc.product?.productId === targetProductId,
    );
    return account?.product;
  };

  const productInfo = getProductInfo();

  // 적금인지 확인하는 타입 가드 (period가 있으면 적금으로 판단)
  const isSavingsAccount =
    form.productType === ACCOUNT_TYPES.SAVINGS ||
    ('period' in form && form.period);

  // 예상 만기 금액 간단 계산 (적금만)
  const maturityAmount =
    isSavingsAccount && 'depositAmount' in form && 'period' in form
      ? (form.depositAmount || 0) * (form.period || 0)
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
      <div className="flex flex-1 flex-col px-6 py-8">
        <h1 className="mb-2 text-xl font-bold text-gray-900">
          설정한 조건을 확인해주세요
        </h1>
        <p className="mb-6 text-gray-600">마지막으로 한 번 더 확인해보세요</p>

        {/* 요약 박스 */}
        <div className="rounded-lg border bg-gray-50 p-4">
          <div className="flex justify-between py-1">
            <span className="text-gray-600">계좌 유형</span>
            <span className="font-medium">
              {productInfo?.productName ?? form.productType ?? '-'}
            </span>
          </div>

          {isSavingsAccount && (
            <>
              <div className="flex justify-between py-1">
                <span className="text-gray-600">납입액</span>
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
                  {'period' in form && form.period ? `${form.period}주` : '-'}
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
              <span className="font-medium">
                {productInfo?.description ?? '자유로운 입출금이 가능합니다'}
              </span>
            </div>
          )}
        </div>

        {/* 에러 메시지 */}
        {hasError && (
          <div className="mt-4 rounded-lg bg-red-50 p-3">
            <p className="text-sm text-red-600">
              계좌 개설 중 오류가 발생했습니다. 다시 시도해주세요.
            </p>
          </div>
        )}

        {/* 수정 버튼 */}
        <div className="mt-6">
          <Button
            variant="outline"
            onClick={handleEdit}
            disabled={isLoading}
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
          disabled={isLoading}
          className="h-12 w-full rounded-lg bg-primary text-white disabled:bg-gray-200 disabled:text-gray-400"
        >
          {isLoading ? '계좌 개설 중...' : '계좌 개설하기'}
        </Button>
      </div>
    </>
  );
};

export default ConfirmStep;
