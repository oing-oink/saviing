import { useNavigate, useParams } from 'react-router-dom';
import { useQueryClient } from '@tanstack/react-query';
import { useSavingsSettingsStore } from '@/features/savings/store/useSavingsSettingsStore';
import { useSavingsSettingsChange } from '@/features/savings/hooks/useSavingsSettingsChange';
import { useAccountsList } from '@/features/savings/query/useSavingsQuery';
import { savingsKeys } from '@/features/savings/query/savingsKeys';
import { Button } from '@/shared/components/ui/button';
import { PAGE_PATH } from '@/shared/constants/path';
import { useCustomerStore } from '@/features/auth/store/useCustomerStore';

const CompleteStep = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { accountId } = useParams<{ accountId: string }>();
  const { newSettings, reset } = useSavingsSettingsStore();
  const { cancelAndGoBack } = useSavingsSettingsChange();
  const customerId = useCustomerStore(state => state.customerId);

  // 계좌 목록 조회 (계좌명 표시용)
  const { data: accounts } = useAccountsList();

  const handleGoToDetail = () => {
    if (accountId) {
      // 모든 적금 관련 캐시 무효화
      queryClient.invalidateQueries({
        queryKey: savingsKeys.all,
      });

      // 특정 계좌 관련 캐시 제거
      queryClient.removeQueries({
        queryKey: savingsKeys.detail(accountId),
      });
      queryClient.removeQueries({
        queryKey: savingsKeys.savingsAccountDetail(accountId),
      });

      // 계좌 목록도 새로고침
      queryClient.invalidateQueries({
        queryKey: savingsKeys.accountsList(customerId ?? undefined),
      });
    }

    // 설정 변경 상태 초기화
    reset();

    // 적금 상세 페이지로 이동
    cancelAndGoBack();
  };

  const handleGoToSavingsList = () => {
    // 설정 변경 상태 초기화
    reset();

    // 적금 목록 페이지로 이동
    navigate(PAGE_PATH.SAVINGS);
  };

  const getChangedSettings = () => {
    const changes = [];

    if (newSettings.newAmount) {
      changes.push({
        label: '월 납입금액',
        value: `${newSettings.newAmount.toLocaleString()}원`,
      });
    }

    // 납입 주기와 자동이체 날짜를 하나로 합치기 (위아래 분리 표시)
    if (newSettings.newTransferCycle || newSettings.newTransferDate) {
      const cycleToUse = newSettings.newTransferCycle || 'MONTHLY'; // 기본값은 월간

      // 납입 주기 정보
      const cycleText = cycleToUse === 'WEEKLY' ? '주간' : '월간';

      // 날짜 정보
      let dayText = '';
      if (newSettings.newTransferDate) {
        if (cycleToUse === 'WEEKLY') {
          const weekDays = ['일', '월', '화', '수', '목', '금', '토'];
          dayText = `매주 ${weekDays[Number(newSettings.newTransferDate)]}요일`;
        } else {
          dayText = `매월 ${newSettings.newTransferDate}일`;
        }
      }

      changes.push({
        label: '자동이체 날짜',
        value: {
          cycle: cycleText,
          date: dayText,
        },
      });
    }

    if (newSettings.newAutoAccount) {
      const selectedAccount = accounts?.find(
        acc => acc.accountId === Number(newSettings.newAutoAccount),
      );
      changes.push({
        label: '연결 계좌',
        value: selectedAccount
          ? `${selectedAccount.product.productName} (*${selectedAccount.accountNumber.slice(-4)})`
          : `계좌 ID: ${newSettings.newAutoAccount}`,
      });
    }

    return changes;
  };

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col items-center justify-center px-6 py-8 text-center">
        {/* 성공 아이콘 */}
        <div className="mx-auto mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-green-100">
          <svg
            className="h-10 w-10 text-green-600"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M5 13l4 4L19 7"
            />
          </svg>
        </div>

        <h2 className="mb-2 text-2xl font-bold text-gray-900">
          설정 변경 완료! 🎉
        </h2>
        <p className="mb-6 text-gray-600">
          적금 설정이 성공적으로 변경되었습니다.
        </p>

        {/* 변경된 설정 요약 */}
        <div className="mb-8 w-full rounded-lg bg-white p-6 shadow-sm">
          <h3 className="mb-4 font-semibold text-gray-900">변경 완료된 설정</h3>
          <div className="space-y-3">
            {getChangedSettings().map((setting, index) => (
              <div key={index} className="rounded-lg bg-primary/10 p-4">
                {typeof setting.value === 'object' &&
                setting.value.cycle &&
                setting.value.date ? (
                  <div className="space-y-2">
                    <div className="flex justify-between">
                      <div className="flex items-start space-x-2">
                        <svg
                          className="mt-0.5 h-4 w-4 text-primary"
                          fill="currentColor"
                          viewBox="0 0 20 20"
                        >
                          <path
                            fillRule="evenodd"
                            d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                            clipRule="evenodd"
                          />
                        </svg>
                        <span className="font-medium text-gray-700">
                          납입 주기
                        </span>
                      </div>
                      <span className="font-semibold text-primary">
                        {setting.value.cycle}
                      </span>
                    </div>
                    <div className="flex justify-between">
                      <div className="flex items-start space-x-2">
                        <div className="mt-0.5 h-4 w-4"></div>
                        <span className="font-medium text-gray-700">
                          자동이체 날짜
                        </span>
                      </div>
                      <span className="font-semibold text-primary">
                        {setting.value.date}
                      </span>
                    </div>
                  </div>
                ) : (
                  <div className="flex justify-between">
                    <div className="flex items-start space-x-2">
                      <svg
                        className="mt-0.5 h-4 w-4 text-primary"
                        fill="currentColor"
                        viewBox="0 0 20 20"
                      >
                        <path
                          fillRule="evenodd"
                          d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                          clipRule="evenodd"
                        />
                      </svg>
                      <span className="font-medium text-gray-700">
                        {setting.label}
                      </span>
                    </div>
                    <span className="font-semibold text-primary">
                      {typeof setting.value === 'string' ? setting.value : ''}
                    </span>
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>

        {/* 안내 메시지 */}
        <div className="relative mb-8 rounded-lg border border-blue-200 bg-blue-50 p-4">
          <svg
            className="absolute top-4 left-4 h-5 w-5 text-blue-600"
            fill="currentColor"
            viewBox="0 0 20 20"
          >
            <path
              fillRule="evenodd"
              d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z"
              clipRule="evenodd"
            />
          </svg>
          <div className="text-center text-sm text-blue-700">
            <p className="mb-1 font-medium">알려드립니다</p>
            <ul className="mt-3 space-y-1 text-xs">
              <li>• 변경된 설정은 다음 이체일부터 적용됩니다</li>
              <li>• 변경 후 30일 동안 재변경이 제한됩니다</li>
              <li>• 변경 내역은 거래내역에서 확인하실 수 있습니다</li>
            </ul>
          </div>
        </div>
      </div>

      {/* 하단 고정 버튼 */}
      <div className="fixed right-0 bottom-0 left-0 z-10 bg-white p-4">
        <div className="space-y-3">
          <Button
            onClick={handleGoToDetail}
            className="h-12 w-full rounded-lg bg-primary text-white hover:bg-primary/90"
          >
            적금 상세보기
          </Button>
          <Button
            variant="outline"
            onClick={handleGoToSavingsList}
            className="h-12 w-full rounded-lg"
          >
            적금 목록으로
          </Button>
        </div>
      </div>
    </>
  );
};

export default CompleteStep;
