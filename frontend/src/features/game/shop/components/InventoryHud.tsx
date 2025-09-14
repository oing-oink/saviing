import Coin from '@/features/game/shared/components/Coin';
import backButton from '@/assets/game_button/backButton.png';
import gachaButton from '@/assets/game_button/gachaButton.png';
import { useNavigate } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';

const InventoryHud = () => {
  const navigate = useNavigate();
  return (
    <div className="flex w-full justify-between px-3">
      <button onClick={() => navigate(PAGE_PATH.GAME)}>
        <img className="w-8 pt-10" src={backButton} />
      </button>
      <Coin />
      <button onClick={() => navigate(PAGE_PATH.GACHA)}>
        <img className="w-8 pt-10" src={gachaButton} />
      </button>
    </div>
  );
};

export default InventoryHud;
