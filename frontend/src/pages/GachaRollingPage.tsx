import { useState, useEffect } from 'react';
import backButton from '@/assets/game_button/backButton.png';
import { PAGE_PATH } from '@/shared/constants/path';
import { useNavigate, useSearchParams } from 'react-router-dom';
import catPaw from '@/assets/catPaw.png';
import gachaMachine from '@/assets/gachaMachine.png';
import { useGachaDraw } from '@/features/game/shop/query/useGachaDraw';
import { useGachaInfo } from '@/features/game/shop/query/useGachaInfo';
import { useFireworks } from '@/features/game/shop/hooks/useFireworks';
import GachaResult from '@/features/game/shop/components/GachaResult';
import type { GachaDrawResponse } from '@/features/game/shop/types/item';
import toast from 'react-hot-toast';
import { useQueryClient } from '@tanstack/react-query';
import InsufficientFundsModal from '@/features/game/shop/components/InsufficientFundsModal';
import { gameKeys } from '@/features/game/shared/query/gameKeys';
import { useGameEntryQuery } from '@/features/game/entry/query/useGameEntryQuery';
import { useGameQuery } from '@/features/game/shared/query/useGameQuery';

const GachaRollingPage = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const queryClient = useQueryClient();
  const { data: gachaInfo } = useGachaInfo();
  const { mutate: drawGacha } = useGachaDraw();
  const [gachaResult, setGachaResult] = useState<GachaDrawResponse | null>(
    null,
  );
  const { data: gameEntry } = useGameEntryQuery();
  const characterId = gameEntry?.characterId;
  const { data: gameData } = useGameQuery(characterId);
  const [showInsufficientFundsModal, setShowInsufficientFundsModal] =
    useState(false);

  useFireworks(Boolean(gachaResult));

  // timestamp가 변경될 때마다 새로운 가챠 실행
  const timestamp = searchParams.get('t');

  useEffect(() => {
    // 상태 초기화
    setGachaResult(null);

    // 잔액 부족 체크 (즉시 실행)
    if (gachaInfo && gameData && typeof characterId === 'number') {
      const gachaPrice = gachaInfo.gachaInfo.drawPrice.coin;
      const currentCoin = gameData.coin;

      if (currentCoin < gachaPrice) {
        setShowInsufficientFundsModal(true);
        return;
      }
    }

    // 페이지 진입과 동시에 가챠 API 호출 (잔액이 충분한 경우에만)
    if (gachaInfo && gameData && typeof characterId === 'number') {
      const timer = setTimeout(() => {
        drawGacha(
          {
            characterId,
            gachaPoolId: gachaInfo.gachaPoolId,
            paymentMethod: 'COIN', // 기본적으로 코인 사용
          },
          {
            onSuccess: result => {
              setGachaResult(result);

              // 가챠 성공 후 캐릭터 게임 데이터 캐시 업데이트
              queryClient.setQueryData(
                gameKeys.characterData(characterId),
                (oldData: any) => {
                  if (oldData) {
                    return {
                      ...oldData,
                      coin: result.currencies.coin,
                      fishCoin: result.currencies.fishCoin,
                    };
                  }
                  return oldData;
                },
              );

              // 다른 게임 관련 쿼리들도 무효화 (안전장치)
              queryClient.invalidateQueries({
                queryKey: gameKeys.characterData(characterId),
              });
            },
            onError: error => {
              // 잔액 부족 에러인지 확인 (에러 코드 또는 메시지로 판단)
              const isInsufficientFunds =
                (error as any)?.code === 'PURCHASE_INSUFFICIENT_FUNDS' ||
                (error.message && error.message.includes('잔액이 부족합니다'));

              if (isInsufficientFunds) {
                setShowInsufficientFundsModal(true);
              } else {
                toast.error(`가챠 뽑기 실패: ${error.message}`, {
                  className: 'game font-galmuri',
                });
                navigate(PAGE_PATH.GACHA);
              }
            },
          },
        );
      }, 3000);

      return () => clearTimeout(timer);
    }
  }, [
    characterId,
    drawGacha,
    gachaInfo,
    gameData,
    navigate,
    queryClient,
    timestamp,
  ]);

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

      <InsufficientFundsModal
        isOpen={showInsufficientFundsModal}
        onClose={() => {
          setShowInsufficientFundsModal(false);
          navigate(PAGE_PATH.GACHA);
        }}
        message="잔액이 부족합니다."
      />
    </div>
  );
};
export default GachaRollingPage;
