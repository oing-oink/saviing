import { useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useSavingsSettingsStore } from '@/features/savings/store/useSavingsSettingsStore';
import { useSavingsSettingsChange } from '@/features/savings/hooks/useSavingsSettingsChange';
import {
  useAccountsList,
  useSavingsAccountDetail,
} from '@/features/savings/query/useSavingsQuery';
import { Button } from '@/shared/components/ui/button';

const CurrentInfoStep = () => {
  const { accountId } = useParams<{ accountId: string }>();
  const { setCurrentInfo } = useSavingsSettingsStore();
  const { goToNextStep } = useSavingsSettingsChange();

  // 계좌 목록 조회 (자동이체 연결 계좌 정보 표시용)
  const { data: accounts } = useAccountsList();

  // 적금 계좌 상세 정보 조회 (savings 정보 포함)
  const {
    data: savingsDetail,
    isLoading,
    error,
  } = useSavingsAccountDetail(accountId!);

  // API 데이터로 현재 정보 설정

  useEffect(() => {
    if (savingsDetail) {
      if (savingsDetail.savings?.autoTransfer) {
        const currentInfo = {
          currentAmount: savingsDetail.savings.autoTransfer.amount,
          currentTransferDate: String(
            savingsDetail.savings.autoTransfer.transferDay,
          ),
          currentTransferCycle: savingsDetail.savings.autoTransfer.cycle as
            | 'WEEKLY'
            | 'MONTHLY',
          currentBalance: savingsDetail.balance,
          currentAutoAccount: String(
            savingsDetail.savings.autoTransfer.withdrawAccountId,
          ),
        };

        setCurrentInfo(currentInfo);
      }
    }
  }, [savingsDetail, setCurrentInfo, isLoading, error]);

  // 현재 연결된 자동이체 계좌 찾기
  const withdrawAccountId =
    savingsDetail?.savings?.autoTransfer?.withdrawAccountId;
  const currentAutoAccount = accounts?.find(
    account => account.accountId === withdrawAccountId,
  );

  // 계좌가 없으면 기본값 표시
  const autoAccountDisplay = currentAutoAccount
    ? `${currentAutoAccount.product.productName} (*${currentAutoAccount.accountNumber.slice(-4)})`
    : '연결된 계좌 정보 없음';

  // 납입 주기 표시 변환
  const getCycleDisplay = (cycle: string) => {
    switch (cycle) {
      case 'WEEKLY':
        return '주간';
      case 'MONTHLY':
        return '월간';
      case 'DAILY':
        return '일간';
      default:
        return cycle;
    }
  };

  // 자동이체 날짜 표시 변환
  const getTransferDateDisplay = (cycle: string, transferDay: number) => {
    switch (cycle) {
      case 'WEEKLY':
        return `매주 ${['일', '월', '화', '수', '목', '금', '토'][transferDay]}요일`;
      case 'MONTHLY':
        return `매월 ${transferDay}일`;
      case 'DAILY':
        return '매일';
      default:
        return `${transferDay}`;
    }
  };

  // 로딩 상태
  if (isLoading) {
    return (
      <div className="flex flex-1 flex-col items-center justify-center px-6 py-8">
        <div className="animate-pulse">
          <div className="mb-4 h-6 w-32 rounded bg-gray-200"></div>
          <div className="mb-6 h-4 w-48 rounded bg-gray-200"></div>
          <div className="space-y-4 rounded-lg border bg-white p-6">
            <div className="h-4 w-full rounded bg-gray-200"></div>
            <div className="h-4 w-full rounded bg-gray-200"></div>
            <div className="h-4 w-full rounded bg-gray-200"></div>
            <div className="h-4 w-full rounded bg-gray-200"></div>
          </div>
        </div>
      </div>
    );
  }

  // 에러 상태
  if (error) {
    return (
      <div className="flex flex-1 flex-col items-center justify-center px-6 py-8">
        <div className="text-center text-red-500">
          <p>적금 정보를 불러오는데 실패했습니다.</p>
          <p className="mt-1 text-sm">잠시 후 다시 시도해주세요.</p>
        </div>
      </div>
    );
  }

  if (!savingsDetail?.savings) {
    return (
      <div className="flex flex-1 flex-col items-center justify-center px-6 py-8">
        <div className="text-center text-gray-500">
          <p>적금 정보를 찾을 수 없습니다.</p>
        </div>
      </div>
    );
  }

  const handleNext = () => {
    goToNextStep();
  };

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col px-6 py-8">
        <h1 className="mb-2 text-xl font-bold text-gray-900">현재 적금 정보</h1>
        <p className="mb-6 text-gray-600">현재 설정된 적금 정보를 확인하세요</p>

        <div className="space-y-4 rounded-lg border bg-white p-6">
          <div className="flex items-center justify-between border-b py-2">
            <span className="text-gray-600">계좌번호</span>
            <span className="font-semibold">{savingsDetail.accountNumber}</span>
          </div>

          <div className="flex items-center justify-between border-b py-2">
            <span className="text-gray-600">상품명</span>
            <span className="font-semibold">
              {savingsDetail.product.productName}
            </span>
          </div>

          {savingsDetail.savings.autoTransfer ? (
            <>
              <div className="flex items-center justify-between border-b py-2">
                <span className="text-gray-600">월 납입금액</span>
                <span className="font-semibold">
                  {savingsDetail.savings.autoTransfer.amount.toLocaleString()}원
                </span>
              </div>

              <div className="flex items-center justify-between border-b py-2">
                <span className="text-gray-600">납입 주기</span>
                <span className="font-semibold">
                  {getCycleDisplay(savingsDetail.savings.autoTransfer.cycle)}
                </span>
              </div>

              <div className="flex items-center justify-between border-b py-2">
                <span className="text-gray-600">자동이체 날짜</span>
                <span className="font-semibold">
                  {getTransferDateDisplay(
                    savingsDetail.savings.autoTransfer.cycle,
                    savingsDetail.savings.autoTransfer.transferDay,
                  )}
                </span>
              </div>

              <div className="flex items-center justify-between border-b py-2">
                <span className="text-gray-600">연결 계좌</span>
                <span className="font-semibold">{autoAccountDisplay}</span>
              </div>
            </>
          ) : (
            <>
              <div className="flex items-center justify-center border-b py-4">
                <span className="text-gray-500">
                  자동이체 설정이 되어있지 않습니다
                </span>
              </div>
            </>
          )}

          <div className="flex items-center justify-between border-b py-2">
            <span className="text-gray-600">기본 금리</span>
            <span className="font-semibold">
              {(savingsDetail.baseRate / 100).toFixed(2)}%
            </span>
          </div>

          <div className="flex items-center justify-between py-2">
            <span className="text-gray-600">현재 잔액</span>
            <span className="font-semibold text-primary">
              {savingsDetail.balance.toLocaleString()}원
            </span>
          </div>
        </div>
      </div>

      {/* 하단 고정 버튼 */}
      <div className="fixed right-0 bottom-0 left-0 z-10 bg-white p-4 shadow-lg">
        <Button
          onClick={handleNext}
          className="h-12 w-full rounded-lg bg-primary text-white hover:bg-primary/90"
        >
          설정 변경하기
        </Button>
      </div>
    </>
  );
};

export default CurrentInfoStep;
