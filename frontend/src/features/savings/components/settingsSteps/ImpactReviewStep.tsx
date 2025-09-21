import { useEffect } from 'react';
import { useSavingsSettingsStore } from '@/features/savings/store/useSavingsSettingsStore';
import { useSavingsSettingsChange } from '@/features/savings/hooks/useSavingsSettingsChange';
import { Button } from '@/shared/components/ui/button';

const ImpactReviewStep = () => {
  const { currentInfo, newSettings, impactAnalysis, setImpactAnalysis } =
    useSavingsSettingsStore();
  const { goToNextStep, goToPreviousStep } = useSavingsSettingsChange();

  // TODO: 실제 API 호출로 영향 분석 계산
  useEffect(() => {
    if (currentInfo && Object.keys(newSettings).length > 0) {
      // Mock 계산 - 실제로는 API에서 계산된 결과를 받아올 예정
      const mockAnalysis = {
        finalAmountChange: 500000, // 50만원 증가
        interestChange: 25000, // 이자 2.5만원 증가
        completionDateChange: '2개월 연장', // 완료일 변화
        monthlyBurdenChange: 50000, // 월 부담금 5만원 증가
      };

      setImpactAnalysis(mockAnalysis);
    }
  }, [currentInfo, newSettings, setImpactAnalysis]);

  const handleNext = () => {
    goToNextStep();
  };

  const handleBack = () => {
    goToPreviousStep();
  };

  if (!impactAnalysis) {
    return (
      <div className="-mt-16 flex min-h-screen flex-col items-center justify-center p-6">
        <div className="h-8 w-8 animate-spin rounded-full border-b-2 border-primary"></div>
        <p className="mt-4 text-gray-600">변경 영향을 분석 중입니다...</p>
      </div>
    );
  }

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col px-6 py-8">
        <h1 className="mb-2 text-xl font-bold text-gray-900">변경 영향 검토</h1>
        <p className="mb-6 text-gray-600">
          변경된 설정이 적금에 미치는 영향을 확인해주세요
        </p>

        <div className="space-y-4 rounded-lg border bg-white p-6">
          <div className="border-b pb-4">
            <h3 className="mb-2 font-semibold text-gray-900">변경 사항 요약</h3>
            <div className="space-y-2">
              {newSettings.newAmount && (
                <div className="flex justify-between text-sm">
                  <span>월 납입금액</span>
                  <span className="font-medium">
                    300,000원 → {newSettings.newAmount?.toLocaleString()}원
                  </span>
                </div>
              )}
              {newSettings.newTransferDate && (
                <div className="flex justify-between text-sm">
                  <span>자동이체 날짜</span>
                  <span className="font-medium">
                    25일 → {newSettings.newTransferDate}일
                  </span>
                </div>
              )}
              {newSettings.newAutoAccount && (
                <div className="flex justify-between text-sm">
                  <span>연결 계좌</span>
                  <span className="font-medium">
                    하나 입출금통장 (*1234) → 새 계좌 (*
                    {newSettings.newAutoAccount.slice(-4)})
                  </span>
                </div>
              )}
            </div>
          </div>

          <div className="space-y-3">
            <h3 className="font-semibold text-gray-900">예상 변화</h3>

            <div className="flex items-center justify-between rounded-lg bg-green-50 px-3 py-2">
              <span className="text-sm text-gray-700">예상 최종 금액</span>
              <span className="font-semibold text-green-600">
                +{impactAnalysis.finalAmountChange.toLocaleString()}원
              </span>
            </div>

            <div className="flex items-center justify-between rounded-lg bg-blue-50 px-3 py-2">
              <span className="text-sm text-gray-700">이자 수익</span>
              <span className="font-semibold text-blue-600">
                +{impactAnalysis.interestChange.toLocaleString()}원
              </span>
            </div>

            <div className="flex items-center justify-between rounded-lg bg-purple-50 px-3 py-2">
              <span className="text-sm text-gray-700">월 부담금 변화</span>
              <span className="font-semibold text-purple-600">
                +{impactAnalysis.monthlyBurdenChange.toLocaleString()}원
              </span>
            </div>
          </div>

          <div className="mt-4 rounded-lg border border-yellow-200 bg-yellow-50 p-3">
            <p className="text-sm text-yellow-800">
              ⚠️ 설정 변경 후에는 일정 기간 동안 재변경이 제한될 수 있습니다.
            </p>
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
            className="h-12 flex-1 rounded-lg bg-primary text-white hover:bg-primary/90"
          >
            변경 확정
          </Button>
        </div>
      </div>
    </>
  );
};

export default ImpactReviewStep;
