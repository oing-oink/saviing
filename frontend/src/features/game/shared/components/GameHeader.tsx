import Coin from '@/features/game/shared/components/Coin';
import closeButton from '@/assets/game_button/closeButton.png';
import storeButton from '@/assets/game_button/storeButton.png';
import { useNavigate } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';

/**
 * 게임 메인 페이지 상단바
 *
 * home 버튼 + 코인 + shop 버튼
 */
const GameHeader = () => {
  const navigate = useNavigate();
  return (
    <div className="flex h-20 w-full items-center justify-between px-3">
      <button onClick={() => navigate(PAGE_PATH.HOME)}>
        <img className="w-9 pt-5" src={closeButton} />
      </button>
      <Coin />
      <button onClick={() => navigate(PAGE_PATH.SHOP)}>
        <img className="w-9 pt-5" src={storeButton} />
      </button>
    </div>
  );
};

export default GameHeader;
