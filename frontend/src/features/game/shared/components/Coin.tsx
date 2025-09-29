import coinImage from '@/assets/game_button/coin.png';
import fishCoinImage from '@/assets/game_button/fishCoin.png';
import { useGameStore } from '@/features/game/shared/store/useGameStore';
import { useGameQuery } from '@/features/game/shared/query/useGameQuery';
import { useGameEntryQuery } from '@/features/game/entry/query/useGameEntryQuery';
import { useEffect } from 'react';

interface CoinProps {
  coin?: number;
  fishCoin?: number;
}

/**
 * 사용자의 보유 재화(코인, 피시코인)를 표시하는 컴포넌트
 *
 * @param coin - 표시할 코인 수량 (props로 전달된 경우)
 * @param fishCoin - 표시할 피시코인 수량 (props로 전달된 경우)
 * props가 전달되지 않으면 게임 전역 상태에서 가져옵니다.
 */
const Coin = ({ coin, fishCoin }: CoinProps) => {
  const { data: gameEntry } = useGameEntryQuery();
  const characterId = gameEntry?.characterId;

  const { gameData, setGameData } = useGameStore();
  const { data: apiGameData } = useGameQuery(characterId);

  useEffect(() => {
    if (apiGameData) {
      setGameData(apiGameData);
    }
  }, [apiGameData, setGameData]);

  const coinAmount = coin ?? gameData?.coin ?? 0;
  const fishCoinAmount = fishCoin ?? gameData?.fishCoin ?? 0;

  return (
    <div className="flex items-end justify-center gap-2 pt-4">
      {/* Coin */}
      <div className="relative flex h-16 w-28 items-center">
        <img
          src={coinImage}
          alt="coin"
          className="h-full w-auto object-contain"
        />
        <span className="text-md absolute right-4 bottom-4 mb-1 font-bold text-gray-600">
          {coinAmount}
        </span>
      </div>

      {/* Fish Coin */}
      <div className="relative ml-1 flex h-16 w-28 items-center">
        <img
          src={fishCoinImage}
          alt="fish coin"
          className="-ml-2 h-full w-auto scale-105 object-contain"
        />
        <span className="text-md absolute right-5 bottom-4 mb-1 font-bold text-gray-600">
          {fishCoinAmount}
        </span>
      </div>
    </div>
  );
};

export default Coin;
