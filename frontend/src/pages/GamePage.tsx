import GameHeader from '@/features/game/shared/components/GameHeader';
import ElevatorButton from '@/features/game/shared/components/ElevatorButton';
import PetStatusCard from '@/features/game/pet/components/PetStatusCard';
import PetErrorModal from '@/features/game/pet/components/PetErrorModal';
import Room from '@/features/game/room/Room';
import { useCallback, useEffect, useRef, useState } from 'react';
import { usePetStore } from '@/features/game/pet/store/usePetStore';
import type { PetConsumableCategory } from '@/features/game/pet/types/petTypes';
import { useEnterTransitionStore } from '@/features/game/shared/store/useEnterTransitionStore';
import GameBackgroundLayout from '@/features/game/shared/layouts/GameBackgroundLayout';
import { Loader2 } from 'lucide-react';
import { useDecoStore } from '@/features/game/deco/store/useDecoStore';
import type { PlacedItem } from '@/features/game/deco/types/decoTypes';
import type { Item } from '@/features/game/shop/types/item';
import { RoomCanvas } from '@/features/game/deco/components/roomCanvas';
import { cn } from '@/lib/utils';
import { useGameEntryQuery } from '@/features/game/entry/query/useGameEntryQuery';
import { useRoomSnapshotQuery } from '@/features/game/room/query/useRoomSnapshotQuery';
import { ROOM_INITIAL_SCALE } from '@/features/game/room/constants';

