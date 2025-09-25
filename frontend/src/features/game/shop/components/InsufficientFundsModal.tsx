import closeButton from '@/assets/game_button/closeButton.png';
import infoHeader from '@/assets/game_etc/infoHeader.png';

interface InsufficientFundsModalProps {
  isOpen: boolean;
  onClose: () => void;
  message: string;
}

const InsufficientFundsModal = ({
  isOpen,
  onClose,
  message,
}: InsufficientFundsModalProps) => {
  if (!isOpen) {
    return null;
  }

  return (
    <div className="game fixed inset-0 z-[99999] flex items-center justify-center bg-white/50 font-galmuri">
      <div className="relative">
        <img
          src={infoHeader}
          alt="infoHeader"
          className="mx-auto -mb-8 w-[50%]"
        />
        <div className="mx-4 max-w-lg justify-center rounded-4xl bg-secondary p-6 px-6 shadow-lg">
          <div className="mb-4 flex justify-end">
            <button
              onClick={onClose}
              className="text-gray-500 hover:text-gray-700"
            >
              <img src={closeButton} alt="closeButton" className="w-[60%]" />
            </button>
          </div>

          <div className="flex flex-col items-center">
            <h2 className="mb-6 text-xl font-semibold text-gray-800">
              잔액 부족
            </h2>

            <div className="w-full space-y-4 text-center">
              <div className="rounded-lg border border-red-200 bg-red-50 p-4">
                <p className="text-base leading-relaxed whitespace-pre-line text-red-700">
                  {message}
                </p>
              </div>

              <div className="text-sm text-gray-600">
                <p>코인이 부족하여 가챠를 뽑을 수 없습니다.</p>
                <p>게임을 통해 코인을 모아보세요!</p>
              </div>
            </div>

            {/* 확인 버튼 */}
            <div className="mt-6 flex justify-center">
              <button
                onClick={onClose}
                className="rounded-lg bg-primary px-8 py-2 text-center font-medium text-white hover:bg-primary/80"
              >
                확인
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default InsufficientFundsModal;
