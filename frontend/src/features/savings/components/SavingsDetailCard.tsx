import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from '@/shared/components/ui/card';
import { Progress } from '@/shared/components/ui/progress';
import { Button } from '@/shared/components/ui/button';
import { Badge } from '@/shared/components/ui/badge';
import { useNavigate, useParams, useSearchParams } from 'react-router-dom';
import { PAGE_PATH, changeSavingsSettingsPath } from '@/shared/constants/path';
import { formatDate } from '@/shared/utils/dateFormat';
import type { SavingsDisplayData } from '@/features/savings/types/savingsTypes';
import { useSavingsAccountDetail } from '@/features/savings/query/useSavingsQuery';

interface SavingsDetailCardProps {
  data?: SavingsDisplayData;
  isLoading: boolean;
  error: Error | null;
  progressSectionRef?: React.RefObject<HTMLDivElement | null>;
  isSticky?: boolean;
}

const SavingsDetailCard = ({
  data: savingsData,
  isLoading,
  error,
  progressSectionRef,
  isSticky = false,
}: SavingsDetailCardProps) => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { accountId } = useParams<{ accountId: string }>();

  // URL 파라미터에서 from 값을 읽어옴
  const fromParam = searchParams.get('from');
  const entryPoint = fromParam ? decodeURIComponent(fromParam) : PAGE_PATH.HOME;

  // 계좌 상태 정보 가져오기
  const { data: accountDetail } = useSavingsAccountDetail(accountId!);

  // 달성률 계산
  const progressPercentage = savingsData
    ? Math.round((savingsData.balance / savingsData.targetAmount) * 100)
    : 0;

  // 계좌 상태 라벨 생성
  const getStatusLabel = (status?: string) => {
    switch (status) {
      case 'ACTIVE':
        return { text: '진행 중', color: 'bg-green-100 text-green-800' };
      case 'CLOSED':
        return { text: '해지됨', color: 'bg-red-100 text-red-800' };
      case 'SUSPENDED':
        return { text: '정지됨', color: 'bg-red-100 text-red-800' };
      default:
        return { text: '진행 중', color: 'bg-green-100 text-green-800' };
    }
  };

  const statusLabel = getStatusLabel(accountDetail?.status);
  const isAccountClosed = accountDetail?.status === 'CLOSED';

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
    <Card className="gap-5 rounded-t-none rounded-b-xl px-3 pt-3 pb-6">
      <CardHeader>
        <div className="flex items-center gap-2">
          <CardTitle className="text-lg">{savingsData.productName}</CardTitle>
          <Badge className={`text-xs ${statusLabel.color}`}>
            {statusLabel.text}
          </Badge>
        </div>
        <p className="text-sm text-gray-600">
          계좌번호: {savingsData.accountNumber}
        </p>
      </CardHeader>
      <CardContent>
        <div>
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-semibold text-gray-700">만기일</h3>
            <p>{formatDate(savingsData.maturityDate)}</p>
          </div>

          <div ref={progressSectionRef} className="pt-2">
            <div className="flex items-center justify-between gap-1">
              <p className="text-2xl font-bold text-primary">
                {savingsData.balance.toLocaleString()}원
              </p>
              <div className="flex gap-2">
                <Badge className="bg-gray/50 text-gray-600">
                  연 {savingsData.interestRate}%
                </Badge>
                <Badge className="bg-primary/50">
                  달성 {progressPercentage}%
                </Badge>
              </div>
            </div>
            <Progress
              value={(savingsData.balance / savingsData.targetAmount) * 100}
              className="my-1 bg-gray"
            />
            <div className="flex justify-end text-sm text-gray-600">
              <p>목표 {savingsData.targetAmount.toLocaleString()}원</p>
            </div>
          </div>

          <div className={`${isSticky ? 'hidden' : 'block'}`}>
            <div className="flex justify-center gap-3 pt-3">
              <Button
                disabled={isAccountClosed}
                className={`text-black-900 flex-1 ${
                  isAccountClosed
                    ? 'cursor-not-allowed bg-gray-300 text-gray-500'
                    : 'bg-secondary'
                }`}
                onClick={() => {
                  if (accountId && !isAccountClosed) {
                    navigate(changeSavingsSettingsPath(accountId, entryPoint));
                  }
                }}
              >
                설정 변경
              </Button>
              <Button
                disabled={isAccountClosed}
                className={`flex-1 ${
                  isAccountClosed
                    ? 'cursor-not-allowed bg-gray-300 text-gray-500 hover:bg-gray-300'
                    : 'bg-primary text-white hover:bg-primary/90'
                }`}
                onClick={() => {
                  if (!isAccountClosed) {
                    navigate(PAGE_PATH.HOME);
                  }
                }}
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

export default SavingsDetailCard;
