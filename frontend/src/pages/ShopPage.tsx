import Inventory from '@/features/game/shop/components/inventory';
import { mockInventoryItems } from '@/features/game/shop/mocks/inventoryMockData';

const ShopPage = () => {
  return (
    <div className="font-galmuri">
      <div>Shop Page</div>
      <Inventory items={mockInventoryItems} />
    </div>
  );
};

export default ShopPage;
