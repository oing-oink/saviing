import Inventory from '@/features/game/shop/components/inventory';
import { mockInventoryItems } from '@/features/game/shop/mocks/inventoryMockData';
import GameMomey from '@/assets/game_button/gameMoney.png';
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
          <img
            className="pt-10"
            src={backButton}
            alt=""
            style={{ width: '2.1rem' }}
          />
        </button>
        <div className="relative" style={{ width: '50%', height: '50%' }}>
          <img src={GameMomey} alt="" className="h-full w-full pt-10" />
          <div
            className="absolute pt-10"
            style={{ left: '2rem', top: '50%', transform: 'translateY(-50%)' }}
          >
            {/*TODO: 실제 코인 보유량으로 전환 */}
            <span className="text-md font-bold text-white">1000</span>
          </div>
          <div
            className="absolute pt-10"
            style={{
              left: '7.8rem',
              top: '50%',
              transform: 'translateY(-50%)',
            }}
          >
            <span className="text-md font-bold text-white">1000</span>
          </div>
        </div>
        <button
          onClick={() => {
            navigate(PAGE_PATH.GACHA);
          }}
        >
          <img
            className="pt-10"
            src={gachaButton}
            alt=""
            style={{ width: '2.1rem' }}
          />
        </button>
      </div>
      <img src={sampleRoom} alt="" className="mt-10" />
      <Inventory items={mockInventoryItems} />
    </div>
  );
};

export default ShopPage;
