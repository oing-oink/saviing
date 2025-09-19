import { formatCurrency } from '@/lib/formatters';
import { cn } from '@/lib/utils';
import type { SourceAccount } from '@/features/savings/types/deposit';

interface SourceAccountListProps {
  accounts: SourceAccount[];
  selectedAccountId: string;
  onSelect: (accountId: string) => void;
}

const SourceAccountList = ({
  accounts,
  selectedAccountId,
  onSelect,
}: SourceAccountListProps) => {
  return (
    <div className="space-y-2">
      {accounts.map(account => {
        const isSelected = account.id === selectedAccountId;

        return (
          <button
            key={account.id}
            type="button"
            onClick={() => onSelect(account.id)}
            className={cn(
              'w-full rounded-2xl border px-4 py-4 text-left transition-colors',
              'bg-card shadow-sm hover:border-primary/40',
              isSelected
                ? 'border-primary ring-2 ring-primary/20'
                : 'border-border',
            )}
          >
            <div className="flex items-center justify-between gap-3">
              <div className="flex flex-col gap-1">
                <span className="text-sm font-medium text-foreground">
                  {account.bankName}
                </span>
                <div className="flex flex-wrap items-center gap-1 text-xs text-muted-foreground">
                  <span className="whitespace-nowrap">
                    {account.productName}
                  </span>
                  <span className="text-muted-foreground/40">·</span>
                  <span className="whitespace-nowrap">
                    {account.maskedNumber}
                  </span>
                </div>
                {account.description ? (
                  <span className="text-xs text-muted-foreground/80">
                    {account.description}
                  </span>
                ) : null}
              </div>
              <span className="text-sm font-semibold text-primary">
                {formatCurrency(account.balance)}원
              </span>
            </div>
          </button>
        );
      })}
    </div>
  );
};

export default SourceAccountList;
