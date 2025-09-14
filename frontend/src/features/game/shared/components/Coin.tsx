import coin from '@/assets/game_button/coin.png';
import fishCoin from '@/assets/game_button/fishCoin.png';
import { useGameStore } from '@/features/game/shared/store/useGameStore';

/**
 * 사용자의 보유 재화(코인, 피시코인)를 표시하는 컴포넌트
 * 
 * 전역 상태에서 실시간 재화 정보를 가져와 표시합니다.
 * 게임 데이터가 로드되지 않은 경우 기본값(0)을 표시합니다.
 */
const Coin = () => {
  const { gameData } = useGameStore();
  
  const coinAmount = gameData?.coin ?? 0;
  const fishCoinAmount = gameData?.fishCoin ?? 0;

  return (
    <div className="flex items-end justify-center gap-2 pt-4">
      {/* Coin */}
      <div className="relative flex h-16 w-28 items-center">
        <img src={coin} alt="coin" className="h-full w-auto object-contain" />
        <span className="absolute right-4 bottom-4 text-lg font-bold text-gray-600">
          {coinAmount}
        </span>
      </div>

      {/* Fish Coin */}
      <div className="relative ml-1 flex h-16 w-28 items-center">
        <img
          src={fishCoin}
          alt="fish coin"
          className="-ml-2 h-full w-auto scale-105 object-contain"
        />
        <span className="absolute right-5 bottom-4 text-lg font-bold text-gray-600">
          {fishCoinAmount}
        </span>
      </div>
    </div>
  );
};

export default Coin;
