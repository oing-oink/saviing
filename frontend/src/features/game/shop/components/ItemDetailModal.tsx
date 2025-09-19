import { useGameItemDetail } from '@/features/game/shop/query/useItemsQuery';
import { getItemImage } from '@/features/game/shop/utils/getItemImage';
import closeButton from '@/assets/game_button/closeButton.png';
import Coin from '@/features/game/shared/components/Coin';
import itemHeader from '@/assets/game_etc/itemHeader.png';

interface ItemDetailModalProps {
  itemId: number | null;
  isOpen: boolean;
  onClose: () => void;
}

const ItemDetailModal = ({ itemId, isOpen, onClose }: ItemDetailModalProps) => {
  const { data: item, isLoading, error } = useGameItemDetail(itemId);

  if (!isOpen) {
    return null;
  }

  if (isLoading) {
    return (
      <div className="game fixed inset-0 z-50 flex items-center justify-center bg-white/50">
        <div className="rounded-4xl bg-secondary p-6 shadow-lg">
          <div className="text-center">아이템 정보를 불러오는 중...</div>
        </div>
      </div>
    );
  }

  if (error || !item) {
    return (
      <div className="game fixed inset-0 z-50 flex items-center justify-center bg-white/50">
        <div className="rounded-4xl bg-secondary p-6 shadow-lg">
          <div className="text-center text-red-500">아이템 정보를 불러올 수 없습니다.</div>
          <button onClick={onClose} className="mt-4 px-4 py-2 bg-primary rounded">
            닫기
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="game fixed inset-0 z-50 flex items-center justify-center bg-white/50">
      <div className="relative">
        <img
          src={itemHeader}
          alt="itemHeader"
          className="mx-auto -mb-8 w-[50%]"
        />
        <div className="mx-4 max-w-md justify-center rounded-4xl bg-secondary p-6 px-1 shadow-lg">
          <div className="mb-4 flex justify-end">
            <button
              onClick={onClose}
              className="text-gray-500 hover:text-gray-700"
            >
              <img src={closeButton} alt="closeButton" className="w-[60%]" />
            </button>
          </div>
          <div className="mx-auto mb-4 flex h-48 w-48 items-center rounded-full bg-white">
            <img
              src={getItemImage(item.itemId)}
              alt={item.itemName}
              className="mx-auto h-32 w-32 object-contain"
            />
          </div>
          <div className="flex flex-col items-center pt-2">
            <h2 className="text-xl font-semibold text-gray-800">{item.itemName}</h2>
            <p className="px-10 pt-3 text-center text-sm leading-relaxed whitespace-pre-line text-gray-600">
              {item.itemDescription}
            </p>
            <div className="mt-5 mb-2 rounded-3xl border-2 border-level-05 bg-store-bg p-2 px-10 text-lg">
              PREVIEW
            </div>
            <Coin coin={item.coin} fishCoin={item.fishCoin} />
          </div>
        </div>
      </div>
    </div>
  );
};

export default ItemDetailModal;
