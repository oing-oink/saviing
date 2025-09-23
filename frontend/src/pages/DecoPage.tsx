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
import { useDecoStore } from '@/features/game/deco/store/useDecoStore';
import GameBackgroundLayout from '@/features/game/shared/layouts/GameBackgroundLayout';

const DecoPage = () => {
  const navigate = useNavigate();
  const [showSaveModal, setShowSaveModal] = useState(false);
  const [isPlacementBlockedOpen, setIsPlacementBlockedOpen] = useState(false);
  const { activeTab, setActiveTab } = useTabs(TABS[3]);
  const [gridType, setGridType] = useState<TabId | null>('floor');

  const { items, isLoading, isError, error } = useDecoInventory();
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

  const mapCategoryToGridType = (category: string): TabId | null => {
    switch (category) {
      case 'LEFT':
      case 'LEFT_WALL':
        return 'leftWall';
      case 'RIGHT':
      case 'RIGHT_WALL':
        return 'rightWall';
      case 'BOTTOM':
      case 'FLOOR':
      case 'ROOM_COLOR':
        return 'floor';
      default:
        return null;
    }
  };

  // 탭에 따라 인벤토리 아이템을 필터링한다. (데코도 샵과 동일한 UX 유지)
  const filteredItems = useMemo(() => {
    switch (activeTab.id) {
      case 'cat':
        return items.filter(item => item.itemType === 'PET');
      case 'leftWall':
        return items.filter(
          item =>
            item.itemCategory === 'LEFT' || item.itemCategory === 'LEFT_WALL',
        );
      case 'rightWall':
        return items.filter(
          item =>
            item.itemCategory === 'RIGHT' || item.itemCategory === 'RIGHT_WALL',
        );
      case 'floor':
        return items.filter(
          item =>
            item.itemCategory === 'BOTTOM' ||
            item.itemCategory === 'FLOOR' ||
            item.itemCategory === 'ROOM_COLOR',
        );
      default:
        return items;
    }
  }, [activeTab.id, items]);

  const handleItemSelect = (item: Item) => {
    setIsPlacementBlockedOpen(false);

    if (item.itemType === 'PET') {
      // PET은 바닥에만 배치할 수 있으며 최대 2마리까지 허용한다.
      const currentPetCount = draftItems.filter(
        draft => draft.itemType === 'PET',
      ).length;
      const alreadyPlacedPet = draftItems.some(
        draft => draft.itemType === 'PET' && draft.itemId === item.itemId,
      );
      if (currentPetCount >= 2 && !alreadyPlacedPet) {
        setIsPlacementBlockedOpen(true);
        return;
      }
      setGridType('floor');
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
    startDragFromInventory(String(item.itemId), {
      allowedGridType: targetGrid,
      xLength: item.xLength ?? 1,
      yLength: item.yLength ?? 1,
      itemType: item.itemType,
    });
  };

  const handleTabChange = (tab: Tab) => {
    // 탭 전환 시 진행 중인 드래그를 취소해 고스트가 남지 않도록 한다.
    cancelDrag();
    setActiveTab(tab);
    if (tab.id === 'cat') {
      setGridType('floor');
    } else {
      setGridType(tab.id);
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
              gridType={gridType}
              panEnabled={!dragSession || Boolean(pendingPlacement)}
              editOverlay={ctx => (
                <RoomCanvas
                  context={ctx}
                  onAutoPlacementFail={handlePlacementBlocked}
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
