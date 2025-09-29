import { useState, useEffect, useCallback, useRef } from 'react';
import { useLocation, useNavigate, useSearchParams } from 'react-router-dom';
import backButton from '@/assets/game_button/backButton.png';
import catPaw from '@/assets/catPaw.png';
import gachaMachine from '@/assets/gachaMachine.png';
import GachaResult from '@/features/game/shop/components/GachaResult';
import InsufficientFundsModal from '@/features/game/shop/components/InsufficientFundsModal';
import { useFireworks } from '@/features/game/shop/hooks/useFireworks';
import { useGachaDraw } from '@/features/game/shop/query/useGachaDraw';
import { useGachaInfo } from '@/features/game/shop/query/useGachaInfo';
import type { GachaDrawResponse } from '@/features/game/shop/types/item';
import { useGameEntryQuery } from '@/features/game/entry/query/useGameEntryQuery';
import { useGameQuery } from '@/features/game/shared/query/useGameQuery';
import { gameKeys } from '@/features/game/shared/query/gameKeys';
import type { CharacterGameData } from '@/features/game/shared/types/gameTypes';
import { PAGE_PATH } from '@/shared/constants/path';
import toast from 'react-hot-toast';
import { useQueryClient } from '@tanstack/react-query';

const DRAW_DELAY_MS = 3000;
type RollStatus = 'idle' | 'waiting' | 'rolling' | 'result';

interface ApiErrorResponse {
  code?: string;
  message?: string;
}

const GachaRollingPage = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const location = useLocation();
  const queryClient = useQueryClient();

  const { data: gachaInfo } = useGachaInfo();
  const { mutateAsync: drawGachaAsync } = useGachaDraw();
  const { data: gameEntry } = useGameEntryQuery();
  const characterId = gameEntry?.characterId;
  const { data: gameData } = useGameQuery(characterId);

  const [gachaResult, setGachaResult] = useState<GachaDrawResponse | null>(
    null,
  );
  const [showInsufficientFundsModal, setShowInsufficientFundsModal] =
    useState(false);
  const [rollStatus, setRollStatus] = useState<RollStatus>('idle');

  const drawTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const previousSessionIdRef = useRef<string | null>(null);

  useFireworks(rollStatus === 'result');

  const sessionId = searchParams.get('t') ?? location.key;

  const updateCharacterCurrency = useCallback(
    (characterIdToUpdate: number, result: GachaDrawResponse) => {
      const { coinType, balance, coin, fishCoin } = result.currencies;

      queryClient.setQueryData<CharacterGameData>(
        gameKeys.characterData(characterIdToUpdate),
        oldData => {
          if (!oldData) {
            return oldData;
          }

          const nextCoinBalance =
            coinType === 'COIN' ? balance : (coin ?? oldData.coin);
          const nextFishCoinBalance =
            coinType === 'FISH_COIN' ? balance : (fishCoin ?? oldData.fishCoin);

          return {
            ...oldData,
            coin: nextCoinBalance,
            fishCoin: nextFishCoinBalance,
          };
        },
      );

      queryClient.invalidateQueries({
        queryKey: gameKeys.characterData(characterIdToUpdate),
      });
    },
    [queryClient],
  );

  const executeDraw = useCallback(async () => {
    if (!gachaInfo || typeof characterId !== 'number') {
      return;
    }

    setRollStatus('rolling');

    try {
      const result = await drawGachaAsync({
        characterId,
        gachaPoolId: gachaInfo.gachaPoolId,
        paymentMethod: 'COIN',
      });

      setGachaResult(result);
      setRollStatus('result');
      updateCharacterCurrency(characterId, result);
    } catch (error) {
      const apiErrorResponse = (error as { response?: ApiErrorResponse } | null)
        ?.response;
      const fallbackMessage =
        error instanceof Error
          ? error.message
          : '알 수 없는 오류가 발생했습니다.';
      const errorMessage = apiErrorResponse?.message ?? fallbackMessage;
      const errorCode = apiErrorResponse?.code;

      const isInsufficientFunds =
        errorCode === 'PURCHASE_INSUFFICIENT_FUNDS' ||
        (errorMessage && errorMessage.includes('잔액이 부족합니다'));

      if (isInsufficientFunds) {
        setShowInsufficientFundsModal(true);
      } else {
        toast.error(`가챠 뽑기 실패: ${errorMessage}`, {
          className: 'game font-galmuri',
        });
        navigate(PAGE_PATH.GACHA);
      }

      setRollStatus('idle');
    }
  }, [
    characterId,
    drawGachaAsync,
    gachaInfo,
    navigate,
    updateCharacterCurrency,
  ]);

  const startRoll = useCallback(
    (availableCoin: number) => {
      if (!gachaInfo || typeof characterId !== 'number') {
        return;
      }

      if (rollStatus === 'waiting' || rollStatus === 'rolling') {
        return;
      }

      const coinPrice = gachaInfo.gachaInfo.drawPrice.coin;

      if (availableCoin < coinPrice) {
        setShowInsufficientFundsModal(true);
        setRollStatus('idle');
        return;
      }

      setShowInsufficientFundsModal(false);
      setGachaResult(null);
      setRollStatus('waiting');
    },
    [characterId, gachaInfo, rollStatus],
  );

  useEffect(() => {
    if (previousSessionIdRef.current === sessionId) {
      return;
    }

    previousSessionIdRef.current = sessionId;
    setGachaResult(null);
    setRollStatus('idle');
  }, [sessionId]);

  useEffect(() => {
    if (!gachaInfo || !gameData || typeof characterId !== 'number') {
      return;
    }

    if (rollStatus !== 'idle') {
      return;
    }

    startRoll(gameData.coin);
  }, [characterId, gameData, gachaInfo, rollStatus, startRoll]);

  useEffect(() => {
    if (rollStatus !== 'waiting') {
      return undefined;
    }

    drawTimerRef.current = setTimeout(() => {
      drawTimerRef.current = null;
      executeDraw();
    }, DRAW_DELAY_MS);

    return () => {
      if (drawTimerRef.current) {
        clearTimeout(drawTimerRef.current);
        drawTimerRef.current = null;
      }
    };
  }, [executeDraw, rollStatus]);

  const handleCloseResult = () => {
    setGachaResult(null);
    setRollStatus('idle');
    navigate(PAGE_PATH.GACHA);
  };

  const handleRetry = () => {
    if (!gachaInfo || typeof characterId !== 'number' || !gachaResult) {
      return;
    }

    const paymentCoinType = gachaResult.currencies.coinType;
    const remainingCoin =
      paymentCoinType === 'COIN'
        ? gachaResult.currencies.balance
        : gachaResult.currencies.coin;

    startRoll(remainingCoin);
  };

  return (
    <div className="game flex min-h-screen flex-col bg-store-bg font-galmuri">
      <div className="flex h-20 items-center justify-between px-3">
        <button onClick={() => navigate(PAGE_PATH.SHOP)}>
          <img className="w-9 pt-5" src={backButton} alt="" />
        </button>
        <div className="pt-5 text-3xl text-red-400">GACHA</div>
        <div className="w-9" />
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

      {gachaResult && (
        <GachaResult
          item={gachaResult.item}
          currencies={gachaResult.currencies}
          drawPrice={gachaInfo?.gachaInfo.drawPrice.coin ?? 0}
          onClose={handleCloseResult}
          onRetry={handleRetry}
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
