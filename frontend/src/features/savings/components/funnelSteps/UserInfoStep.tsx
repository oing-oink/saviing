import { useState } from 'react';
import { useAccountCreationStore } from '@/features/savings/store/useAccountCreationStore';
import { useStepProgress } from '@/features/savings/hooks/useStepProgress';
import { Button } from '@/shared/components/ui/button';
import {
  validateName,
  validateBirthDate,
  validatePhoneNumber,
  formatPhoneNumber,
} from '@/features/savings/utils/validation';

const UserInfoStep = () => {
  const { setForm, form } = useAccountCreationStore();
  const { goToNextStep } = useStepProgress();
  const [name, setName] = useState(form.name);
  const [birth, setBirth] = useState(form.birth);
  const [phone, setPhone] = useState(form.phone);

  // 각 필드별 에러 상태
  const [nameError, setNameError] = useState('');
  const [birthError, setBirthError] = useState('');
  const [phoneError, setPhoneError] = useState('');

  // 이름 입력 처리
  const handleNameChange = (value: string) => {
    setName(value);
    const validation = validateName(value);
    setNameError(validation.isValid ? '' : validation.message);
  };

  // 생년월일 입력 처리
  const handleBirthChange = (value: string) => {
    setBirth(value);
    const validation = validateBirthDate(value);
    setBirthError(validation.isValid ? '' : validation.message);
  };

  // 휴대폰 번호 입력 처리
  const handlePhoneChange = (value: string) => {
    const formattedPhone = formatPhoneNumber(value);
    setPhone(formattedPhone);
    const validation = validatePhoneNumber(formattedPhone);
    setPhoneError(validation.isValid ? '' : validation.message);
  };

  // 전체 유효성 검사
  const isValid =
    validateName(name).isValid &&
    validateBirthDate(birth).isValid &&
    validatePhoneNumber(phone).isValid;

  const handleNext = () => {
    // 최종 검증
    const nameValidation = validateName(name);
    const birthValidation = validateBirthDate(birth);
    const phoneValidation = validatePhoneNumber(phone);

    setNameError(nameValidation.isValid ? '' : nameValidation.message);
    setBirthError(birthValidation.isValid ? '' : birthValidation.message);
    setPhoneError(phoneValidation.isValid ? '' : phoneValidation.message);

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
            onChange={e => handleNameChange(e.target.value)}
            className={`w-full rounded-lg border px-3 py-2 text-sm focus:outline-none ${
              nameError
                ? 'border-red-500 focus:border-red-500'
                : 'border-gray-300 focus:border-violet-500'
            }`}
          />
          {nameError && (
            <p className="mt-1 text-xs text-red-600">{nameError}</p>
          )}
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
            onChange={e => handleBirthChange(e.target.value)}
            className={`w-full rounded-lg border px-3 py-2 text-sm focus:outline-none ${
              birthError
                ? 'border-red-500 focus:border-red-500'
                : 'border-gray-300 focus:border-violet-500'
            }`}
          />
          {birthError && (
            <p className="mt-1 text-xs text-red-600">{birthError}</p>
          )}
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
            onChange={e => handlePhoneChange(e.target.value)}
            maxLength={13}
            className={`w-full rounded-lg border px-3 py-2 text-sm focus:outline-none ${
              phoneError
                ? 'border-red-500 focus:border-red-500'
                : 'border-gray-300 focus:border-violet-500'
            }`}
          />
          {phoneError && (
            <p className="mt-1 text-xs text-red-600">{phoneError}</p>
          )}
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
