import { useMemo, useState } from 'react';

import { parseNumericInput } from '@/lib/formatters';
import {
  QUICK_AMOUNTS,
  SAVING_ACCOUNT,
  SOURCE_ACCOUNTS,
} from '@/features/savings/mocks/depositData';
import type {
  QuickAmount,
  SavingAccount,
  SourceAccount,
} from '@/features/savings/types/deposit';

interface UseSavingsTransferFormOptions {
  sourceAccounts?: SourceAccount[];
  savingAccount?: SavingAccount;
  quickAmounts?: QuickAmount[];
}

const computeProgress = (balance: number, targetAmount: number) => {
  if (targetAmount <= 0) {
    return 0;
  }

  return Math.min(100, Math.round((balance / targetAmount) * 100));
};

export const useSavingsTransferForm = ({
  sourceAccounts: sourceAccountsProp,
  savingAccount: savingAccountProp,
  quickAmounts: quickAmountsProp,
}: UseSavingsTransferFormOptions = {}) => {
  const sourceAccounts = sourceAccountsProp ?? SOURCE_ACCOUNTS;
  const savingAccount = savingAccountProp ?? SAVING_ACCOUNT;
  const quickAmounts = quickAmountsProp ?? QUICK_AMOUNTS;

  const [selectedAccountId, setSelectedAccountId] = useState(
    sourceAccounts[0]?.id ?? '',
  );
  const [amount, setAmount] = useState(0);

  const selectedAccount = useMemo(() => {
    return sourceAccounts.find(account => account.id === selectedAccountId);
  }, [selectedAccountId, sourceAccounts]);

  const availableBalance = selectedAccount?.balance ?? 0;
  const initialProgress = computeProgress(
    savingAccount.balance,
    savingAccount.targetAmount,
  );
  const initialRemainingTarget = Math.max(
    0,
    savingAccount.targetAmount - savingAccount.balance,
  );
  const projectedBalance = savingAccount.balance + amount;
  const projectedProgress = computeProgress(
    projectedBalance,
    savingAccount.targetAmount,
  );
  const remainingAfterDeposit = Math.max(
    0,
    savingAccount.targetAmount - projectedBalance,
  );
  const afterTransferBalance = Math.max(0, availableBalance - amount);
  const canSubmit =
    Boolean(selectedAccount) && amount > 0 && amount <= availableBalance;

  const handleSelectAccount = (accountId: string) => {
    setSelectedAccountId(accountId);
    const nextAccount = sourceAccounts.find(
      account => account.id === accountId,
    );

    setAmount(prev => {
      const nextMax = nextAccount?.balance ?? 0;
      if (nextMax === 0) {
        return 0;
      }

      return Math.min(prev, nextMax);
    });
  };

  const handleAmountInput = (value: string) => {
    if (!selectedAccount) {
      setAmount(0);
      return;
    }

    const parsed = parseNumericInput(value);
    setAmount(Math.min(parsed, selectedAccount.balance));
  };

  const handleQuickAdd = (value: number) => {
    if (!selectedAccount) {
      return;
    }

    setAmount(prev => Math.min(selectedAccount.balance, prev + value));
  };

  const handleFillAll = () => {
    if (!selectedAccount) {
      return;
    }

    setAmount(selectedAccount.balance);
  };

  const handleResetAmount = () => {
    setAmount(0);
  };

  return {
    savingAccount,
    sourceAccounts,
    quickAmounts,
    selectedAccountId,
    selectedAccount,
    amount,
    canSubmit,
    metrics: {
      availableBalance,
      afterTransferBalance,
      initialProgress,
      initialRemainingTarget,
      projectedBalance,
      projectedProgress,
      remainingAfterDeposit,
    },
    actions: {
      selectAccount: handleSelectAccount,
      changeAmount: handleAmountInput,
      addQuickAmount: handleQuickAdd,
      fillAll: handleFillAll,
      resetAmount: handleResetAmount,
    },
  };
};

export type SavingsTransferFormState = ReturnType<
  typeof useSavingsTransferForm
>;
