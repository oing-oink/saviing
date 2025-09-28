import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useSavingsSettingsStore } from '@/features/savings/store/useSavingsSettingsStore';
import { useSavingsSettingsChange } from '@/features/savings/hooks/useSavingsSettingsChange';
import {
  useAccountsList,
  useSavingsAccountDetail,
} from '@/features/savings/query/useSavingsQuery';
import { Button } from '@/shared/components/ui/button';
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/shared/components/ui/accordion';

const NewSettingsStep = () => {
  const { accountId } = useParams<{ accountId: string }>();
  const { selectedChangeTypes, newSettings, updateNewSettings } =
    useSavingsSettingsStore();
  const { goToNextStep, goToPreviousStep } = useSavingsSettingsChange();

  // 계좌 목록 조회 (자동이체 계좌 선택용)
  const { data: accounts } = useAccountsList();

  // 적금 계좌 상세 정보 조회 (현재 정보 표시용)
  const { data: savingsDetail } = useSavingsAccountDetail(accountId!);

  // 현재 연결된 자동이체 계좌 ID
  const currentAutoAccountId =
    savingsDetail?.savings?.autoTransfer?.withdrawAccountId;

  const [formData, setFormData] = useState({
    newAmount: newSettings.newAmount?.toString() || '',
    newTransferDate: newSettings.newTransferDate || '',
    newTransferCycle: newSettings.newTransferCycle || '',
    newAutoAccount: newSettings.newAutoAccount || '',
  });
  const [amountError, setAmountError] = useState('');
  const [accordionValue, setAccordionValue] = useState<string | undefined>(
    undefined,
  );

  // 납입금액 유효성 검사
  const validateAmount = (value: string) => {
    const numbers = value.replace(/[^\d]/g, '');
    if (numbers === '') {
      return { isValid: false, message: '납입금액을 입력해주세요.' };
    }
    const amount = Number(numbers);
    if (amount < 1000) {
      return { isValid: false, message: '최소 1,000원 이상 입력해야 합니다.' };
    }
    if (amount > 1000000) {
      return {
        isValid: false,
        message: '최대 1,000,000원까지 입력 가능합니다.',
      };
    }
    return { isValid: true, message: '' };
  };

  // 적금 데이터가 로드되면 기존 값으로 폼 초기화
  useEffect(() => {
    if (
      savingsDetail &&
      !newSettings.newAmount &&
      !newSettings.newTransferDate &&
      !newSettings.newTransferCycle &&
      !newSettings.newAutoAccount
    ) {
      const currentAmount = savingsDetail.savings?.autoTransfer?.amount;
      setFormData({
        newAmount: currentAmount ? currentAmount.toLocaleString() : '',
        newTransferDate:
          savingsDetail.savings?.autoTransfer?.transferDay?.toString() || '',
        newTransferCycle: savingsDetail.savings?.autoTransfer?.cycle || '',
        newAutoAccount: currentAutoAccountId?.toString() || '',
      });
    }
  }, [savingsDetail, currentAutoAccountId, newSettings]);

  // 입출금 계좌만 필터링하고 현재 연결된 계좌는 제외
  const checkingAccounts =
    accounts?.filter(
      account =>
        account.product.productCategory === 'DEMAND_DEPOSIT' &&
        account.accountId !== currentAutoAccountId,
    ) || [];

  // 현재 연결된 자동이체 계좌 정보
  const currentAutoAccount = accounts?.find(
    account => account.accountId === currentAutoAccountId,
  );

  const selectedAutoAccount =
    formData.newAutoAccount && accounts
      ? accounts.find(
          account =>
            account.accountId.toString() === formData.newAutoAccount,
        ) ?? null
      : null;

  const autoAccountLabel = (() => {
    if (!formData.newAutoAccount) {
      return '자동이체 계좌를 선택해주세요';
    }

    const accountToShow =
      selectedAutoAccount ??
      (currentAutoAccountId?.toString() === formData.newAutoAccount
        ? currentAutoAccount
        : null);

    if (accountToShow) {
      const accountSuffix = accountToShow.accountNumber?.slice(-4) ?? '****';
      return `${accountToShow.product.productName} (*${accountSuffix})`;
    }

    return '자동이체 계좌 정보를 불러올 수 없습니다';
  })();

  // 납입 주기 표시 변환
  const getCycleDisplay = (cycle: string) => {
    switch (cycle) {
      case 'WEEKLY':
        return '주간';
      case 'MONTHLY':
        return '월간';
      case 'DAILY':
        return '일간';
      default:
        return cycle;
    }
  };

  // 자동이체 날짜 표시 변환
  const getTransferDateDisplay = (cycle: string, transferDay: number) => {
    switch (cycle) {
      case 'WEEKLY':
        return `매주 ${['일', '월', '화', '수', '목', '금', '토'][transferDay]}요일`;
      case 'MONTHLY':
        return `매월 ${transferDay}일`;
      case 'DAILY':
        return '매일';
      default:
        return `${transferDay}`;
    }
  };

  // 숫자 포맷팅 함수
  const formatNumber = (value: string) => {
    // 숫자만 추출
    const numbers = value.replace(/[^\d]/g, '');
    // 숫자를 천단위 쉼표로 포맷팅
    return numbers ? Number(numbers).toLocaleString() : '';
  };

  // 납입금액 변경 핸들러
  const handleAmountChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    const numbers = value.replace(/[^\d]/g, '');

    if (Number(numbers) > 1000000) {
      setFormData(prev => ({ ...prev, newAmount: formatNumber('1000000') }));
      const validation = validateAmount('1000000');
      setAmountError(validation.isValid ? '' : validation.message);
      return;
    }

    setFormData(prev => ({ ...prev, newAmount: formatNumber(value) }));
    const validation = validateAmount(value);
    setAmountError(validation.isValid ? '' : validation.message);
  };

  const handleInputChange = (field: string, value: string | number) => {
    setFormData(prev => {
      const newData = { ...prev, [field]: value };

      // 납입 주기가 변경되면 날짜 선택 초기화
      if (field === 'newTransferCycle') {
        newData.newTransferDate = '';
      }

      return newData;
    });
  };

  const handleNext = () => {
    // 선택된 변경 타입에 해당하는 값들만 저장
    const settingsToUpdate: Record<string, unknown> = {};

    if (selectedChangeTypes.includes('AMOUNT') && formData.newAmount) {
      const validation = validateAmount(formData.newAmount);
      if (!validation.isValid) {
        setAmountError(validation.message);
        return;
      }
      settingsToUpdate.newAmount = Number(
        formData.newAmount.replace(/[^\d]/g, ''),
      );
    }
    if (selectedChangeTypes.includes('TRANSFER_DATE')) {
      if (formData.newTransferCycle) {
        settingsToUpdate.newTransferCycle = formData.newTransferCycle;
      }
      if (formData.newTransferDate) {
        settingsToUpdate.newTransferDate = formData.newTransferDate;
      }
    }
    if (
      selectedChangeTypes.includes('AUTO_ACCOUNT') &&
      formData.newAutoAccount
    ) {
      settingsToUpdate.newAutoAccount = formData.newAutoAccount;
    }

    updateNewSettings(settingsToUpdate);
    goToNextStep();
  };

  const handleBack = () => {
    goToPreviousStep();
  };

  const isFormValid = () => {
    return selectedChangeTypes.every(type => {
      switch (type) {
        case 'AMOUNT': {
          const amount = Number(formData.newAmount.replace(/[^\d]/g, ''));
          return formData.newAmount && amount >= 1000 && amount <= 1000000;
        }
        case 'TRANSFER_DATE':
          return formData.newTransferCycle && formData.newTransferDate;
        case 'AUTO_ACCOUNT':
          return formData.newAutoAccount;
        default:
          return true;
      }
    });
  };

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col px-6 py-8 pb-24">
        <h1 className="mb-2 text-xl font-bold text-gray-900">
          새로운 설정값 입력
        </h1>
        <p className="mb-6 text-gray-600">변경할 새로운 값을 입력해주세요</p>

        <div className="space-y-4">
          {selectedChangeTypes.includes('AMOUNT') && (
            <div className="mb-3">
              <label className="mb-1 block text-sm font-medium text-gray-700">
                새로운 자동 납입금액
              </label>
              <div className="relative">
                <input
                  type="text"
                  value={formData.newAmount}
                  onChange={handleAmountChange}
                  placeholder="0"
                  className={`w-full rounded-lg border px-3 py-2 pr-8 focus:outline-none ${
                    amountError
                      ? 'border-red-500 focus:border-red-500'
                      : 'border-gray-300 focus:border-violet-500'
                  }`}
                />
                <span className="absolute top-1/2 right-3 -translate-y-1/2 text-sm text-gray-500">
                  원
                </span>
              </div>
              {amountError && (
                <p className="mt-1 text-xs text-red-600">{amountError}</p>
              )}
              <p className="mt-1 text-xs text-gray-500">
                현재:{' '}
                {savingsDetail?.savings?.autoTransfer?.amount?.toLocaleString() ||
                  '정보 없음'}
                원
              </p>
              <p className="mt-1 text-xs text-gray-400">
                * 최대 1,000,000(백만)원까지 입력 가능합니다
              </p>
            </div>
          )}

          {selectedChangeTypes.includes('TRANSFER_DATE') && (
            <>
              <div className="mb-3">
                <label className="mb-1 block text-sm font-medium text-gray-700">
                  납입 주기
                </label>
                <div className="flex gap-2">
                  <button
                    type="button"
                    onClick={() => {
                      handleInputChange('newTransferCycle', 'WEEKLY');
                      handleInputChange('newTransferDate', ''); // 주기 변경 시 이체일 초기화
                      setAccordionValue('transfer-date-selection'); // 이체일 선택 자동 열기
                    }}
                    className={`flex-1 rounded-lg border px-4 py-2 text-sm ${
                      formData.newTransferCycle === 'WEEKLY'
                        ? 'bg-violet-500 text-white'
                        : 'bg-white text-gray-700 hover:bg-gray-100'
                    }`}
                  >
                    매주
                  </button>
                  <button
                    type="button"
                    onClick={() => {
                      handleInputChange('newTransferCycle', 'MONTHLY');
                      handleInputChange('newTransferDate', ''); // 주기 변경 시 이체일 초기화
                      setAccordionValue('transfer-date-selection'); // 이체일 선택 자동 열기
                    }}
                    className={`flex-1 rounded-lg border px-4 py-2 text-sm ${
                      formData.newTransferCycle === 'MONTHLY'
                        ? 'bg-violet-500 text-white'
                        : 'bg-white text-gray-700 hover:bg-gray-100'
                    }`}
                  >
                    매월
                  </button>
                </div>
                <p className="mt-1 text-xs text-gray-500">
                  현재:{' '}
                  {savingsDetail?.savings?.autoTransfer
                    ? getCycleDisplay(savingsDetail.savings.autoTransfer.cycle)
                    : '정보 없음'}
                </p>
              </div>

              <div className="mb-3">
                <Accordion
                  type="single"
                  collapsible
                  className="w-full"
                  value={accordionValue}
                  onValueChange={setAccordionValue}
                >
                  <AccordionItem value="transfer-date-selection">
                    <AccordionTrigger className="text-left text-gray-700">
                      {formData.newTransferDate
                        ? formData.newTransferCycle === 'WEEKLY'
                          ? `매주 ${['일', '월', '화', '수', '목', '금', '토'][Number(formData.newTransferDate) % 7]}요일 이체`
                          : `매월 ${formData.newTransferDate}일 이체`
                        : '이체일을 선택해주세요'}
                    </AccordionTrigger>
                    <AccordionContent>
                      {formData.newTransferCycle === 'WEEKLY' ? (
                        <div className="grid grid-cols-2 gap-2 pt-2">
                          {[
                            '월요일',
                            '화요일',
                            '수요일',
                            '목요일',
                            '금요일',
                            '토요일',
                            '일요일',
                          ].map((day, index) => (
                            <button
                              key={day}
                              type="button"
                              onClick={() => {
                                handleInputChange(
                                  'newTransferDate',
                                  String(index + 1),
                                );
                                setAccordionValue('auto-account-selection');
                              }}
                              className={`rounded-md border px-4 py-2 text-sm ${
                                formData.newTransferDate === String(index + 1)
                                  ? 'bg-violet-500 text-white'
                                  : 'bg-white text-gray-700 hover:bg-gray-100'
                              }`}
                            >
                              {day}
                            </button>
                          ))}
                        </div>
                      ) : (
                        <div className="grid grid-cols-4 gap-2 pt-2">
                          {Array.from({ length: 28 }, (_, i) => i + 1).map(
                            day => (
                              <button
                                key={day}
                                type="button"
                                onClick={() => {
                                  handleInputChange(
                                    'newTransferDate',
                                    String(day),
                                  );
                                  setAccordionValue('auto-account-selection');
                                }}
                                className={`rounded-md border px-4 py-2 text-sm ${
                                  formData.newTransferDate === String(day)
                                    ? 'bg-violet-500 text-white'
                                    : 'bg-white text-gray-700 hover:bg-gray-100'
                                }`}
                              >
                                {day}일
                              </button>
                            ),
                          )}
                        </div>
                      )}
                    </AccordionContent>
                  </AccordionItem>
                </Accordion>
                <p className="mt-1 text-xs text-gray-500">
                  현재:{' '}
                  {savingsDetail?.savings?.autoTransfer
                    ? getTransferDateDisplay(
                        savingsDetail.savings.autoTransfer.cycle,
                        savingsDetail.savings.autoTransfer.transferDay,
                      )
                    : '정보 없음'}
                </p>
              </div>
            </>
          )}

          {selectedChangeTypes.includes('AUTO_ACCOUNT') && (
            <div>
              <Accordion
                type="single"
                collapsible
                className="w-full"
                value={accordionValue}
                onValueChange={setAccordionValue}
              >
                <AccordionItem value="auto-account-selection">
                  <AccordionTrigger className="text-left text-gray-700">
                    {autoAccountLabel}
                  </AccordionTrigger>
                  <AccordionContent>
                    <div className="space-y-2 pt-2">
                      {checkingAccounts.map(account => (
                        <button
                          key={account.accountId}
                          type="button"
                          onClick={() =>
                            handleInputChange(
                              'newAutoAccount',
                              account.accountId.toString(),
                            )
                          }
                          className={`w-full rounded-lg border p-3 text-left text-sm ${
                            formData.newAutoAccount ===
                            account.accountId.toString()
                              ? 'bg-violet-500 text-white'
                              : 'bg-white text-gray-700 hover:bg-gray-100'
                          }`}
                        >
                          <div className="font-medium">
                            {account.product.productName}
                          </div>
                          <div className="py-1 text-xs opacity-80">
                            {account.accountNumber}
                            {account.balance &&
                              ` • 잔액: ${account.balance.toLocaleString()}원`}
                          </div>
                        </button>
                      ))}
                    </div>
                  </AccordionContent>
                </AccordionItem>
              </Accordion>
              <div className="mt-1 space-y-1">
                <p className="text-xs text-gray-500">
                  현재:{' '}
                  {currentAutoAccount
                    ? `${currentAutoAccount.product.productName} (*${currentAutoAccount.accountNumber.slice(-4)})`
                    : '정보 없음'}
                </p>
                <p className="text-xs text-gray-400">
                  * 현재 연결된 계좌는 목록에서 제외됩니다
                </p>
              </div>
            </div>
          )}
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
            disabled={!isFormValid()}
            className="h-12 flex-1 rounded-lg bg-primary text-white hover:bg-primary/90 disabled:bg-gray-300 disabled:text-gray-500"
          >
            다음
          </Button>
        </div>
      </div>
    </>
  );
};

export default NewSettingsStep;
