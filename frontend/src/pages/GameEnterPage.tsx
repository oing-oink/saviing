import { useEffect, useRef, useState } from 'react';
import GameHeader from '@/features/game/shared/components/GameHeader';
import Room from '@/features/game/room/Room';
import CatSprite from '@/features/game/pet/components/CatSprite';
import { useEnterTransitionStore } from '@/features/game/shared/store/useEnterTransitionStore';
import { useNavigate } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';
import GameBackgroundLayout from '@/features/game/shared/layouts/GameBackgroundLayout';
import { useGameEntryQuery } from '@/features/game/entry/query/useGameEntryQuery';
import { Loader2 } from 'lucide-react';

const GameEnterPage = () => {
  const navigate = useNavigate();
  const origin = useEnterTransitionStore(state => state.origin);
  const startTransitionToGame = useEnterTransitionStore(
    state => state.startTransitionToGame,
  );
  const reset = useEnterTransitionStore(state => state.reset);
  const anim = 'sleep';
  const [atCenter, setAtCenter] = useState(false);
  const [roomRise, setRoomRise] = useState(false);
  const hasNavigatedRef = useRef(false);

  const {
    data: gameEntry,
    isLoading: isGameEntryLoading,
    error: gameEntryError,
  } = useGameEntryQuery();

  const currentPetItemId = gameEntry?.pet?.itemId;
  const hasPet = typeof currentPetItemId === 'number';

  useEffect(() => {
    // 시작 위치에서 중앙으로 이동
    const enter = requestAnimationFrame(() => setAtCenter(true));
    // 룸이 바닥에서 올라오도록 트리거
    const rise = setTimeout(() => setRoomRise(true), 150);
    // 전환 완료 후 GamePage 이동
    const toGame = setTimeout(() => {
      hasNavigatedRef.current = true;
      startTransitionToGame();
      navigate(PAGE_PATH.GAME);
    }, 1600);
    return () => {
      clearTimeout(toGame);
      clearTimeout(rise);
      cancelAnimationFrame(enter);
      if (!hasNavigatedRef.current) {
        reset();
      }
    };
  }, [navigate, reset, startTransitionToGame]);

  // 시작 위치 (Home의 CatSprite 위치) -> 화면 중앙 목표로 낙하
  const startX = origin?.x ?? 0;
  const startY = origin?.y ?? -80;
  const scale = origin?.scale ?? 4;

  if (isGameEntryLoading) {
    return (
      <GameBackgroundLayout className="game relative touch-none overflow-hidden font-galmuri">
        <div className="flex h-full items-center justify-center gap-2">
          <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
          <span className="text-sm text-muted-foreground">불러오는 중...</span>
        </div>
      </GameBackgroundLayout>
    );
  }

  if (gameEntryError) {
    return (
      <GameBackgroundLayout className="game relative touch-none overflow-hidden font-galmuri">
        <div className="flex h-full items-center justify-center">
          <span className="text-sm text-red-500">
            펫 정보를 불러올 수 없습니다
            {gameEntryError ? `: ${gameEntryError.message}` : ''}
          </span>
        </div>
      </GameBackgroundLayout>
    );
  }

  return (
    <GameBackgroundLayout className="game relative touch-none overflow-hidden font-galmuri">
      <div className="relative flex h-full flex-col">
        {/* GameHeader 자리 확보 (투명) */}
        <div className="pointer-events-none relative z-10 opacity-0">
          <GameHeader />
        </div>

        <div className="relative flex w-full flex-1 justify-center">
          <div className="relative inline-block w-full">
            {/* 바닥에서 올라오는 룸 (GamePage의 위치와 동일) */}
            <div
              className={`pointer-events-none absolute left-1/2 z-0 w-full -translate-x-1/2 -translate-y-1/2 transition-all duration-700 ease-out ${
                roomRise ? 'top-1/2' : 'top-[120%]'
              }`}
            >
              <Room mode="readonly" placementArea={null} />
            </div>

            {/* 시작 위치에서 중앙으로 이동하는 스프라이트 (동일 기준 좌표) */}
            <div className="pointer-events-none absolute top-1/2 left-1/2 z-10 -translate-x-1/2 -translate-y-1/2">
              <div
                className={`transition-transform duration-[1200ms] ease-out`}
                style={{
                  transform: atCenter
                    ? 'translate(0px, 0px)'
                    : `translate(${startX}px, ${startY}px)`,
                }}
              >
                <div style={{ transform: `scale(${scale})` }}>
                  {hasPet && (
                    <CatSprite
                      itemId={currentPetItemId}
                      currentAnimation={anim}
                    />
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </GameBackgroundLayout>
  );
};

export default GameEnterPage;
