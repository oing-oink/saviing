import { useState } from 'react';
import { useAccountCreationStore } from '@/features/savings/store/useAccountCreationStore';
import { useStepProgress } from '@/features/savings/hooks/useStepProgress';
import { Button } from '@/shared/components/ui/button';

const UserInfoStep = () => {
  const { setForm, form } = useAccountCreationStore();
  const { goToNextStep, goToPreviousStep } = useStepProgress();
  const [name, setName] = useState(form.name);
  const [birth, setBirth] = useState(form.birth);
  const [phone, setPhone] = useState(form.phone);

  // 휴대폰 번호 포맷팅 함수
  const formatPhoneNumber = (value: string) => {
    // 숫자만 추출
    const numbers = value.replace(/[^\d]/g, '');

    // 최대 11자리까지만
    const trimmed = numbers.slice(0, 11);

    // 자동 포맷팅
    if (trimmed.length <= 3) {
      return trimmed;
    } else if (trimmed.length <= 7) {
      return `${trimmed.slice(0, 3)}-${trimmed.slice(3)}`;
    } else {
      return `${trimmed.slice(0, 3)}-${trimmed.slice(3, 7)}-${trimmed.slice(7)}`;
    }
  };

  // 휴대폰 번호 변경 핸들러
  const handlePhoneChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const formatted = formatPhoneNumber(e.target.value);
    setPhone(formatted);
  };

  // 생년월일 변경 핸들러
  const handleBirthChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedDate = e.target.value;
    const today = new Date().toISOString().split('T')[0];

    // 미래 날짜나 1900년 이전 날짜 입력 방지
    if (selectedDate > today || selectedDate < '1900-01-01') {
      return; // 변경하지 않음
    }

    setBirth(selectedDate);
  };

  const isValid = name.trim() && birth.trim() && phone.trim();

  const handleNext = () => {
    if (!isValid) {
      return;
    }
    setForm({ name, birth, phone });
    goToNextStep();
  };

  const handleBack = () => {
    goToPreviousStep();
  };

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col px-6 py-8 pb-24">
        <h1 className="mb-2 text-xl font-bold text-gray-900">
          가입 자격을 확인해드릴게요
        </h1>
        <p className="mb-6 text-gray-600">간단한 정보를 입력해주세요</p>

        {/* 이름 */}
        <div className="mb-4">
          <label className="mb-1 block text-sm font-medium text-gray-700">
            이름
          </label>
          <input
            type="text"
            placeholder="이름을 입력해주세요"
            value={name}
            onChange={e => setName(e.target.value)}
            className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-violet-500 focus:outline-none"
            maxLength={10}
          />
        </div>

        {/* 생년월일 */}
        <div className="mb-4">
          <label className="mb-1 block text-sm font-medium text-gray-700">
            생년월일
          </label>
          <input
            type="date"
            placeholder="연도-월-일"
            value={birth}
            onChange={handleBirthChange}
            className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-violet-500 focus:outline-none"
            min="1900-01-01"
            max={new Date().toISOString().split('T')[0]}
          />
        </div>

        {/* 휴대폰 번호 */}
        <div className="mb-4">
          <label className="mb-1 block text-sm font-medium text-gray-700">
            휴대폰 번호
          </label>
          <input
            type="tel"
            placeholder="010-0000-0000"
            value={phone}
            onChange={handlePhoneChange}
            className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-violet-500 focus:outline-none"
            maxLength={13}
          />
        </div>
      </div>

      {/* 하단 고정 버튼 */}
      <div
        className="fixed right-0 bottom-0 left-0 z-10 bg-white p-4"
        style={{ borderTop: 'none' }}
      >
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
            disabled={!isValid}
            className="h-12 flex-1 rounded-lg bg-primary text-white hover:bg-primary/90 disabled:bg-gray-300 disabled:text-gray-500"
          >
            자격 확인하기
          </Button>
        </div>
      </div>
    </>
  );
};

export default UserInfoStep;
