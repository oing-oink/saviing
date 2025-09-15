import { useState, useEffect } from 'react';
import backButton from '@/assets/game_button/backButton.png';
import { PAGE_PATH } from '@/shared/constants/path';
import { useNavigate } from 'react-router-dom';
import catPaw from '@/assets/catPaw.png';
import gachaMachine from '@/assets/gachaMachine.png';
import GachaResult from '@/features/game/shop/components/GachaResult';
import { mockInventoryItems } from '@/features/game/shop/mocks/inventoryMockData';
import Fireworks from '@/features/game/shop/components/Fireworks';

const GachaRollingPage = () => {
  const navigate = useNavigate();
  const [showResult, setShowResult] = useState(false);

  useEffect(() => {
    const timer = setTimeout(() => {
      setShowResult(true);
    }, 3000);

    return () => clearTimeout(timer);
  }, []);

  const handleCloseResult = () => {
    setShowResult(false);
  };
  return (
    <div className="game flex min-h-screen flex-col bg-store-bg font-galmuri">
      {/* 상단 영역 */}
      <div className="flex items-end justify-between px-6">
        <button onClick={() => navigate(PAGE_PATH.GACHA)}>
          <img className="w-[70%] pt-10" src={backButton} alt="" />
        </button>
        <div className="text-3xl text-red-400">GACHA</div>
        <div className="px-4"></div>
      </div>

      <div className="flex justify-center pt-10">
        <img
          src={gachaMachine}
          alt=""
          className="animate-gacha-shake w-[60%]"
        />
      </div>

      <div className="flex flex-col items-center pt-10 text-xl">
        <div>뽑는중이다냥!</div>
      </div>
      <div className="flex flex-col items-center pt-5 text-sm">
        <div>두근두근 어떤 아이템이 나올지</div>
        <div>너무 떨린다옹!!</div>
      </div>

      <div className="mt-auto flex justify-center pb-6">
        <img src={catPaw} alt="" className="w-[85%]" />
      </div>

      {showResult && (
        <GachaResult
          item={mockInventoryItems[0]}
          onClose={handleCloseResult}
        />
      )}
      <Fireworks isActive={showResult} />
    </div>
  );
};
export default GachaRollingPage;
