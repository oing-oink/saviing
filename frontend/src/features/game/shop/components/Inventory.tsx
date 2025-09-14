import type { Item } from '@/features/game/shop/types/item';
import { useTabs } from '@/features/game/shop/hooks/useTabs';
import { useSlots } from '@/features/game/shop/hooks/useSlots';
import { useItemModal } from '@/features/game/shop/hooks/useItemModal';
import inventory_square from '@/assets/inventory_square.png';
import ItemDetailModal from './ItemDetailModal';

interface InventoryProps {
  items: Item[];
}

const Inventory = ({ items }: InventoryProps) => {
  const { activeTab, setActiveTab, TABS } = useTabs();
  const { selectedItem, isModalOpen, handleItemClick, handleCloseModal } =
    useItemModal();

  // 현재 탭에 해당하는 아이템 필터링
  const filteredItems = items.filter(item => item.category === activeTab);

  const slots = useSlots(filteredItems);

  return (
    <div className="game absolute bottom-0 left-0 w-full font-galmuri">
      {/* 탭 영역 */}
      <div className="flex border-b">
        {TABS.map(tab => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            className={`rounded-t-xl px-4 py-2 text-sm ${
              activeTab === tab
                ? 'border-t border-r border-l bg-secondary font-semibold'
                : 'bg-primary text-gray-600'
            }`}
          >
            {tab}
          </button>
        ))}
      </div>

      {/* 인벤토리 슬롯 */}
      <div className="max-h-80 overflow-y-auto bg-secondary px-4 pt-1 pb-5">
        <div className="grid grid-cols-3 gap-1">
          {slots.map(slot => (
            <div
              key={slot.id}
              className="relative -mb-5 flex aspect-square items-center justify-center"
            >
              <img
                src={inventory_square}
                alt="slot"
                className="absolute inset-0 h-full w-full object-contain"
              />
              {slot.item && (
                <button
                  onClick={() => handleItemClick(slot.item!)}
                  className="relative h-[70%] w-[70%] hover:opacity-80"
                >
                  <img
                    src={slot.item.image}
                    alt={slot.item.name}
                    className="h-full w-full object-contain"
                  />
                </button>
              )}
            </div>
          ))}
        </div>
      </div>

      {selectedItem && (
        <ItemDetailModal
          item={selectedItem}
          isOpen={isModalOpen}
          onClose={handleCloseModal}
        />
      )}
    </div>
  );
};

export default Inventory;
