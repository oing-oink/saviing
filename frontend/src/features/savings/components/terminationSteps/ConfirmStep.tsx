import { useSavingsTermination } from '@/features/savings/hooks/useSavingsTermination';
import {
  useSavingsAccountDetail,
  useTerminateSavingsAccount,
} from '@/features/savings/query/useSavingsQuery';
import { Button } from '@/shared/components/ui/button';
import { useParams } from 'react-router-dom';
import { formatDate } from '@/shared/utils/dateFormat';

const ConfirmStep = () => {
  const { goToNextStep, goToPrevStep } = useSavingsTermination();
  const { accountId } = useParams<{ accountId: string }>();

  console.log('ConfirmStep - accountId:', accountId);

  const {
    data: accountData,
    isLoading,
    isError,
    error,
  } = useSavingsAccountDetail(accountId!);
  const terminateMutation = useTerminateSavingsAccount();

  console.log('ConfirmStep - API response:', {
    accountData,
    isLoading,
    isError,
    error,
  });

  const handleNext = async () => {
    if (!accountId) {
      return;
    }

    try {
      console.log(
        'ConfirmStep - Starting termination for accountId:',
        accountId,
      );
      const result = await terminateMutation.mutateAsync(accountId);
      console.log('ConfirmStep - Termination success:', result);

      goToNextStep();
    } catch (error) {
      console.error('ConfirmStep - Termination failed:', error);
    }
  };

  const handleBack = () => {
    goToPrevStep();
  };

  if (!accountId) {
    return (
      <div className="flex flex-1 items-center justify-center">
        <div className="text-red-500">계좌 ID가 없습니다.</div>
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="flex flex-1 items-center justify-center">
        <div className="text-gray-500">로딩 중...</div>
      </div>
    );
  }

  if (isError || !accountData) {
    return (
      <div className="flex flex-1 items-center justify-center">
        <div className="text-red-500">
          데이터를 불러올 수 없습니다.
          {error && <div className="mt-1 text-sm">{String(error)}</div>}
        </div>
      </div>
    );
  }

  const account = accountData;
  const achievementRate =
    account?.savings?.targetAmount > 0
      ? Math.round((account.balance / account.savings.targetAmount) * 100)
      : 0;
  const terminationDate = formatDate(new Date().toISOString());
  const estimatedInterest = Math.round((account?.balance || 0) * 0.012);
  const finalAmount = (account?.balance || 0) + estimatedInterest;

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col px-6 py-8 pb-24">
        <h1 className="mb-2 text-xl font-bold text-gray-900">해지 최종 확인</h1>
        <p className="mb-6 text-gray-600">
          아래 내용을 확인하고 적금 해지를 진행하세요.
        </p>

        <div className="space-y-6">
          <div className="rounded-lg border border-gray-200 p-4">
            <h3 className="mb-3 font-semibold text-gray-900">해지 대상 적금</h3>
            <div className="space-y-2 text-sm">
              <div className="flex justify-between">
                <span className="text-gray-600">상품명</span>
                <span className="font-medium text-gray-900">
                  {account?.product?.productName || '-'}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">계좌번호</span>
                <span className="font-medium text-gray-900">
                  {account?.accountNumber || '-'}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">잔액</span>
                <span className="font-medium text-gray-900">
                  {(account?.balance || 0).toLocaleString()}원
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">목표 금액</span>
                <span className="font-medium text-gray-900">
                  {(account?.savings?.targetAmount || 0).toLocaleString()}원
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">달성률</span>
                <span className="font-medium text-primary">
                  {achievementRate}%
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">가입일</span>
                <span className="font-medium text-gray-900">
                  {account?.createdAt ? formatDate(account.createdAt) : '-'}
                </span>
              </div>
            </div>
          </div>

          <div className="rounded-lg border border-gray-200 p-4">
            <h3 className="mb-3 font-semibold text-gray-900">해지 정보</h3>
            <div className="space-y-2 text-sm">
              <div className="flex justify-between">
                <span className="text-gray-600">해지일</span>
                <span className="font-medium text-gray-900">
                  {terminationDate}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">지급 이율</span>
                <span className="font-medium text-red-600">
                  1.2% (중도해지)
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">해지 수수료</span>
                <span className="font-medium text-gray-900">없음</span>
              </div>
              <div className="mt-2 flex justify-between border-t border-gray-200 pt-2">
                <span className="font-semibold text-gray-900">최종 지급액</span>
                <span className="font-bold text-primary">
                  {finalAmount.toLocaleString()}원
                </span>
              </div>
            </div>
          </div>

          <div className="rounded-lg border border-yellow-200 bg-yellow-50 p-4">
            <h3 className="mb-2 font-semibold text-yellow-800">⚠️ 최종 안내</h3>
            <ul className="space-y-1 text-sm text-yellow-700">
              <li>• 해지 후에는 되돌릴 수 없습니다.</li>
              <li>• 해지 금액은 연결된 계좌로 즉시 입금됩니다.</li>
              <li>• 만기 전 해지로 약정 이율이 적용되지 않습니다.</li>
            </ul>
          </div>
        </div>
      </div>

      {/* 하단 고정 버튼 */}
      <div className="fixed right-0 bottom-0 left-0 z-10 bg-white p-4">
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
            disabled={terminateMutation.isPending}
            className="h-12 flex-1 rounded-lg bg-red-600 text-white hover:bg-red-700 disabled:bg-gray-300 disabled:text-gray-500"
          >
            {terminateMutation.isPending ? '해지 처리 중...' : '해지하기'}
          </Button>
        </div>
      </div>
    </>
  );
};

export default ConfirmStep;
