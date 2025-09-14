import decoButton from '@/assets/game_button/decoButton.png';
import elevatorBasic from '@/assets/game_button/elevatorBasic.png';
import { useNavigate } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';

/**
 * 게임 메인 페이지 하단바
 *
 * deco 버튼 + room 변경 버튼
 */
const GameHeader2 = () => {
  const navigate = useNavigate();
  return (
    <div className="flex w-full justify-between px-3">
      <button onClick={() => navigate(PAGE_PATH.DECO)}>
        <img className="w-9" src={decoButton} />
      </button>
      <button>
        <img className="w-9" src={elevatorBasic} />
      </button>
    </div>
  );
};

export default GameHeader2;
