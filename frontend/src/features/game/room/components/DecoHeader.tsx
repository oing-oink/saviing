import Coin from '@/features/game/shared/components/Coin';
import backButton from '@/assets/game_button/backButton.png';
import storeButton from '@/assets/game_button/storeButton.png';
import { useNavigate } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';

/** 게임 데코 페이지 상단 바로, 홈/샵 이동 버튼과 보유 코인을 보여준다. */
const DecoHeader = () => {
  const navigate = useNavigate();
  return (
    <div className="flex h-20 w-full items-center justify-between px-3">
      <button onClick={() => navigate(PAGE_PATH.GAME)}>
        <img className="w-9 pt-5" src={backButton} />
      </button>
      <Coin />
      <button onClick={() => navigate(PAGE_PATH.SHOP)}>
        <img className="w-9 pt-5" src={storeButton} />
      </button>
    </div>
  );
};

export default DecoHeader;
