import Coin from '@/features/game/shared/components/Coin';
import backButton from '@/assets/game_button/backButton.png';
import gachaButton from '@/assets/game_button/gachaButton.png';
import { useNavigate } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';

const InventoryHud = () => {
  const navigate = useNavigate();
  return (
    <div className="flex w-full justify-between items-center px-3 h-20">
      <button onClick={() => navigate(PAGE_PATH.GAME)}>
        <img className="w-9 pt-5" src={backButton} />
      </button>
      <Coin />
      <button onClick={() => navigate(PAGE_PATH.GACHA)}>
        <img className="w-9 pt-5" src={gachaButton} />
      </button>
    </div>
  );
};

export default InventoryHud;
