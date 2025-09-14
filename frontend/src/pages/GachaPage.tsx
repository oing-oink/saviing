import backButton from '@/assets/game_button/backButton.png';
import { PAGE_PATH } from '@/shared/constants/path';
import { useNavigate } from 'react-router-dom';
import catPaw from '@/assets/catPaw.png';
import gachaMachine from '@/assets/gachaMachine.png';
import coin from '@/assets/coin.png';
import Coin from '@/features/game/shared/components/Coin';

const GachaPage = () => {
  const navigate = useNavigate();
  return (
    <div className="game flex min-h-screen flex-col bg-store-bg font-galmuri">
      {/* 상단 영역 */}
      <div className="flex h-20 items-center justify-between px-3">
        <button onClick={() => navigate(PAGE_PATH.SHOP)}>
          <img className="w-9 pt-5" src={backButton} alt="" />
        </button>
        <div className="pt-5 text-3xl text-red-400">GACHA</div>
        <div className="w-9"></div>
      </div>

      <div className="flex justify-center pt-10">
        <img src={gachaMachine} alt="" className="w-[60%]" />
      </div>

      <div className="flex flex-col items-center text-lg">
        <div>재화를 넣으면</div>
        <div>아이템이 나온다냥!</div>
      </div>
      <div className="flex flex-col items-center pt-5 text-sm">
        <div>단, 어떤 아이템이 나올지는 랜덤이라옹</div>
        <div>도전하겠냥?</div>
      </div>

      <button
        className="mx-auto flex justify-center pt-9"
        onClick={() => {
          navigate(PAGE_PATH.GACHA_ROLLING);
        }}
      >
        <img src={coin} alt="" className="w-[70%]" />
      </button>
      <div className="mt-auto flex justify-center pb-6">
        <img src={catPaw} alt="" className="w-[85%]" />
      </div>
    </div>
  );
};

export default GachaPage;
