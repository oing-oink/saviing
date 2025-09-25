import { useState, useEffect } from 'react';
import { useAccountCreationStore } from '@/features/savings/store/useAccountCreationStore';
import { useStepProgress } from '@/features/savings/hooks/useStepProgress';
import { useQuery } from '@tanstack/react-query';
import { useErrorBoundary } from 'react-error-boundary';
import { getAllAccounts } from '@/features/savings/api/savingsApi';
import { Button } from '@/shared/components/ui/button';
import {
  validateMonthlyAmount,
  // formatNumber,
} from '@/features/savings/utils/validation';
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/shared/components/ui/accordion';
import { useCustomerStore } from '@/features/auth/store/useCustomerStore';

const SetConditionStep = () => {
  const { setForm, form } = useAccountCreationStore();
  const { goToNextStep, goToPreviousStep } = useStepProgress();
  const customerId = useCustomerStore(state => state.customerId);
  const { showBoundary } = useErrorBoundary();

  // 내 계좌 목록 가져오기
  const {
    data: accounts,
    isLoading: isLoadingAccounts,
    error: accountsError,
  } = useQuery({
    queryKey: ['allAccounts', customerId],
    queryFn: () => {
      if (customerId == null) {
        throw new Error('로그인 정보가 없습니다.');
      }
      return getAllAccounts(customerId);
    },
    staleTime: 1000 * 60 * 5, // 5분간 캐시 유지
    enabled: customerId != null,
  });

  // API 에러 발생 시 ErrorBoundary로 전달
  useEffect(() => {
    if (accountsError) {
      showBoundary(accountsError);
    }
  }, [accountsError, showBoundary]);

  // 입출금 계좌만 필터링 (productId가 1이고 상태가 ACTIVE인 것)
  const checkingAccounts =
    accounts?.filter(
      account =>
        account.product?.productId === 1 && account.status === 'ACTIVE',
    ) || [];

  const [depositAmount, setDepositAmount] = useState(
    'depositAmount' in form && form.depositAmount
      ? form.depositAmount.toLocaleString()
      : '',
  );
  const [depositAmountError, setDepositAmountError] = useState('');
  const [transferDate, setTransferDate] = useState(
    'transferDate' in form ? form.transferDate || '' : '',
  );
  const [period, setPeriod] = useState(
    'period' in form && form.period ? String(form.period) : '',
  );
  const [autoAccount, setAutoAccount] = useState(
    'autoAccount' in form ? form.autoAccount || '' : '',
  );
  const [transferCycle, setTransferCycle] = useState<'WEEKLY' | 'MONTHLY'>(
    'transferCycle' in form ? form.transferCycle || 'MONTHLY' : 'MONTHLY',
  );
  const [accordionValue, setAccordionValue] = useState<string | undefined>(
    undefined,
  );

  // 숫자 포맷팅 함수
  const formatNumber = (value: string) => {
    // 숫자만 추출
    const numbers = value.replace(/[^\d]/g, ''); // 숫자만 입력 가능
    // 숫자를 천단위 쉼표로 포맷팅
    return numbers ? Number(numbers).toLocaleString() : '';
  };

  // 납입액 입력 처리
  const handleDepositAmountChange = (value: string) => {
    // 숫자만 입력 허용
    const numericValue = value.replace(/[^0-9]/g, '');

    // 백만원 한도 제한
    if (numericValue && Number(numericValue) > 1000000) {
      return;
    }

    setDepositAmount(numericValue);

    const validation = validateMonthlyAmount(numericValue);
    setDepositAmountError(validation.isValid ? '' : validation.message);
  };

  // 전체 유효성 검사
  const isValid =
    validateMonthlyAmount(depositAmount).isValid &&
    transferDate &&
    period &&
    autoAccount;

  const handleNext = () => {
    // 최종 검증
    const amountValidation = validateMonthlyAmount(depositAmount);
    setDepositAmountError(
      amountValidation.isValid ? '' : amountValidation.message,
    );

    if (!isValid) {
      return;
    }

    // 선택된 계좌의 ID 찾기
    const selectedAccount = checkingAccounts.find(
      acc => acc.accountNumber === autoAccount,
    );
    const withdrawAccountId = selectedAccount?.accountId || 0;

    setForm({
      depositAmount: Number(depositAmount.replace(/[^\d]/g, '')),
      transferDate,
      period: Number(period),
      autoAccount,
      transferCycle,
      withdrawAccountId,
    });
    goToNextStep();
  };

  const handleBack = () => {
    goToPreviousStep();
  };

  return (
    <div className="h-screen">
      {/* 메인 컨텐츠 */}
      <div className="h-[calc(100vh-5rem)] overflow-y-auto px-6 py-6">
        <h1 className="mb-2 text-xl font-bold text-gray-900">
          적금 조건을 설정해주세요
        </h1>
        <p className="mb-4 text-gray-600">나에게 맞는 조건으로 설정하세요</p>

        {/* 자동 납입액 */}
        <div className="mb-3">
          <label className="mb-1 block text-sm font-medium text-gray-700">
            자동 납입액
          </label>
          <div className="relative">
            <input
              type="text"
              value={depositAmount ? formatNumber(depositAmount) : ''}
              onChange={e => handleDepositAmountChange(e.target.value)}
              placeholder="0"
              className={`w-full rounded-lg border px-3 py-2 pr-8 focus:outline-none ${
                depositAmountError
                  ? 'border-red-500 focus:border-red-500'
                  : 'border-gray-300 focus:border-violet-500'
              }`}
            />
            <span className="absolute top-1/2 right-3 -translate-y-1/2 text-sm text-gray-500">
              원
            </span>
          </div>
          {depositAmountError && (
            <p className="mt-1 text-xs text-red-600">{depositAmountError}</p>
          )}
          <p className="mt-1 text-xs text-gray-400">
            * 최대 1,000,000(백만)원까지만 입력 가능합니다
          </p>
        </div>

        {/* 자동이체 주기 */}
        <div className="mb-3">
          <label className="mb-1 block text-sm font-medium text-gray-700">
            자동이체 주기
          </label>
          <div className="flex gap-2">
            <button
              type="button"
              onClick={() => {
                setTransferCycle('WEEKLY');
                setTransferDate(''); // 주기 변경 시 이체일 초기화
                setAccordionValue('transfer-date-selection'); // 이체일 선택 자동 열기
              }}
              className={`flex-1 rounded-lg border px-4 py-2 text-sm ${
                transferCycle === 'WEEKLY'
                  ? 'bg-violet-500 text-white'
                  : 'bg-white text-gray-700 hover:bg-gray-100'
              }`}
            >
              매주
            </button>
            <button
              type="button"
              onClick={() => {
                setTransferCycle('MONTHLY');
                setTransferDate(''); // 주기 변경 시 이체일 초기화
                setAccordionValue('transfer-date-selection'); // 이체일 선택 자동 열기
              }}
              className={`flex-1 rounded-lg border px-4 py-2 text-sm ${
                transferCycle === 'MONTHLY'
                  ? 'bg-violet-500 text-white'
                  : 'bg-white text-gray-700 hover:bg-gray-100'
              }`}
            >
              매월
            </button>
          </div>
        </div>

        {/* 이체일 */}
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
                {transferDate
                  ? transferCycle === 'WEEKLY'
                    ? `매주 ${['일', '월', '화', '수', '목', '금', '토'][Number(transferDate) % 7]}요일 이체`
                    : `매월 ${transferDate}일 이체`
                  : '이체일을 선택해주세요'}
              </AccordionTrigger>
              <AccordionContent>
                {transferCycle === 'WEEKLY' ? (
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
                          setTransferDate(String(index + 1));
                          setAccordionValue('period-selection'); // 적금 기간 선택 자동 열기
                        }}
                        className={`rounded-md border px-4 py-2 text-sm ${
                          transferDate === String(index + 1)
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
                    {Array.from({ length: 28 }, (_, i) => i + 1).map(day => (
                      <button
                        key={day}
                        type="button"
                        onClick={() => {
                          setTransferDate(String(day));
                          setAccordionValue('period-selection'); // 적금 기간 선택 자동 열기
                        }}
                        className={`rounded-md border px-4 py-2 text-sm ${
                          transferDate === String(day)
                            ? 'bg-violet-500 text-white'
                            : 'bg-white text-gray-700 hover:bg-gray-100'
                        }`}
                      >
                        {day}일
                      </button>
                    ))}
                  </div>
                )}
              </AccordionContent>
            </AccordionItem>
          </Accordion>
        </div>

        {/* 적금 기간 (주 단위) */}
        <div className="mb-3">
          <Accordion
            type="single"
            collapsible
            className="w-full"
            value={accordionValue}
            onValueChange={setAccordionValue}
          >
            <AccordionItem value="period-selection">
              <AccordionTrigger className="text-left text-gray-700">
                {period ? `${period}주 설정` : '적금 기간을 선택해주세요'}
              </AccordionTrigger>
              <AccordionContent>
                <div className="grid grid-cols-5 gap-2 pt-2">
                  {Array.from({ length: 21 }, (_, i) => i + 4).map(weeks => (
                    <button
                      key={weeks}
                      type="button"
                      onClick={() => {
                        setPeriod(String(weeks));
                        setAccordionValue('account-selection'); // 자동이체 계좌 선택 자동 열기
                      }}
                      className={`rounded-md border px-3 py-2 text-sm ${
                        period === String(weeks)
                          ? 'bg-violet-500 text-white'
                          : 'bg-white text-gray-700 hover:bg-gray-100'
                      }`}
                    >
                      {weeks}주
                    </button>
                  ))}
                </div>
              </AccordionContent>
            </AccordionItem>
          </Accordion>
        </div>

        {/* 자동이체 계좌 */}
        <div>
          {isLoadingAccounts ? (
            <div className="w-full rounded-lg border px-3 py-2 text-gray-500">
              계좌 목록을 불러오는 중...
            </div>
          ) : accountsError ? (
            <div className="w-full rounded-lg border px-3 py-2 text-red-500">
              계좌 목록을 불러오는데 실패했습니다.
            </div>
          ) : (
            <Accordion
              type="single"
              collapsible
              className="w-full"
              value={accordionValue}
              onValueChange={setAccordionValue}
            >
              <AccordionItem value="account-selection">
                <AccordionTrigger className="text-left text-gray-700">
                  {autoAccount
                    ? checkingAccounts.find(
                        acc => acc.accountNumber === autoAccount,
                      )?.product?.productName +
                      ' ' +
                      autoAccount
                    : '자동이체 계좌를 선택해주세요'}
                </AccordionTrigger>
                <AccordionContent>
                  <div className="space-y-2 pt-2">
                    {checkingAccounts.map(account => (
                      <button
                        key={account.accountId}
                        type="button"
                        onClick={() => setAutoAccount(account.accountNumber)}
                        className={`w-full rounded-lg border p-3 text-left text-sm ${
                          autoAccount === account.accountNumber
                            ? 'bg-violet-500 text-white'
                            : 'bg-white text-gray-700 hover:bg-gray-100'
                        }`}
                      >
                        <div className="font-medium">
                          {account.product?.productName}
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
          )}
          {checkingAccounts.length === 0 &&
            !isLoadingAccounts &&
            !accountsError && (
              <p className="mt-1 text-sm text-red-600">
                자동이체할 입출금 계좌가 없습니다. 먼저 입출금 계좌를
                개설해주세요.
              </p>
            )}
        </div>
      </div>

      {/* 하단 고정 버튼 */}
      <div
        className="fixed right-0 bottom-0 left-0 z-10 h-20 bg-white p-4"
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
            className="h-12 flex-1 rounded-lg bg-primary text-white hover:bg-primary/90 focus-visible:border-transparent focus-visible:ring-0 disabled:bg-gray-300 disabled:text-gray-500"
          >
            설정 완료
          </Button>
        </div>
      </div>
    </div>
  );
};

export default SetConditionStep;
