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

import { useDecoStore } from '@/features/game/deco/store/useDecoStore';
import { useRoomSnapshotQuery } from '@/features/game/room/query/useRoomSnapshotQuery';

const ROOM_RISE_DURATION = 700;

const GameEnterPage = () => {
  const navigate = useNavigate();
  const origin = useEnterTransitionStore(state => state.origin);
  const startTransitionToGame = useEnterTransitionStore(
    state => state.startTransitionToGame,
  );
  const reset = useEnterTransitionStore(state => state.reset);
  const loadRoomSnapshot = useDecoStore(state => state.loadRoomSnapshot);
  const setHydrationError = useDecoStore(state => state.setHydrationError);

  const anim = 'sleep';
  const [atCenter, setAtCenter] = useState(false);
  const [roomRise, setRoomRise] = useState(false);
  const [roomDataLoaded, setRoomDataLoaded] = useState(false);
  const [roomDataError, setRoomDataError] = useState<Error | null>(null);
  const hasNavigatedRef = useRef(false);
  const hasSnapshotAppliedRef = useRef(false);

  const {
    data: gameEntry,
    isLoading: isGameEntryLoading,
    error: gameEntryError,
  } = useGameEntryQuery();

  const currentPetItemId = gameEntry?.pet?.itemId;
  const hasPet = typeof currentPetItemId === 'number';

  useEffect(() => {
    const enter = requestAnimationFrame(() => setAtCenter(true));

    return () => {
      cancelAnimationFrame(enter);
      if (!hasNavigatedRef.current) {
        reset();
      }
    };
  }, [reset]);

  const roomId = gameEntry?.roomId;
  const characterId = gameEntry?.characterId;
  const canRequestSnapshot =
    typeof roomId === 'number' && typeof characterId === 'number';

  const snapshotQuery = useRoomSnapshotQuery(
    canRequestSnapshot ? roomId : undefined,
    canRequestSnapshot ? characterId : undefined,
    {
      enabled: canRequestSnapshot && !hasSnapshotAppliedRef.current,
    },
  );

  useEffect(() => {
    if (!gameEntry || hasSnapshotAppliedRef.current) {
      return;
    }
    if (!canRequestSnapshot) {
      const error = new Error('방 또는 캐릭터 정보를 불러올 수 없습니다.');
      setRoomDataError(error);
      setHydrationError(error);
    }
  }, [canRequestSnapshot, gameEntry, setHydrationError]);

  useEffect(() => {
    if (snapshotQuery.isFetching) {
      setRoomDataLoaded(false);
      setRoomDataError(null);
      setHydrationError(null);
      setRoomRise(false);
    }
  }, [setHydrationError, snapshotQuery.isFetching]);

  useEffect(() => {
    if (!snapshotQuery.data || hasSnapshotAppliedRef.current) {
      return;
    }
    loadRoomSnapshot(snapshotQuery.data);
    hasSnapshotAppliedRef.current = true;
    setRoomDataLoaded(true);
    setRoomRise(true);
  }, [loadRoomSnapshot, snapshotQuery.data]);

  useEffect(() => {
    if (!snapshotQuery.error) {
      return;
    }
    const normalizedError = snapshotQuery.error;
    setRoomDataError(normalizedError);
    setHydrationError(normalizedError);
  }, [snapshotQuery.error, setHydrationError]);

  const handleRetry = () => {
    hasSnapshotAppliedRef.current = false;
    setRoomDataError(null);
    setHydrationError(null);
    snapshotQuery.refetch();
  };

  useEffect(() => {
    if (!roomDataLoaded || roomDataError || hasNavigatedRef.current) {
      return;
    }

    setRoomRise(true);
    const timer = window.setTimeout(() => {
      if (hasNavigatedRef.current) {
        return;
      }
      hasNavigatedRef.current = true;
      startTransitionToGame();
      navigate(PAGE_PATH.GAME);
    }, ROOM_RISE_DURATION);

    return () => {
      clearTimeout(timer);
    };
  }, [navigate, roomDataError, roomDataLoaded, startTransitionToGame]);

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

  if (roomDataError) {
    return (
      <GameBackgroundLayout className="game relative touch-none overflow-hidden font-galmuri">
        <div className="flex h-full flex-col items-center justify-center gap-3">
          <span className="text-sm text-red-500">
            방 구성을 불러올 수 없습니다: {roomDataError.message}
          </span>
          <button
            type="button"
            className="rounded-full bg-primary px-4 py-2 text-sm font-medium text-white shadow"
            onClick={handleRetry}
          >
            다시 시도
          </button>
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
          {!roomDataLoaded && !roomDataError && (
            <div className="pointer-events-none absolute inset-0 z-20 flex items-end justify-center pb-12">
              <div className="flex items-center gap-2 rounded-full bg-black/40 px-3 py-1 text-xs text-white backdrop-blur">
                <Loader2 className="h-4 w-4 animate-spin" />
                <span>방을 준비하는 중...</span>
              </div>
            </div>
          )}

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
