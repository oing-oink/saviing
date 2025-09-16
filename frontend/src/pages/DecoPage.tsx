import Room from '@/features/game/room/Room';
import Inventory from '@/features/game/shop/components/Inventory';
import { mockInventoryItems } from '@/features/game/shop/mocks/inventoryMockData';
import { SaveModal } from '@/features/game/room/components/SaveModal';
import { useState } from 'react';
import GameBackground from '@/features/game/shared/components/GameBackground';
import DecoHeader from '@/features/game/room/components/DecoHeader';
import elevatorBasic from '@/assets/game_button/elevatorBasic.png';

const DecoPage = () => {
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
    <div className="game relative flex h-screen flex-col overflow-hidden font-galmuri">
      <GameBackground />

      <div className="relative z-10 flex h-full flex-col">
        <DecoHeader />
        <div className="flex flex-1 flex-col">
          <div className="relative">
            <Room />
          </div>
          <div className="my-2 text-center">
            <button
              onClick={handleSaveClick}
              className="focus:ring-opacity-50 rounded-lg bg-primary px-8 py-2 text-xl font-bold tracking-widest text-white shadow-lg focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
            >
              SAVE
            </button>
          </div>
          <div className="flex-grow" />
          <div className="relative">
            <Inventory items={mockInventoryItems} />
            <button className="absolute -top-4 right-3 cursor-pointer focus:outline-none active:scale-95 active:brightness-90">
              <img src={elevatorBasic} alt="1st floor" className="h-9 w-9" />
            </button>
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
