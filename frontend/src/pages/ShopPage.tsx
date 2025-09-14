import Inventory from '@/features/game/shop/components/Inventory';
import { mockInventoryItems } from '@/features/game/shop/mocks/inventoryMockData';
import sampleRoom from '@/assets/sampleRoom.png';
import InventoryHud from '@/features/game/shop/components/InventoryHud';

const ShopPage = () => {
  return (
    <div className="game relative min-h-screen w-full bg-store-bg font-galmuri">
      <InventoryHud />
      <img src={sampleRoom} alt="" className="mt-10" />
      <div className="inset-0">
        <Inventory items={mockInventoryItems} />
      </div>
    </div>
  );
};

export default ShopPage;
