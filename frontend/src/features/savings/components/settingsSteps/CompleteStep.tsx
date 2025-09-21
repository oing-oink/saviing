import { useNavigate } from 'react-router-dom';
import { useSavingsSettingsStore } from '@/features/savings/store/useSavingsSettingsStore';
import { useSavingsSettingsChange } from '@/features/savings/hooks/useSavingsSettingsChange';
import { Button } from '@/shared/components/ui/button';
import { PAGE_PATH, createSavingsDetailPath } from '@/shared/constants/path';

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
    navigate(PAGE_PATH.SAVINGS, { replace: true });
  };

  const getChangedSettings = () => {
    const changes = [];
    if (newSettings.newAmount) changes.push('월 납입금액');
    if (newSettings.newTransferDate) changes.push('자동이체 날짜');
    if (newSettings.newAutoAccount) changes.push('연결 계좌');
    return changes;
  };

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col items-center justify-center px-6 py-8 text-center">
        {/* 성공 아이콘 */}
        <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6">
          <svg
            className="w-10 h-10 text-green-600"
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

        <h2 className="text-2xl font-bold text-gray-900 mb-2">
          설정 변경 완료! 🎉
        </h2>
        <p className="text-gray-600 mb-6">
          적금 설정이 성공적으로 변경되었습니다.
        </p>

        {/* 변경된 설정 요약 */}
        <div className="bg-white rounded-lg p-6 shadow-sm mb-8">
          <h3 className="font-semibold text-gray-900 mb-4">변경 완료된 설정</h3>
          <div className="space-y-2">
            {getChangedSettings().map((setting, index) => (
              <div
                key={index}
                className="flex items-center justify-center space-x-2 bg-green-50 rounded-lg py-2 px-3"
              >
                <svg className="w-4 h-4 text-green-600" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                </svg>
                <span className="text-sm font-medium text-green-700">{setting}</span>
              </div>
            ))}
          </div>
        </div>

        {/* 안내 메시지 */}
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-8 relative">
          <svg className="w-5 h-5 text-blue-600 absolute top-4 left-4" fill="currentColor" viewBox="0 0 20 20">
            <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clipRule="evenodd" />
          </svg>
          <div className="text-sm text-blue-700 text-center">
            <p className="font-medium mb-1">알려드립니다</p>
            <ul className="space-y-1 text-xs mt-3">
              <li>• 변경된 설정은 다음 이체일부터 적용됩니다</li>
              <li>• 변경 후 30일 동안 재변경이 제한됩니다</li>
              <li>• 변경 내역은 거래내역에서 확인하실 수 있습니다</li>
            </ul>
          </div>
        </div>

      </div>

      {/* 하단 고정 버튼 */}
      <div className="fixed bottom-0 left-0 right-0 bg-white p-4 z-10">
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