import Inventory from '@/features/game/shop/components/Inventory';
import { mockInventoryItems } from '@/features/game/shop/mocks/inventoryMockData';
// import sampleRoom from '@/assets/sampleRoom.png';
import InventoryHud from '@/features/game/shop/components/InventoryHud';
import Room from '@/features/game/room/Room';
import type { TabId } from '@/features/game/shop/types/item';
import { useState } from 'react';

const ShopPage = () => {
  // ShopPage는 어떤 그리드를 보여줄지 상태만 관리
  const [gridType, setGridType] = useState<TabId | null>(null);

  return (
    <div className="game relative min-h-screen w-full bg-store-bg font-galmuri">
      <div className="relative z-10">
        <InventoryHud />
      </div>
      <div className="flex justify-center items-center min-h-[60vh] pt-8">
        {/* <img src={sampleRoom} alt="" /> */}
        <Room gridType={gridType} />
      </div>
      <div className="absolute bottom-0 left-0 w-full z-10">
        <Inventory items={mockInventoryItems} onCategoryClick={setGridType} />
      </div>
    </div>
  );
};

export default ShopPage;
