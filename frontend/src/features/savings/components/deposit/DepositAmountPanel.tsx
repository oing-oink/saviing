import { formatCurrency } from '@/lib/formatters';
import type { QuickAmount } from '@/features/savings/types/deposit';
import { Button } from '@/shared/components/ui/button';

interface DepositAmountPanelProps {
  amount: number;
  onAmountChange: (value: number) => void;
  onQuickAdd: (value: number) => void;
  onFillAll: () => void;
  onReset: () => void;
  quickAmounts: QuickAmount[];
  availableBalance: number;
  afterTransferBalance: number;
  projectedBalance: number;
  projectedProgress?: number;
  remainingAfterDeposit?: number;
  isSourceSelectable: boolean;
  memo: string;
  onMemoChange: (value: string) => void;
}

const DepositAmountPanel = ({
  amount,
  onAmountChange,
  onQuickAdd,
  onFillAll,
  onReset,
  quickAmounts,
  availableBalance,
  afterTransferBalance,
  projectedBalance,
  projectedProgress,
  remainingAfterDeposit,
  isSourceSelectable,
  memo,
  onMemoChange,
}: DepositAmountPanelProps) => {
  const amountInputValue = amount === 0 ? '' : formatCurrency(amount);
  const quickDisabled = !isSourceSelectable || availableBalance === 0;

  return (
    <div className="space-y-[1rem]">
      <div className="relative">
        <input
          inputMode="numeric"
          pattern="\\d*"
          value={amountInputValue}
          onChange={event => {
            const numericValue = event.target.value.replace(/[^0-9]/g, '');
            onAmountChange(
              numericValue === '' ? 0 : parseInt(numericValue, 10),
            );
          }}
          placeholder="0"
          className="w-full rounded-[1rem] border border-border bg-card px-[1.25rem] py-[1.25rem] text-3xl font-semibold text-foreground shadow-inner focus:border-primary focus:ring-[0.125rem] focus:ring-primary/30 focus:outline-none"
        />
        <span className="pointer-events-none absolute top-1/2 right-[1.5rem] -translate-y-1/2 text-base font-medium text-muted-foreground">
          원
        </span>
      </div>

      <div className="flex flex-wrap gap-[0.5rem]">
        <Button
          type="button"
          variant="outline"
          size="sm"
          disabled={!isSourceSelectable || amount === 0}
          onClick={onReset}
          className="rounded-[1rem] border-border bg-card px-[1rem] py-[0.5rem] text-sm font-medium text-muted-foreground hover:border-primary hover:bg-primary/10 disabled:text-muted-foreground/60"
        >
          금액 초기화
        </Button>
        {quickAmounts.map(value => (
          <Button
            key={value}
            type="button"
            variant="outline"
            size="sm"
            disabled={quickDisabled}
            onClick={() => onQuickAdd(value)}
            className="rounded-[1rem] border-border bg-card px-[1rem] py-[0.5rem] text-sm font-medium text-foreground hover:border-primary hover:bg-primary/10"
          >
            +{formatCurrency(value)}원
          </Button>
        ))}
        <Button
          type="button"
          variant="outline"
          size="sm"
          disabled={quickDisabled}
          onClick={onFillAll}
          className="rounded-[1rem] border-border bg-card px-[1rem] py-[0.5rem] text-sm font-medium text-primary hover:border-primary hover:bg-primary/10"
        >
          전액 이체
        </Button>
      </div>

      {/* 메모 입력 필드 */}
      <div>
        <label className="mb-3 block pt-3 text-sm font-medium text-foreground">
          받는 분에게 표시
        </label>
        <input
          type="text"
          value={memo}
          onChange={e => onMemoChange(e.target.value)}
          placeholder="입금 내역에 대한 메모를 입력하세요"
          maxLength={7}
          className="w-full rounded-[1rem] border border-border bg-card px-[1rem] py-[0.75rem] text-sm text-foreground placeholder:text-muted-foreground focus:border-primary focus:ring-[0.125rem] focus:ring-primary/30 focus:outline-none"
        />
        <p className="mt-2 h-4 text-xs">
          {memo.length === 7 && (
            <span className="text-red-500">7자까지 입력 가능해요</span>
          )}
        </p>
      </div>

      <div className="space-y-[0.75rem] rounded-[1rem] bg-muted/80 px-[1.25rem] py-[1rem] text-sm">
        <SummaryRow
          label="이체 가능"
          value={`${formatCurrency(availableBalance)}원`}
        />
        <SummaryRow
          label="이체 후 잔액"
          value={`${formatCurrency(afterTransferBalance)}원`}
        />
        <SummaryRow
          label="예상 잔액"
          value={`${formatCurrency(projectedBalance)}원${projectedProgress !== undefined ? ` · ${projectedProgress}%` : ''}`}
        />
      </div>

      {amount > 0 && remainingAfterDeposit !== undefined ? (
        <div className="rounded-[1rem] bg-primary/5 px-[1.25rem] py-[1rem] text-center text-xs text-primary">
          입금 후 목표까지 남은 금액은{' '}
          <span className="font-semibold">
            {formatCurrency(remainingAfterDeposit)}원
          </span>
          입니다
        </div>
      ) : null}
    </div>
  );
};

const SummaryRow = ({ label, value }: { label: string; value: string }) => {
  return (
    <div className="flex items-center justify-between text-sm">
      <span className="text-muted-foreground">{label}</span>
      <span className="font-medium text-foreground">{value}</span>
    </div>
  );
};

export default DepositAmountPanel;
