import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from '@/shared/components/ui/card';
import { Button } from '@/shared/components/ui/button';
import { useNavigate } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';
import { formatDate } from '@/shared/utils/dateFormat';
import type { SavingsAccountData } from '@/features/savings/types/savingsTypes';

interface AccountDetailCardProps {
  data?: SavingsAccountData;
  isLoading: boolean;
  error: Error | null;
  balanceSectionRef?: React.RefObject<HTMLDivElement | null>;
  isSticky?: boolean;
}

const AccountDetailCard = ({
  data: accountData,
  isLoading,
  error,
  balanceSectionRef,
  isSticky = false,
}: AccountDetailCardProps) => {
  const navigate = useNavigate();

  if (isLoading) {
    return (
      <Card className="rounded-t-none rounded-b-xl">
        <CardHeader>
          <div className="h-4 w-3/4 rounded bg-gray-200"></div>
        </CardHeader>
        <CardContent>
          <div className="space-y-3">
            <div className="h-4 rounded bg-gray-200"></div>
            <div className="h-4 w-5/6 rounded bg-gray-200"></div>
          </div>
        </CardContent>
      </Card>
    );
  }

  if (error) {
    return (
      <Card className="rounded-t-none rounded-b-xl">
        <CardContent>
          <p className="text-red-500">
            데이터를 불러오는 중 오류가 발생했습니다.
          </p>
        </CardContent>
      </Card>
    );
  }

  if (!accountData) {
    return (
      <Card className="rounded-t-none rounded-b-xl">
        <CardContent>
          <p className="text-gray-500">계좌 정보를 찾을 수 없습니다.</p>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className="gap-5 rounded-t-none rounded-b-xl px-3 pt-3 pb-6">
      <CardHeader>
        <CardTitle className="text-lg">
          {accountData.product.productName}
        </CardTitle>
        <p className="text-sm text-gray-600">
          계좌번호: {accountData.accountNumber}
        </p>
      </CardHeader>
      <CardContent>
        <div>
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-semibold text-gray-700">개설일</h3>
            <p>{formatDate(accountData.openedAt)}</p>
          </div>

          <div ref={balanceSectionRef} className="pt-2">
            <div className="flex items-center justify-between gap-1">
              <p className="text-2xl font-bold text-primary">
                {accountData.balance.toLocaleString()}원
              </p>
            </div>
          </div>

          <div className={`${isSticky ? 'hidden' : 'block'}`}>
            <div className="flex justify-center gap-3 pt-3">
              <Button
                className="text-black-900 flex-1 bg-secondary"
                onClick={() => navigate(PAGE_PATH.HOME)}
              >
                계좌 설정
              </Button>
              <Button
                className="flex-1"
                onClick={() => navigate(PAGE_PATH.HOME)}
              >
                입금
              </Button>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default AccountDetailCard;
