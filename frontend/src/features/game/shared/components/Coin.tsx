import coin from '@/assets/game_button/coin.png';
import fishCoin from '@/assets/game_button/fishCoin.png';

const Coin = () => {
  // Mock data
  const coinAmount = 10000;
  const fishCoinAmount = 10000;

  return (
    <div className="flex items-end justify-center gap-3">
      {/* Coin */}
      <div className="relative h-16 w-24">
        <img
          src={coin}
          alt="coin"
          className="h-full w-full object-contain object-bottom"
        />
        <span className="absolute inset-0 flex items-end justify-end pr-2 pb-1 text-lg font-bold text-gray-600">
          {coinAmount}
        </span>
      </div>

      {/* Fish Coin */}
      <div className="relative h-16 w-24">
        <img
          src={fishCoin}
          alt="fish coin"
          className="h-full w-full object-contain object-bottom"
        />
        <span className="absolute inset-0 flex items-end justify-end pr-2 pb-1 text-lg font-bold text-gray-600">
          {fishCoinAmount}
        </span>
      </div>
    </div>
  );
};

export default Coin;
