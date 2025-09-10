import Inventory from '@/features/game/shop/components/inventory';
import { mockInventoryItems } from '@/features/game/shop/mocks/inventoryMockData';
import GameMoney from '@/assets/game_button/gameMoney.png';
import backButton from '@/assets/game_button/backButton.png';
import gachaButton from '@/assets/game_button/gachaButton.png';
import sampleRoom from '@/assets/sampleRoom.png';
import { useNavigate } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';

const ShopPage = () => {
  const navigate = useNavigate();
  return (
    <div className="game min-h-screen w-full bg-store-bg font-galmuri">
      <div className="flex items-center justify-between px-6">
        <button onClick={() => navigate(PAGE_PATH.GAME)}>
          <img className="w-8 pt-10" src={backButton} alt="" />
        </button>
        <div className="relative h-1/2 w-1/2">
          <img src={GameMoney} alt="" className="h-full w-full pt-10" />
          <div className="absolute top-1/2 left-8 -translate-y-1/2 pt-10">
            {/*TODO: 실제 코인 보유량으로 전환 */}
            <span className="text-md font-bold text-white">1000</span>
          </div>
          <div
            className="absolute top-1/2 -translate-y-1/2 pt-10"
            style={{ left: '7.8rem' }}
          >
            <span className="text-md font-bold text-white">1000</span>
          </div>
        </div>
        <button
          onClick={() => {
            navigate(PAGE_PATH.GACHA);
          }}
        >
          <img className="w-8 pt-10" src={gachaButton} alt="" />
        </button>
      </div>
      <img src={sampleRoom} alt="" className="mt-10" />
      <Inventory items={mockInventoryItems} />
    </div>
  );
};

export default ShopPage;
