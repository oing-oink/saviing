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

  // 디버깅: gachaResult 상태 변화 감지
  console.log('GachaRollingPage 렌더링:', {
    gachaResult: Boolean(gachaResult),
    hasItem: gachaResult?.item,
    hasCurrencies: gachaResult?.currencies,
  });
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

    // 페이지 진입과 동시에 가챠 API 호출 (데이터가 준비되면)
    if (gachaInfo && gameData && typeof characterId === 'number') {
      const gachaPrice = gachaInfo.gachaInfo.drawPrice.coin;
      const currentCoin = gameData.coin;

      // 잔액 부족 체크 (즉시 실행)
      if (currentCoin < gachaPrice) {
        setShowInsufficientFundsModal(true);
        return;
      }

      // 3초 후에 가챠 실행 (UI 효과를 위해)
      const timer = setTimeout(() => {
        drawGacha(
          {
            characterId,
            gachaPoolId: gachaInfo.gachaPoolId,
            paymentMethod: 'COIN', // 기본적으로 코인 사용
          },
          {
            onSuccess: result => {
              console.log('가챠 성공:', result); // 디버깅용
              console.log('setGachaResult 호출 전 - result 구조:', JSON.stringify(result, null, 2));
              setGachaResult(result);
              console.log('setGachaResult 호출 완료');

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
              console.error('가챠 실패:', error); // 디버깅용
              // ApiError 객체에서 실제 에러 정보 추출
              const apiErrorResponse = (error as any)?.response;
              const errorCode = apiErrorResponse?.code;
              const errorMessage = apiErrorResponse?.message || error.message;

              // 잔액 부족 에러인지 확인
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

  const handleRetry = () => {
    if (!gachaInfo || !gameEntry?.characterId) {
      console.error('가챠 재실행 실패: 필수 데이터 누락', {
        gachaInfo,
        characterId: gameEntry?.characterId,
      });
      return;
    }

    console.log('가챠 재실행 시작');

    // 가챠 재실행
    drawGacha(
      {
        characterId: gameEntry.characterId,
        gachaPoolId: gachaInfo.gachaPoolId,
        paymentMethod: 'COIN',
      },
      {
        onSuccess: result => {
          console.log('가챠 재실행 성공:', result);
          setGachaResult(result);

          // 가챠 성공 후 캐릭터 게임 데이터 캐시 업데이트
          queryClient.setQueryData(
            gameKeys.characterData(gameEntry.characterId),
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
            queryKey: gameKeys.characterData(gameEntry.characterId),
          });
        },
        onError: error => {
          console.error('가챠 재실행 실패:', error);
          // ApiError 객체에서 실제 에러 정보 추출
          const apiErrorResponse = (error as any)?.response;
          const errorCode = apiErrorResponse?.code;
          const errorMessage = apiErrorResponse?.message || error.message;

          // 잔액 부족 에러인지 확인
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
        },
      },
    );
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
        <>
          {console.log('GachaResult 모달 렌더링:', gachaResult)}
          <GachaResult
            item={gachaResult.item}
            currencies={gachaResult.currencies}
            onClose={handleCloseResult}
            onRetry={handleRetry}
          />
        </>
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
