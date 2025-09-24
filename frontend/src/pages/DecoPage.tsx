import backButton from '@/assets/game_button/backButton.png';
import storeButton from '@/assets/game_button/storeButton.png';
import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Room from '@/features/game/room/Room';
import { SaveModal } from '@/features/game/room/components/SaveModal';
import Coin from '@/features/game/shared/components/Coin';
import { useTabs } from '@/features/game/shop/hooks/useTabs';
import {
  TABS,
  type Item,
  type Tab,
  type TabId,
} from '@/features/game/shop/types/item';
import { PAGE_PATH } from '@/shared/constants/path';
import Toolbar from '@/features/game/deco/components/Toolbar';
import InventoryPanel from '@/features/game/deco/components/InventoryPanel';
import { PlacementBlockedModal } from '@/features/game/deco/components/PlacementBlockedModal';
import { RoomCanvas } from '@/features/game/deco/components/roomCanvas';
import { useDecoInventory } from '@/features/game/deco/query/useDecoInventory';
import { useDecoSaveMutation } from '@/features/game/deco/query/useDecoSaveMutation';
import { useQueryClient } from '@tanstack/react-query';
import { itemsKeys } from '@/features/game/shop/query/itemsKeys';
import { useDecoStore } from '@/features/game/deco/store/useDecoStore';
import { getRoomPlacements } from '@/features/game/room/api/roomApi';
import { getInventoryItems } from '@/features/game/shop/api/itemsApi';
import type { PlacedItem } from '@/features/game/deco/types/decoTypes';
import GameBackgroundLayout from '@/features/game/shared/layouts/GameBackgroundLayout';

