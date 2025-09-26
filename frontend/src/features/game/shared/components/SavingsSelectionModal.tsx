import { useQuery } from '@tanstack/react-query';
import { useState } from 'react';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogOverlay,
  DialogPortal,
} from '@/shared/components/ui/dialog';
import { Button } from '@/shared/components/ui/button';
import closeButton from '@/assets/game_button/closeButton.png';
import infoHeader from '@/assets/game_etc/infoHeader.png';
import { getAllAccounts } from '@/features/savings/api/savingsApi';
import { useCustomerStore } from '@/features/auth/store/useCustomerStore';
import { useConnectCharacterToAccount } from '@/features/game/shared/query/useGameQuery';
import { useGameEntryQuery } from '@/features/game/entry/query/useGameEntryQuery';
import { Loader2 } from 'lucide-react';
import type { SavingsAccountData } from '@/features/savings/types/savingsTypes';

interface SavingsSelectionModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSelectAccount?: (accountId: number) => void; // optional로 변경
}

/**
 * 적금 계좌 선택 모달 컴포넌트
 *
 * 게임과 금융 도메인 연결이 안 되어있지만 적금 계좌가 있는 경우 표시됩니다.
 * 사용자의 기존 적금 계좌 목록을 보여주고 연결할 계좌를 선택할 수 있습니다.
 */
