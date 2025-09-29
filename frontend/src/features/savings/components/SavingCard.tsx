import { useNavigate } from 'react-router-dom';
import { Progress } from '@/shared/components/ui/progress';
import {
  useAccountsList,
  useSavingsDisplayData,
} from '@/features/savings/query/useSavingsQuery';
import { useConnectedCharacterRate } from '@/features/game/shared/hooks/useConnectedCharacterRate';
import { useGameQuery } from '@/features/game/shared/query/useGameQuery';
import { useGameEntryQuery } from '@/features/game/entry/query/useGameEntryQuery';
import saving from '@/assets/saving/saving.png';
import {
  createSavingsDetailPath,
  createDepositPath,
} from '@/shared/constants/path';

const SavingCard = () => {
  const { data: accounts, isLoading, error } = useAccountsList();
  const navigate = useNavigate();

  // 게임 데이터 조회
  const { data: gameEntry } = useGameEntryQuery();
  const { data: gameData } = useGameQuery(gameEntry?.characterId);

  // 계좌 유형별로 분리
  const allSavingsAccounts = accounts?.filter(
    account => account.product.productCategory === 'INSTALLMENT_SAVINGS',
  );

  // 게임과 연결된 적금 계좌만 찾기
  const savingsAccount = allSavingsAccounts?.find(
    account =>
      gameData?.connectionStatus === 'CONNECTED' &&
      gameData?.accountId === account.accountId,
  );

  // 입출금 계좌는 게임 연동 카드에서 표시하지 않음

  // 적금 계좌가 있으면 SavingsDisplayData 조회 (기본 데이터용)
  const { data: savingsDisplayData } = useSavingsDisplayData(
    savingsAccount?.accountId ? savingsAccount.accountId.toString() : '',
  );

  // 게임 연결 상태 및 계산된 이자율 조회
  const { calculatedRate, isConnected } = useConnectedCharacterRate(
    savingsAccount?.accountId,
  );

  // 실제 표시할 이자율 계산 (게임 보너스 포함)
  const displayInterestRate = (() => {
    if (savingsDisplayData) {
      // 게임 연결 시 계산된 이자율 사용, 아니면 기본 이자율 사용
      return calculatedRate ?? savingsDisplayData.interestRate;
    }
    // fallback: savingsAccount의 기본 이자율
    return savingsAccount
      ? (savingsAccount.baseRate + savingsAccount.bonusRate) / 100
      : 0;
  })();

  // 디버깅용 로그 (개발 환경에서만) - 항상 출력하도록 수정
  console.log('🎯 SavingCard Debug - GAME CONNECTED ONLY:', {
    allSavingsAccountsCount: allSavingsAccounts?.length || 0,
    gameConnectionStatus: gameData?.connectionStatus,
    gameConnectedAccountId: gameData?.accountId,
    connectedSavingsAccount: savingsAccount
      ? {
          accountId: savingsAccount.accountId,
          productName: savingsAccount.product.productName,
        }
      : null,
    isConnected,
    calculatedRate,
    displayInterestRate,
    savingsDisplayData: Boolean(savingsDisplayData),
  });

  // 저축 상세 페이지로 이동
  const handleSavingsManagement = () => {
    if (savingsAccount?.accountId) {
      navigate(
        // createSavingsDetailPath(savingsAccount.accountId, PAGE_PATH.HOME), // [채은 코드]
        `${createSavingsDetailPath(savingsAccount.accountId)}?from=home`, // [승윤 코드]
      );
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
      {/* 제목 */}
      <h2 className="mb-4 text-lg font-medium text-gray-500">
        게임과 연동한 내 적금
      </h2>

      {/* 자유적금 */}
      {savingsAccount && (
        <div className="mb-6 flex items-start gap-3">
          <img src={saving} alt="자유적금" className="h-10 w-10" />
          <div className="flex-1">
            <p className="text-xl font-bold text-primary">
              {savingsDisplayData
                ? savingsDisplayData.balance.toLocaleString()
                : savingsAccount.balance.toLocaleString()}
              원
            </p>
            <p className="text-sm text-gray-500">
              {savingsAccount.product.productName}
            </p>
            <div className="mt-3">
              <Progress
                value={
                  savingsDisplayData
                    ? (savingsDisplayData.balance /
                        savingsDisplayData.targetAmount) *
                      100
                    : (savingsAccount.balance /
                        savingsAccount.savings!.targetAmount) *
                      100
                }
                className="h-3 bg-gray-200"
              />
              <div className="mt-1 flex justify-between text-xs text-gray-400">
                <span>
                  만기 금액{' '}
                  {savingsDisplayData
                    ? savingsDisplayData.targetAmount.toLocaleString()
                    : savingsAccount.savings!.targetAmount.toLocaleString()}
                  원
                </span>
                <span>연이율 {displayInterestRate.toFixed(2)}%</span>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* 게임 연동 안내 메시지 */}
      {!savingsAccount && (
        <div className="text-md mb-6 text-center text-gray-400">
          {allSavingsAccounts && allSavingsAccounts.length > 0 ? (
            <div>
              적금을 게임과 연동해
              <br />더 높은 이자율 혜택을 받아보세요!
            </div>
          ) : (
            <p>지금 적금을 만들어 게임과 연동해보세요!</p>
          )}
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
          onClick={() => {
            if (savingsAccount?.accountId) {
              navigate(createDepositPath(savingsAccount.accountId));
            }
          }}
          disabled={!savingsAccount?.accountId}
          className="font-lg flex-1 border-l border-gray-200 py-1 text-center font-bold text-primary disabled:text-gray-400"
        >
          입금
        </button>
      </div>
    </div>
  );
};

export default SavingCard;
