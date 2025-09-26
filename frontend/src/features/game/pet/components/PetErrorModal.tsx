import { Modal } from '@/features/game/shared/components/GameModal';
import { usePetStore } from '@/features/game/pet/store/usePetStore';

const PetErrorModal = () => {
  const { errorDialog, hideErrorDialog } = usePetStore();

  const buttons = [
    {
      text: '확인',
      onClick: hideErrorDialog,
      className:
        'text-white focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90',
    },
  ];

  return (
    <Modal
      isOpen={errorDialog.isOpen}
      onClose={hideErrorDialog}
      title="알림"
      description={errorDialog.message || '펫이 배고파서 놀 수 없습니다.'}
      buttons={buttons}
    />
  );
};

export default PetErrorModal;
