import Inventory from '@/features/game/shop/components/Inventory';
import type { Item, Tab } from '@/features/game/shop/types/item';

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
