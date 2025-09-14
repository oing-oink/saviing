import coin from '@/assets/game_button/coin.png';
import fishCoin from '@/assets/game_button/fishCoin.png';

const Coin = () => {
  // Mock data
  const coinAmount = 10000;
  const fishCoinAmount = 10000;

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
