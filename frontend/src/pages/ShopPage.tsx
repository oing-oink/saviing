import Inventory from '@/features/game/shop/components/Inventory';
import { mockInventoryItems } from '@/features/game/shop/mocks/inventoryMockData';
import sampleRoom from '@/assets/sampleRoom.png';
import InventoryHud from '@/features/game/shop/components/InventoryHud';

const ShopPage = () => {
  return (
    <div className="game min-h-screen w-full bg-store-bg font-galmuri">
      <div className="px-3">
        <InventoryHud />
      </div>
      <img src={sampleRoom} alt="" className="mt-10" />
      <Inventory items={mockInventoryItems} />
    </div>
  );
};

export default ShopPage;
