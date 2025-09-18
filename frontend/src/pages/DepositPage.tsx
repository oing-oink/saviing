import { useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import AccountSummaryCard from '@/features/savings/components/deposit/AccountSummaryCard';
import SourceAccountList from '@/features/savings/components/deposit/SourceAccountList';
import DepositAmountPanel from '@/features/savings/components/deposit/DepositAmountPanel';
import DepositPinDrawer from '@/features/savings/components/dialogs/DepositPinDrawer';
import { useSavingsTransferForm } from '@/features/savings/hooks/useSavingsTransferForm';
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/shared/components/ui/accordion';
import { Button } from '@/shared/components/ui/button';
import { PAGE_PATH } from '@/shared/constants/path';

const DepositPage = () => {
  const {
    savingAccount,
    sourceAccounts,
    quickAmounts,
    selectedAccountId,
    selectedAccount,
    amount,
    canSubmit,
    metrics,
    actions,
  } = useSavingsTransferForm();

  const navigate = useNavigate();
  const [isDialogOpen, setIsDialogOpen] = useState(false);

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
    const transferSummary = {
      amount,
      fromAccountName: selectedAccount
        ? `${selectedAccount.bankName} ${selectedAccount.productName}`
        : '선택된 계좌',
      fromAccountNumber: selectedAccount?.maskedNumber ?? '-',
      toAccountName: `${savingAccount.bankName} ${savingAccount.name}`,
      toAccountNumber: savingAccount.maskedNumber,
    };

    actions.resetAmount();
    handleCloseDialog();
    navigate(PAGE_PATH.DEPOSIT_RESULT, { state: transferSummary });
  };

  return (
    <main className="saving flex min-h-dvh justify-center bg-background px-6 py-12 pb-32">
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
                <AccountSummaryCard
                  savingAccount={savingAccount}
                  progress={metrics.initialProgress}
                  remainingTarget={metrics.initialRemainingTarget}
                />
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
                  accounts={sourceAccounts}
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
          />
        </section>
      </div>

      <div className="fixed inset-x-0 bottom-0 z-40 bg-background/95 pt-3 pb-4 backdrop-blur">
        <div className="mx-auto w-full max-w-md px-6">
          <Button
            type="button"
            size="lg"
            disabled={!canSubmit}
            onClick={handleOpenDialog}
            className="w-full rounded-2xl bg-primary text-base font-semibold text-primary-foreground shadow-lg transition-colors disabled:cursor-not-allowed disabled:opacity-60"
          >
            자유적금으로 보내기
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
    </main>
  );
};

export default DepositPage;
