import GameHeader from '@/features/game/shared/components/GameHeader';
import ElevatorButton from '@/features/game/shared/components/ElevatorButton';
import PetStatusCard from '@/features/game/pet/components/PetStatusCard';
import CatSprite from '@/features/game/pet/components/CatSprite';
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
  PopoverAnchor,
} from '@/shared/components/ui/popover';
import Room from '@/features/game/room/Room';
import { useEffect, useState } from 'react';
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

const GamePage = () => {
  const {
    data: gameEntry,
    isLoading: isGameEntryLoading,
    error: gameEntryError,
  } = useGameEntryQuery();

  const currentPetId = gameEntry?.pet?.petId;
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
    const loadRoomPlacements = async () => {
      try {
        const roomId = 1; // 하드코딩된 roomId
        const characterId = 1; // 하드코딩된 characterId

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
  }, [applyServerState]);

  useEffect(() => {
    setBehavior({ currentAnimation: 'idle' });
    if (isTransitioningToGame) {
      finishTransitionToGame();
    }
  }, [finishTransitionToGame, isTransitioningToGame, setBehavior]);

  // Popover 상태 관리
  const [isPopoverOpen, setIsPopoverOpen] = useState(false);

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

  return (
    <GameBackgroundLayout className="game relative touch-none overflow-hidden font-galmuri">
      <div className="relative flex h-full flex-col">
        <div className="relative z-10">
          <GameHeader />
        </div>

        <div className="relative flex w-full flex-1 justify-center">
          <div className="relative inline-block w-full">
            <Popover open={isPopoverOpen} onOpenChange={setIsPopoverOpen}>
              <PopoverTrigger asChild>
                <button
                  className={`absolute left-1/2 z-[1] -translate-x-1/2 -translate-y-1/2 cursor-pointer outline-none ${
                    isPopoverOpen ? 'top-[30%]' : 'top-1/2'
                  }`}
                  type="button"
                  disabled={!hasPet}
                >
                  {hasPet && (
                    <CatSprite
                      itemId={currentPetItemId}
                      currentAnimation={behavior.currentAnimation}
                      className="scale-400"
                      onAnimationComplete={handleAnimationComplete}
                    />
                  )}
                </button>
              </PopoverTrigger>

              <div
                className={`absolute left-1/2 z-0 w-full -translate-x-1/2 -translate-y-1/2 transition-all duration-300 ease-in-out ${
                  isPopoverOpen && hasPet ? 'top-[30%]' : 'top-1/2'
                }`}
              >
                <Room mode="readonly" placementArea={null}>
                  {context => (
                    <RoomCanvas
                      context={context}
                      allowItemPickup={false}
                      showActions={false}
                      allowDelete={false}
                    />
                  )}
                </Room>
              </div>

              <PopoverAnchor className="absolute right-0 bottom-0 left-0" />

              {hasPet && (
                <PopoverContent
                  side="top"
                  align="center"
                  sideOffset={16}
                  className="w-screen max-w-md p-0"
                >
                  <PetStatusCard petId={currentPetId} />
                </PopoverContent>
              )}
            </Popover>
          </div>
        </div>

        <div className="absolute right-0 bottom-0 z-10 pr-3 pb-5">
          <ElevatorButton />
        </div>
      </div>
    </GameBackgroundLayout>
  );
};

export default GamePage;
