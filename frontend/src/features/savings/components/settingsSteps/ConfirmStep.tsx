import { useState, useEffect } from 'react';
import { useSavingsSettingsStore } from '@/features/savings/store/useSavingsSettingsStore';
import { useSavingsSettingsChange } from '@/features/savings/hooks/useSavingsSettingsChange';
import { Button } from '@/shared/components/ui/button';

const ConfirmStep = () => {
  const { currentInfo, newSettings, impactAnalysis, setImpactAnalysis } =
    useSavingsSettingsStore();
  const { goToNextStep, goToPreviousStep } = useSavingsSettingsChange();
  const [isProcessing, setIsProcessing] = useState(false);
  const [hasAgreed, setHasAgreed] = useState(false);

  // Mock 영향 분석 계산 (실제로는 API 호출)
  const calculateImpact = () => {
    if (!currentInfo) {
      return;
    }

    // 간단한 Mock 계산
    const amountChange = newSettings.newAmount
      ? (newSettings.newAmount - currentInfo.currentAmount) * 12
      : 0;
    const interestChange = amountChange * 0.03; // 3% 이자율 적용

    const analysis = {
      finalAmountChange: amountChange,
      interestChange: Math.round(interestChange),
      completionDateChange: '변경 없음',
      monthlyBurdenChange: newSettings.newAmount
        ? newSettings.newAmount - currentInfo.currentAmount
        : 0,
    };

    setImpactAnalysis(analysis);
  };

  // 컴포넌트 마운트 시 영향 분석 계산
  useEffect(() => {
    if (!impactAnalysis && currentInfo) {
      calculateImpact();
    }
  }, [currentInfo, newSettings, impactAnalysis]);

  const handleConfirm = async () => {
    if (!hasAgreed) {
      return;
    }

    setIsProcessing(true);

    try {
      // TODO: 실제 API 호출로 설정 변경 요청
      await new Promise(resolve => setTimeout(resolve, 2000)); // Mock delay

      // 성공 시 완료 페이지로 이동
      goToNextStep();
    } catch {
      // 에러 처리
      alert('설정 변경 중 오류가 발생했습니다. 다시 시도해주세요.');
      setIsProcessing(false);
    }
  };

  const handleBack = () => {
    if (isProcessing) {
      return;
    }
    goToPreviousStep();
  };

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col px-6 py-8">
        <h1 className="mb-2 text-xl font-bold text-gray-900">최종 확인</h1>
        <p className="mb-6 text-gray-600">
          아래 내용을 확인하고 설정 변경을 완료해주세요
        </p>

        <div className="space-y-6 rounded-lg border bg-white p-6">
          {/* 변경 내용 요약 */}
          <div className="border-b pb-4">
            <h3 className="mb-3 font-semibold text-gray-900">변경 내용</h3>
            <div className="space-y-2">
              {newSettings.newAmount && (
                <div className="rounded-lg bg-primary/10 p-3">
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-700">월 납입금액</span>
                    <span className="font-medium text-primary">
                      {currentInfo?.currentAmount.toLocaleString()}원 →{' '}
                      {newSettings.newAmount.toLocaleString()}원
                    </span>
                  </div>
                </div>
              )}
              {newSettings.newTransferDate && (
                <div className="rounded-lg bg-primary/10 p-3">
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-700">자동이체 날짜</span>
                    <span className="font-medium text-primary">
                      매월 {currentInfo?.currentTransferDate}일 → 매월{' '}
                      {newSettings.newTransferDate}일
                    </span>
                  </div>
                </div>
              )}
              {newSettings.newAutoAccount && (
                <div className="rounded-lg bg-primary/10 p-3">
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-700">연결 계좌</span>
                    <span className="font-medium text-primary">
                      {currentInfo?.currentAutoAccount
                        ? `현재 계좌 (*${currentInfo.currentAutoAccount.slice(-4)})`
                        : '하나 입출금통장 (*1234)'}{' '}
                      → 새 계좌 (*{newSettings.newAutoAccount.slice(-4)})
                    </span>
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* 예상 변화 요약 */}
          {impactAnalysis && (
            <div className="border-b pb-4">
              <h3 className="mb-3 font-semibold text-gray-900">
                예상 변화 요약
              </h3>
              <div className="grid grid-cols-2 gap-2 text-sm">
                <div className="rounded bg-gray-50 p-2 text-center">
                  <div className="text-gray-600">최종 금액</div>
                  <div className="font-semibold text-green-600">
                    +{impactAnalysis.finalAmountChange.toLocaleString()}원
                  </div>
                </div>
                <div className="rounded bg-gray-50 p-2 text-center">
                  <div className="text-gray-600">이자 수익</div>
                  <div className="font-semibold text-blue-600">
                    +{impactAnalysis.interestChange.toLocaleString()}원
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* 동의 체크박스 */}
          <div className="space-y-3">
            <div className="flex items-start space-x-3">
              <input
                type="checkbox"
                id="agree"
                checked={hasAgreed}
                onChange={e => setHasAgreed(e.target.checked)}
                className="mt-1 h-4 w-4 rounded border-gray-300 text-primary focus:ring-primary"
              />
              <label htmlFor="agree" className="text-sm text-gray-700">
                위 내용을 확인했으며, 적금 설정 변경에 동의합니다. 변경 후 일정
                기간 동안 재변경이 제한될 수 있음에 동의합니다.
              </label>
            </div>
          </div>
        </div>
      </div>

      {/* 하단 고정 버튼 */}
      <div className="fixed right-0 bottom-0 left-0 z-10 bg-white p-4">
        <div className="flex space-x-3">
          <Button
            variant="outline"
            onClick={handleBack}
            disabled={isProcessing}
            className="h-12 flex-1 rounded-lg"
          >
            이전
          </Button>
          <Button
            onClick={handleConfirm}
            disabled={!hasAgreed || isProcessing}
            className="flex h-12 flex-1 items-center justify-center space-x-2 rounded-lg bg-primary text-white hover:bg-primary/90 disabled:bg-gray-300 disabled:text-gray-500"
          >
            {isProcessing ? (
              <>
                <div className="h-4 w-4 animate-spin rounded-full border-b-2 border-white"></div>
                <span>처리 중...</span>
              </>
            ) : (
              <span>설정 변경 완료</span>
            )}
          </Button>
        </div>
      </div>
    </>
  );
};

export default ConfirmStep;
