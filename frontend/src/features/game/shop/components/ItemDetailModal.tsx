import { useGameItemDetail } from '@/features/game/shop/query/useItemsQuery';
import { usePurchase } from '@/features/game/shop/query/usePurchaseQuery';
import { getItemImage } from '@/features/game/shop/utils/getItemImage';
import closeButton from '@/assets/game_button/closeButton.png';
import itemHeader from '@/assets/game_etc/itemHeader.png';
import type { Item, PaymentMethod } from '@/features/game/shop/types/item';
import { CAT_SPRITE_PATHS } from '@/features/game/pet/data/catAnimations';
import CatSprite from '@/features/game/pet/components/CatSprite';
import { useEffect, useState, type ChangeEvent } from 'react';
import toast from 'react-hot-toast';
import { useGameEntryQuery } from '@/features/game/entry/query/useGameEntryQuery';
import InsufficientFundsModal from '@/features/game/shop/components/InsufficientFundsModal';

/** 상점 아이템 상세 모달에 필요한 속성. */
interface ItemDetailModalProps {
  itemId: number | null;
  isOpen: boolean;
  onClose: () => void;
  onPreview?: (item: Item) => void;
  /** 구매/미리보기 기능을 숨길지 여부 (기본값: false) */
  hideActions?: boolean;
  /** 가격 정보를 숨길지 여부 (기본값: false) */
  hidePrice?: boolean;
}

const hasCatSprite = (petId: number): petId is keyof typeof CAT_SPRITE_PATHS =>
  Object.prototype.hasOwnProperty.call(CAT_SPRITE_PATHS, petId);

