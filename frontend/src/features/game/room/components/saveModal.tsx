import closeButton from '@/assets/game_button/closeButton.png';

export const SaveModal = ({
  isOpen,
  onClose,
}: {
  isOpen: boolean;
  onClose: () => void;
}) => {
  if (!isOpen) {
    return null;
  }

  return (
    <div className="game fixed inset-0 z-50 flex items-center justify-center p-4">
      {/* 오버레이 */}
      <div
        className="bg-gray fixed inset-0 backdrop-blur-sm"
        onClick={onClose}
      ></div>

      {/* 모달창 */}
      <div className="relative z-10 rounded-xl bg-secondary px-8 pt-4 pb-6 text-xl font-bold tracking-widest text-black shadow-lg">
        <img
          src={closeButton}
          alt="Store"
          onClick={onClose}
          className="focus:ring-opacity-50 absolute top-4 right-4 h-7 w-7 cursor-pointer focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
        />
        <p className="m-8 mt-12 text-center">저장하시겠습니까?</p>
        <div className="flex justify-center space-x-4">
          <button
            onClick={onClose}
            className="focus:ring-opacity-50 flex-1 cursor-pointer rounded-md bg-primary px-6 py-2 text-lg font-bold tracking-widest text-white focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
          >
            CANCEL
          </button>
          <button
            // onClick={저장 로직} // 실제로 저장 로직이 들어갈 부분
            onClick={onClose}
            className="focus:ring-opacity-50 flex-1 cursor-pointer rounded-md bg-primary px-6 py-2 text-lg font-bold tracking-widest text-white focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
          >
            SAVE
          </button>
        </div>
      </div>
    </div>
  );
};
