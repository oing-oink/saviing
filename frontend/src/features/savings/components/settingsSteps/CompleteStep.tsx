import { useNavigate } from 'react-router-dom';
import { useSavingsSettingsStore } from '@/features/savings/store/useSavingsSettingsStore';
import { useSavingsSettingsChange } from '@/features/savings/hooks/useSavingsSettingsChange';
import { Button } from '@/shared/components/ui/button';
import { PAGE_PATH } from '@/shared/constants/path';

const CompleteStep = () => {
  const navigate = useNavigate();
  const { newSettings, reset } = useSavingsSettingsStore();
  const { cancelAndGoBack } = useSavingsSettingsChange();


  const handleGoToDetail = () => {
    // 설정 변경 상태 초기화
    reset();

    // 적금 상세 페이지로 이동
    cancelAndGoBack();
  };

  const handleGoToSavingsList = () => {
    // 설정 변경 상태 초기화
    reset();

    // 적금 목록 페이지로 이동
    navigate(PAGE_PATH.SAVINGS);
  };

  const getChangedSettings = () => {
    const changes = [];
    if (newSettings.newAmount) {
      changes.push('월 납입금액');
    }
    if (newSettings.newTransferDate) {
      changes.push('자동이체 날짜');
    }
    if (newSettings.newAutoAccount) {
      changes.push('연결 계좌');
    }
    return changes;
  };

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col items-center justify-center px-6 py-8 text-center">
        {/* 성공 아이콘 */}
        <div className="mx-auto mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-green-100">
          <svg
            className="h-10 w-10 text-green-600"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M5 13l4 4L19 7"
            />
          </svg>
        </div>

        <h2 className="mb-2 text-2xl font-bold text-gray-900">
          설정 변경 완료! 🎉
        </h2>
        <p className="mb-6 text-gray-600">
          적금 설정이 성공적으로 변경되었습니다.
        </p>

        {/* 변경된 설정 요약 */}
        <div className="mb-8 rounded-lg bg-white p-6 shadow-sm">
          <h3 className="mb-4 font-semibold text-gray-900">변경 완료된 설정</h3>
          <div className="space-y-2">
            {getChangedSettings().map((setting, index) => (
              <div
                key={index}
                className="flex items-center justify-center space-x-2 rounded-lg bg-green-50 px-3 py-2"
              >
                <svg
                  className="h-4 w-4 text-green-600"
                  fill="currentColor"
                  viewBox="0 0 20 20"
                >
                  <path
                    fillRule="evenodd"
                    d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                    clipRule="evenodd"
                  />
                </svg>
                <span className="text-sm font-medium text-green-700">
                  {setting}
                </span>
              </div>
            ))}
          </div>
        </div>

        {/* 안내 메시지 */}
        <div className="relative mb-8 rounded-lg border border-blue-200 bg-blue-50 p-4">
          <svg
            className="absolute top-4 left-4 h-5 w-5 text-blue-600"
            fill="currentColor"
            viewBox="0 0 20 20"
          >
            <path
              fillRule="evenodd"
              d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z"
              clipRule="evenodd"
            />
          </svg>
          <div className="text-center text-sm text-blue-700">
            <p className="mb-1 font-medium">알려드립니다</p>
            <ul className="mt-3 space-y-1 text-xs">
              <li>• 변경된 설정은 다음 이체일부터 적용됩니다</li>
              <li>• 변경 후 30일 동안 재변경이 제한됩니다</li>
              <li>• 변경 내역은 거래내역에서 확인하실 수 있습니다</li>
            </ul>
          </div>
        </div>
      </div>

      {/* 하단 고정 버튼 */}
      <div className="fixed right-0 bottom-0 left-0 z-10 bg-white p-4">
        <div className="space-y-3">
          <Button
            onClick={handleGoToDetail}
            className="h-12 w-full rounded-lg bg-primary text-white hover:bg-primary/90"
          >
            적금 상세보기
          </Button>
          <Button
            variant="outline"
            onClick={handleGoToSavingsList}
            className="h-12 w-full rounded-lg"
          >
            적금 목록으로
          </Button>
        </div>
      </div>
    </>
  );
};

export default CompleteStep;
