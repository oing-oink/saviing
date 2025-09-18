import backButton from '@/assets/game_button/backButton.png';
import storeButton from '@/assets/game_button/storeButton.png';
import Room from '@/features/game/room/Room';
import { SaveModal } from '@/features/game/room/components/SaveModal';
import Coin from '@/features/game/shared/components/Coin';
import GameBackground from '@/features/game/shared/components/GameBackground';
import Inventory from '@/features/game/shop/components/Inventory';
import { mockInventoryItems } from '@/features/game/shop/mocks/inventoryMockData';
import type { TabId } from '@/features/game/shop/types/item';
import { PAGE_PATH } from '@/shared/constants/path';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const DecoPage = () => {
  const navigate = useNavigate();
  const [showSaveModal, setShowSaveModal] = useState(false);

  // DecoPage는 이제 어떤 그리드를 보여줄지 상태만 관리합니다.
  const [gridType, setGridType] = useState<TabId | null>(null);

  const handleSaveClick = () => setShowSaveModal(true);
  const handleCloseModal = () => setShowSaveModal(false);
  const handleSave = () => {
    // 저장 로직 추가!!
    setShowSaveModal(false);
  };

  return (
    <div className="game relative flex h-screen flex-col overflow-hidden font-galmuri">
      <GameBackground />

      <div className="relative z-10 flex h-full flex-col">
        {/* Header */}
        <div className="relative z-10 mt-4 flex items-center justify-between p-4">
          <button
            onClick={() => navigate(PAGE_PATH.GAME)}
            className="cursor-pointer rounded-full bg-transparent p-0 focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
          >
            <img src={backButton} alt="back" className="h-10 w-10" />
          </button>
          <Coin />
          <button
            onClick={() => navigate(PAGE_PATH.SHOP)}
            className="cursor-pointer rounded-full bg-transparent p-0 focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
          >
            <img src={storeButton} alt="store" className="h-10 w-10" />
          </button>
        </div>

        <div className="flex flex-1 flex-col">
          {/* 재사용 가능한 Room 컴포넌트는 gridType prop으로 제어됩니다. */}
          <div className="relative">
            <Room gridType={gridType} />
          </div>

          {/* SAVE 버튼 */}
          <div className="relative z-10 my-2 text-center">
            <button
              onClick={handleSaveClick}
              className="rounded-lg bg-primary px-8 py-2 text-xl font-bold tracking-widest text-white shadow-lg focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
            >
              SAVE
            </button>
          </div>

          <div className="flex-grow" />

          {/* 인벤토리는 클릭된 탭의 ID를 onCategoryClick으로 알려줍니다. */}
          <div className="relative z-10">
            <Inventory
              items={mockInventoryItems}
              onCategoryClick={setGridType}
            />
          </div>
        </div>

        <SaveModal
          isOpen={showSaveModal}
          onClose={handleCloseModal}
          onSave={handleSave}
        />
      </div>
    </div>
  );
};

export default DecoPage;
