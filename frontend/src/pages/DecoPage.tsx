import { Room } from '@/features/game/room/Room';
import Inventory from '@/features/game/shop/components/inventory';
import { mockInventoryItems } from '@/features/game/shop/mocks/inventoryMockData';
import backButton from '@/assets/game_button/backButton.png';
import storeButton from '@/assets/game_button/storeButton.png';
import floor1stButton from '@/assets/game_button/floor1st.png';
import { SaveModal } from '@/features/game/room/components/saveModal';
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

  return (
    <div className="game flex h-screen flex-col overflow-hidden bg-sky-bg font-galmuri">
      <div className="mt-4 flex items-center justify-between p-4">
        <img
          src={backButton}
          alt="Back"
          onClick={() => {
            navigate(PAGE_PATH.GAME);
          }}
          className="focus:ring-opacity-50 h-10 w-10 cursor-pointer focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
        />
        <img
          src={storeButton}
          alt="Store"
          onClick={() => {
            navigate(PAGE_PATH.SHOP);
          }}
          className="focus:ring-opacity-50 h-10 w-10 cursor-pointer focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
        />
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

        <img
          src={floor1stButton}
          alt="Floor 1st"
          className="focus:ring-opacity-50 absolute top-[-14px] right-2 h-10 w-12 cursor-pointer focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
        />
      </div>

      {showSaveModal && (
        <SaveModal isOpen={showSaveModal} onClose={handleCloseModal} />
      )}
    </div>
  );
};

export default DecoPage;
