import Inventory from '@/features/game/shop/components/Inventory';
import { mockInventoryItems } from '@/features/game/shop/mocks/inventoryMockData';
import sampleRoom from '@/assets/sampleRoom.png';
import InventoryHud from '@/features/game/shop/components/InventoryHud';

const ShopPage = () => {
  return (
    <div className="game relative min-h-screen w-full bg-store-bg font-galmuri">
      <div className="px-3">
        <InventoryHud />
      </div>
      <div className="flex justify-center">
        <img src={sampleRoom} alt="" />
      </div>
      <div className="absolute bottom-0 left-0 w-full">
        <Inventory items={mockInventoryItems} />
      </div>
    </div>
  );
};

export default ShopPage;
