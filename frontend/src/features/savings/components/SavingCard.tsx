import { useNavigate } from 'react-router-dom';
import { Progress } from '@/shared/components/ui/progress';
import { useAccountsList } from '@/features/savings/query/useSavingsQuery';
import saving from '@/assets/saving/saving.png';
import freeSaving from '@/assets/saving/freeSaving.png';
import { createSavingsDetailPath, PAGE_PATH } from '@/shared/constants/path';

const SavingCard = () => {
  const { data: accounts, isLoading, error } = useAccountsList();
  const navigate = useNavigate();

  // 계좌 유형별로 분리
  const savingsAccount = accounts?.find(
    account => account.product.productCategory === 'INSTALLMENT_SAVINGS',
  );
  const demandAccount = accounts?.find(
    account => account.product.productCategory === 'DEMAND_DEPOSIT',
  );

  // 저축 관리 페이지로 이동
  const handleSavingsManagement = () => {
    if (savingsAccount?.accountId) {
      navigate(createSavingsDetailPath(savingsAccount.accountId));
    }
  };

  // 로딩 상태
  if (isLoading) {
    return (
      <div className="saving w-full max-w-md rounded-2xl bg-white p-6 font-pretendard shadow">
        <div className="animate-pulse">
          <div className="mb-4 h-6 w-32 rounded bg-gray-200"></div>
          <div className="mb-6 flex items-start gap-3">
            <div className="h-10 w-10 rounded bg-gray-200"></div>
            <div className="flex-1">
              <div className="mb-2 h-8 w-24 rounded bg-gray-200"></div>
              <div className="mb-3 h-4 w-20 rounded bg-gray-200"></div>
              <div className="mb-1 h-3 w-full rounded bg-gray-200"></div>
              <div className="flex justify-between">
                <div className="h-3 w-32 rounded bg-gray-200"></div>
                <div className="h-3 w-16 rounded bg-gray-200"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // 에러 상태
  if (error) {
    return (
      <div className="saving w-full max-w-md rounded-2xl bg-white p-6 font-pretendard shadow">
        <div className="text-center text-red-500">
          <p>계좌 정보를 불러오는데 실패했습니다.</p>
          <p className="mt-1 text-sm">잠시 후 다시 시도해주세요.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="saving w-full max-w-md rounded-2xl bg-white p-6 font-pretendard shadow">
      {/* 타이틀 */}
      <h2 className="mb-4 font-medium text-gray-500">내 적금 계좌</h2>

      {/* 자유적금 */}
      {savingsAccount && (
        <div className="mb-6 flex items-start gap-3">
          <img src={saving} alt="자유적금" className="h-10 w-10" />
          <div className="flex-1">
            <p className="text-xl font-bold text-primary">
              {savingsAccount.balance.toLocaleString()}원
            </p>
            <p className="text-sm text-gray-500">
              {savingsAccount.product.productName}
            </p>
            <div className="mt-3">
              <Progress
                value={
                  (savingsAccount.balance /
                    savingsAccount.savings!.targetAmount) *
                  100
                }
                className="h-3 bg-gray-200"
              />
              <div className="mt-1 flex justify-between text-xs text-gray-400">
                <span>
                  목표 금액{' '}
                  {savingsAccount.savings!.targetAmount.toLocaleString()}원
                </span>
                <span>
                  연이율{' '}
                  {(
                    (savingsAccount.baseRate + savingsAccount.bonusRate) /
                    100
                  ).toFixed(1)}
                  %
                </span>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* 입출금 */}
      {demandAccount && (
        <div className="mb-4 flex items-start gap-3">
          <img src={freeSaving} alt="입출금" className="h-10 w-10" />
          <div>
            <p className="text-xl font-bold text-primary">
              {demandAccount.balance.toLocaleString()}원
            </p>
            <p className="text-sm text-gray-500">
              {demandAccount.product.productName}
            </p>
          </div>
        </div>
      )}

      {/* 계좌가 없는 경우 */}
      {!savingsAccount && !demandAccount && (
        <div className="mb-6 text-center text-gray-500">
          <p>등록된 계좌가 없습니다.</p>
          <p className="mt-1 text-sm">새 계좌를 개설해보세요.</p>
        </div>
      )}

      {/* 하단 버튼 */}
      <div className="flex border-t border-gray-200 pt-3">
        <button
          onClick={handleSavingsManagement}
          disabled={!savingsAccount?.accountId}
          className="font-lg flex-1 py-1 text-center font-bold text-primary disabled:text-gray-400"
        >
          저축 관리
        </button>
        <button
          className="font-lg flex-1 border-l border-gray-200 py-1 text-center font-bold text-primary"
          onClick={() => navigate(PAGE_PATH.DEPOSIT)}
        >
          입금
        </button>
      </div>
    </div>
  );
};

export default SavingCard;
