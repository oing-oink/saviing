/**
 * 계좌 생성 폼 validation 유틸리티
 */

/**
 * 이름 validation
 * - 특수문자, 숫자 금지
 * - 최대 6자
 */
export const validateName = (name: string): { isValid: boolean; message: string } => {
  const trimmedName = name.trim();

  if (!trimmedName) {
    return { isValid: false, message: '이름을 입력해주세요.' };
  }

  if (trimmedName.length > 6) {
    return { isValid: false, message: '이름은 최대 6자까지 입력 가능합니다.' };
  }

  // 한글, 영문만 허용 (특수문자, 숫자 금지)
  const nameRegex = /^[가-힣a-zA-Z\s]+$/;
  if (!nameRegex.test(trimmedName)) {
    return { isValid: false, message: '이름은 한글과 영문만 입력 가능합니다.' };
  }

  return { isValid: true, message: '' };
};

/**
 * 생년월일 validation
 * - 미래 날짜 금지
 * - YYYY-MM-DD 형식
 */
export const validateBirthDate = (birthDate: string): { isValid: boolean; message: string } => {
  if (!birthDate) {
    return { isValid: false, message: '생년월일을 입력해주세요.' };
  }

  const selectedDate = new Date(birthDate);
  const today = new Date();

  // 미래 날짜 체크
  if (selectedDate > today) {
    return { isValid: false, message: '미래 날짜는 선택할 수 없습니다.' };
  }

  // 너무 오래된 날짜 체크 (1900년 이전)
  const minDate = new Date('1900-01-01');
  if (selectedDate < minDate) {
    return { isValid: false, message: '유효하지 않은 생년월일입니다.' };
  }

  return { isValid: true, message: '' };
};

/**
 * 휴대폰 번호 validation
 * - 숫자만 허용
 * - 010으로 시작하는 11자리
 */
export const validatePhoneNumber = (phone: string): { isValid: boolean; message: string } => {
  const trimmedPhone = phone.trim();

  if (!trimmedPhone) {
    return { isValid: false, message: '휴대폰 번호를 입력해주세요.' };
  }

  // 숫자만 허용 (하이픈 제거)
  const phoneNumbers = trimmedPhone.replace(/-/g, '');
  const phoneRegex = /^\d+$/;

  if (!phoneRegex.test(phoneNumbers)) {
    return { isValid: false, message: '휴대폰 번호는 숫자만 입력 가능합니다.' };
  }

  // 010으로 시작하는 11자리 체크
  const validPhoneRegex = /^010\d{8}$/;
  if (!validPhoneRegex.test(phoneNumbers)) {
    return { isValid: false, message: '올바른 휴대폰 번호를 입력해주세요. (예: 010-1234-5678)' };
  }

  return { isValid: true, message: '' };
};

/**
 * 납입금액 validation
 * - 최대 10,000,000원
 * - 숫자만 허용
 * - 최소 1,000원 이상
 */
export const validateDepositAmount = (amount: string): { isValid: boolean; message: string } => {
  const trimmedAmount = amount.trim();

  if (!trimmedAmount) {
    return { isValid: false, message: '납입금액을 입력해주세요.' };
  }

  const numericAmount = Number(trimmedAmount);

  if (isNaN(numericAmount)) {
    return { isValid: false, message: '숫자만 입력 가능합니다.' };
  }

  if (numericAmount < 1000) {
    return { isValid: false, message: '최소 1,000원 이상 입력해주세요.' };
  }

  if (numericAmount > 10000000) {
    return { isValid: false, message: '최대 10,000,000원까지 입력 가능합니다.' };
  }

  return { isValid: true, message: '' };
};

/**
 * 월 납입액 validation
 * - 숫자만 허용
 * - 최소 1,000원 이상
 * - 최대 10,000,000원
 */
export const validateMonthlyAmount = (amount: string): { isValid: boolean; message: string } => {
  const trimmedAmount = amount.trim();

  if (!trimmedAmount) {
    return { isValid: false, message: '월 납입액을 입력해주세요.' };
  }

  const numericAmount = Number(trimmedAmount);

  if (isNaN(numericAmount)) {
    return { isValid: false, message: '숫자만 입력 가능합니다.' };
  }

  if (numericAmount < 1000) {
    return { isValid: false, message: '최소 1,000원 이상 입력해주세요.' };
  }

  if (numericAmount > 10000000) {
    return { isValid: false, message: '최대 10,000,000원까지 입력 가능합니다.' };
  }

  return { isValid: true, message: '' };
};

/**
 * 휴대폰 번호 포맷팅 (하이픈 자동 추가)
 */
export const formatPhoneNumber = (phone: string): string => {
  const numbers = phone.replace(/\D/g, '');

  if (numbers.length <= 3) {
    return numbers;
  } else if (numbers.length <= 7) {
    return `${numbers.slice(0, 3)}-${numbers.slice(3)}`;
  } else {
    return `${numbers.slice(0, 3)}-${numbers.slice(3, 7)}-${numbers.slice(7, 11)}`;
  }
};

/**
 * 숫자 포맷팅 (천 단위 콤마)
 */
export const formatNumber = (value: string): string => {
  const numbers = value.replace(/\D/g, '');
  return numbers.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
};