import Coin from '@/features/game/shared/components/Coin';
import backButton from '@/assets/game_button/backButton.png';
import gachaButton from '@/assets/game_button/gachaButton.png';
import { useNavigate } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';
import decoButton from '@/assets/game_button/decoButton.png';

/** 상점 인벤토리 상단/하단의 네비게이션 버튼 HUD. */
const InventoryHud = () => {
  const navigate = useNavigate();

  return (
    <div>
      <div className="flex h-20 w-full items-center justify-between px-3">
        <button onClick={() => navigate(PAGE_PATH.GAME)}>
          <img
            className="w-9 pt-5 focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
            src={backButton}
          />
        </button>
        <Coin />
        <button onClick={() => navigate(PAGE_PATH.GACHA)}>
          <img
            className="w-9 pt-5 focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
            src={gachaButton}
          />
        </button>
      </div>
      <div className="flex justify-end px-3">
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

export default InventoryHud;
