import { Card, CardContent } from '@/shared/components/ui/card';

const SavingsTransactionSkeleton = () => {
  return (
    <div className="space-y-3">
      {Array.from({ length: 5 }).map((_, index) => (
        <Card key={index}>
          <CardContent>
            <div className="flex items-center justify-between">
              {/* 왼쪽 설명 부분 */}
              <div className="flex-1">
                <div className="h-4 w-32 animate-pulse rounded bg-gray-200"></div>
                <div className="mt-2 h-3 w-20 animate-pulse rounded bg-gray-200"></div>
              </div>

              {/* 오른쪽 금액 부분 */}
              <div className="text-right">
                <div className="h-4 w-24 animate-pulse rounded bg-gray-200"></div>
                <div className="mt-2 h-3 w-16 animate-pulse rounded bg-gray-200"></div>
              </div>
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  );
};

export default SavingsTransactionSkeleton;
