import Coin from '@/features/game/shared/components/Coin';
import closeButton from '@/assets/game_button/closeButton.png';
import storeButton from '@/assets/game_button/storeButton.png';
import decoButton from '@/assets/game_button/decoButton2.png';
import { useNavigate } from 'react-router-dom';
import {
  PAGE_PATH,
  ACCOUNT_CREATION_STEPS_PATH,
} from '@/shared/constants/path';
import { useAccountConnection } from '@/features/savings/query/useAccountsQuery';
import { Button } from '@/shared/components/ui/button';

/**
 * 게임 메인 페이지 상단바
 *
 * home 버튼 + 코인 + shop 버튼
 */
const GameHeader = () => {
  const navigate = useNavigate();
  const { isLoading, hasSavingsAccount, savingsAccount } =
    useAccountConnection();

  const handleAccountConnection = () => {
    if (!hasSavingsAccount) {
      navigate(`${ACCOUNT_CREATION_STEPS_PATH.START}?type=savings&from=game`);
    }
  };

  const getInterestRateDisplay = () => {
    if (!savingsAccount) {
      return '';
    }
    const interestRate =
      (savingsAccount.baseRate + savingsAccount.bonusRate) / 100;
    return `이자율 ${interestRate.toFixed(2)}%`;
  };

  const getButtonText = () => {
    if (isLoading) {
      return '확인 중...';
    }
    if (hasSavingsAccount) {
      return getInterestRateDisplay();
    }
    return '적금계좌 개설';
  };

  return (
    <div>
      <div className="flex h-20 w-full items-center justify-between px-3">
        <button onClick={() => navigate(PAGE_PATH.HOME)}>
          <img
            className="w-9 pt-5 focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
            src={closeButton}
          />
        </button>
        <Coin />
        <button onClick={() => navigate(PAGE_PATH.SHOP)}>
          <img
            className="w-9 pt-5 focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
            src={storeButton}
          />
        </button>
      </div>
      <div className="flex items-center justify-between px-3">
        <Button
          onClick={handleAccountConnection}
          disabled={isLoading}
          className="rounded-2xl border border-gray-600 text-base text-gray-600"
        >
          {getButtonText()}
        </Button>
        <button onClick={() => navigate(PAGE_PATH.DECO)}>
          <img
            className="w-9 focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
            src={decoButton}
          />
        </button>
      </div>
    </div>
  );
};

export default GameHeader;
