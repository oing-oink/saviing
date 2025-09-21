import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogTitle,
} from '@/shared/components/ui/dialog';

/** 데코 아이템 자동 배치가 실패했을 때 보여주는 모달 속성. */
interface PlacementBlockedModalProps {
  isOpen: boolean;
  onClose: () => void;
}

/** 배치 불가 안내 모달을 렌더링한다. */
export const PlacementBlockedModal = ({
  isOpen,
  onClose,
}: PlacementBlockedModalProps) => {
  return (
    <Dialog open={isOpen} onOpenChange={open => (open ? undefined : onClose())}>
      <DialogContent className="w-[320px] text-center">
        <DialogTitle>배치할 수 없어요</DialogTitle>
        <DialogDescription className="mt-2 text-sm text-gray-600">
          선택한 아이템을 놓을 수 있는 공간이 없습니다. 다른 위치를 선택해
          주세요.
        </DialogDescription>
        <button
          type="button"
          onClick={onClose}
          className="mt-6 w-full rounded-md bg-primary py-2 text-sm font-semibold text-white"
        >
          확인
        </button>
      </DialogContent>
    </Dialog>
  );
};
