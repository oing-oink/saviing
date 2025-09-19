import inventory_square from '@/assets/inventory_square.png';
import { useItemModal } from '@/features/game/shop/hooks/useItemModal';
import { useSlots } from '@/features/game/shop/hooks/useSlots';
import { useTabs } from '@/features/game/shop/hooks/useTabs';
import type { Item, TabId } from '@/features/game/shop/types/item';
import ItemDetailModal from './ItemDetailModal';

interface InventoryProps {
  items: Item[];
  // **외부(부모 컴포넌트)에서 전달받은 클릭 이벤트 감지하여 함수 호출(존재할 경우에만)
  onCategoryClick?: (tabId: TabId) => void;
}

const Inventory = ({ items, onCategoryClick }: InventoryProps) => {
  const { activeTab, setActiveTab, TABS } = useTabs();
  const { selectedItem, isModalOpen, handleItemClick, handleCloseModal } =
    useItemModal();

  // 현재 탭에 해당하는 아이템 필터링
  const filteredItems = items.filter(item => item.category === activeTab);

  const slots = useSlots(filteredItems);

  // **탭 클릭 시 내부 상태 변경 + 외부로 클릭 이벤트 전달하는 함수 통합
  const handleTabClick = (tab: (typeof TABS)[number]) => {
    setActiveTab(tab.name);
    onCategoryClick?.(tab.id);
  };

  return (
    <div className="game w-full font-galmuri">
      {/* 탭 영역 */}
      <div className="flex border-b">
        {TABS.map(tab => (
          <button
            key={tab.id}
            onClick={() => handleTabClick(tab)}
            className={`rounded-t-xl px-4 py-2 text-sm active:scale-95 active:brightness-90 ${
              activeTab === tab.name
                ? 'border-t border-r border-l bg-secondary font-semibold'
                : 'bg-primary text-gray-600'
            }`}
          >
            {tab.name}
          </button>
        ))}
      </div>

      {/* 인벤토리 슬롯 */}
      <div className="h-45 overflow-y-auto bg-secondary px-2 pt-2">
        <div className="grid grid-cols-4 gap-0.5">
          {slots.map(slot => (
            <div
              key={slot.id}
              className="relative -mb-3 flex aspect-square items-center justify-center"
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
