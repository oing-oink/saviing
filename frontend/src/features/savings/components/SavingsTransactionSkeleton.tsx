const SavingsTransactionSkeleton = () => {
  return (
    <div className="space-y-4">
      {Array.from({ length: 5 }).map((_, index) => (
        <div
          key={index}
          className="flex items-center justify-between border-b border-gray-100 p-4"
        >
          {/* 왼쪽 설명 부분 */}
          <div className="flex-1">
            <div className="h-4 w-32 animate-pulse rounded bg-gray-200"></div>
            <div className="mt-2 h-3 w-20 animate-pulse rounded bg-gray-200"></div>
          </div>

          {/* 오른쪽 금액 부분 */}
          <div className="text-right">
            <div className="h-4 w-24 animate-pulse rounded bg-gray-200"></div>
            <div className="mt-2 h-3 w-12 animate-pulse rounded bg-gray-200"></div>
          </div>
        </div>
      ))}
    </div>
  );
};

export default SavingsTransactionSkeleton;
