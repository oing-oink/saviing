import { useEffect } from 'react';
import { useSavingsSettingsStore } from '@/features/savings/store/useSavingsSettingsStore';
import { useSavingsSettingsChange } from '@/features/savings/hooks/useSavingsSettingsChange';
import { useAccountsList } from '@/features/savings/query/useSavingsQuery';
import { Button } from '@/shared/components/ui/button';

const CurrentInfoStep = () => {
  const { setCurrentInfo } = useSavingsSettingsStore();
  const { goToNextStep } = useSavingsSettingsChange();

  // 계좌 목록 조회 (자동이체 연결 계좌 정보 표시용)
  const { data: accounts } = useAccountsList();

  // TODO: 실제 API 호출로 현재 적금 정보 가져오기
  useEffect(() => {
    // Mock 데이터 - 실제로는 API에서 가져올 데이터
    const mockCurrentInfo = {
      currentAmount: 300000,
      currentTransferDate: '25',
      currentBalance: 3600000,
      currentAutoAccount: '123456789', // 현재 연결된 자동이체 계좌 ID
    };

    setCurrentInfo(mockCurrentInfo);
  }, [setCurrentInfo]);

  // 현재 연결된 자동이체 계좌 찾기
  const currentAutoAccount = accounts?.find(
    account => String(account.accountId) === '123456789', // Mock ID - 실제로는 API에서 받은 현재 계좌 ID
  );

  // 계좌가 없으면 기본값 표시
  const autoAccountDisplay = currentAutoAccount
    ? `${currentAutoAccount.product.productName} (*${currentAutoAccount.accountNumber.slice(-4)})`
    : '하나 입출금통장 (*1234)';

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
            <span className="text-gray-600">월 납입금액</span>
            <span className="font-semibold">300,000원</span>
          </div>

          <div className="flex items-center justify-between border-b py-2">
            <span className="text-gray-600">자동이체 날짜</span>
            <span className="font-semibold">매월 25일</span>
          </div>

          <div className="flex items-center justify-between border-b py-2">
            <span className="text-gray-600">연결 계좌</span>
            <span className="font-semibold">{autoAccountDisplay}</span>
          </div>

          <div className="flex items-center justify-between py-2">
            <span className="text-gray-600">현재 누적 금액</span>
            <span className="font-semibold text-primary">3,600,000원</span>
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
