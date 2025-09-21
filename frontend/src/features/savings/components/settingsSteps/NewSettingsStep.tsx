import { useState } from 'react';
import { useSavingsSettingsStore } from '@/features/savings/store/useSavingsSettingsStore';
import { useSavingsSettingsChange } from '@/features/savings/hooks/useSavingsSettingsChange';
import { useAccountsList } from '@/features/savings/query/useSavingsQuery';
import { Button } from '@/shared/components/ui/button';

const NewSettingsStep = () => {
  const { selectedChangeTypes, newSettings, updateNewSettings } =
    useSavingsSettingsStore();
  const { goToNextStep, goToPreviousStep } = useSavingsSettingsChange();

  // 계좌 목록 조회 (자동이체 계좌 선택용)
  const { data: accounts } = useAccountsList();

  const [formData, setFormData] = useState({
    newAmount: newSettings.newAmount || '',
    newTransferDate: newSettings.newTransferDate || '',
    newAutoAccount: newSettings.newAutoAccount || '',
  });

  // 입출금 계좌만 필터링 (자동이체용)
  const checkingAccounts =
    accounts?.filter(
      account => account.product.productCategory === 'DEMAND_DEPOSIT',
    ) || [];

  const handleInputChange = (field: string, value: string | number) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  const handleNext = () => {
    // 선택된 변경 타입에 해당하는 값들만 저장
    const settingsToUpdate: any = {};

    if (selectedChangeTypes.includes('AMOUNT') && formData.newAmount) {
      settingsToUpdate.newAmount = Number(formData.newAmount);
    }
    if (
      selectedChangeTypes.includes('TRANSFER_DATE') &&
      formData.newTransferDate
    ) {
      settingsToUpdate.newTransferDate = formData.newTransferDate;
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
        case 'AMOUNT':
          return formData.newAmount && Number(formData.newAmount) > 0;
        case 'TRANSFER_DATE':
          return formData.newTransferDate;
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
      <div className="flex flex-1 flex-col px-6 py-8">
        <h1 className="mb-2 text-xl font-bold text-gray-900">
          새로운 설정값 입력
        </h1>
        <p className="mb-6 text-gray-600">변경할 새로운 값을 입력해주세요</p>

        <div className="space-y-6 rounded-lg border bg-white p-6">
          {selectedChangeTypes.includes('AMOUNT') && (
            <div>
              <label className="mb-2 block text-sm font-medium text-gray-700">
                새로운 월 납입금액
              </label>
              <div className="relative">
                <input
                  type="number"
                  value={formData.newAmount}
                  onChange={e => handleInputChange('newAmount', e.target.value)}
                  placeholder="300000"
                  className="w-full rounded-lg border border-gray-300 px-3 py-2 focus:border-primary focus:ring-2 focus:ring-primary"
                />
                <span className="absolute top-2 right-3 text-gray-500">원</span>
              </div>
              <p className="mt-1 text-xs text-gray-500">현재: 300,000원</p>
            </div>
          )}

          {selectedChangeTypes.includes('TRANSFER_DATE') && (
            <div>
              <label className="mb-2 block text-sm font-medium text-gray-700">
                새로운 자동이체 날짜
              </label>
              <select
                value={formData.newTransferDate}
                onChange={e =>
                  handleInputChange('newTransferDate', e.target.value)
                }
                className="w-full appearance-none rounded-lg border border-gray-300 bg-white px-3 py-2 focus:border-primary focus:ring-2 focus:ring-primary"
              >
                <option value="" className="text-gray-500">
                  선택해주세요
                </option>
                {Array.from({ length: 28 }, (_, i) => i + 1).map(day => (
                  <option
                    key={day}
                    value={day.toString()}
                    className="text-gray-900"
                  >
                    매월 {day}일
                  </option>
                ))}
              </select>
              <p className="mt-1 text-xs text-gray-500">현재: 매월 25일</p>
            </div>
          )}

          {selectedChangeTypes.includes('AUTO_ACCOUNT') && (
            <div>
              <label className="mb-2 block text-sm font-medium text-gray-700">
                새로운 자동이체 연결 계좌
              </label>
              <select
                value={formData.newAutoAccount}
                onChange={e =>
                  handleInputChange('newAutoAccount', e.target.value)
                }
                className="w-full appearance-none rounded-lg border border-gray-300 bg-white px-3 py-2 focus:border-primary focus:ring-2 focus:ring-primary"
              >
                <option value="" className="text-gray-500">
                  계좌를 선택해주세요
                </option>
                {checkingAccounts.map(account => (
                  <option
                    key={account.accountId}
                    value={account.accountId}
                    className="text-gray-900"
                  >
                    {account.product.productName} (*
                    {account.accountNumber.slice(-4)})
                  </option>
                ))}
              </select>
              <p className="mt-1 text-xs text-gray-500">
                현재: 하나 입출금통장 (*1234)
              </p>
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
            영향 검토
          </Button>
        </div>
      </div>
    </>
  );
};

export default NewSettingsStep;
