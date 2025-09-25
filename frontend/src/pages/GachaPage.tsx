import backButton from '@/assets/game_button/backButton.png';
import { PAGE_PATH } from '@/shared/constants/path';
import { useNavigate, useSearchParams } from 'react-router-dom';
import catPaw from '@/assets/catPaw.png';
import gachaMachine from '@/assets/gachaMachine.png';
import coin from '@/assets/coin.png';
import infoButton from '@/assets/game_button/infoButton.png';
import { useGachaInfo } from '@/features/game/shop/query/useGachaInfo';
import { useGameQuery } from '@/features/game/shared/query/useGameQuery';
import { useGameEntryQuery } from '@/features/game/entry/query/useGameEntryQuery';
import { useState, useEffect } from 'react';
import InsufficientFundsModal from '@/features/game/shop/components/InsufficientFundsModal';

const GachaPage = () => {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const { data: gachaInfo } = useGachaInfo();
  const { data: gameEntry } = useGameEntryQuery();
  const { data: gameData } = useGameQuery(gameEntry?.characterId);
  const [showInsufficientFundsModal, setShowInsufficientFundsModal] =
    useState(false);

  // URL 파라미터에서 showInsufficientFunds 확인
  useEffect(() => {
    if (searchParams.get('showInsufficientFunds') === 'true') {
      setShowInsufficientFundsModal(true);
      // URL 파라미터 제거
      searchParams.delete('showInsufficientFunds');
      setSearchParams(searchParams, { replace: true });
    }
  }, [searchParams, setSearchParams]);

  const handleGachaAttempt = () => {
    if (!gachaInfo || !gameData) {
      return;
    }

    const gachaPrice = gachaInfo.gachaInfo.drawPrice.coin;
    const currentCoin = gameData.coin;

    if (currentCoin < gachaPrice) {
      setShowInsufficientFundsModal(true);
      return;
    }

    navigate(PAGE_PATH.GACHA_ROLLING + `?t=${Date.now()}`);
  };

  return (
    <div className="game flex min-h-screen flex-col bg-store-bg font-galmuri">
      {/* 상단 영역 */}
      <div className="flex h-20 items-center justify-between px-3">
        <button onClick={() => navigate(PAGE_PATH.SHOP)}>
          <img
            className="w-9 pt-5 focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
            src={backButton}
            alt=""
          />
        </button>
        <div className="pt-5 text-3xl text-red-400">GACHA</div>
        <button onClick={() => navigate(PAGE_PATH.GACHA_INFO)}>
          <img
            className="w-9 pt-5 focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
            src={infoButton}
            alt=""
          />
        </button>
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
        onClick={handleGachaAttempt}
      >
        <div className="relative w-[70%]">
          <img src={coin} alt="" className="w-full" />
          <div className="absolute inset-0 ml-8 flex items-center justify-center text-lg font-bold text-black">
            {gachaInfo?.gachaInfo.drawPrice.coin ?? 500}
          </div>
        </div>
      </button>
      <div className="mt-auto flex justify-center pb-6">
        <img src={catPaw} alt="" className="w-[85%]" />
      </div>

      <InsufficientFundsModal
        isOpen={showInsufficientFundsModal}
        onClose={() => setShowInsufficientFundsModal(false)}
        message="잔액이 부족합니다."
      />
    </div>
  );
};

export default GachaPage;