/** 아이템 상세 정보 조회 및 구매/미리보기를 제공하는 모달 컴포넌트. */
const ItemDetailModal = ({
  itemId,
  isOpen,
  onClose,
  onPreview,
  hideActions = false,
}: ItemDetailModalProps) => {
  const { data: item, isLoading, error } = useGameItemDetail(itemId);
  const { mutate: purchase, isPending: isPurchasing } = usePurchase();
  const [selectedPayment, setSelectedPayment] = useState<PaymentMethod>('COIN');
  const [purchaseCountInput, setPurchaseCountInput] = useState('1');
  const { data: gameEntry } = useGameEntryQuery();
  const characterId = gameEntry?.characterId;
  const [showInsufficientFundsModal, setShowInsufficientFundsModal] =
    useState(false);

  useEffect(() => {
    setPurchaseCountInput('1');
    if (!item) {
      return;
    }
    const hasCoinPrice = (item.coin ?? 0) > 0;
    const hasFishCoinPrice = (item.fishCoin ?? 0) > 0;

    if (hasCoinPrice) {
      setSelectedPayment('COIN');
      return;
    }
    if (hasFishCoinPrice) {
      setSelectedPayment('FISH_COIN');
      return;
    }
    setSelectedPayment('COIN');
  }, [item]);

  const handleCountChange = (event: ChangeEvent<HTMLInputElement>) => {
    const rawValue = event.target.value;

    if (rawValue === '') {
      setPurchaseCountInput('');
      return;
    }

    const digitsOnly = rawValue.replace(/\D/g, '');
    const normalized = digitsOnly.replace(/^0+/, '');
    setPurchaseCountInput(normalized);
  };

  const handleCountBlur = () => {
    if (purchaseCountInput === '' || Number(purchaseCountInput) < 1) {
      setPurchaseCountInput('1');
    }
  };

  const handlePurchase = () => {
    if (!item) {
      return;
    }

    if (typeof characterId !== 'number') {
      toast.error('캐릭터 정보를 확인할 수 없습니다.', {
        className: 'game font-galmuri',
      });
      return;
    }

    const requiresCount =
      item.itemCategory === 'TOY' || item.itemCategory === 'FOOD';
    const normalizedCount = requiresCount ? Math.max(1, purchaseCount) : 1;

    if (
      requiresCount &&
      (!Number.isFinite(normalizedCount) || normalizedCount < 1)
    ) {
      toast.error('구매 수량은 1 이상이어야 합니다.', {
        className: 'game font-galmuri',
      });
      return;
    }

    if (requiresCount && purchaseCountInput === '') {
      setPurchaseCountInput(String(normalizedCount));
    }

    purchase(
      {
        characterId,
        itemId: item.itemId,
        paymentMethod: selectedPayment,
        count: requiresCount ? normalizedCount : undefined,
      },
      {
        onSuccess: () => {
          toast.success('구매가 완료되었습니다!', {
            className: 'game font-galmuri',
          });
          onClose();
        },
        onError: error => {
          // ApiError 객체에서 실제 에러 정보 추출
          const apiErrorResponse = (error as any)?.response;
          const errorCode = apiErrorResponse?.code;
          const errorMessage = apiErrorResponse?.message || error.message;

          // 잔액 부족 에러인지 확인
          const isInsufficientFunds =
            errorCode === 'PURCHASE_INSUFFICIENT_FUNDS' ||
            (errorMessage && errorMessage.includes('잔액이 부족합니다'));

          if (isInsufficientFunds) {
            setShowInsufficientFundsModal(true);
          } else {
            toast.error(`구매 실패: ${errorMessage}`, {
              className: 'game font-galmuri',
            });
          }
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

  const isCatItem = item.itemType === 'PET' && item.itemCategory === 'CAT';
  const canUseCatSprite = isCatItem && hasCatSprite(item.itemId);
  const requiresCountInput =
    !hideActions &&
    (item.itemCategory === 'TOY' || item.itemCategory === 'FOOD');
  const rawCount = purchaseCountInput === '' ? NaN : Number(purchaseCountInput);
  const purchaseCount = Number.isFinite(rawCount)
    ? Math.max(1, Math.floor(rawCount))
    : 0;
  const coinPrice = item.coin ?? 0;
  const fishCoinPrice = item.fishCoin ?? 0;
  const effectiveCountForPrice = purchaseCount > 0 ? purchaseCount : 0;
  const totalCoinPrice = coinPrice * effectiveCountForPrice;
  const totalFishCoinPrice = fishCoinPrice * effectiveCountForPrice;

  const formatPrice = (value: number) => value.toLocaleString();

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
          <div className="mx-auto mb-4 flex h-48 w-48 items-center justify-center rounded-full bg-white">
            {canUseCatSprite ? (
              <CatSprite
                itemId={item.itemId}
                currentAnimation="idle"
                className="origin-bottom scale-[2.4]"
              />
            ) : (
              <img
                src={getItemImage(item.itemId)}
                alt={item.itemName}
                className="mx-auto h-32 w-32 object-contain"
              />
            )}
          </div>
          <div className="flex flex-col items-center pt-2">
            <h2 className="text-xl font-semibold text-gray-800">
              {item.itemName}
            </h2>
            <p className="px-10 pt-3 text-center text-sm leading-relaxed whitespace-pre-line text-gray-600">
              {item.itemDescription}
            </p>

            {!hideActions && !requiresCountInput && (
              <button
                type="button"
                onClick={handlePreview}
                className="mt-5 mb-2 rounded-3xl border-2 border-primary bg-store-bg px-10 py-2 text-lg font-semibold text-primary hover:bg-primary/10"
              >
                방에 미리 배치하기
              </button>
            )}

            {!hideActions && requiresCountInput && (
              <div className="mt-5 mb-2 flex w-full flex-col items-center gap-2 px-8">
                <label className="text-sm font-semibold text-gray-700">
                  구매 수량을 입력하세요
                </label>
                <input
                  type="number"
                  min={1}
                  value={purchaseCountInput}
                  onChange={handleCountChange}
                  onBlur={handleCountBlur}
                  className="w-full rounded-3xl border-2 border-primary bg-white px-4 py-2 text-center text-lg font-semibold text-primary focus:ring-2 focus:ring-primary/40 focus:outline-none"
                />
              </div>
            )}

            {!hideActions && (
              <>
                {/* 결제 방법 선택 */}
                <div className="mt-4 mb-4">
                  <div className="mb-2 text-center text-sm text-gray-600">
                    결제 방법 선택
                  </div>
                  <div className="flex justify-center gap-2">
                    <button
                      onClick={() => setSelectedPayment('COIN')}
                      disabled={coinPrice <= 0}
                      className={`min-h-[60px] flex-1 rounded-lg px-4 py-2 text-xs leading-tight ${
                        selectedPayment === 'COIN'
                          ? 'bg-primary text-white'
                          : 'bg-gray-200 text-gray-700'
                      } ${
                        coinPrice <= 0 ? 'cursor-not-allowed opacity-50' : ''
                      }`}
                    >
                      <span>코인으로 결제</span>
                      <span className="text-[11px] whitespace-nowrap">
                        (총 {formatPrice(totalCoinPrice)} 코인)
                      </span>
                    </button>
                    <button
                      onClick={() => setSelectedPayment('FISH_COIN')}
                      disabled={fishCoinPrice <= 0}
                      className={`min-h-[60px] flex-1 rounded-lg px-4 py-2 text-xs leading-tight ${
                        selectedPayment === 'FISH_COIN'
                          ? 'bg-primary text-white'
                          : 'bg-gray-200 text-gray-700'
                      } ${
                        fishCoinPrice <= 0
                          ? 'cursor-not-allowed opacity-50'
                          : ''
                      }`}
                    >
                      <span>피시코인으로 결제</span>
                      <span className="text-[11px] whitespace-nowrap">
                        (총 {formatPrice(totalFishCoinPrice)} 피시코인)
                      </span>
                    </button>
                  </div>
                </div>

                {/* 구매 버튼 */}
                <div className="flex justify-center gap-2">
                  <button
                    onClick={handlePurchase}
                    disabled={isPurchasing || typeof characterId !== 'number'}
                    className={`rounded-lg px-6 py-2 text-center font-semibold ${
                      isPurchasing || typeof characterId !== 'number'
                        ? 'cursor-not-allowed bg-gray-300 text-gray-500'
                        : 'bg-green-500 text-white hover:bg-green-600'
                    }`}
                  >
                    {isPurchasing ? '구매 중...' : '구매하기'}
                  </button>
                  <button
                    onClick={onClose}
                    className="flex-1 rounded-lg bg-gray-500 px-6 py-2 text-center text-sm whitespace-nowrap text-white hover:bg-gray-600"
                  >
                    취소
                  </button>
                </div>
              </>
            )}

            {hideActions && (
              <div className="mt-4 flex justify-center">
                <button
                  onClick={onClose}
                  className="rounded-lg bg-primary px-8 py-2 text-center text-white hover:bg-primary/80"
                >
                  닫기
                </button>
              </div>
            )}
          </div>
        </div>
      </div>

      <InsufficientFundsModal
        isOpen={showInsufficientFundsModal}
        onClose={() => setShowInsufficientFundsModal(false)}
        message="잔액이 부족합니다."
      />
    </div>
  );
};

export default ItemDetailModal;
