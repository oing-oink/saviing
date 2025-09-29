import { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { CheckCircle2 } from 'lucide-react';

import { Button } from '@/shared/components/ui/button';
import { formatCurrency } from '@/lib/formatters';
import { PAGE_PATH } from '@/shared/constants/path';

interface DepositResultState {
  amount: number;
  fromAccountName: string;
  fromAccountNumber: string;
  toAccountName: string;
  toAccountNumber: string;
}

const DepositResultPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const state = location.state as DepositResultState | undefined;

  useEffect(() => {
    if (!state) {
      navigate(PAGE_PATH.DEPOSIT, { replace: true });
    }
  }, [navigate, state]);

  if (!state) {
    return null;
  }

  return (
    <main className="saving flex min-h-dvh justify-center bg-background px-6 py-12 pb-8">
      <div className="flex w-full max-w-md flex-col gap-6">
        <header className="space-y-4 text-center">
          <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-primary/10">
            <CheckCircle2 className="h-9 w-9 text-primary" />
          </div>
          <div className="space-y-2">
            <h1 className="text-3xl font-semibold text-primary">송금 완료!</h1>
            <p className="text-sm text-muted-foreground">
              입력하신 금액이 자유적금 계좌에 입금되었어요.
            </p>
          </div>
        </header>

        <section className="space-y-4 rounded-2xl border border-border/60 bg-card px-5 py-6 text-sm text-foreground shadow-sm">
          <div className="flex justify-between">
            <span className="text-muted-foreground">출금 계좌</span>
            <span className="text-right font-medium text-foreground">
              {state.fromAccountName}
              <br />
              <span className="text-xs text-muted-foreground">
                {state.fromAccountNumber}
              </span>
            </span>
          </div>
          <div className="flex justify-between">
            <span className="text-muted-foreground">입금 계좌</span>
            <span className="text-right font-medium text-foreground">
              {state.toAccountName}
              <br />
              <span className="text-xs text-muted-foreground">
                {state.toAccountNumber}
              </span>
            </span>
          </div>
          <div className="flex items-center justify-between border-t border-border/60 pt-4">
            <span className="text-muted-foreground">이체 금액</span>
            <span className="text-2xl font-semibold text-primary">
              {formatCurrency(state.amount)}원
            </span>
          </div>
        </section>

        <div className="mt-auto flex flex-col gap-3">
          <Button
            type="button"
            variant="outline"
            size="lg"
            className="text-md w-full rounded-2xl bg-primary py-5 text-white"
            onClick={() => navigate(PAGE_PATH.HOME)}
          >
            홈으로 이동
          </Button>
        </div>
      </div>
    </main>
  );
};

export default DepositResultPage;
