import { formatCurrency, formatDateLabel } from '@/lib/formatters';
import type { SavingAccount } from '@/features/savings/types/deposit';
import { Badge } from '@/shared/components/ui/badge';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/shared/components/ui/card';
import { Progress } from '@/shared/components/ui/progress';

interface AccountSummaryCardProps {
  savingAccount: SavingAccount;
  progress: number;
  remainingTarget: number;
}

const AccountSummaryCard = ({
  savingAccount,
  progress,
  remainingTarget,
}: AccountSummaryCardProps) => {
  return (
    <Card className="rounded-2xl border border-border/60 bg-card shadow-sm">
      <CardHeader className="space-y-4">
        <div className="flex items-start justify-between gap-3">
          <div className="space-y-1">
            <CardTitle className="text-lg font-medium text-foreground">
              {savingAccount.name}
            </CardTitle>
            <CardDescription className="flex items-center gap-1 text-xs">
              <span className="whitespace-nowrap">
                {savingAccount.bankName}
              </span>
              <span className="text-muted-foreground/40">·</span>
              <span className="whitespace-nowrap">
                {savingAccount.maskedNumber}
              </span>
            </CardDescription>
          </div>
          <Badge variant="secondary" className="bg-primary/10 text-primary">
            기본 {savingAccount.baseRate}% + 우대 {savingAccount.bonusRate}%p
          </Badge>
        </div>

        <div className="flex flex-col space-y-1">
          <span className="text-4xl font-semibold text-primary">
            {formatCurrency(savingAccount.balance)}원
          </span>
          <span className="pt-1 text-sm text-muted-foreground">
            목표 {formatCurrency(savingAccount.targetAmount)}원 중 {progress}%
            달성
          </span>
        </div>
      </CardHeader>

      <CardContent className="space-y-4">
        <Progress value={progress} className="h-2 rounded-full bg-primary/15" />

        <div className="flex items-center justify-between rounded-2xl bg-muted/70 px-4 py-3">
          <span className="text-xs text-muted-foreground">다음 자동이체</span>
          <span className="text-sm font-medium text-foreground">
            {formatDateLabel(savingAccount.nextAutoTransferDate)}
          </span>
        </div>

        <div className="flex items-center justify-between rounded-2xl bg-muted/70 px-4 py-3">
          <span className="text-xs text-muted-foreground">
            목표까지 남은 금액
          </span>
          <span className="text-sm font-medium text-foreground">
            {formatCurrency(remainingTarget)}원
          </span>
        </div>
      </CardContent>
    </Card>
  );
};

export default AccountSummaryCard;
