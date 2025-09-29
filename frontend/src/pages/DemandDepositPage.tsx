import { useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';

import SourceAccountList from '@/features/savings/components/deposit/SourceAccountList';
import DepositAmountPanel from '@/features/savings/components/deposit/DepositAmountPanel';
import DepositPinDrawer from '@/features/savings/components/dialogs/DepositPinDrawer';
import { useDemandDepositTransferForm } from '@/features/savings/hooks/useDemandDepositTransferForm';
import { transferToDemandDeposit } from '@/features/savings/api/savingsApi';
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
import DepositLayout from '@/features/savings/layouts/DepositLayout';

const DemandDepositPage = () => {
  const { accountId } = useParams<{ accountId: string }>();
  const { customer } = useCustomerStore();

  // 실제 API에서 입출금 계좌 정보 가져오기
  const { data: demandDepositAccountData } = useSavingsAccount(accountId || '');

  // 실제 API에서 모든 계좌 목록 가져오기
  const { data: allAccounts } = useAccountsList();

  // 입출금이 아닌 계좌들을 출금 계좌로 사용
  const sourceAccountsData = useMemo(() => {
    if (!allAccounts) {
      return [];
    }
    return allAccounts
      .filter(
        account =>
          account.product.productCategory !== 'DEMAND_DEPOSIT' ||
          account.accountId !== parseInt(accountId || '0'),
      )
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
  }, [allAccounts, accountId]);

  // 입출금 계좌 데이터 변환
  const demandAccountData = useMemo(() => {
    if (!demandDepositAccountData) {
      return undefined;
    }
    return {
      id: demandDepositAccountData.accountId.toString(),
      accountId: demandDepositAccountData.accountId,
      name: demandDepositAccountData.product.productName,
      bankName: '싸피은행',
      maskedNumber: demandDepositAccountData.accountNumber.replace(
        /(\d{4})(\d{4})(\d{4})(\d{4})/,
        '$1-$2-****-$4',
      ),
      balance: demandDepositAccountData.balance,
    };
  }, [demandDepositAccountData]);

  const {
    quickAmounts,
    selectedAccountId,
    selectedAccount,
    amount,
    canSubmit,
    metrics,
    actions,
  } = useDemandDepositTransferForm({
    sourceAccounts: sourceAccountsData,
    demandAccount: demandAccountData,
  });

  const navigate = useNavigate();
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [memo, setMemo] = useState('');
  const queryClient = useQueryClient();

  const transferMutation = useMutation({
    mutationFn: ({
      sourceAccountId,
      targetAccountId,
      amount,
      memo,
    }: {
      sourceAccountId: number;
      targetAccountId: number;
      amount: number;
      memo?: string;
    }) =>
      transferToDemandDeposit(sourceAccountId, targetAccountId, amount, memo),
    onSuccess: data => {
      const transferSummary = {
        amount,
        fromAccountName: selectedAccount
          ? `${selectedAccount.bankName} ${selectedAccount.productName}`
          : '선택된 계좌',
        fromAccountNumber: selectedAccount?.maskedNumber ?? '-',
        toAccountName: `${demandAccountData?.bankName || ''} ${demandAccountData?.name || ''}`,
        toAccountNumber: demandAccountData?.maskedNumber || '',
        transactionId: data.transactionId,
      };

      // 계좌 관련 쿼리들 무효화하여 최신 데이터로 업데이트
      queryClient.invalidateQueries({ queryKey: savingsKeys.accountsList() });
      if (accountId) {
        queryClient.invalidateQueries({
          queryKey: savingsKeys.detail(accountId),
        });
        queryClient.invalidateQueries({
          queryKey: savingsKeys.transactionsList(accountId),
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
    if (!selectedAccount?.accountId || !demandAccountData?.accountId) {
      console.error('Account IDs are missing:', {
        selectedAccount: selectedAccount?.accountId,
        demandAccount: demandAccountData?.accountId,
      });
      return;
    }

    const finalMemo = memo.trim() || customer?.name;

    transferMutation.mutate({
      sourceAccountId: selectedAccount.accountId,
      targetAccountId: demandAccountData.accountId,
      amount,
      memo: finalMemo,
    });
  };

  return (
    <DepositLayout>
      <div className="flex w-full flex-col gap-6 px-6 pt-6 pb-32">
        <header className="space-y-2">
          <span className="text-xs font-semibold tracking-wide text-primary uppercase">
            입출금 계좌 이체
          </span>
          <h1 className="text-3xl font-semibold text-foreground">
            자유로운 입출금
          </h1>
          <p className="text-sm text-muted-foreground">
            언제든지 자유롭게 입출금하세요
          </p>
        </header>

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
              '입출금 계좌로 보내기'
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
    </DepositLayout>
  );
};

export default DemandDepositPage;
