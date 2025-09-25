import { useState } from 'react';
import { useAccountCreationStore } from '@/features/savings/store/useAccountCreationStore';
import { useStepProgress } from '@/features/savings/hooks/useStepProgress';
import { useQuery } from '@tanstack/react-query';
import { getAllAccounts } from '@/features/savings/api/savingsApi';
import { Button } from '@/shared/components/ui/button';
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/shared/components/ui/accordion';
import { useCustomerStore } from '@/features/auth/store/useCustomerStore';

const SetConditionStep = () => {
  const { setForm, form } = useAccountCreationStore();
  const { goToNextStep } = useStepProgress();
  const customerId = useCustomerStore(state => state.customerId);

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

  // 입출금 계좌만 필터링 (productId가 1이고 상태가 ACTIVE인 것)
  const checkingAccounts =
    accounts?.filter(
      account =>
        account.product?.productId === 1 && account.status === 'ACTIVE',
    ) || [];

  const [depositAmount, setDepositAmount] = useState(
    'depositAmount' in form && form.depositAmount
      ? String(form.depositAmount)
      : '',
  );
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

  const isValid =
    depositAmount.trim() !== '' && transferDate && period && autoAccount;

  const handleNext = () => {
    if (!isValid) {
      return;
    }
    // 선택된 계좌의 ID 찾기
    const selectedAccount = checkingAccounts.find(
      acc => acc.accountNumber === autoAccount,
    );
    const withdrawAccountId = selectedAccount?.accountId || 0;

    setForm({
      depositAmount: Number(depositAmount),
      transferDate,
      period: Number(period),
      autoAccount,
      transferCycle,
      withdrawAccountId,
    });
    goToNextStep();
  };

  return (
    <div className="h-screen">
      {/* 메인 컨텐츠 */}
      <div className="h-[calc(100vh-5rem)] overflow-y-auto px-6 py-6">
        <h1 className="mb-2 text-xl font-bold text-gray-900">
          적금 조건을 설정해주세요
        </h1>
        <p className="mb-4 text-gray-600">나에게 맞는 조건으로 설정하세요</p>

        {/* 월 납입액 */}
        <div className="mb-3">
          <label className="mb-1 block text-sm font-medium text-gray-700">
            월 납입액
          </label>
          <input
            type="number"
            value={depositAmount}
            onChange={e => setDepositAmount(e.target.value)}
            placeholder="100,000"
            className="w-full rounded-lg border px-3 py-2"
          />
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
          <Accordion type="single" collapsible className="w-full">
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
                        onClick={() => setTransferDate(String(index + 1))}
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
                        onClick={() => setTransferDate(String(day))}
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
          <Accordion type="single" collapsible className="w-full">
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
                      onClick={() => setPeriod(String(weeks))}
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
            <Accordion type="single" collapsible className="w-full">
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

      {/* 하단 버튼 */}
      <div className="fixed right-0 bottom-0 left-0 h-20 border-t border-gray-200 bg-white p-4">
        <Button
          onClick={handleNext}
          disabled={!isValid}
          className="h-12 w-full rounded-lg bg-primary text-white disabled:bg-gray-200 disabled:text-gray-400"
        >
          설정 완료
        </Button>
      </div>
    </div>
  );
};

export default SetConditionStep;
