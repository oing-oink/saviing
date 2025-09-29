import { Modal } from '@/features/game/shared/components/GameModal';

/** 방 저장 확인 모달에 필요한 속성. */
interface SaveModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: () => void;
}

/** 방 편집 내용을 저장할지 사용자에게 묻는 확인 모달. */
export const SaveModal = ({ isOpen, onClose, onSave }: SaveModalProps) => {
  const buttons = [
    {
      text: 'CANCEL',
      onClick: onClose,
      className:
        'bg-white text-primary focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90',
    },
    {
      text: 'SAVE',
      onClick: onSave,
      className:
        'text-white focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90',
    },
  ];

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title="저장하시겠습니까?"
      description="방의 현재 상태가 저장됩니다."
      buttons={buttons}
    />
  );
};
