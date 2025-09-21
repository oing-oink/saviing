import { useGameItemDetail } from '@/features/game/shop/query/useItemsQuery';
import { usePurchase } from '@/features/game/shop/query/usePurchaseQuery';
import { getItemImage } from '@/features/game/shop/utils/getItemImage';
import closeButton from '@/assets/game_button/closeButton.png';
import Coin from '@/features/game/shared/components/Coin';
import itemHeader from '@/assets/game_etc/itemHeader.png';
import type { Item, PaymentMethod } from '@/features/game/shop/types/item';
import { useState } from 'react';
import toast from 'react-hot-toast';

interface ItemDetailModalProps {
  itemId: number | null;
  isOpen: boolean;
  onClose: () => void;
  onPreview?: (item: Item) => void;
}

const ItemDetailModal = ({
  itemId,
  isOpen,
  onClose,
  onPreview,
}: ItemDetailModalProps) => {
  const { data: item, isLoading, error } = useGameItemDetail(itemId);
  const { mutate: purchase, isPending: isPurchasing } = usePurchase();
  const [selectedPayment, setSelectedPayment] = useState<PaymentMethod>('COIN');

  const handlePurchase = () => {
    if (!item) {
      return;
    }

    const characterId = 1;

    purchase(
      {
        characterId,
        itemId: item.itemId,
        paymentMethod: selectedPayment,
      },
      {
        onSuccess: () => {
          toast.success('구매가 완료되었습니다!', {
            className: 'game font-galmuri',
          });
          onClose();
        },
        onError: error => {
          toast.error(`구매 실패: ${error.message}`, {
            className: 'game font-galmuri',
          });
        },
      },
    );
  };

  const handlePreview = () => {
    if (!item || !onPreview) {
      return;
    }

    onPreview(item);
    onClose();
  };

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
          <div className="text-center text-red-500">
            아이템 정보를 불러올 수 없습니다.
          </div>
          <button
            onClick={onClose}
            className="mt-4 rounded bg-primary px-4 py-2"
          >
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
            <h2 className="text-xl font-semibold text-gray-800">
              {item.itemName}
            </h2>
            <p className="px-10 pt-3 text-center text-sm leading-relaxed whitespace-pre-line text-gray-600">
              {item.itemDescription}
            </p>
            <button
              type="button"
              onClick={handlePreview}
              className="mt-5 mb-2 rounded-3xl border-2 border-primary bg-store-bg px-10 py-2 text-lg font-semibold text-primary hover:bg-primary/10"
            >
              방에 미리 배치하기
            </button>
            <Coin coin={item.coin} fishCoin={item.fishCoin} />

            {/* 결제 방법 선택 */}
            <div className="mt-4 mb-4">
              <div className="mb-2 text-center text-sm text-gray-600">
                결제 방법 선택
              </div>
              <div className="flex justify-center gap-2">
                <button
                  onClick={() => setSelectedPayment('COIN')}
                  className={`rounded-lg px-4 py-2 text-sm ${
                    selectedPayment === 'COIN'
                      ? 'bg-primary text-white'
                      : 'bg-gray-200 text-gray-700'
                  }`}
                >
                  코인으로 결제 ({item.coin})
                </button>
                <button
                  onClick={() => setSelectedPayment('FISH_COIN')}
                  className={`rounded-lg px-4 py-2 text-sm ${
                    selectedPayment === 'FISH_COIN'
                      ? 'bg-primary text-white'
                      : 'bg-gray-200 text-gray-700'
                  }`}
                >
                  피시코인으로 결제 ({item.fishCoin})
                </button>
              </div>
            </div>

            {/* 구매 버튼 */}
            <div className="flex justify-center gap-2">
              <button
                onClick={handlePurchase}
                disabled={isPurchasing}
                className={`rounded-lg px-6 py-2 text-center font-semibold ${
                  isPurchasing
                    ? 'cursor-not-allowed bg-gray-300 text-gray-500'
                    : 'bg-green-500 text-white hover:bg-green-600'
                }`}
              >
                {isPurchasing ? '구매 중...' : '구매하기'}
              </button>
              <button
                onClick={onClose}
                className="rounded-lg bg-gray-500 px-6 py-2 text-center text-white hover:bg-gray-600"
              >
                취소
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ItemDetailModal;
