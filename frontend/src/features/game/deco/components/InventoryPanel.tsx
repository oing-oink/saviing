import Inventory from '@/features/game/shop/components/Inventory';
import type { Item, Tab } from '@/features/game/shop/types/item';

/**
 * 데코 전용 인벤토리 패널에 전달되는 속성 정의.
 */
interface InventoryPanelProps {
  items: Item[];
  activeTab: Tab;
  onTabChange: (tab: Tab) => void;
  onItemSelect?: (item: Item) => void;
  isLoading?: boolean;
  isError?: boolean;
  error?: Error | null;
  emptyMessage?: string;
}

/**
 * 공용 인벤토리 컴포넌트를 데코 모드로 감싸서 보여주는 래퍼 컴포넌트.
 */
const InventoryPanel = ({
  items,
  activeTab,
  onTabChange,
  onItemSelect,
  isLoading,
  isError,
  error,
  emptyMessage,
}: InventoryPanelProps) => {
  return (
    <Inventory
      items={items}
      activeTab={activeTab}
      onTabChange={onTabChange}
      onItemSelect={onItemSelect}
      mode="deco"
      isLoading={isLoading}
      isError={isError}
      error={error}
      emptyMessage={emptyMessage}
    />
  );
};

export default InventoryPanel;
