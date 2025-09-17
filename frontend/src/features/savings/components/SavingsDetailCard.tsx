import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from '@/shared/components/ui/card';
import { Progress } from '@/shared/components/ui/progress';
import { Button } from '@/shared/components/ui/button';
import { useNavigate } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';
import type { SavingsDisplayData } from '@/features/savings/types/savingsTypes';

interface SavingsDetailCardProps {
  data?: SavingsDisplayData;
  isLoading: boolean;
  error: Error | null;
}

const SavingsDetailCard = ({ data: savingsData, isLoading, error }: SavingsDetailCardProps) => {
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

  if (!savingsData) {
    return (
      <Card className="rounded-t-none rounded-b-xl">
        <CardContent>
          <p className="text-gray-500">적금 정보를 찾을 수 없습니다.</p>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className="rounded-t-none rounded-b-xl px-3">
      <CardHeader>
        <CardTitle className="text-lg">{savingsData.productName}</CardTitle>
        <p className="text-sm text-gray-600">
          계좌번호: {savingsData.accountNumber}
        </p>
      </CardHeader>
      <CardContent>
        <div className="space-y-3">
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-semibold text-gray-700">현재 잔액</h3>
            <p className="text-2xl font-bold text-primary">
              {savingsData.balance.toLocaleString()}원
            </p>
          </div>
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-semibold text-gray-700">목표 금액</h3>
            <p className="text-lg text-gray-900">
              {savingsData.targetAmount.toLocaleString()}원
            </p>
          </div>
          <div>
            <Progress
              value={(savingsData.balance / savingsData.targetAmount) * 100}
              className="mb-2 bg-gray"
            />
            <div className="text-sm text-gray-600">
              <div className="flex justify-between">
                <p>이자율 {savingsData.interestRate}%</p>
                <p>
                  {Math.round(
                    (savingsData.balance / savingsData.targetAmount) * 100,
                  )}
                  % 달성
                </p>
              </div>
              <p>만기일 {savingsData.maturityDate}</p>
            </div>
          </div>
          <div className="flex justify-center gap-3">
            <Button
              className="text-black-900 flex-1 bg-secondary"
              onClick={() => navigate(PAGE_PATH.HOME)}
            >
              설정 변경
            </Button>
            <Button
              className="flex-1"
              onClick={() => navigate(PAGE_PATH.HOME)}
            >
              입금
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default SavingsDetailCard;