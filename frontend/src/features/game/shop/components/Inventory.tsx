import type { Item } from '@/features/game/shop/types/item';
import { useTabs } from '@/features/game/shop/hooks/useTabs';
import { useSlots } from '@/features/game/shop/hooks/useSlots';
import inventory_square from '@/assets/inventory_square.png';

interface InventoryProps {
  items: Item[];
}

const Inventory = ({ items }: InventoryProps) => {
  const { activeTab, setActiveTab, TABS } = useTabs();

  // 현재 탭에 해당하는 아이템 필터링
  const filteredItems = items.filter(item => item.category === activeTab);

  const slots = useSlots(filteredItems);

  return (
    <div className="game font-galmuri absolute bottom-0 left-0 w-full">
      {/* 탭 영역 */}
      <div className="flex border-b">
        {TABS.map(tab => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            className={`rounded-t-xl px-4 py-2 text-sm active:scale-95 active:brightness-90 ${
              activeTab === tab
                ? 'bg-secondary border-l border-r border-t font-semibold'
                : 'bg-primary text-gray-600'
            }`}
          >
            {tab}
          </button>
        ))}
      </div>

      {/* 인벤토리 슬롯 */}
      <div className="bg-secondary max-h-80 overflow-y-auto px-4 pb-5 pt-1">
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
                <img
                  src={slot.item.image}
                  alt={slot.item.name}
                  className="relative h-[70%] w-[70%] object-contain"
                />
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Inventory;
