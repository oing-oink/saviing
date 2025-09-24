import { useState, useMemo } from 'react';

interface SourceAccount {
  id: string;
  accountId: number;
  bankName: string;
  productName: string;
  maskedNumber: string;
  balance: number;
}

interface DemandAccount {
  id: string;
  accountId: number;
  name: string;
  bankName: string;
  maskedNumber: string;
  balance: number;
}

interface UseDemandDepositTransferFormProps {
  sourceAccounts: SourceAccount[];
  demandAccount?: DemandAccount;
}

export const useDemandDepositTransferForm = ({
  sourceAccounts,
  demandAccount,
}: UseDemandDepositTransferFormProps) => {
  const [selectedAccountId, setSelectedAccountId] = useState<string | null>(null);
  const [amount, setAmount] = useState<number>(0);

  const selectedAccount = useMemo(() => {
    return sourceAccounts.find(account => account.id === selectedAccountId);
  }, [sourceAccounts, selectedAccountId]);

  const quickAmounts = useMemo(() => {
    if (!selectedAccount) return [10000, 50000, 100000, 500000];

    const balance = selectedAccount.balance;
    const amounts = [10000, 50000, 100000, 500000];
    return amounts.filter(amt => amt <= balance);
  }, [selectedAccount]);

  const metrics = useMemo(() => {
    const availableBalance = selectedAccount?.balance || 0;
    const afterTransferBalance = Math.max(0, availableBalance - amount);
    const projectedBalance = (demandAccount?.balance || 0) + amount;

    return {
      availableBalance,
      afterTransferBalance,
      projectedBalance,
    };
  }, [selectedAccount, demandAccount, amount]);

  const canSubmit = useMemo(() => {
    return (
      selectedAccount &&
      demandAccount &&
      amount > 0 &&
      amount <= selectedAccount.balance
    );
  }, [selectedAccount, demandAccount, amount]);

  const actions = {
    selectAccount: (accountId: string) => setSelectedAccountId(accountId),
    changeAmount: (newAmount: number) => setAmount(newAmount),
    addQuickAmount: (quickAmount: number) => setAmount(prev => prev + quickAmount),
    fillAll: () => {
      if (selectedAccount) {
        setAmount(selectedAccount.balance);
      }
    },
    resetAmount: () => setAmount(0),
  };

  return {
    quickAmounts,
    selectedAccountId,
    selectedAccount,
    amount,
    canSubmit,
    metrics,
    actions,
  };
};