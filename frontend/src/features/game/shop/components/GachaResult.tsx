import type { Item } from '@/features/game/shop/types/item';
import closeButton from '@/assets/game_button/closeButton.png';
import { useNavigate } from 'react-router-dom';
import { PAGE_PATH } from '@/shared/constants/path';
import {
  Dialog,
  DialogContent,
  DialogOverlay,
  DialogPortal,
} from '@/shared/components/ui/dialog';

interface ItemDetailModalProps {
  item: Item;
  onClose: () => void;
}

const GachaResult = ({ item }: ItemDetailModalProps) => {
  const navigate = useNavigate();

  return (
    <Dialog open={true} onOpenChange={() => navigate(PAGE_PATH.GACHA)}>
      <DialogPortal>
        <DialogOverlay className="game bg-white/50" />
        <DialogContent
          showCloseButton={false}
          className="game font-galmuri max-w-none border-none bg-transparent p-0 shadow-none top-[50%] left-[50%] translate-x-[-50%] translate-y-[-50%]">
          <div className="relative">
            <div className="mx-auto -mb-8 w-[55%] text-center text-2xl">
              <span className="inline-block rounded-xl bg-primary px-7 py-2 text-secondary">
                축하한다냥!
              </span>
            </div>
            <div className="mx-4 max-w-md justify-center rounded-3xl bg-secondary p-6 px-1 shadow-lg">
              <div className="mb-4 flex justify-end">
                <button
                  onClick={() => navigate(PAGE_PATH.GACHA)}
                  className="text-gray-500 hover:text-gray-700 focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
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
                <button onClick={() => navigate(PAGE_PATH.GACHA)}>
                  <div 
                    className="mt-5 mb-2 rounded-2xl border-3 border-level-06 bg-store-bg p-2 px-10 text-xl text-red-300
                    focus:ring-1 focus:ring-primary focus:outline-none active:scale-95 active:brightness-90"
                  >
                    BACK
                  </div>
                </button>
              </div>
            </div>
          </div>
        </DialogContent>
      </DialogPortal>
    </Dialog>
  );
};

export default GachaResult;
