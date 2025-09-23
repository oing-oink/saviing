import { useState, useEffect } from 'react';
import backButton from '@/assets/game_button/backButton.png';
import { PAGE_PATH } from '@/shared/constants/path';
import { useNavigate } from 'react-router-dom';
import catPaw from '@/assets/catPaw.png';
import gachaMachine from '@/assets/gachaMachine.png';
import { useGachaDraw } from '@/features/game/shop/query/useGachaDraw';
import { useGachaInfo } from '@/features/game/shop/query/useGachaInfo';
import { useFireworks } from '@/features/game/shop/hooks/useFireworks';
import GachaResult from '@/features/game/shop/components/GachaResult';
import type { GachaDrawResponse } from '@/features/game/shop/types/item';
import toast from 'react-hot-toast';

const GachaRollingPage = () => {
  const navigate = useNavigate();
  const { data: gachaInfo } = useGachaInfo();
  const { mutate: drawGacha } = useGachaDraw();
  const [gachaResult, setGachaResult] = useState<GachaDrawResponse | null>(
    null,
  );

  useFireworks(Boolean(gachaResult));

  useEffect(() => {
    // 페이지 진입과 동시에 가챠 API 호출
    if (gachaInfo) {
      const timer = setTimeout(() => {
        drawGacha(
          {
            characterId: 1, // TODO: 실제 캐릭터 ID 사용
            gachaPoolId: gachaInfo.gachaPoolId,
            paymentMethod: 'COIN', // 기본적으로 코인 사용
          },
          {
            onSuccess: result => {
              setGachaResult(result);
            },
            onError: error => {
              toast.error(`가챠 뽑기 실패: ${error.message}`, {
                className: 'game font-galmuri',
              });
              navigate(PAGE_PATH.GACHA);
            },
          },
        );
      }, 3000);

      return () => clearTimeout(timer);
    }
  }, [gachaInfo, drawGacha, navigate]);

  const handleCloseResult = () => {
    setGachaResult(null);
    navigate(PAGE_PATH.GACHA);
  };

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

      {/* 가챠 결과 모달 */}
      {gachaResult && (
        <GachaResult
          item={gachaResult.item}
          currencies={gachaResult.currencies}
          onClose={handleCloseResult}
        />
      )}
    </div>
  );
};
export default GachaRollingPage;
