import { Progress } from '@/shared/components/ui/progress';
import { Copy } from 'lucide-react';
import { CopyToClipboard } from 'react-copy-to-clipboard';
import toast from 'react-hot-toast';
import { useNavigate } from 'react-router-dom';
import {
  createDepositPath,
  createSavingsDetailPath,
} from '@/shared/constants/path';
import type { SavingsAccountData } from '@/features/savings/types/savingsTypes';
import { Badge } from '@/shared/components/ui/badge';
import { useConnectedCharacterRate } from '@/features/game/shared/hooks/useConnectedCharacterRate';
import { useSavingsDisplayData } from '@/features/savings/query/useSavingsQuery';

interface SavingsAccountWalletCardProps {
  account: SavingsAccountData;
}

const SavingsAccountWalletCard = ({
  account,
}: SavingsAccountWalletCardProps) => {
  const navigate = useNavigate();
  const target = account.savings?.targetAmount || 0;
  const current = account.balance;
  const percent = target > 0 ? (current / target) * 100 : 0;

  // 적금 계좌 표시 데이터 조회 (게임 보너스 포함)
  const { data: savingsDisplayData } = useSavingsDisplayData(
    account.accountId.toString(),
  );

  // 연결된 캐릭터의 계산된 이자율 조회
  const { calculatedRate, isConnected } = useConnectedCharacterRate(
    account.accountId,
  );

  // 실제 표시할 이자율 계산 (SavingsDetailCard와 동일한 로직)
  const displayInterestRate = (() => {
    if (savingsDisplayData) {
      // 게임 연결 시 계산된 이자율 사용, 아니면 기본 이자율 사용
      return calculatedRate ?? savingsDisplayData.interestRate;
    }
    // fallback: account의 기본 이자율
    return (account.baseRate + account.bonusRate) / 100;
  })();

  const interestRate = displayInterestRate.toFixed(2);

  // 디버깅용 로그 (개발 환경에서만)
  if (process.env.NODE_ENV === 'development') {
    console.log('🎯 SavingsAccountWalletCard Debug:', {
      accountId: account.accountId,
      isConnected,
      calculatedRate,
      savingsDisplayDataRate: savingsDisplayData?.interestRate,
      displayInterestRate,
      finalDisplayRate: interestRate,
      savingsDisplayData: Boolean(savingsDisplayData),
      baseRate: account.baseRate,
      bonusRate: account.bonusRate,
    });
  }

  const isAccountClosed = account.status === 'CLOSED';
  const isAccountActive = account.status === 'ACTIVE';

  // 저축 상세 페이지로 이동
  const handleSavingsManagement = () => {
    if (account?.accountId) {
      // navigate(createSavingsDetailPath(account.accountId, PAGE_PATH.WALLET)); // [채은 코드]
      navigate(`${createSavingsDetailPath(account.accountId)}?from=wallet`); // [승윤 코드]
    }
  };

  return (
    <div
      className={`w-full max-w-md rounded-2xl p-6 font-pretendard shadow ${
        isAccountClosed ? 'bg-gray-100' : 'bg-white'
      }`}
    >
      <div className="mb-6 flex items-start gap-3">
        <div className="flex-1">
          <div className="flex items-center justify-between">
            <p
              className={`text-2xl font-bold ${
                isAccountClosed ? 'text-primary/40' : 'text-primary'
              }`}
            >
              {current.toLocaleString()}원
            </p>
            <div className="text-right">
              <p className="text-md text-gray-500">
                {account.product.productName}
              </p>
              {isAccountClosed && (
                <Badge className="mt-1 bg-red-100 text-xs text-red-800">
                  해지됨
                </Badge>
              )}
              {isAccountActive && (
                <Badge className="mt-1 bg-green-100 text-xs text-green-800">
                  진행 중
                </Badge>
              )}
            </div>
          </div>
          <div className="flex items-center gap-1 text-gray-400">
            <p className="text-sm">{account.accountNumber}</p>
            <CopyToClipboard
              text={account.accountNumber}
              onCopy={() => {
                toast.dismiss(); // 기존 토스트 닫기
                toast.success('계좌번호가 복사되었습니다!');
              }}
            >
              <button>
                <Copy className="h-3 w-3" />
              </button>
            </CopyToClipboard>
          </div>
          <div className="mt-3">
            <Progress
              value={percent}
              className={`h-3 bg-gray-200 ${
                isAccountClosed ? '[&>div]:bg-primary/30' : '[&>div]:bg-primary'
              }`}
            />
            <div className="mt-2 flex justify-between text-xs text-gray-400">
              <span>목표 금액 {target.toLocaleString()}원</span>
              <span>연이율 {interestRate}%</span>
            </div>
          </div>
        </div>
      </div>

      {/* 하단 버튼 */}
      <div className="flex border-t border-gray-200 pt-3">
        <button
          onClick={handleSavingsManagement}
          className="font-lg flex-1 py-1 text-center font-bold text-primary"
        >
          저축 관리
        </button>
        <button
          disabled={isAccountClosed}
          className={`font-lg flex-1 border-l border-gray-200 py-1 text-center font-bold ${
            isAccountClosed
              ? 'cursor-not-allowed text-gray-400'
              : 'text-primary'
          }`}
          onClick={() => {
            if (!isAccountClosed && account?.accountId) {
              navigate(createDepositPath(account.accountId));
            }
          }}
        >
          입금
        </button>
      </div>
    </div>
  );
};

export default SavingsAccountWalletCard;