const GamePage = () => {
  // 룸에서 선택된 펫 ID (상태 패널에 표시할 대상)
  const [currentPetId, setCurrentPetId] = useState<number | null>(null);
  const behavior = usePetStore(state => state.behavior);
  const setBehavior = usePetStore(state => state.setBehavior);
  const setPetInventory = usePetStore(state => state.setInventory);
  const isTransitioningToGame = useEnterTransitionStore(
    state => state.isTransitioningToGame,
  );
  const finishTransitionToGame = useEnterTransitionStore(
    state => state.finishTransitionToGame,
  );
  const placedItems = useDecoStore(state => state.placedItems);
  const isHydrated = useDecoStore(state => state.isHydrated);
  const hydrationError = useDecoStore(state => state.hydrationError);
  const roomContext = useDecoStore(state => state.roomContext);
  const loadRoomSnapshot = useDecoStore(state => state.loadRoomSnapshot);
  const setHydrationError = useDecoStore(state => state.setHydrationError);
  const decoInventoryItems = useDecoStore(state => state.inventoryItems);

  const shouldHydrate = !isHydrated;
  const { data: gameEntry } = useGameEntryQuery({
    enabled: shouldHydrate && !roomContext,
  });

  const snapshotRoomId = roomContext?.roomId ?? gameEntry?.roomId;
  const snapshotCharacterId =
    roomContext?.characterId ?? gameEntry?.characterId;
  const shouldFetchSnapshot =
    shouldHydrate &&
    typeof snapshotRoomId === 'number' &&
    typeof snapshotCharacterId === 'number';

  const snapshotQuery = useRoomSnapshotQuery(
    shouldFetchSnapshot ? (snapshotRoomId as number) : undefined,
    shouldFetchSnapshot ? (snapshotCharacterId as number) : undefined,
    {
      enabled: shouldFetchSnapshot,
    },
  );

  useEffect(() => {
    if (!snapshotQuery.isFetching) {
      return;
    }
    setHydrationError(null);
  }, [setHydrationError, snapshotQuery.isFetching]);

  const syncPetInventory = useCallback(
    (items: Item[] | undefined) => {
      const sourceItems = items ?? [];

      const consumableItems = sourceItems
        .filter(
          item =>
            item.itemType === 'CONSUMPTION' &&
            (item.itemCategory === 'FOOD' || item.itemCategory === 'TOY'),
        )
        .filter(item => typeof item.inventoryItemId === 'number')
        .map(item => {
          const category = item.itemCategory as PetConsumableCategory;
          return {
            inventoryItemId: item.inventoryItemId as number,
            itemId: item.itemId,
            category,
            name: item.itemName,
            description: item.itemDescription,
            imageUrl: item.imageUrl,
            rarity: item.rarity,
            count: item.count ?? 0,
          };
        });

      const feed = consumableItems
        .filter(item => item.category === 'FOOD')
        .reduce((sum, item) => sum + item.count, 0);
      const toy = consumableItems
        .filter(item => item.category === 'TOY')
        .reduce((sum, item) => sum + item.count, 0);

      setPetInventory({
        feed,
        toy,
        items: consumableItems,
      });
    },
    [setPetInventory],
  );

  useEffect(() => {
    const snapshot = snapshotQuery.data;
    if (!snapshot) {
      return;
    }
    loadRoomSnapshot(snapshot);
    syncPetInventory(snapshot.inventoryItems);
  }, [loadRoomSnapshot, snapshotQuery.data, syncPetInventory]);

  useEffect(() => {
    syncPetInventory(decoInventoryItems);
  }, [decoInventoryItems, syncPetInventory]);

  useEffect(() => {
    if (!snapshotQuery.error) {
      return;
    }
    setHydrationError(snapshotQuery.error);
  }, [setHydrationError, snapshotQuery.error]);

  useEffect(() => {
    if (!shouldHydrate || roomContext) {
      return;
    }
    if (
      gameEntry &&
      (typeof snapshotRoomId !== 'number' ||
        typeof snapshotCharacterId !== 'number')
    ) {
      setHydrationError(new Error('방 또는 캐릭터 정보를 불러올 수 없습니다.'));
    }
  }, [
    gameEntry,
    roomContext,
    setHydrationError,
    shouldHydrate,
    snapshotCharacterId,
    snapshotRoomId,
  ]);

  const isInitialLoading = !isHydrated;
  const dataError = hydrationError ?? undefined;

  useEffect(() => {
    if (!isHydrated) {
      setCurrentPetId(null);
      return;
    }

    const availablePetIds = placedItems
      .filter(item => item.itemType === 'PET')
      .map(item => item.inventoryItemId ?? item.itemId);

    if (availablePetIds.length === 0) {
      setCurrentPetId(null);
      return;
    }

    if (currentPetId !== null && availablePetIds.includes(currentPetId)) {
      return;
    }

    setCurrentPetId(availablePetIds[0] ?? null);
  }, [currentPetId, isHydrated, placedItems]);
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

  const initialScaleBoostRef = useRef(false);
  if (isTransitioningToGame && !initialScaleBoostRef.current) {
    initialScaleBoostRef.current = true;
  }
  const initialRoomTransform = initialScaleBoostRef.current
    ? { scale: ROOM_INITIAL_SCALE }
    : undefined;

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

    // 펫 정보 시트가 열렸을 때 룸이 가려지지 않도록 적당히 위로 이동시키는 로직
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

    const frame = requestAnimationFrame(updateOffset);

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

    // 팝오버 폭을 룸 그리드와 동일하게 맞춘다.
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

  // 룸에서 펫 타일을 클릭하면 해당 펫을 정보 카드에 표시한다.
  const handlePlacedItemClick = useCallback(
    ({ item }: { item: PlacedItem; clientX: number; clientY: number }) => {
      if (item.itemType !== 'PET') {
        return;
      }
      const nextPetId =
        item.itemType === 'PET'
          ? (item.inventoryItemId ?? item.itemId)
          : item.itemId;
      setCurrentPetId(nextPetId);
      setIsPopoverOpen(true);
    },
    [setIsPopoverOpen, setCurrentPetId],
  );

  if (isInitialLoading) {
    return (
      <GameBackgroundLayout className="game relative touch-none overflow-hidden font-galmuri">
        <div className="flex h-full items-center justify-center gap-2">
          <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
          <span className="text-sm text-muted-foreground">불러오는 중...</span>
        </div>
      </GameBackgroundLayout>
    );
  }

  if (dataError) {
    return (
      <GameBackgroundLayout className="game relative touch-none overflow-hidden font-galmuri">
        <div className="flex h-full items-center justify-center">
          <span className="text-sm text-red-500">
            게임 데이터를 불러올 수 없습니다
            {dataError ? `: ${dataError.message}` : ''}
          </span>
        </div>
      </GameBackgroundLayout>
    );
  }

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
            style={
              roomOffset
                ? { transform: `translateY(-${roomOffset}px)` }
                : undefined
            }
          >
            <div className="relative flex w-full justify-center">
              {/* 룸 캔버스를 읽기 전용으로 실행하고, 펫 클릭 이벤트와 애니메이션 상태를 연동한다. */}
              <Room
                mode="readonly"
                placementArea={null}
                initialTransform={initialRoomTransform}
              >
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

      {/* 선택된 펫의 상태 정보를 화면 하단 팝오버로 표시 */}
      <div
        className="pointer-events-none fixed bottom-[env(safe-area-inset-bottom,0)] z-40 px-4 pb-6"
        style={{
          left: '50%',
          transform: 'translateX(-50%)',
          width: sheetWidth !== null ? `${sheetWidth}px` : undefined,
        }}
      >
        <div
          className={cn(
            'max-w-[min(calc(100vw - env(safe-area-inset-left,0) - env(safe-area-inset-right,0)),80rem)] mx-auto w-full transition-all duration-300 ease-out',
            isPopoverOpen
              ? 'pointer-events-auto translate-y-0 opacity-100'
              : 'pointer-events-none translate-y-6 opacity-0',
          )}
        >
          <div
            ref={sheetRef}
            className="overflow-hidden rounded-2xl border border-black/10 bg-white/95 shadow-lg backdrop-blur-sm"
          >
            {currentPetId !== null ? (
              <PetStatusCard petId={currentPetId} />
            ) : (
              <div className="flex h-60 items-center justify-center p-4 text-sm text-muted-foreground">
                표시할 펫이 없습니다.
              </div>
            )}
          </div>
        </div>
      </div>
      <PetErrorModal />
    </GameBackgroundLayout>
  );
};

export default GamePage;
