import { useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';

import AccountSummaryCard from '@/features/savings/components/deposit/AccountSummaryCard';
import SourceAccountList from '@/features/savings/components/deposit/SourceAccountList';
import DepositAmountPanel from '@/features/savings/components/deposit/DepositAmountPanel';
import DepositPinDrawer from '@/features/savings/components/dialogs/DepositPinDrawer';
import { useSavingsTransferForm } from '@/features/savings/hooks/useSavingsTransferForm';
import { transferToSavings } from '@/features/savings/api/savingsApi';
import type {
  TransferRequest,
  TransferResponse,
} from '@/features/savings/types/deposit';
import {
  useSavingsAccount,
  useAccountsList,
} from '@/features/savings/query/useSavingsQuery';
import { savingsKeys } from '@/features/savings/query/savingsKeys';
import { useCustomerStore } from '@/features/auth/store/useCustomerStore';
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/shared/components/ui/accordion';
import { Button } from '@/shared/components/ui/button';
import { PAGE_PATH } from '@/shared/constants/path';

type TransferVariables = Omit<TransferRequest, 'idempotencyKey'>;

const DepositPage = () => {
  const { accountId } = useParams<{ accountId: string }>();
  const customer = useCustomerStore(state => state.customer);
  const customerId = useCustomerStore(state => state.customerId);

  // 실제 API에서 적금 계좌 정보 가져오기
  const { data: savingsAccountData, isLoading: isSavingsLoading } =
    useSavingsAccount(accountId || '');

  // 실제 API에서 모든 계좌 목록 가져오기
  const { data: allAccounts } = useAccountsList();

  // 적금이 아닌 계좌들을 출금 계좌로 사용
  const sourceAccountsData = useMemo(() => {
    if (!allAccounts) {
      return [];
    }
    return allAccounts
      .filter(account => account.product.productCategory === 'DEMAND_DEPOSIT')
      .map(account => ({
        id: account.accountId.toString(),
        accountId: account.accountId,
        bankName: '싸피은행',
        productName: account.product.productName,
        maskedNumber: account.accountNumber.replace(
          /(\d{4})(\d{4})(\d{4})(\d{4})/,
          '$1-$2-****-$4',
        ),
        balance: account.balance,
      }));
  }, [allAccounts]);

  // 적금 계좌 데이터 변환
  const savingAccountData = useMemo(() => {
    if (!savingsAccountData) {
      return undefined;
    }
    return {
      id: savingsAccountData.accountId.toString(),
      accountId: savingsAccountData.accountId,
      name: savingsAccountData.product.productName,
      bankName: '싸피은행',
      maskedNumber: savingsAccountData.accountNumber.replace(
        /(\d{4})(\d{4})(\d{4})(\d{4})/,
        '$1-$2-****-$4',
      ),
      balance: savingsAccountData.balance,
      targetAmount: savingsAccountData.savings.targetAmount,
      baseRate: savingsAccountData.baseRate,
      bonusRate: savingsAccountData.bonusRate,
      nextAutoTransferDate: savingsAccountData.savings.maturityDate,
    };
  }, [savingsAccountData]);

  const {
    quickAmounts,
    selectedAccountId,
    selectedAccount,
    amount,
    canSubmit,
    metrics,
    actions,
  } = useSavingsTransferForm({
    sourceAccounts: sourceAccountsData,
    savingAccount: savingAccountData,
  });

  const navigate = useNavigate();
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [memo, setMemo] = useState('');
  const queryClient = useQueryClient();

  const transferMutation = useMutation<
    TransferResponse,
    unknown,
    TransferVariables
  >({
    mutationFn: ({
      sourceAccountId,
      targetAccountId,
      amount,
      memo,
    }: TransferVariables) =>
      transferToSavings(sourceAccountId, targetAccountId, amount, memo),
    onSuccess: (data, variables) => {
      const transferSummary = {
        amount,
        fromAccountName: selectedAccount
          ? `${selectedAccount.bankName} ${selectedAccount.productName}`
          : '선택된 계좌',
        fromAccountNumber: selectedAccount?.maskedNumber ?? '-',
        toAccountName: `${savingAccountData?.bankName || ''} ${savingAccountData?.name || ''}`,
        toAccountNumber: savingAccountData?.maskedNumber || '',
        transactionId: data.transactionId,
      };

      // 계좌 관련 쿼리들 무효화하여 최신 데이터로 업데이트
      queryClient.invalidateQueries({
        queryKey: savingsKeys.accountsList(customerId ?? undefined),
      });
      const targetAccountKey =
        variables.targetAccountId != null
          ? String(variables.targetAccountId)
          : savingAccountData?.accountId != null
            ? String(savingAccountData.accountId)
            : undefined;

      if (targetAccountKey) {
        queryClient.invalidateQueries({
          queryKey: savingsKeys.detail(targetAccountKey),
        });
        queryClient.invalidateQueries({
          queryKey: savingsKeys.transactionsList(targetAccountKey),
        });
      }

      actions.resetAmount();
      handleCloseDialog();
      navigate(PAGE_PATH.DEPOSIT_RESULT, { state: transferSummary });
    },
    onError: error => {
      console.error('Transfer failed:', error);
      handleCloseDialog();
    },
  });

  const selectedAccountName = useMemo(() => {
    if (!selectedAccount) {
      return '선택된 계좌 없음';
    }
    return `${selectedAccount.bankName} ${selectedAccount.productName}`;
  }, [selectedAccount]);

  const handleOpenDialog = () => {
    setIsDialogOpen(true);
  };

  const handleCloseDialog = () => {
    setIsDialogOpen(false);
  };

  const handlePinSubmit = () => {
    if (!selectedAccount?.accountId || !savingAccountData?.accountId) {
      console.error('Account IDs are missing:', {
        selectedAccount: selectedAccount?.accountId,
        savingAccount: savingAccountData?.accountId,
      });
      return;
    }

    const finalMemo = memo.trim() || customer?.name;

    transferMutation.mutate({
      sourceAccountId: selectedAccount.accountId,
      targetAccountId: savingAccountData.accountId,
      amount,
      memo: finalMemo,
    });
  };

  return (
    <div className="saving flex w-full flex-col bg-background px-6 pt-6 pb-32">
      <div className="flex w-full max-w-md flex-col gap-6">
        <header className="space-y-2">
          <span className="text-xs font-semibold tracking-wide text-primary uppercase">
            자유적금 입금
          </span>
          <h1 className="text-3xl font-semibold text-foreground">
            필요할 때마다 자유롭게
          </h1>
          <p className="text-sm text-muted-foreground">
            목표까지 남은 거리를 한눈에 확인해요
          </p>
        </header>

        <section className="-mb-2">
          <Accordion type="single" collapsible>
            <AccordionItem value="account-summary">
              <AccordionTrigger className="text-base font-semibold text-foreground">
                적금 요약
              </AccordionTrigger>
              <AccordionContent className="pt-0">
                {isSavingsLoading ? (
                  <div className="flex justify-center py-4">
                    <div className="text-sm text-muted-foreground">
                      로딩 중...
                    </div>
                  </div>
                ) : savingAccountData ? (
                  <AccountSummaryCard
                    savingAccount={savingAccountData}
                    progress={metrics.initialProgress}
                    remainingTarget={metrics.initialRemainingTarget}
                  />
                ) : (
                  <div className="flex justify-center py-4">
                    <div className="text-sm text-muted-foreground">
                      적금 정보를 불러올 수 없습니다
                    </div>
                  </div>
                )}
              </AccordionContent>
            </AccordionItem>
          </Accordion>
        </section>

        <section className="-mt-2">
          <Accordion type="single" collapsible>
            <AccordionItem value="source-accounts">
              <AccordionTrigger className="text-base font-semibold text-foreground">
                출금 계좌
              </AccordionTrigger>
              <AccordionContent className="space-y-3 pt-0">
                <SourceAccountList
                  accounts={sourceAccountsData}
                  selectedAccountId={selectedAccountId}
                  onSelect={actions.selectAccount}
                />
              </AccordionContent>
            </AccordionItem>
          </Accordion>
        </section>

        <section className="space-y-3">
          <h2 className="text-base font-semibold text-foreground">보낼 금액</h2>
          <p className="text-sm text-muted-foreground">
            필요할 때마다 입력하거나 금액 버튼으로 빠르게 채우세요.
          </p>
          <DepositAmountPanel
            amount={amount}
            onAmountChange={actions.changeAmount}
            onQuickAdd={actions.addQuickAmount}
            onFillAll={actions.fillAll}
            onReset={actions.resetAmount}
            quickAmounts={quickAmounts}
            availableBalance={metrics.availableBalance}
            afterTransferBalance={metrics.afterTransferBalance}
            projectedBalance={metrics.projectedBalance}
            projectedProgress={metrics.projectedProgress}
            remainingAfterDeposit={metrics.remainingAfterDeposit}
            isSourceSelectable={Boolean(selectedAccount)}
            memo={memo}
            onMemoChange={setMemo}
          />
        </section>
      </div>

      <div className="fixed right-0 bottom-0 left-0 z-10 bg-white p-4">
        <div className="mx-auto w-full max-w-md">
          <Button
            type="button"
            size="lg"
            disabled={!canSubmit || transferMutation.isPending}
            onClick={handleOpenDialog}
            className="flex h-12 w-full flex-1 items-center justify-center space-x-2 rounded-lg bg-primary text-white hover:bg-primary/90 disabled:bg-gray-300 disabled:text-gray-500"
          >
            {transferMutation.isPending ? (
              <>
                <div className="h-4 w-4 animate-spin rounded-full border-b-2 border-white"></div>
                <span>처리 중...</span>
              </>
            ) : (
              '자유적금으로 보내기'
            )}
          </Button>
        </div>
      </div>

      <DepositPinDrawer
        open={isDialogOpen}
        amount={amount}
        fromAccountName={selectedAccountName}
        onConfirm={handlePinSubmit}
        onClose={handleCloseDialog}
      />
    </div>
  );
};

export default DepositPage;
