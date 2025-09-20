import { useEffect, useState } from 'react';
import Room from '@/features/game/room/Room';
import Inventory from '@/features/game/shop/components/Inventory';
import InventoryHud from '@/features/game/shop/components/InventoryHud';
import { useTabs } from '@/features/game/shop/hooks/useTabs';
import type { Item, Tab, TabId } from '@/features/game/shop/types/item';
import { useShopInventory } from '@/features/game/shop/hooks/useShopInventory';
import { useRoomState } from '@/features/game/room/hooks/useRoomState';
import { useDecoStore } from '@/features/deco/state/deco.store';
import { RoomCanvas } from '@/features/deco/components/RoomCanvas';
import { PlacementBlockedModal } from '@/features/deco/components/PlacementBlockedModal';

const ShopPage = () => {
  const { activeTab, setActiveTab } = useTabs();
  const [gridType, setGridType] = useState<TabId | null>('floor');
  const [isPlacementBlockedOpen, setIsPlacementBlockedOpen] = useState(false);

  const { items, isLoading, isError, error } = useShopInventory(activeTab);
  useRoomState();

  const startDragFromInventory = useDecoStore((state) => state.startDragFromInventory);
  const cancelDrag = useDecoStore((state) => state.cancelDrag);
  const draftItems = useDecoStore((state) => state.draftItems);

  const mapCategoryToGridType = (category: string): TabId | null => {
    switch (category) {
      case 'LEFT':
        return 'leftWall';
      case 'RIGHT':
        return 'rightWall';
      case 'BOTTOM':
      case 'FLOOR':
      case 'ROOM_COLOR':
        return 'floor';
      default:
        return null;
    }
  };

  const handleTabChange = (tab: Tab) => {
    setActiveTab(tab);
    if (tab.id === 'cat') {
      setGridType(null);
    } else {
      setGridType(tab.id);
    }
  };

  useEffect(() => {
    return () => {
      cancelDrag();
    };
  }, [cancelDrag]);

  const handlePreviewItem = (item: Item) => {
    cancelDrag();

    if (item.itemType === 'PET') {
      // PET은 최대 두 마리까지만 배치 가능하므로 초과 시 미리보기 시작을 막는다.
      const petCount = draftItems.filter((draft) => draft.itemType === 'PET').length;
      const alreadyPlacedPet = draftItems.some((draft) => draft.itemType === 'PET' && draft.itemId === item.itemId);
      if (petCount >= 2 && !alreadyPlacedPet) {
        setIsPlacementBlockedOpen(true);
        return;
      }
      setGridType('floor');
      setIsPlacementBlockedOpen(false);
      startDragFromInventory(String(item.itemId), {
        allowedGridType: 'floor',
        xLength: item.xLength ?? 1,
        yLength: item.yLength ?? 1,
        itemType: item.itemType,
      });
      return;
    }

    const targetGrid = mapCategoryToGridType(item.itemCategory);
    if (targetGrid) {
      setGridType(targetGrid);
    }
    setIsPlacementBlockedOpen(false);
    startDragFromInventory(String(item.itemId), {
      allowedGridType: targetGrid,
      xLength: item.xLength ?? 1,
      yLength: item.yLength ?? 1,
      itemType: item.itemType,
    });
  };

  const handlePlacementBlocked = () => {
    setIsPlacementBlockedOpen(true);
  };

  return (
    <div className="game relative min-h-screen w-full bg-store-bg overflow-hidden font-galmuri">
      <div className="relative z-10">
        <InventoryHud />
      </div>

      <div className="flex justify-center px-6 pt-6">
        <div className="w-full max-w-5xl">
          <Room
            mode="preview"
            gridType={gridType}
            previewOverlay={(ctx) => (
              <RoomCanvas
                context={ctx}
                onAutoPlacementFail={handlePlacementBlocked}
                allowItemPickup={false}
              />
            )}
          />
        </div>
      </div>

      <div className="absolute bottom-0 left-0 z-10 w-full">
        <Inventory
          items={items}
          activeTab={activeTab}
          onTabChange={handleTabChange}
          onCategoryClick={handleTabChange}
          onPreviewItem={handlePreviewItem}
          isLoading={isLoading}
          isError={isError}
          error={error ?? undefined}
          emptyMessage={
            isError ? '아이템을 불러오지 못했습니다.' : '표시할 아이템이 없습니다.'
          }
        />
      </div>

      <PlacementBlockedModal
        isOpen={isPlacementBlockedOpen}
        onClose={() => setIsPlacementBlockedOpen(false)}
      />
    </div>
  );
};

export default ShopPage;
