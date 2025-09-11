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
    <div className="flex h-screen flex-col game bg-sky-bg font-galmuri overflow-hidden">
      <div className='flex justify-between items-center p-4 mt-4'>
        <img src={backButton} alt="Back"
          onClick={() => {
              navigate(PAGE_PATH.GAME);
            }}
          className="w-10 h-10 cursor-pointer active:brightness-90 active:scale-95 
          focus:outline-none focus:ring-1 focus:ring-primary focus:ring-opacity-50" 
        />
        <img src={storeButton} alt="Store"
          onClick={() => {
              navigate(PAGE_PATH.SHOP);
            }}
          className="w-10 h-10 cursor-pointer active:brightness-90 active:scale-95 
          focus:outline-none focus:ring-1 focus:ring-primary focus:ring-opacity-50" 
        />
      </div>

      <div className="relative flex flex-1 justify-center mt-4 px-4">
        <Room />
        <button
          onClick={handleSaveClick}
          className="absolute bottom-8 left-1/2 -translate-x-1/2 transform 
          rounded-lg bg-primary px-8 py-2 text-xl text-white tracking-widest font-bold shadow-lg 
          active:brightness-90 active:scale-95 focus:outline-none focus:ring-1 focus:ring-primary focus:ring-opacity-50"
        >
          SAVE
        </button>
      </div>

      <div className="relative">
        <Inventory items={mockInventoryItems} />

        <img src={floor1stButton} alt="Floor 1st" 
          className="absolute top-[-14px] right-2 w-12 h-10 cursor-pointer active:brightness-90 active:scale-95 
          focus:outline-none focus:ring-1 focus:ring-primary focus:ring-opacity-50" 
        />
      </div>

      {showSaveModal && <SaveModal isOpen={showSaveModal} onClose={handleCloseModal} />}
    </div>
  );
};

export default DecoPage;
