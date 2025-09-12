import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogOverlay,
  DialogPortal,
  DialogTitle,
} from '@/shared/components/ui/dialog';
import { Button } from '@/shared/components/ui/button';
import closeButton from '@/assets/game_button/closeButton.png';

interface SaveModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: () => void;
}

export const SaveModal = ({ isOpen, onClose, onSave }: SaveModalProps) => {
  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogPortal>
        <DialogOverlay className="!bg-transparent backdrop-blur-sm backdrop-brightness-110" />
        <DialogContent
          showCloseButton={false} // 기본 닫기 버튼 숨기기
          className="game h-[14rem] border-0 bg-secondary p-4 font-galmuri shadow-xl sm:max-w-lg"
        >
          <DialogClose className="absolute top-4 right-4 ring-offset-background transition-opacity">
            <img
              src={closeButton}
              alt="close"
              className="h-7 w-7 border-none outline-none focus:ring-1 focus:ring-secondary focus:outline-none active:scale-95 active:brightness-90"
            />
          </DialogClose>

          <DialogHeader>
            <DialogTitle className="mt-12 text-center text-2xl tracking-widest">
              저장하시겠습니까?
            </DialogTitle>
            <DialogDescription className="text-center">
              방의 현재 상태가 저장됩니다.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter className="flex-row !justify-center gap-4 pb-0">
            <Button
              onClick={onClose}
              className="w-24 bg-white text-lg tracking-widest text-primary focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
            >
              CANCEL
            </Button>
            <Button
              onClick={onSave}
              className="w-24 text-lg tracking-widest text-white focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
            >
              SAVE
            </Button>
          </DialogFooter>
        </DialogContent>
      </DialogPortal>
    </Dialog>
  );
};
