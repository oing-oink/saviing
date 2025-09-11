import closeButton from '@/assets/game_button/closeButton.png';

export const SaveModal = ({ isOpen, onClose }: { isOpen: boolean; onClose: () => void }) => {
  if (!isOpen) {return null;}

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 game">
      {/* 오버레이 */}
      <div className="fixed inset-0 bg-gray backdrop-blur-sm" onClick={onClose}></div>

      {/* 모달 */}
      <div className="relative rounded-xl bg-secondary px-8 pt-4 pb-6 text-xl text-black tracking-widest font-bold shadow-lg z-10">
        <img src={closeButton} alt="Store" onClick={onClose}
          className="absolute w-7 h-7 top-4 right-4 cursor-pointer active:brightness-90 active:scale-95 
          focus:outline-none focus:ring-1 focus:ring-primary focus:ring-opacity-50" 
        />
        <p className="mt-12 m-8 text-center">저장하시겠습니까?</p>
        <div className="flex justify-center space-x-4">
          <button
            onClick={onClose}
            className="flex-1 rounded-md bg-primary px-6 py-2 cursor-pointer text-lg text-white tracking-widest font-bold
            active:brightness-90 active:scale-95 focus:outline-none focus:ring-1 focus:ring-primary focus:ring-opacity-50"
          >
            CANCEL
          </button>
          <button
            // onClick={저장 로직}
            onClick={onClose}
            className="flex-1 rounded-md bg-primary px-6 py-2 cursor-pointer text-lg text-white tracking-widest font-bold
            active:brightness-90 active:scale-95 focus:outline-none focus:ring-1 focus:ring-primary focus:ring-opacity-50"
          >
            SAVE
          </button>
        </div>
      </div>
    </div>
  )
}