const DecoPage = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [showSaveModal, setShowSaveModal] = useState(false);
  const [isPlacementBlockedOpen, setIsPlacementBlockedOpen] = useState(false);
  const { activeTab, setActiveTab } = useTabs(TABS[3]);
  const [placementArea, setPlacementArea] = useState<TabId | null>('BOTTOM');

  const { items, isLoading, isError, error } = useDecoInventory(activeTab);
  const saveMutation = useDecoSaveMutation();

  const draftItems = useDecoStore(state => state.draftItems);
  const placedItems = useDecoStore(state => state.placedItems);
  const dragSession = useDecoStore(state => state.dragSession);
  const pendingPlacement = useDecoStore(state => state.pendingPlacement);
  const startDragFromInventory = useDecoStore(
    state => state.startDragFromInventory,
  );
  const cancelDrag = useDecoStore(state => state.cancelDrag);
  const resetToLastSaved = useDecoStore(state => state.resetToLastSaved);
  const applyServerState = useDecoStore(state => state.applyServerState);

  // 컴포넌트 초기화 시 방 배치 정보 로드
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

        // 배치된 아이템들의 inventoryItemId 세트 생성
        const placedInventoryIds = new Set(
          placementsResponse.placements.map(
            placement => placement.inventoryItemId,
          ),
        );

        // 배치된 아이템들을 비활성화 처리한 인벤토리 아이템 목록
        const updatedInventoryItems = allInventoryItems.map(item => ({
          ...item,
          isAvailable: item.inventoryItemId
            ? !placedInventoryIds.has(item.inventoryItemId)
            : item.isAvailable,
        }));

        // inventoryItemId를 키로 하는 Map 생성 (업데이트된 아이템들로)
        const inventoryMap = new Map(
          updatedInventoryItems.map(item => [item.inventoryItemId!, item]),
        );

        // React Query 캐시를 업데이트하여 인벤토리 아이템들을 비활성화
        const categories = [
          { type: 'DECORATION', category: 'LEFT' },
          { type: 'DECORATION', category: 'RIGHT' },
          { type: 'DECORATION', category: 'BOTTOM' },
          { type: 'DECORATION', category: 'ROOM_COLOR' },
          { type: 'PET', category: 'CAT' },
        ];

        categories.forEach(({ type, category }) => {
          const queryKey = itemsKeys.inventoryByTypeAndCategory(
            characterId,
            type,
            category,
          );
          console.log(
            `캐시 업데이트 시도: ${type}-${category}, queryKey:`,
            queryKey,
          );

          queryClient.setQueryData(queryKey, (oldData: any) => {
            console.log(`${type}-${category} 캐시 데이터:`, oldData);
            if (!oldData) {
              console.log(`${type}-${category} 캐시 데이터 없음`);
              return oldData;
            }

            const updatedItems = oldData.items.map((item: Item) => {
              const shouldDisable =
                item.inventoryItemId &&
                placedInventoryIds.has(item.inventoryItemId);
              console.log(
                `아이템 체크: inventoryItemId=${item.inventoryItemId}, shouldDisable=${shouldDisable}, 현재 isAvailable=${item.isAvailable}`,
              );

              if (shouldDisable) {
                console.log(
                  `✅ 인벤토리 아이템 비활성화: inventoryItemId=${item.inventoryItemId}, itemName=${item.itemName || item.itemName}`,
                );
              }
              return {
                ...item,
                isAvailable: shouldDisable ? false : item.isAvailable,
              };
            });

            console.log(
              `${type}-${category} 업데이트된 아이템:`,
              updatedItems.filter((item: Item) => !item.isAvailable),
            );

            return {
              ...oldData,
              items: updatedItems,
            };
          });
        });

        console.log('배치된 inventoryItemId:', Array.from(placedInventoryIds));
        console.log('인벤토리 캐시 업데이트 완료');

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

        console.log('로드된 배치 아이템:', placedItems);

        // 데코 스토어에 서버 상태 적용
        applyServerState({ placedItems });
      } catch (error) {
        console.error('방 배치 정보 로드 실패:', error);
      }
    };

    loadRoomPlacements();
  }, [applyServerState]);

  useEffect(() => {
    return () => {
      cancelDrag();
      resetToLastSaved();
    };
  }, [cancelDrag, resetToLastSaved]);

  const isDirty = useMemo(() => {
    if (draftItems.length !== placedItems.length) {
      return true;
    }
    return JSON.stringify(draftItems) !== JSON.stringify(placedItems);
  }, [draftItems, placedItems]);

  const handleSaveClick = () => {
    if (!isDirty) {
      return;
    }
    setShowSaveModal(true);
  };
  const handleCloseModal = () => setShowSaveModal(false);

  const handleSave = () => {
    saveMutation.mutate(undefined, {
      onSuccess: () => {
        setShowSaveModal(false);
      },
    });
  };

  const handleCancelChanges = () => {
    if (!isDirty) {
      return;
    }
    cancelDrag();
    resetToLastSaved();
  };

  const getCategoryPlacementArea = (category: string): TabId | null => {
    if (category === 'LEFT') {
      return 'LEFT';
    }
    if (category === 'RIGHT') {
      return 'RIGHT';
    }
    if (category === 'BOTTOM') {
      return 'BOTTOM';
    }
    return null;
  };

  // API 표준 카테고리로 인벤토리 아이템 필터링
  const filteredItems = useMemo(() => {
    switch (activeTab.id) {
      case 'CAT':
        return items.filter(item => item.itemCategory === 'CAT');
      case 'LEFT':
        return items.filter(item => item.itemCategory === 'LEFT');
      case 'RIGHT':
        return items.filter(item => item.itemCategory === 'RIGHT');
      case 'BOTTOM':
        return items.filter(item => item.itemCategory === 'BOTTOM');
      case 'ROOM_COLOR':
        return items.filter(item => item.itemCategory === 'ROOM_COLOR');
      default:
        return items;
    }
  }, [activeTab.id, items]);

  const handleItemSelect = (item: Item, slotId?: string) => {
    setIsPlacementBlockedOpen(false);

    if (item.itemType === 'PET' || item.itemCategory === 'CAT') {
      const currentPetCount = draftItems.filter(
        draft => draft.itemType === 'PET',
      ).length;
      if (currentPetCount >= 2) {
        setIsPlacementBlockedOpen(true);
        return;
      }
      setPlacementArea('BOTTOM');
      startDragFromInventory(String(item.itemId), {
        inventoryItemId: item.inventoryItemId,
        allowedGridType: 'BOTTOM',
        xLength: item.xLength ?? 1,
        yLength: item.yLength ?? 1,
        itemType: item.itemType,
        slotId,
      });
      return;
    }

    const targetArea = getCategoryPlacementArea(item.itemCategory);
    if (!targetArea) {
      setIsPlacementBlockedOpen(true);
      return;
    }
    setPlacementArea(targetArea);
    startDragFromInventory(String(item.itemId), {
      inventoryItemId: item.inventoryItemId,
      allowedGridType: targetArea,
      xLength: item.xLength ?? 1,
      yLength: item.yLength ?? 1,
      itemType: item.itemType,
      slotId,
    });
  };

  const handleTabChange = (tab: Tab) => {
    // 탭 전환 시 진행 중인 드래그를 취소해 고스트가 남지 않도록 한다.
    cancelDrag();
    setActiveTab(tab);
    if (tab.id === 'CAT') {
      setPlacementArea('BOTTOM');
    } else {
      setPlacementArea(tab.id);
    }
  };

  const handlePlacementBlocked = () => {
    setIsPlacementBlockedOpen(true);
  };

  return (
    <GameBackgroundLayout className="game relative overflow-hidden font-galmuri">
      <div className="relative z-10 flex h-full flex-col">
        {/* Header */}
        <div className="z-10 flex h-20 w-full items-center justify-between px-3">
          <button
            onClick={() => navigate(PAGE_PATH.GAME)}
            className="cursor-pointer rounded-full bg-transparent p-0 focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
          >
            <img src={backButton} alt="back" className="w-9 pt-5" />
          </button>
          <Coin />
          <button
            onClick={() => navigate(PAGE_PATH.SHOP)}
            className="cursor-pointer rounded-full bg-transparent p-0 focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
          >
            <img src={storeButton} alt="store" className="w-9 pt-5" />
          </button>
        </div>

        <Toolbar
          onSaveClick={handleSaveClick}
          onCancelClick={handleCancelChanges}
          isSaving={saveMutation.isPending}
          isDirty={isDirty}
        />

        <div className="flex flex-1 flex-col">
          <div className="flex-grow" />

          {/* Room (해당 컴포넌트는 gridType prop으로 제어) */}
          <div className="relative">
            <Room
              mode="edit"
              placementArea={placementArea}
              panEnabled={!dragSession || Boolean(pendingPlacement)}
              editOverlay={ctx => (
                <RoomCanvas
                  context={ctx}
                  onAutoPlacementFail={handlePlacementBlocked}
                  allowItemPickupPredicate={item => {
                    if (activeTab.id === 'CAT') {
                      return item.itemType === 'PET';
                    }
                    if (item.itemType === 'PET') {
                      return false;
                    }
                    return true;
                  }}
                />
              )}
            />
          </div>

          <div className="flex-grow" />

          {/* 인벤토리 (클릭된 탭의 ID를 onCategoryClick으로 알려줌.) */}
          <div className="relative z-10">
            <InventoryPanel
              items={filteredItems}
              activeTab={activeTab}
              onTabChange={handleTabChange}
              onItemSelect={handleItemSelect}
              isLoading={isLoading}
              isError={isError}
              error={error ?? undefined}
              emptyMessage={
                isError
                  ? '인벤토리를 불러오지 못했습니다.'
                  : '배치 가능한 아이템이 없습니다.'
              }
            />
          </div>
        </div>

        <SaveModal
          isOpen={showSaveModal}
          onClose={handleCloseModal}
          onSave={handleSave}
        />

        <PlacementBlockedModal
          isOpen={isPlacementBlockedOpen}
          onClose={() => setIsPlacementBlockedOpen(false)}
        />
      </div>
    </GameBackgroundLayout>
  );
};

export default DecoPage;
