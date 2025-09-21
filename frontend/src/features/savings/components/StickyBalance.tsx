import { Button } from '@/shared/components/ui/button';
import { Badge } from '@/shared/components/ui/badge';
import { Progress } from '@/shared/components/ui/progress';
import { useNavigate, useLocation } from 'react-router-dom';
import { PAGE_PATH, changeSavingsSettingsPath } from '@/shared/constants/path';
import type { SavingsDisplayData } from '@/features/savings/types/savingsTypes';
import { Card, CardContent } from '@/shared/components/ui/card';

type ScrollDirection = 'up' | 'down' | null;

interface StickyBalanceProps {
  data: SavingsDisplayData;
  isVisible: boolean;
  scrollDirection?: ScrollDirection;
  accountId?: string;
}

const StickyBalance = ({
  data,
  isVisible,
  scrollDirection,
  accountId,
}: StickyBalanceProps) => {
  const navigate = useNavigate();
  const location = useLocation();

  // 현재 페이지의 entryPoint를 가져옴
  const entryPoint = location.state?.entryPoint ?? PAGE_PATH.HOME;

  // 달성률 계산
  const progressPercentage = Math.round(
    (data.balance / data.targetAmount) * 100,
  );

  if (!isVisible) {
    return null;
  }

  return (
    <Card className="fixed top-16 right-0 left-0 z-40 mx-auto max-w-md gap-5 rounded-t-none rounded-b-xl px-3 pt-2 pb-6">
      <CardContent>
        <div>
          <div className="flex items-center justify-between gap-1">
            <p className="text-2xl font-bold text-primary">
              {data.balance.toLocaleString()}원
            </p>
            <div className="flex gap-2">
              <Badge className="bg-gray/50 text-gray-600">
                연 {data.interestRate}%
              </Badge>
              <Badge className="bg-primary/50">
                달성 {progressPercentage}%
              </Badge>
            </div>
          </div>
          <Progress
            value={(data.balance / data.targetAmount) * 100}
            className="my-1 bg-gray"
          />
          <div className="flex justify-end text-sm text-gray-600">
            <p>목표 {data.targetAmount.toLocaleString()}원</p>
          </div>
        </div>

        {/* 버튼 영역 - 카드 하단에 추가, 스크롤 업 시에만 표시 */}
        <div
          className={`overflow-hidden transition-all duration-500 ease-in-out ${
            scrollDirection === 'up'
              ? 'max-h-20 translate-y-0 transform opacity-100'
              : 'max-h-0 translate-y-2 transform opacity-0'
          }`}
        >
          <div className="flex justify-center gap-3 pt-3">
            <Button
              className="text-black-900 flex-1 bg-secondary"
              onClick={() => {
                if (accountId) {
                  navigate(changeSavingsSettingsPath(accountId), {
                    state: { entryPoint },
                  });
                } else {
                  console.error(
                    'accountId가 없어서 설정 변경 페이지로 이동할 수 없습니다.',
                  );
                }
              }}
            >
              설정 변경
            </Button>
            <Button className="flex-1" onClick={() => navigate(PAGE_PATH.HOME)}>
              입금
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default StickyBalance;
