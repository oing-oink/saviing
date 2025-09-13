import { Modal } from '@/features/game/shared/components/GameModal';

interface SaveModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: () => void;
}

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
