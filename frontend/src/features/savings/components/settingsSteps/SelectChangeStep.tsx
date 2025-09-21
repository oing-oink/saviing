import {
  useSavingsSettingsStore,
  type ChangeType,
} from '@/features/savings/store/useSavingsSettingsStore';
import { useSavingsSettingsChange } from '@/features/savings/hooks/useSavingsSettingsChange';
import { Button } from '@/shared/components/ui/button';

const SelectChangeStep = () => {
  const { selectedChangeTypes, toggleChangeType } = useSavingsSettingsStore();
  const { goToNextStep, goToPreviousStep } = useSavingsSettingsChange();

  const changeOptions = [
    {
      type: 'AMOUNT' as ChangeType,
      title: '월 납입금액 변경',
      description: '매월 납입하는 금액을 조정합니다',
    },
    {
      type: 'TRANSFER_DATE' as ChangeType,
      title: '자동이체 날짜 변경',
      description: '매월 자동이체되는 날짜를 변경합니다',
    },
    {
      type: 'AUTO_ACCOUNT' as ChangeType,
      title: '연결 계좌 변경',
      description: '자동이체가 연결된 입출금 계좌를 변경합니다',
    },
  ];

  const handleNext = () => {
    if (selectedChangeTypes.length === 0) {
      return;
    }
    goToNextStep();
  };

  const handleBack = () => {
    goToPreviousStep();
  };

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col px-6 py-8">
        <h1 className="mb-2 text-xl font-bold text-gray-900">
          변경할 설정 선택
        </h1>
        <p className="mb-6 text-gray-600">
          변경하고 싶은 설정을 선택해주세요 (복수 선택 가능)
        </p>

        <div className="space-y-3">
          {changeOptions.map(option => {
            const isSelected = selectedChangeTypes.includes(option.type);
            return (
              <div
                key={option.type}
                onClick={() => toggleChangeType(option.type)}
                className={`cursor-pointer rounded-lg border p-4 transition-all ${
                  isSelected
                    ? 'border-primary bg-primary/10'
                    : 'border-gray-200 hover:border-gray-300'
                }`}
              >
                <div className="flex items-start space-x-3">
                  <div className="flex-1">
                    <h3 className="font-semibold text-gray-900">
                      {option.title}
                    </h3>
                    <p className="mt-1 text-sm text-gray-600">
                      {option.description}
                    </p>
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
            disabled={selectedChangeTypes.length === 0}
            className="h-12 flex-1 rounded-lg bg-primary text-white hover:bg-primary/90 disabled:bg-gray-300 disabled:text-gray-500"
          >
            다음
          </Button>
        </div>
      </div>
    </>
  );
};

export default SelectChangeStep;