const SavingsSelectionModal = ({
  isOpen,
  onClose,
}: SavingsSelectionModalProps) => {
  const { customerId } = useCustomerStore();
  const { data: gameEntry } = useGameEntryQuery();
  const characterId = gameEntry?.characterId;
  const [selectedAccountId, setSelectedAccountId] = useState<number | null>(
    null,
  );

  // 게임 캐릭터와 적금 계좌 연결 mutation
  const connectMutation = useConnectCharacterToAccount();

  const {
    data: accounts,
    isLoading,
    error,
  } = useQuery({
    queryKey: ['accounts', customerId],
    queryFn: () => getAllAccounts(customerId!),
    enabled: isOpen && Boolean(customerId),
  });

  // savings 객체가 있는 계좌만 필터링 (적금 계좌)
  const savingsAccounts =
    accounts?.filter(
      account => account.savings !== null && account.savings !== undefined,
    ) || [];

  const handleOpenChange = (open: boolean) => {
    if (!open) {
      setSelectedAccountId(null);
      onClose();
    }
  };

  const handleCancel = () => {
    setSelectedAccountId(null);
    onClose();
  };

  const handleAccountSelect = (accountId: number) => {
    setSelectedAccountId(accountId);
  };

  const handleConnect = async () => {
    if (!selectedAccountId || !characterId) {
      console.error('selectedAccountId 또는 characterId가 없습니다');
      return;
    }

    try {
      const result = await connectMutation.mutateAsync({
        characterId,
        accountId: selectedAccountId,
      });

      // 연결 성공 로그
      const connectedAccount = savingsAccounts.find(
        account => account.accountId === selectedAccountId,
      );
      console.log('✅ 적금 계좌 연결 성공:', {
        success: true,
        characterId,
        accountId: selectedAccountId,
        accountInfo: connectedAccount
          ? {
              productName: connectedAccount.product.productName,
              accountNumber: connectedAccount.accountNumber,
              balance: connectedAccount.balance,
            }
          : null,
        apiResponse: result,
      });

      // 연결 성공 후 모달 닫기
      setSelectedAccountId(null);
      onClose();
    } catch (error) {
      console.error('❌ 적금 계좌 연결 실패:', {
        success: false,
        characterId,
        accountId: selectedAccountId,
        error,
      });
      // 에러 시에는 모달을 닫지 않고 사용자에게 재시도 기회 제공
    }
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('ko-KR').format(amount);
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  if (isLoading) {
    return (
      <Dialog open={isOpen} onOpenChange={handleOpenChange}>
        <DialogPortal>
          <DialogOverlay className="bg-transparent backdrop-blur-sm backdrop-brightness-110" />
          <DialogContent
            className="game max-w-xs border-0 bg-transparent p-0 font-galmuri shadow-none"
            showCloseButton={false}
          >
            <DialogDescription className="sr-only">
              적금 계좌 목록을 불러오고 있습니다
            </DialogDescription>
            <div className="relative">
              <img
                src={infoHeader}
                alt="itemHeader"
                className="absolute top-0 left-1/2 z-10 w-44 -translate-x-1/2 -translate-y-1/2"
              />
              <div className="rounded-4xl bg-secondary px-5 pt-8 pb-4 shadow-xl">
                <div className="flex items-center justify-center gap-2 py-8">
                  <Loader2 className="h-5 w-5 animate-spin text-primary" />
                  <span className="text-sm text-gray-600">불러오는 중...</span>
                </div>
              </div>
            </div>
          </DialogContent>
        </DialogPortal>
      </Dialog>
    );
  }

  if (error || !accounts) {
    return (
      <Dialog open={isOpen} onOpenChange={handleOpenChange}>
        <DialogPortal>
          <DialogOverlay className="bg-transparent backdrop-blur-sm backdrop-brightness-110" />
          <DialogContent
            className="game max-w-xs border-0 bg-transparent p-0 font-galmuri shadow-none"
            showCloseButton={false}
          >
            <DialogDescription className="sr-only">
              적금 계좌 목록을 불러오는 중 오류가 발생했습니다
            </DialogDescription>
            <div className="relative">
              <img
                src={infoHeader}
                alt="itemHeader"
                className="absolute top-0 left-1/2 z-10 w-44 -translate-x-1/2 -translate-y-1/2"
              />
              <div className="rounded-4xl bg-secondary px-5 pt-8 pb-4 shadow-xl">
                <div className="mb-3 flex justify-end">
                  <button
                    onClick={onClose}
                    className="text-gray-500 hover:text-gray-700 active:scale-95 active:brightness-90"
                  >
                    <img src={closeButton} alt="closeButton" className="w-7" />
                  </button>
                </div>
                <div className="py-4 text-center">
                  <div className="mb-4 text-sm text-red-500">
                    계좌 정보를 불러올 수 없습니다
                  </div>
                  <Button
                    onClick={onClose}
                    className="bg-primary px-6 py-2 text-sm text-white hover:bg-primary/80"
                  >
                    닫기
                  </Button>
                </div>
              </div>
            </div>
          </DialogContent>
        </DialogPortal>
      </Dialog>
    );
  }

  return (
    <Dialog open={isOpen} onOpenChange={handleOpenChange}>
      <DialogPortal>
        <DialogOverlay className="bg-transparent backdrop-blur-sm backdrop-brightness-110" />
        <DialogContent
          className="game max-w-xs border-0 bg-transparent p-0 font-galmuri shadow-none"
          showCloseButton={false}
        >
          <DialogDescription className="sr-only">
            게임과 연결할 적금 계좌를 선택하세요
          </DialogDescription>
          <div className="relative">
            <img
              src={infoHeader}
              alt="itemHeader"
              className="absolute top-0 left-1/2 z-10 w-44 -translate-x-1/2 -translate-y-1/2"
            />
            <div className="max-h-96 overflow-hidden rounded-4xl bg-secondary px-5 pt-6 pb-4 shadow-xl">
              <div className="mb-2 flex justify-end">
                <button
                  onClick={onClose}
                  className="text-gray-500 hover:text-gray-700 active:scale-95 active:brightness-90"
                >
                  <img src={closeButton} alt="closeButton" className="w-7" />
                </button>
              </div>

              <div className="flex flex-col">
                <DialogHeader className="mb-3">
                  <DialogTitle className="text-lg font-semibold text-gray-800">
                    연결할 적금 선택
                  </DialogTitle>
                </DialogHeader>

                <div className="max-h-48 space-y-2 overflow-y-auto">
                  {savingsAccounts.map((account: SavingsAccountData) => (
                    <div
                      key={account.accountId}
                      className={`cursor-pointer rounded-lg p-3 transition-colors ${
                        selectedAccountId === account.accountId
                          ? 'border-2 border-primary bg-primary/20'
                          : 'border-2 border-transparent bg-white/50 hover:bg-white/70'
                      }`}
                      onClick={() => handleAccountSelect(account.accountId)}
                    >
                      <div className="flex items-start justify-between">
                        <div className="flex-1">
                          <div className="text-sm font-medium text-gray-800">
                            {account.product.productName}
                          </div>
                          <div className="mt-1 text-xs text-gray-600">
                            {account.accountNumber.replace(
                              /(\d{4})(\d{4})(\d{4})(\d{4})/,
                              '$1-$2-$3-$4',
                            )}
                          </div>
                          <div className="mt-1 text-xs text-gray-500">
                            개설일: {formatDate(account.createdAt)}
                          </div>
                        </div>
                        <div className="text-right">
                          <div className="text-sm font-semibold text-primary">
                            {formatCurrency(account.balance)}원
                          </div>
                          <div className="text-xs text-gray-500">
                            금리{' '}
                            {(
                              (account.baseRate + account.bonusRate) /
                              100
                            ).toFixed(2)}
                            %
                          </div>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>

                <div className="mt-4 flex gap-2">
                  <Button
                    onClick={handleCancel}
                    variant="ghost"
                    className="flex-1 bg-white px-4 py-2 text-sm text-primary hover:bg-primary/10 active:scale-95 active:brightness-90"
                  >
                    취소
                  </Button>
                  <Button
                    onClick={handleConnect}
                    disabled={!selectedAccountId || connectMutation.isPending}
                    className="flex-1 bg-primary px-4 py-2 text-sm font-medium text-white hover:bg-primary/80 active:scale-95 active:brightness-90 disabled:bg-gray-300 disabled:text-gray-500"
                  >
                    {connectMutation.isPending ? (
                      <>
                        <Loader2 className="mr-1 h-3 w-3 animate-spin" />
                        연결 중...
                      </>
                    ) : (
                      '연결하기'
                    )}
                  </Button>
                </div>
              </div>
            </div>
          </div>
        </DialogContent>
      </DialogPortal>
    </Dialog>
  );
};

export default SavingsSelectionModal;
