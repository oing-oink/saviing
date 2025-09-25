import { useEffect, useState } from 'react';
import Room from '@/features/game/room/Room';
import Inventory from '@/features/game/shop/components/Inventory';
import InventoryHud from '@/features/game/shop/components/InventoryHud';
import { useTabs } from '@/features/game/shop/hooks/useTabs';
import type { Item, Tab, TabId } from '@/features/game/shop/types/item';
import { useShopInventory } from '@/features/game/shop/hooks/useShopInventory';
import { useRoomState } from '@/features/game/room/hooks/useRoomState';
import { useDecoStore } from '@/features/game/deco/store/useDecoStore';
import { RoomCanvas } from '@/features/game/deco/components/roomCanvas';
import { PlacementBlockedModal } from '@/features/game/deco/components/PlacementBlockedModal';

const ShopPage = () => {
  const { activeTab, setActiveTab } = useTabs();
  const [placementArea, setPlacementArea] = useState<TabId | null>('BOTTOM');
  const [isPlacementBlockedOpen, setIsPlacementBlockedOpen] = useState(false);

  const { items, isLoading, isError, error } = useShopInventory(activeTab);
  useRoomState();

  const startDragFromInventory = useDecoStore(
    state => state.startDragFromInventory,
  );
  const cancelDrag = useDecoStore(state => state.cancelDrag);

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

  const handleTabChange = (tab: Tab) => {
    setActiveTab(tab);
    if (tab.id === 'CAT') {
      setPlacementArea(null);
    } else {
      setPlacementArea(tab.id);
    }
  };

  useEffect(() => {
    return () => {
      cancelDrag();
    };
  }, [cancelDrag]);

  const handlePreviewItem = (item: Item) => {
    cancelDrag();

    if (item.itemType === 'PET' || item.itemCategory === 'CAT') {
      setPlacementArea('BOTTOM');
      setIsPlacementBlockedOpen(false);
      startDragFromInventory(String(item.itemId), {
        allowedGridType: 'BOTTOM',
        xLength: item.xLength ?? 1,
        yLength: item.yLength ?? 1,
        itemType: item.itemType,
        isPreview: true,
      });
      return;
    }

    const targetArea = getCategoryPlacementArea(item.itemCategory);
    if (!targetArea) {
      setIsPlacementBlockedOpen(true);
      return;
    }
    setPlacementArea(targetArea);
    setIsPlacementBlockedOpen(false);
    startDragFromInventory(String(item.itemId), {
      allowedGridType: targetArea,
      xLength: item.xLength ?? 1,
      yLength: item.yLength ?? 1,
      itemType: item.itemType,
      isPreview: true,
    });
  };

  const handlePlacementBlocked = () => {
    setIsPlacementBlockedOpen(true);
  };

  return (
    <div className="game relative min-h-screen w-full overflow-hidden bg-store-bg font-galmuri">
      <div className="relative z-10">
        <InventoryHud />
      </div>

      <div className="flex justify-center px-6 pt-6">
        <div className="w-full max-w-5xl">
          <Room
            mode="preview"
            placementArea={placementArea}
            previewOverlay={ctx => (
              <RoomCanvas
                context={ctx}
                onAutoPlacementFail={handlePlacementBlocked}
                allowItemPickup
                pickupOnlyPreview
                allowDelete
                deleteOnlyPreview
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
            isError
              ? '아이템을 불러오지 못했습니다.'
              : '표시할 아이템이 없습니다.'
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
