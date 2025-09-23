import { useState } from 'react';
import { useParams } from 'react-router-dom';
import { useSavingsSettingsStore } from '@/features/savings/store/useSavingsSettingsStore';
import { useSavingsSettingsChange } from '@/features/savings/hooks/useSavingsSettingsChange';
import {
  useUpdateSavingsAutoTransfer,
  useAccountsList,
} from '@/features/savings/query/useSavingsQuery';
import { Button } from '@/shared/components/ui/button';
import type { UpdateAutoTransferRequest } from '@/features/savings/types/savingsTypes';

const ConfirmStep = () => {
  const { accountId } = useParams<{ accountId: string }>();
  const { currentInfo, newSettings } = useSavingsSettingsStore();
  const { goToNextStep, goToPreviousStep } = useSavingsSettingsChange();
  const [isProcessing, setIsProcessing] = useState(false);
  const [hasAgreed, setHasAgreed] = useState(false);

  // 자동이체 설정 변경 mutation
  const updateAutoTransferMutation = useUpdateSavingsAutoTransfer();

  // 계좌 목록 조회 (계좌 상태 확인용)
  const { data: accounts } = useAccountsList();

  const handleConfirm = async () => {
    if (!hasAgreed || !accountId) {
      return;
    }

    setIsProcessing(true);

    try {
      // API 요청 데이터 구성 (모든 필드 포함)
      const updateData: UpdateAutoTransferRequest = {};

      // 자동이체 설정을 변경하는 경우 기본적으로 활성화
      updateData.enabled = true;

      // 변경된 항목은 새 값 사용, 변경되지 않은 항목은 현재 값 사용
      updateData.amount =
        newSettings.newAmount || currentInfo?.currentAmount || 0;
      updateData.cycle =
        newSettings.newTransferCycle ||
        currentInfo?.currentTransferCycle ||
        'MONTHLY';
      updateData.transferDay = newSettings.newTransferDate
        ? Number(newSettings.newTransferDate)
        : currentInfo?.currentTransferDate
          ? Number(currentInfo.currentTransferDate)
          : 1;
      updateData.withdrawAccountId = newSettings.newAutoAccount
        ? Number(newSettings.newAutoAccount)
        : currentInfo?.currentAutoAccount
          ? Number(currentInfo.currentAutoAccount)
          : 0;

      // 계좌 상태 검증
      if (updateData.withdrawAccountId) {
        const targetAccount = accounts?.find(
          acc => acc.accountId === updateData.withdrawAccountId,
        );

        if (!targetAccount) {
          alert('선택한 연결 계좌를 찾을 수 없습니다.');
          setIsProcessing(false);
          return;
        }

        if (targetAccount.status !== 'ACTIVE') {
          alert(
            `선택한 계좌(${targetAccount.product.productName})가 활성 상태가 아닙니다.`,
          );
          setIsProcessing(false);
          return;
        }
      }

      // 자동이체 설정 변경 API 호출
      await updateAutoTransferMutation.mutateAsync({
        accountId,
        updateData,
      });

      // 성공 시 완료 페이지로 이동
      goToNextStep();
    } catch (error: unknown) {
      // ApiError에서 실제 서버 응답 메시지 추출
      const serverResponse = (
        error as { axiosError?: { response?: { data?: unknown } } }
      )?.axiosError?.response?.data;
      const errorCode = (serverResponse as { code?: string })?.code;
      const serverMessage =
        (serverResponse as { message?: string })?.message ||
        (error as Error)?.message;

      let userMessage = serverMessage || '설정 변경 중 오류가 발생했습니다.';

      // 특정 에러 코드에 대한 사용자 친화적 메시지
      if (errorCode === 'ACCOUNT_INVALID_ACCOUNT_STATE') {
        userMessage =
          '현재 적금 계좌의 상태로는 자동이체 설정을 변경할 수 없습니다.\n계좌 상태를 확인해주시기 바랍니다.';
      } else if (errorCode === 'INVALID_INPUT_VALUE') {
        userMessage =
          '입력값이 올바르지 않습니다.\n• 납입 금액이 너무 작거나 클 수 있습니다.\n• 유효하지 않은 날짜나 계좌를 선택했을 수 있습니다.';
      }

      alert(`오류: ${userMessage}`);
      setIsProcessing(false);
    }
  };

  const handleBack = () => {
    if (isProcessing) {
      return;
    }
    goToPreviousStep();
  };

  return (
    <>
      {/* 메인 컨텐츠 */}
      <div className="flex flex-1 flex-col px-6 py-8">
        <h1 className="mb-2 text-xl font-bold text-gray-900">최종 확인</h1>
        <p className="mb-6 text-gray-600">
          아래 내용을 확인하고 설정 변경을 완료해주세요
        </p>

        <div className="space-y-6 rounded-lg border bg-white p-6">
          {/* 변경 내용 요약 */}
          <div className="border-b pb-4">
            <h3 className="mb-3 font-semibold text-gray-900">변경 내용</h3>
            <div className="space-y-2">
              {newSettings.newAmount && (
                <div className="rounded-lg bg-primary/10 p-3">
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-700">월 납입금액</span>
                    <span className="font-medium">
                      <span className="text-gray-500">
                        {currentInfo?.currentAmount.toLocaleString()}원
                      </span>
                      <span className="text-primary">
                        {' '}
                        → {newSettings.newAmount.toLocaleString()}원
                      </span>
                    </span>
                  </div>
                </div>
              )}
              {(newSettings.newTransferCycle ||
                newSettings.newTransferDate) && (
                <div className="rounded-lg bg-primary/10 p-3">
                  <div className="space-y-2">
                    {newSettings.newTransferCycle && (
                      <div className="flex justify-between text-sm">
                        <span className="text-gray-700">납입 주기</span>
                        <span className="font-medium">
                          <span className="text-gray-500">
                            {currentInfo?.currentTransferCycle === 'WEEKLY'
                              ? '주간'
                              : '월간'}
                          </span>
                          <span className="text-primary">
                            {' → '}
                            {newSettings.newTransferCycle === 'WEEKLY'
                              ? '주간'
                              : '월간'}
                          </span>
                        </span>
                      </div>
                    )}
                    {newSettings.newTransferDate && (
                      <div className="flex justify-between text-sm">
                        <span className="text-gray-700">자동이체 날짜</span>
                        <span className="font-medium">
                          <span className="text-gray-500">
                            {currentInfo?.currentTransferCycle === 'WEEKLY'
                              ? `매주 ${['일', '월', '화', '수', '목', '금', '토'][Number(currentInfo.currentTransferDate)]}요일`
                              : `매월 ${currentInfo?.currentTransferDate}일`}
                          </span>
                          <span className="text-primary">
                            {' → '}
                            {newSettings.newTransferCycle === 'WEEKLY'
                              ? `매주 ${['일', '월', '화', '수', '목', '금', '토'][Number(newSettings.newTransferDate)]}요일`
                              : `매월 ${newSettings.newTransferDate}일`}
                          </span>
                        </span>
                      </div>
                    )}
                  </div>
                </div>
              )}
              {newSettings.newAutoAccount && (
                <div className="rounded-lg bg-primary/10 p-3">
                  <div className="flex justify-between text-sm">
                    <span className="whitespace-nowrap text-gray-700">
                      연결 계좌
                    </span>
                    <div className="space-y-1 text-right">
                      <div className="text-gray-500">
                        {currentInfo?.currentAutoAccount
                          ? (() => {
                              const currentAccount = accounts?.find(
                                acc =>
                                  acc.accountId ===
                                  Number(currentInfo.currentAutoAccount),
                              );
                              return currentAccount
                                ? `${currentAccount.product.productName} (*${currentAccount.accountNumber.slice(-4)})`
                                : `현재 계좌 (*${currentInfo.currentAutoAccount.slice(-4)})`;
                            })()
                          : '하나 입출금통장 (*1234)'}
                      </div>
                      <div className="flex items-center justify-end space-x-1 font-medium text-primary">
                        <span>→</span>
                        <span>
                          {(() => {
                            const newAccount = accounts?.find(
                              acc =>
                                acc.accountId ===
                                Number(newSettings.newAutoAccount),
                            );
                            return newAccount
                              ? `${newAccount.product.productName} (*${newAccount.accountNumber.slice(-4)})`
                              : `계좌 ID: ${newSettings.newAutoAccount}`;
                          })()}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* 동의 체크박스 */}
          <div className="space-y-3">
            <div className="flex items-start space-x-3">
              <input
                type="checkbox"
                id="agree"
                checked={hasAgreed}
                onChange={e => setHasAgreed(e.target.checked)}
                className="mt-1 h-4 w-4 rounded border-gray-300 text-primary focus:ring-primary"
              />
              <label htmlFor="agree" className="text-sm text-gray-700">
                위 내용을 확인했으며, 적금 설정 변경에 동의합니다. 변경 후 일정
                기간 동안 재변경이 제한될 수 있음에 동의합니다.
              </label>
            </div>
          </div>
        </div>
      </div>

      {/* 하단 고정 버튼 */}
      <div className="fixed right-0 bottom-0 left-0 z-10 bg-white p-4">
        <div className="flex space-x-3">
          <Button
            variant="outline"
            onClick={handleBack}
            disabled={isProcessing}
            className="h-12 flex-1 rounded-lg"
          >
            이전
          </Button>
          <Button
            onClick={handleConfirm}
            disabled={!hasAgreed || isProcessing}
            className="flex h-12 flex-1 items-center justify-center space-x-2 rounded-lg bg-primary text-white hover:bg-primary/90 disabled:bg-gray-300 disabled:text-gray-500"
          >
            {isProcessing ? (
              <>
                <div className="h-4 w-4 animate-spin rounded-full border-b-2 border-white"></div>
                <span>처리 중...</span>
              </>
            ) : (
              <span>설정 변경 완료</span>
            )}
          </Button>
        </div>
      </div>
    </>
  );
};

export default ConfirmStep;
