import type { Item, Tab } from '@/features/game/shop/types/item';
import { useTabs } from '@/features/game/shop/hooks/useTabs';
import { useSlots } from '@/features/game/shop/hooks/useSlots';
import { useItemModal } from '@/features/game/shop/hooks/useItemModal';
import { getItemImage } from '@/features/game/shop/utils/getItemImage';
import inventory_square from '@/assets/inventory_square.png';
import ItemDetailModal from './ItemDetailModal';

interface InventoryProps {
  items: Item[];
  activeTab: Tab;
  onTabChange: (tab: Tab) => void;
}

const Inventory = ({ items, activeTab, onTabChange }: InventoryProps) => {
  const { TABS } = useTabs();
  const { selectedItemId, isModalOpen, handleItemClick, handleCloseModal } =
    useItemModal();

  // API에서 이미 필터링된 데이터를 받으므로 추가 필터링 불필요
  const slots = useSlots(items);

  return (
    <div className="game w-full font-galmuri">
      {/* 탭 영역 */}
      <div className="flex border-b">
        {TABS.map(tab => (
          <button
            key={tab.id}
            onClick={() => onTabChange(tab)}
            className={`h-8 w-16 rounded-t-lg px-1 py-1 text-xs active:scale-95 active:brightness-90 ${
              activeTab === tab
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
                <button
                  onClick={() => handleItemClick(slot.item!)}
                  className="relative flex h-[70%] w-[70%] items-center justify-center hover:opacity-80"
                >
                  <img
                    src={getItemImage(slot.item.itemId)}
                    alt={slot.item.itemName}
                    className="h-[80%] w-[80%] object-contain"
                  />
                </button>
              )}
            </div>
          ))}
        </div>
      </div>

      <ItemDetailModal
        itemId={selectedItemId}
        isOpen={isModalOpen}
        onClose={handleCloseModal}
      />
    </div>
  );
};

export default Inventory;
