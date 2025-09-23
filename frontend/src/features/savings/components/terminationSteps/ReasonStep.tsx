import { useState } from 'react';
import { useSavingsTermination } from '@/features/savings/hooks/useSavingsTermination';
import { Button } from '@/shared/components/ui/button';

const ReasonStep = () => {
  const [selectedReason, setSelectedReason] = useState<string>('');
  const [customReason, setCustomReason] = useState<string>('');
  const { goToNextStep, goToPrevStep } = useSavingsTermination();

  const reasons = [
    { id: 'goal_acheived', label: '목표 달성' },
    { id: 'urgent_money', label: '긴급한 자금 필요' },
    { id: 'other_financial_product', label: '다른 금융상품 이용' },
    { id: 'dissatisfied', label: '서비스 불만족' },
    { id: 'other', label: '기타' },
  ];

  const handleNext = () => {
    if (!selectedReason) {return;}
    goToNextStep();
  };

  const handleBack = () => {
    goToPrevStep();
  };

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col px-6 py-8 pb-24">
        <h1 className="mb-2 text-xl font-bold text-gray-900">해지 사유 선택</h1>
        <p className="mb-6 text-gray-600">
          적금 해지 사유를 선택해 주세요. 더 나은 서비스 제공을 위해 활용됩니다.
        </p>

        <div className="space-y-3">
          {reasons.map((reason) => {
            const isSelected = selectedReason === reason.id;
            return (
              <div
                key={reason.id}
                onClick={() => setSelectedReason(reason.id)}
                className={`cursor-pointer rounded-lg border p-4 transition-all ${
                  isSelected
                    ? 'border-primary bg-primary/10'
                    : 'border-gray-200 hover:border-gray-300'
                }`}
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-3">
                    <div className="flex-1">
                      <span className="font-medium text-gray-900">{reason.label}</span>
                    </div>
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

        {selectedReason === 'other' && (
          <div className="mt-6">
            <label className="mb-2 block text-sm font-medium text-gray-700">
              상세 사유 (선택)
            </label>
            <textarea
              value={customReason}
              onChange={(e) => setCustomReason(e.target.value)}
              placeholder="해지 사유를 자세히 입력해 주세요."
              className="w-full rounded-lg border border-gray-300 p-3 text-sm focus:border-primary focus:outline-none"
              rows={4}
            />
          </div>
        )}
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
            disabled={!selectedReason}
            className="h-12 flex-1 rounded-lg bg-primary text-white hover:bg-primary/90 disabled:bg-gray-300 disabled:text-gray-500"
          >
            다음
          </Button>
        </div>
      </div>
    </>
  );
};

export default ReasonStep;