import { Room } from '@/features/game/room/Room';
import Inventory from '@/features/game/shop/components/inventory';
import { mockInventoryItems } from '@/features/game/shop/mocks/inventoryMockData';
import backButton from '@/assets/game_button/backButton.png';
import storeButton from '@/assets/game_button/storeButton.png';
import floor1stButton from '@/assets/game_button/floor1st.png';
import { SaveModal } from '@/features/game/room/components/SaveModal';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';

const DecoPage = () => {
  const navigate = useNavigate();
  const [showSaveModal, setShowSaveModal] = useState(false);

  const handleSaveClick = () => {
    setShowSaveModal(true);
  };

  const handleCloseModal = () => {
    setShowSaveModal(false);
  };

  const handleSave = () => {
    // 저장 로직 추가!!
    setShowSaveModal(false);
  };

  return (
    <div className="game flex h-screen flex-col overflow-hidden bg-sky-bg font-galmuri">
      <div className="mt-4 flex items-center justify-between p-4">
        <button
          onClick={() => {
            navigate(PAGE_PATH.GAME);
          }}
          className="cursor-pointer rounded-full bg-transparent p-0 focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
        >
          <img src={backButton} alt="back" className="h-10 w-10" />
        </button>
        <button
          onClick={() => {
            navigate(PAGE_PATH.SHOP);
          }}
          className="cursor-pointer rounded-full bg-transparent p-0 focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
        >
          <img src={storeButton} alt="store" className="h-10 w-10" />
        </button>
      </div>

      <div className="relative mt-4 flex flex-1 justify-center px-4">
        <Room />
        <button
          onClick={handleSaveClick}
          className="focus:ring-opacity-50 absolute bottom-8 left-1/2 -translate-x-1/2 transform rounded-lg bg-primary px-8 py-2 text-xl font-bold tracking-widest text-white shadow-lg focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
        >
          SAVE
        </button>
      </div>

      <div className="relative">
        <Inventory items={mockInventoryItems} />

        <button className="absolute -top-4 right-2 h-10 w-12 cursor-pointer focus:outline-none active:scale-95 active:brightness-90">
          <img src={floor1stButton} alt="1st floor" className="h-10 w-12" />
        </button>
      </div>

      <SaveModal
        isOpen={showSaveModal}
        onClose={handleCloseModal}
        onSave={handleSave}
      />
    </div>
  );
};

export default DecoPage;
