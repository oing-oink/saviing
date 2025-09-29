import { useNavigate } from 'react-router-dom';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogOverlay,
  DialogPortal,
} from '@/shared/components/ui/dialog';
import { Button } from '@/shared/components/ui/button';
import closeButton from '@/assets/game_button/closeButton.png';
import infoHeader from '@/assets/game_etc/infoHeader.png';
import { PAGE_PATH } from '@/shared/constants/path';

interface SavingsSetupModalProps {
  isOpen: boolean;
  onClose: () => void;
}

/**
 * 적금 계좌 개설 안내 모달 컴포넌트
 *
 * 게임과 금융 도메인 연결이 안 되어있고, 적금 계좌가 없는 경우 표시됩니다.
 * 사용자에게 적금 개설의 필요성을 안내하고 적금 개설 페이지로 이동할 수 있는 버튼을 제공합니다.
 */
const SavingsSetupModal = ({ isOpen, onClose }: SavingsSetupModalProps) => {
  const navigate = useNavigate();

  const handleOpenChange = (open: boolean) => {
    if (!open) {
      onClose();
    }
  };

  const handleNavigateToProducts = () => {
    onClose();
    navigate(PAGE_PATH.WALLET);
  };

  return (
    <Dialog open={isOpen} onOpenChange={handleOpenChange}>
      <DialogPortal>
        <DialogOverlay className="bg-transparent backdrop-blur-sm backdrop-brightness-110" />
        <DialogContent
          className="game max-w-xs border-0 bg-transparent p-0 font-galmuri shadow-none"
          showCloseButton={false}
        >
          <DialogDescription className="sr-only">
            게임과 적금 계좌를 연결하기 위해 적금 계좌 개설이 필요합니다
          </DialogDescription>
          <div className="relative">
            <img
              src={infoHeader}
              alt="itemHeader"
              className="absolute top-0 left-1/2 z-10 w-44 -translate-x-1/2 -translate-y-1/2"
            />
            <div className="rounded-4xl bg-secondary px-5 pt-6 pb-4 shadow-xl">
              <div className="mb-2 flex justify-end">
                <button
                  onClick={onClose}
                  className="text-gray-500 hover:text-gray-700 active:scale-95 active:brightness-90"
                >
                  <img src={closeButton} alt="closeButton" className="w-7" />
                </button>
              </div>

              <div className="flex flex-col items-center">
                <DialogHeader className="mb-3">
                  <DialogTitle className="mb-3 text-xl font-semibold text-gray-600">
                    지금은 적금이 없어요
                  </DialogTitle>
                </DialogHeader>

                <div className="w-full space-y-3 text-base">
                  <div className="rounded-lg bg-white/70 p-3 text-center">
                    <div className="mb-2 text-sm text-gray-600">
                      🎮 게임 혜택을 받으려면
                    </div>
                    <div className="text-sm font-medium text-gray-800">
                      적금 계좌 개설이 필요해요!
                    </div>
                  </div>

                  <div className="space-y-2 rounded-lg bg-white/70 p-3">
                    <div className="text-center text-sm font-medium text-gray-800">
                      게임에서 얻은 혜택으로
                    </div>
                    <div className="text-center text-xs text-gray-600">
                      ✨ 더 높은 적금 이자율 적용!!
                      <br />
                      💰 펫과 아이템으로 금리 추가 혜택
                    </div>
                  </div>
                </div>

                <div className="mt-4 flex w-full flex-col gap-2">
                  <Button
                    onClick={handleNavigateToProducts}
                    className="w-full bg-primary px-6 py-3 text-sm font-medium text-white hover:bg-primary/80 active:scale-95 active:brightness-90"
                  >
                    적금 개설하기
                  </Button>
                  <Button
                    onClick={onClose}
                    variant="ghost"
                    className="mb-2 w-full bg-white px-6 py-3 text-sm text-primary hover:bg-primary/10 active:scale-95"
                  >
                    나중에
                  </Button>
                </div>
              </div>
            </div>
          </div>
        </DialogContent>
      </DialogPortal>
    </Dialog>
  );
};

export default SavingsSetupModal;
