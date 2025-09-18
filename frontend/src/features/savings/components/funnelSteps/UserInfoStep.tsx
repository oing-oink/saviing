import { useState } from 'react';
import { useAccountCreationStore } from '@/features/savings/store/useAccountCreationStore';
import { useStepProgress } from '@/features/savings/hooks/useStepProgress';
import { Button } from '@/shared/components/ui/button';

const UserInfoStep = () => {
  const { setForm, form } = useAccountCreationStore();
  const { goToNextStep } = useStepProgress();
  const [name, setName] = useState(form.name);
  const [birth, setBirth] = useState(form.birth);
  const [phone, setPhone] = useState(form.phone);

  const isValid = name.trim() && birth.trim() && phone.trim();

  const handleNext = () => {
    if (!isValid) {
      return;
    }
    setForm({ name, birth, phone });
    goToNextStep();
  };

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col px-6 py-8">
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
            onChange={e => setBirth(e.target.value)}
            className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-violet-500 focus:outline-none"
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
            onChange={e => setPhone(e.target.value)}
            className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-violet-500 focus:outline-none"
          />
        </div>
      </div>

      {/* 하단 버튼 */}
      <div className="bg-white p-4">
        <Button
          onClick={handleNext}
          disabled={!isValid}
          className="h-12 w-full rounded-lg bg-primary text-white disabled:bg-gray-200 disabled:text-gray-400"
        >
          자격 확인하기
        </Button>
      </div>
    </>
  );
};

export default UserInfoStep;
