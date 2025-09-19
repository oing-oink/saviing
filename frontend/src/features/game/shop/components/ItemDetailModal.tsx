import type { Item } from '@/features/game/shop/types/item';
import closeButton from '@/assets/game_button/closeButton.png';
import Coin from '@/features/game/shared/components/Coin';
import itemHeader from '@/assets/game_etc/itemHeader.png';
import {
  Dialog,
  DialogContent,
  DialogOverlay,
  DialogPortal,
} from '@/shared/components/ui/dialog';

interface ItemDetailModalProps {
  item: Item;
  isOpen: boolean;
  onClose: () => void;
}

const ItemDetailModal = ({ item, isOpen, onClose }: ItemDetailModalProps) => {
  return (
    <Dialog open={isOpen} onOpenChange={(open) => !open && onClose()}>
      <DialogPortal>
        <DialogOverlay className="backdrop-blur-sm backdrop-brightness-100" />
        <DialogContent
          showCloseButton={false}
          className="game font-galmuri max-w-none border-none bg-transparent p-0 shadow-none top-[50%] left-[50%] translate-x-[-50%] translate-y-[-50%]"
        >
          <div className="relative">
            <img
              src={itemHeader}
              alt="itemHeader"
              className="mx-auto -mb-8 w-[50%]"
            />
            <div className="mx-4 max-w-md justify-center rounded-3xl bg-secondary p-6 px-1 shadow-lg">
              <div className="mb-4 flex justify-end">
                <button
                  onClick={onClose}
                  className="text-gray-500 focus:ring-1 focus:ring-transparent focus:outline-none active:scale-95 active:brightness-90"
                >
                  <img src={closeButton} alt="closeButton" className="w-[60%]" />
                </button>
              </div>
              <div className="mx-auto mb-4 flex h-48 w-48 items-center rounded-full bg-white">
                <img
                  src={item.image}
                  alt={item.name}
                  className="mx-auto h-32 w-32 object-contain"
                />
              </div>
              <div className="flex flex-col items-center pt-2">
                <h2 className="text-xl font-semibold text-gray-800">{item.name}</h2>
                <p className="px-10 pt-3 text-center text-sm leading-relaxed whitespace-pre-line text-gray-600">
                  {item.description}
                </p>
                <div
                  className="mt-5 mb-2 rounded-3xl border-2 border-level-05 bg-store-bg p-2 px-10 text-lg
                  focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
                >
                  PREVIEW
                </div>
                <Coin />
              </div>
            </div>
          </div>
        </DialogContent>
      </DialogPortal>
    </Dialog>
  );
};

export default ItemDetailModal;
