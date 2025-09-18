import { useState } from 'react';
import { useGetAccountStore } from '@/features/savings/store/useGetAccountStore';
import { Button } from '@/shared/components/ui/button';
import FunnelLayout from '@/features/savings/components/FunnelLayout';

const SetConditionStep = () => {
  const { setStep, setForm, form } = useGetAccountStore();

  const [depositAmount, setDepositAmount] = useState(
    form.depositAmount ? String(form.depositAmount) : '',
  );
  const [transferDate, setTransferDate] = useState(form.transferDate || '');
  const [period, setPeriod] = useState(form.period ? String(form.period) : '');
  const [autoAccount, setAutoAccount] = useState(form.autoAccount || '');

  const isValid =
    depositAmount.trim() !== '' && transferDate && period && autoAccount;

  const handleNext = () => {
    if (!isValid) {
      return;
    }
    setForm({
      depositAmount: Number(depositAmount),
      transferDate,
      period: Number(period),
      autoAccount,
    });
    setStep('CONFIRM');
  };

  return (
    <FunnelLayout>

      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col px-6 py-8">
        <h1 className="mb-2 text-xl font-bold text-gray-900">
          적금 조건을 설정해주세요
        </h1>
        <p className="mb-6 text-gray-600">나에게 맞는 조건으로 설정하세요</p>

        {/* 월 납입액 */}
        <div className="mb-4">
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

        {/* 이체일 */}
        <div className="mb-4">
          <label className="mb-2 block text-sm font-medium text-gray-700">
            이체일
          </label>
          <div className="grid grid-cols-5 gap-2">
            {Array.from({ length: 28 }, (_, i) => i + 1).map(day => (
              <button
                key={day}
                type="button"
                onClick={() => setTransferDate(String(day))}
                className={`rounded-md border px-3 py-2 text-sm ${
                  transferDate === String(day)
                    ? 'bg-violet-500 text-white'
                    : 'bg-white text-gray-700 hover:bg-gray-100'
                }`}
              >
                {day}일
              </button>
            ))}
          </div>
        </div>

        {/* 적금 기간 */}
        <div className="mb-4">
          <label className="mb-1 block text-sm font-medium text-gray-700">
            적금 기간
          </label>
          <select
            value={period}
            onChange={e => setPeriod(e.target.value)}
            className="w-full rounded-lg border px-3 py-2"
          >
            <option value="">선택해주세요</option>
            <option value="6">6개월</option>
            <option value="12">12개월</option>
            <option value="24">24개월</option>
            <option value="36">36개월</option>
          </select>
        </div>

        {/* 자동이체 계좌 */}
        <div className="mb-4">
          <label className="mb-1 block text-sm font-medium text-gray-700">
            자동이체 계좌
          </label>
          <select
            value={autoAccount}
            onChange={e => setAutoAccount(e.target.value)}
            className="w-full rounded-lg border px-3 py-2"
          >
            <option value="">선택해주세요</option>
            <option value="account1">국민은행 123-456-7890</option>
            <option value="account2">신한은행 987-654-3210</option>
          </select>
        </div>
      </div>

      {/* 하단 버튼 */}
      <div className="bg-white p-4">
        <Button
          onClick={handleNext}
          disabled={!isValid}
          className="h-12 w-full rounded-lg bg-primary text-white disabled:bg-gray-200 disabled:text-gray-400"
        >
          설정 완료
        </Button>
      </div>
    </FunnelLayout>
  );
};

export default SetConditionStep;
