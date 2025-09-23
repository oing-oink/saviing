import { useMemo } from 'react';
import type { Item, Tab } from '@/features/game/shop/types/item';
import { TABS } from '@/features/game/shop/types/item';
import { useSlots } from '@/features/game/shop/hooks/useSlots';
import { useItemModal } from '@/features/game/shop/hooks/useItemModal';
import { getItemImage } from '@/features/game/shop/utils/getItemImage';
import { useDecoStore } from '@/features/game/deco/store/useDecoStore';
import inventory_square from '@/assets/inventory_square.png';
import ItemDetailModal from './ItemDetailModal';

/** 상점/데코 인벤토리 영역을 구성하기 위한 속성. */
interface InventoryProps {
  items: Item[];
  activeTab: Tab;
  onTabChange: (tab: Tab) => void;
  mode?: 'shop' | 'deco';
  onItemSelect?: (item: Item, slotId?: string) => void;
  onCategoryClick?: (tab: Tab) => void;
  onPreviewItem?: (item: Item) => void;
  isLoading?: boolean;
  isError?: boolean;
  error?: Error | null;
  emptyMessage?: string;
}

/** 상점 또는 데코 화면에서 아이템 슬롯과 탭을 렌더링하는 컴포넌트. */
const Inventory = ({
  items,
  activeTab,
  onTabChange,
  mode = 'shop',
  onItemSelect,
  onCategoryClick,
  onPreviewItem,
  isLoading = false,
  isError = false,
  error,
  emptyMessage = '보유한 아이템이 없습니다.',
}: InventoryProps) => {
  const { selectedItemId, isModalOpen, handleItemClick, handleCloseModal } =
    useItemModal();

  // 데코 모드에서 배치된 아이템들 추적
  const draftItems = useDecoStore(state => state.draftItems);

  // 배치된 슬롯 ID들을 Set으로 관리 (빠른 조회를 위해)
  const placedSlotIds = useMemo(() => {
    if (mode !== 'deco') return new Set<string>();

    return new Set(
      draftItems
        .filter(item => !item.isPreview && item.slotId) // 프리뷰 아이템 제외 및 slotId 있는 것만
        .map(item => item.slotId!),
    );
  }, [mode, draftItems]);

  // 슬롯이 이미 배치되었는지 확인하는 함수
  const isSlotPlaced = (slotId: string) => placedSlotIds.has(slotId);

  // API에서 이미 필터링된 데이터를 받으므로 추가 필터링 불필요
  const slots = useSlots(items);

  const handleTabClick = (tab: Tab) => {
    onCategoryClick?.(tab);
    onTabChange(tab);
  };

  const handleSlotClick = (item: Item, slotId: string) => {
    if (mode === 'deco') {
      // 이미 배치된 슬롯은 클릭할 수 없음
      if (isSlotPlaced(slotId)) {
        return;
      }
      onItemSelect?.(item, slotId);
      return;
    }
    handleItemClick(item);
  };

  return (
    <div className="game w-full font-galmuri">
      {/* 탭 영역 */}
      <div className="flex border-b">
        {TABS.map(tab => (
          <button
            key={tab.id}
            onClick={() => handleTabClick(tab)}
            className={`h-8 w-16 rounded-t-lg px-1 py-1 text-xs active:scale-95 active:brightness-90 ${
              activeTab.id === tab.id
                ? 'border-t border-r border-l bg-secondary font-semibold'
                : 'bg-primary text-gray-600'
            }`}
          >
            {tab.name}
          </button>
        ))}
      </div>

      {/* 인벤토리 슬롯 */}
      <div className="h-[40vh] overflow-y-auto bg-secondary px-2 pt-1 pb-1">
        {isLoading ? (
          <div className="flex h-full items-center justify-center text-xs text-gray-600">
            로딩 중...
          </div>
        ) : isError ? (
          <div className="flex h-full items-center justify-center text-xs text-red-500">
            {error?.message ?? '인벤토리를 불러오지 못했습니다.'}
          </div>
        ) : items.length === 0 ? (
          <div className="flex h-full items-center justify-center text-xs text-gray-600">
            {emptyMessage}
          </div>
        ) : (
          <div className="grid grid-cols-3 gap-1">
            {slots.map(slot => (
              <div
                key={slot.id}
                className="relative -mb-6 flex aspect-square items-center justify-center"
              >
                <img
                  src={inventory_square}
                  alt="slot"
                  className="absolute inset-0 h-full w-full object-contain"
                />
                {slot.item && (
                  <>
                    <button
                      onClick={() => handleSlotClick(slot.item!, slot.id)}
                      disabled={mode === 'deco' && isSlotPlaced(slot.id)}
                      className={`relative flex h-[70%] w-[70%] items-center justify-center ${
                        mode === 'deco' && isSlotPlaced(slot.id)
                          ? 'cursor-not-allowed opacity-50'
                          : 'hover:opacity-80'
                      }`}
                    >
                      <img
                        src={getItemImage(slot.item.itemId)}
                        alt={slot.item.itemName}
                        className={`h-[80%] w-[80%] object-contain ${
                          mode === 'deco' && isSlotPlaced(slot.id)
                            ? 'grayscale'
                            : ''
                        }`}
                      />
                    </button>
                    {/* 배치됨 표시 */}
                    {mode === 'deco' && isSlotPlaced(slot.id) && (
                      <div className="absolute inset-0 flex items-center justify-center">
                        <div className="rounded bg-black/60 px-1 py-0.5 text-[8px] font-bold text-white">
                          배치됨
                        </div>
                      </div>
                    )}
                  </>
                )}
              </div>
            ))}
          </div>
        )}
      </div>

      {mode === 'shop' ? (
        <ItemDetailModal
          itemId={selectedItemId}
          isOpen={isModalOpen}
          onClose={handleCloseModal}
          onPreview={onPreviewItem}
        />
      ) : null}
    </div>
  );
};

export default Inventory;
