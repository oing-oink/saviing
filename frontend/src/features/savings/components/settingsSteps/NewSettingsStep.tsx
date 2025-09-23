import { useState } from 'react';
import { useParams, useNavigate, useSearchParams } from 'react-router-dom';
import { useSavingsSettingsStore } from '@/features/savings/store/useSavingsSettingsStore';
import { useSavingsSettingsChange } from '@/features/savings/hooks/useSavingsSettingsChange';
import {
  useAccountsList,
  useSavingsAccountDetail,
} from '@/features/savings/query/useSavingsQuery';
import { Button } from '@/shared/components/ui/button';
import { createSavingsTerminationPath, PAGE_PATH } from '@/shared/constants/path';

const NewSettingsStep = () => {
  const { accountId } = useParams<{ accountId: string }>();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { selectedChangeTypes, newSettings, updateNewSettings } =
    useSavingsSettingsStore();
  const { goToNextStep, goToPreviousStep } = useSavingsSettingsChange();

  // URL 파라미터에서 from 값을 읽어옴
  const fromParam = searchParams.get('from');
  const entryPoint = fromParam ? decodeURIComponent(fromParam) : PAGE_PATH.HOME;

  // 계좌 목록 조회 (자동이체 계좌 선택용)
  const { data: accounts } = useAccountsList();

  // 적금 계좌 상세 정보 조회 (현재 정보 표시용)
  const { data: savingsDetail } = useSavingsAccountDetail(accountId!);

  const [formData, setFormData] = useState({
    newAmount: newSettings.newAmount || '',
    newTransferDate: newSettings.newTransferDate || '',
    newTransferCycle: newSettings.newTransferCycle || '',
    newAutoAccount: newSettings.newAutoAccount || '',
  });

  // 현재 연결된 자동이체 계좌 ID
  const currentAutoAccountId =
    savingsDetail?.savings?.autoTransfer?.withdrawAccountId;

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
      settingsToUpdate.newAmount = Number(formData.newAmount);
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
        case 'AMOUNT':
          return formData.newAmount && Number(formData.newAmount) > 0;
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
              <p className="mt-1 text-xs text-gray-500">
                현재:{' '}
                {savingsDetail?.savings?.autoTransfer?.amount?.toLocaleString() ||
                  '정보 없음'}
                원
              </p>
            </div>
          )}

          {selectedChangeTypes.includes('TRANSFER_DATE') && (
            <div className="space-y-4">
              {/* 납입 주기 선택 */}
              <div>
                <label className="mb-2 block text-sm font-medium text-gray-700">
                  납입 주기
                </label>
                <select
                  value={formData.newTransferCycle || ''}
                  onChange={e =>
                    handleInputChange('newTransferCycle', e.target.value)
                  }
                  className="w-full appearance-none rounded-lg border border-gray-300 bg-white px-3 py-2 focus:border-primary focus:ring-2 focus:ring-primary"
                >
                  <option value="" className="text-gray-500">
                    선택해주세요
                  </option>
                  <option value="WEEKLY" className="text-gray-900">
                    주간 (매주)
                  </option>
                  <option value="MONTHLY" className="text-gray-900">
                    월간 (매월)
                  </option>
                </select>
                <p className="mt-1 text-xs text-gray-500">
                  현재:{' '}
                  {savingsDetail?.savings?.autoTransfer
                    ? getCycleDisplay(savingsDetail.savings.autoTransfer.cycle)
                    : '정보 없음'}
                </p>
              </div>

              {/* 날짜 선택 */}
              {formData.newTransferCycle && (
                <div>
                  <label className="mb-2 block text-sm font-medium text-gray-700">
                    {formData.newTransferCycle === 'WEEKLY'
                      ? '자동이체 요일'
                      : '자동이체 날짜'}
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
                    {formData.newTransferCycle === 'WEEKLY'
                      ? // 주간: 0=일요일, 1=월요일, ..., 6=토요일
                        [
                          { value: '0', label: '일요일' },
                          { value: '1', label: '월요일' },
                          { value: '2', label: '화요일' },
                          { value: '3', label: '수요일' },
                          { value: '4', label: '목요일' },
                          { value: '5', label: '금요일' },
                          { value: '6', label: '토요일' },
                        ].map(day => (
                          <option
                            key={day.value}
                            value={day.value}
                            className="text-gray-900"
                          >
                            매주 {day.label}
                          </option>
                        ))
                      : // 월간: 1일~31일
                        Array.from({ length: 31 }, (_, i) => i + 1).map(day => (
                          <option
                            key={day}
                            value={day.toString()}
                            className="text-gray-900"
                          >
                            매월 {day}일
                          </option>
                        ))}
                  </select>
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
              )}
            </div>
          )}

          {selectedChangeTypes.includes('AUTO_ACCOUNT') && (
            <div>
              <label className="mb-2 block text-sm font-medium text-gray-700">
                새로운 연결 계좌
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
                {checkingAccounts.length > 0 ? (
                  checkingAccounts.map(account => (
                    <option
                      key={account.accountId}
                      value={account.accountId}
                      className="text-gray-900"
                    >
                      {account.product.productName} (*
                      {account.accountNumber.slice(-4)})
                    </option>
                  ))
                ) : (
                  <option disabled className="text-gray-400">
                    변경 가능한 계좌가 없습니다
                  </option>
                )}
              </select>
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
