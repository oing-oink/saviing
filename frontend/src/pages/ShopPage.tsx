import Inventory from '@/features/game/shop/components/Inventory';
import { useGameItems } from '@/features/game/shop/query/useItemsQuery';
import { TAB_TO_API_PARAMS } from '@/features/game/shop/types/item';
import { useTabs } from '@/features/game/shop/hooks/useTabs';
import sampleRoom from '@/assets/sampleRoom.png';
import InventoryHud from '@/features/game/shop/components/InventoryHud';

const ShopPage = () => {
  const { activeTab, setActiveTab } = useTabs();
  const { type, category } = TAB_TO_API_PARAMS[activeTab.name];
  const { data: itemsData, isLoading, error } = useGameItems(type, category);

  if (isLoading) {
    return (
      <div className="game relative flex min-h-screen w-full items-center justify-center bg-store-bg font-galmuri">
        <div className="text-white">아이템을 불러오는 중...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="game relative flex min-h-screen w-full items-center justify-center bg-store-bg font-galmuri">
        <div className="text-red-500">
          아이템을 불러오는 중 오류가 발생했습니다.
        </div>
      </div>
    );
  }

  const items = itemsData?.items || [];

  return (
    <div className="game relative min-h-screen w-full bg-store-bg font-galmuri">
      <div className="relative z-10">
        <InventoryHud />
      </div>
      <div className="flex justify-center">
        <img src={sampleRoom} alt="" />
      </div>
      <div className="absolute bottom-0 left-0 w-full">
        <Inventory
          items={items}
          activeTab={activeTab}
          onTabChange={setActiveTab}
        />
      </div>
    </div>
  );
};

export default ShopPage;
