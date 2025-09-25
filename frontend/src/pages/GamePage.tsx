import GameHeader from '@/features/game/shared/components/GameHeader';
import ElevatorButton from '@/features/game/shared/components/ElevatorButton';
import PetStatusCard from '@/features/game/pet/components/PetStatusCard';
import Room from '@/features/game/room/Room';
import { useCallback, useEffect, useRef, useState } from 'react';
import { usePetStore } from '@/features/game/pet/store/usePetStore';
import { useEnterTransitionStore } from '@/features/game/shared/store/useEnterTransitionStore';
import GameBackgroundLayout from '@/features/game/shared/layouts/GameBackgroundLayout';
import { Loader2 } from 'lucide-react';
import { useGameEntryQuery } from '@/features/game/entry/query/useGameEntryQuery';
import { useDecoStore } from '@/features/game/deco/store/useDecoStore';
import { getRoomPlacements } from '@/features/game/room/api/roomApi';
import { getInventoryItems } from '@/features/game/shop/api/itemsApi';
import type { PlacedItem } from '@/features/game/deco/types/decoTypes';
import { RoomCanvas } from '@/features/game/deco/components/roomCanvas';
import { cn } from '@/lib/utils';

const GamePage = () => {
  const {
    data: gameEntry,
    isLoading: isGameEntryLoading,
    error: gameEntryError,
  } = useGameEntryQuery();

  const [currentPetId, setCurrentPetId] = useState(gameEntry?.pet?.petId);
  const currentPetItemId = gameEntry?.pet?.itemId;
  const hasPet =
    typeof currentPetId === 'number' && typeof currentPetItemId === 'number';
  const behavior = usePetStore(state => state.behavior);
  const setBehavior = usePetStore(state => state.setBehavior);
  const isTransitioningToGame = useEnterTransitionStore(
    state => state.isTransitioningToGame,
  );
  const finishTransitionToGame = useEnterTransitionStore(
    state => state.finishTransitionToGame,
  );

  const applyServerState = useDecoStore(state => state.applyServerState);

  // 컴포넌트 초기화 시 방 배치 정보 로드 (DecoPage와 동일한 로직)
  useEffect(() => {
    if (!gameEntry) {
      return;
    }

    const { roomId, characterId } = gameEntry;
    if (typeof roomId !== 'number' || typeof characterId !== 'number') {
      console.warn('GamePage - roomId 또는 characterId가 유효하지 않습니다.', {
        roomId,
        characterId,
      });
      return;
    }

    const loadRoomPlacements = async () => {
      try {
        // 배치 정보와 인벤토리 정보를 병렬로 조회
        const [placementsResponse, ...inventoryResponses] = await Promise.all([
          getRoomPlacements(roomId),
          // 모든 카테고리의 인벤토리 조회
          getInventoryItems(characterId, 'DECORATION', 'LEFT'),
          getInventoryItems(characterId, 'DECORATION', 'RIGHT'),
          getInventoryItems(characterId, 'DECORATION', 'BOTTOM'),
          getInventoryItems(characterId, 'DECORATION', 'ROOM_COLOR'),
          getInventoryItems(characterId, 'PET', 'CAT'),
        ]);

        // 모든 인벤토리 아이템을 하나의 배열로 합치기
        const allInventoryItems = inventoryResponses.flatMap(
          response => response.items,
        );

        // inventoryItemId를 키로 하는 Map 생성 (업데이트된 아이템들로)
        const inventoryMap = new Map(
          allInventoryItems.map(item => [item.inventoryItemId!, item]),
        );

        // API 응답을 PlacedItem 형식으로 변환
        const placedItems: PlacedItem[] = placementsResponse.placements.map(
          placement => {
            // 인벤토리에서 해당 아이템 찾기
            const inventoryItem = inventoryMap.get(placement.inventoryItemId);

            return {
              id: `placement-${placement.placementId || placement.inventoryItemId}`,
              inventoryItemId: placement.inventoryItemId,
              itemId: placement.itemId,
              cellId: `${placement.category}-${placement.positionX + 1}-${placement.positionY + 1}`,
              positionX: placement.positionX,
              positionY: placement.positionY,
              rotation: 0,
              layer: placement.category,
              xLength: inventoryItem?.xLength || 1, // 인벤토리에서 실제 크기 사용
              yLength: inventoryItem?.yLength || 1, // 인벤토리에서 실제 크기 사용
              footprintCellIds: undefined,
              offsetX: 0,
              offsetY: 0,
              imageUrl: undefined, // getItemImage(itemId) 사용하도록 undefined 설정
              itemType: inventoryItem?.itemType || 'DECORATION',
              isPreview: false,
            };
          },
        );

        console.log('GamePage - 로드된 배치 아이템:', placedItems);

        // 데코 스토어에 서버 상태 적용
        applyServerState({ placedItems });
      } catch (error) {
        console.error('GamePage - 방 배치 정보 로드 실패:', error);
      }
    };

    loadRoomPlacements();
  }, [applyServerState, gameEntry]);

  useEffect(() => {
    setBehavior({ currentAnimation: 'idle' });
    if (isTransitioningToGame) {
      finishTransitionToGame();
    }
  }, [finishTransitionToGame, isTransitioningToGame, setBehavior]);

  // Popover 상태 관리
  const [isPopoverOpen, setIsPopoverOpen] = useState(false);
  const roomContainerRef = useRef<HTMLDivElement | null>(null);
  const sheetRef = useRef<HTMLDivElement | null>(null);
  const [roomOffset, setRoomOffset] = useState(0);
  const [sheetWidth, setSheetWidth] = useState<number | null>(null);

  useEffect(() => {
    if (!isPopoverOpen) {
      setRoomOffset(0);
      setSheetWidth(null);
      return undefined;
    }
    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape') {
        setIsPopoverOpen(false);
      }
    };
    const handlePointerDown = (event: PointerEvent) => {
      const sheetEl = sheetRef.current;
      if (!sheetEl) {
        return;
      }
      if (!sheetEl.contains(event.target as Node)) {
        setIsPopoverOpen(false);
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    window.addEventListener('pointerdown', handlePointerDown, true);
    return () => {
      window.removeEventListener('keydown', handleKeyDown);
      window.removeEventListener('pointerdown', handlePointerDown, true);
    };
  }, [isPopoverOpen]);

  useEffect(() => {
    if (!isPopoverOpen) {
      setRoomOffset(0);
      setSheetWidth(null);
      return undefined;
    }

    const updateOffset = () => {
      const roomEl = roomContainerRef.current;
      const sheetEl = sheetRef.current;
      if (!roomEl || !sheetEl) {
        setRoomOffset(0);
        return;
      }

      const roomRect = roomEl.getBoundingClientRect();
      const sheetRect = sheetEl.getBoundingClientRect();
      const viewportHeight = window.innerHeight;

      const gapBelowRoom = viewportHeight - roomRect.bottom;
      const margin = 24;
      const neededGap = sheetRect.height + margin - gapBelowRoom;
      if (neededGap <= 0) {
        setRoomOffset(0);
        return;
      }

      const maxLift = Math.max(roomRect.top, 0);
      const lift = Math.min(neededGap, maxLift);
      setRoomOffset(lift > 0 ? lift : 0);
    };

    const handleResize = () => {
      requestAnimationFrame(updateOffset);
    };

    let frame = requestAnimationFrame(updateOffset);

    const sheetEl = sheetRef.current;
    const resizeObserver = sheetEl
      ? new ResizeObserver(() => {
          requestAnimationFrame(updateOffset);
        })
      : null;

    if (sheetEl && resizeObserver) {
      resizeObserver.observe(sheetEl);
    }

    window.addEventListener('resize', handleResize);

    return () => {
      cancelAnimationFrame(frame);
      if (resizeObserver) {
        resizeObserver.disconnect();
      }
      window.removeEventListener('resize', handleResize);
    };
  }, [isPopoverOpen]);

  useEffect(() => {
    if (!isPopoverOpen) {
      return undefined;
    }

    const measure = () => {
      const container = roomContainerRef.current;
      if (container) {
        setSheetWidth(container.getBoundingClientRect().width);
      } else {
        setSheetWidth(null);
      }
    };

    measure();
    window.addEventListener('resize', measure);

    return () => {
      window.removeEventListener('resize', measure);
    };
  }, [isPopoverOpen]);

  // 애니메이션 완료 시 idle로 복귀
  const handleAnimationComplete = (animation: string) => {
    if (animation === 'sitting' || animation === 'jump') {
      setBehavior({
        currentAnimation: 'idle',
      });
    }
  };

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
  
  const handlePlacedItemClick = useCallback(
    ({ item }: { item: PlacedItem; clientX: number; clientY: number }) => {
      if (item.itemType !== 'PET') {
        return;
      }
      setCurrentPetId(item.itemId);
      setIsPopoverOpen(true);
    },
    [setIsPopoverOpen, setCurrentPetId],
  );

  return (
    <GameBackgroundLayout className="game relative touch-none overflow-hidden font-galmuri">
      <div className="relative flex h-full flex-col">
        <div className="relative z-10">
          <GameHeader />
        </div>

        <div
          ref={roomContainerRef}
          className="relative flex w-full flex-1 items-center justify-center transition-transform duration-300"
        >
          <div
            className="relative flex w-full items-center justify-center transition-transform duration-300"
            style={roomOffset ? { transform: `translateY(-${roomOffset}px)` } : undefined}
          >
            <div className="relative flex w-full justify-center">
              <Room mode="readonly" placementArea={null}>
                {context => (
                  <RoomCanvas
                    context={context}
                    allowItemPickup={false}
                    showActions={false}
                    allowDelete={false}
                    onPlacedItemClick={handlePlacedItemClick}
                    catAnimationState={behavior.currentAnimation}
                    onCatAnimationComplete={handleAnimationComplete}
                  />
                )}
              </Room>
            </div>
          </div>
        </div>

        <div className="absolute right-0 bottom-0 z-10 pr-3 pb-5">
          <ElevatorButton />
        </div>
      </div>

      <div
        className="pointer-events-none fixed bottom-[env(safe-area-inset-bottom,0)] z-40 px-4 pb-6"
        style={{
          left: '50%',
          transform: 'translateX(-50%)',
          width:
            sheetWidth !== null
              ? `${sheetWidth}px`
              : undefined,
        }}
      >
        <div
          className={cn(
            'mx-auto w-full max-w-[min(calc(100vw - env(safe-area-inset-left,0) - env(safe-area-inset-right,0)),80rem)] transition-all duration-300 ease-out',
            isPopoverOpen
              ? 'pointer-events-auto translate-y-0 opacity-100'
              : 'pointer-events-none translate-y-6 opacity-0'
          )}
        >
          <div
            ref={sheetRef}
            className="overflow-hidden rounded-2xl border border-black/10 bg-white/95 shadow-lg backdrop-blur-sm"
          >
            <PetStatusCard petId={currentPetId} />
          </div>
        </div>
      </div>
    </GameBackgroundLayout>
  );
}

export default GamePage;
