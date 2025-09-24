import { useState } from 'react';
import { useSavingsTermination } from '@/features/savings/hooks/useSavingsTermination';
import { Button } from '@/shared/components/ui/button';

const AuthStep = () => {
  const [selectedAuth, setSelectedAuth] = useState<string>('');
  const { goToNextStep, goToPrevStep } = useSavingsTermination();

  const authOptions = [
    {
      id: 'phone',
      title: '휴대폰 인증',
      description: 'SMS 인증번호 전송',
      icon: ' ',
    },
    {
      id: 'certificate',
      title: '공인인증서',
      description: '공인인증서로 인증',
      icon: ' ',
    },
    {
      id: 'biometric',
      title: '생체 인증',
      description: '지문/Face ID 인증',
      icon: ' ',
    },
  ];

  const handleNext = () => {
    if (!selectedAuth) {
      return;
    }
    goToNextStep();
  };

  const handleBack = () => {
    goToPrevStep();
  };

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col px-6 py-8 pb-24">
        <h1 className="mb-2 text-xl font-bold text-gray-900">본인 인증</h1>
        <p className="mb-6 text-gray-600">
          적금 해지를 위해 본인 인증을 진행해 주세요.
        </p>

        <div className="space-y-3">
          {authOptions.map(option => {
            const isSelected = selectedAuth === option.id;
            return (
              <div
                key={option.id}
                onClick={() => setSelectedAuth(option.id)}
                className={`cursor-pointer rounded-lg border p-4 transition-all ${
                  isSelected
                    ? 'border-primary bg-primary/10'
                    : 'border-gray-200 hover:border-gray-300'
                }`}
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-3">
                    <div className="text-2xl">{option.icon}</div>
                    <div>
                      <div className="font-medium text-gray-900">
                        {option.title}
                      </div>
                      <div className="text-sm text-gray-500">
                        {option.description}
                      </div>
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
            disabled={!selectedAuth}
            className="h-12 flex-1 rounded-lg bg-primary text-white hover:bg-primary/90 disabled:bg-gray-300 disabled:text-gray-500"
          >
            다음
          </Button>
        </div>
      </div>
    </>
  );
};

export default AuthStep;
